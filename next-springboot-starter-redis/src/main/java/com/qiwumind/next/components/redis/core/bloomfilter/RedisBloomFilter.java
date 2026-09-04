/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.redis.core.bloomfilter;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 基于Redis BITMAP 的分布式布隆过滤器实现
 * 使用 Lettuce 作为 Redis 客户端（连接由 {@link RedisConnectionManager} 统一管理）
 * <p>
 * 优化要点：
 * <ul>
 *   <li>哈希：单次 128-bit MurmurHash3 产生 (h1, h2)，按 double hashing 公式
 *       {@code position(i) = (h1 + i * h2) mod m} 派生 k 个位位置（与 Guava MURMUR128_MITZ_64 同款策略），
 *       一次哈希即可，位分布均匀；避免旧实现 k 次 "toString()+i" 拼接与 Math.abs(Integer.MIN_VALUE) 负数取模缺陷</li>
 *   <li>性能：k 条 SETBIT/GETBIT 通过 Lettuce async pipeline 单次网络往返提交（原来逐条 sync 为 k 次往返）；
 *       连接引用本地缓存（isOpen 校验），避免每次操作经过全局 synchronized 的连接管理器</li>
 *   <li>误判率：构造参数校验 + bitSize 溢出保护 + 哈希函数数量上限，并提供
 *       {@link #getExpectedFpp()}（理论值）与 {@link #getEstimatedFpp()}（基于实际位填充率的估算值）</li>
 *   <li>内存：无哈希结果缓存（哈希计算成本低，缓存反而带来无界内存风险）；统计计数使用 LongAdder</li>
 * </ul>
 * 注意：哈希算法升级后位布局与旧版本不兼容，升级时需对存量过滤器执行 clear + 重建。
 */
@Slf4j
public class RedisBloomFilter<T> implements BloomFilter<T> {

    /** 最大哈希函数数量上限（再大对误判率无收益且 pipeline 开销线性增长） */
    private static final int MAX_HASH_FUNCTIONS = 255;
    /** 最大位大小（int 安全上限，按字节对齐） */
    private static final int MAX_BIT_SIZE = Integer.MAX_VALUE - 7;

    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 100;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 5000; // 默认连接超时时间（毫秒）
    private static final long DEFAULT_COMMAND_TIMEOUT = 3000; // 默认命令超时时间（毫秒）

    private final String name;
    private final long expectedInsertions;
    private final double falsePositiveProbability;
    private final String redisKey;
    private final String redisUri;
    private final int bitSize;
    private final int hashFunctions;
    private final int retryCount;
    private final long connectionTimeout;
    private final long commandTimeout;

    /** 缓存的 Redis 连接（Lettuce 连接线程安全且断线自动重连，isOpen 失效时重建） */
    private volatile StatefulRedisConnection<String, String> cachedConnection;

    // 轻量级性能统计（线程安全）
    private final LongAdder putCount = new LongAdder();
    private final LongAdder getCount = new LongAdder();
    private final LongAdder clearCount = new LongAdder();
    private final LongAdder totalTimeNanos = new LongAdder();

    /**
     * 构造函数
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     * @param redisKeyPrefix           Redis键前缀
     * @param redisUri                 Redis连接URI
     */
    public RedisBloomFilter(String name, long expectedInsertions, double falsePositiveProbability,
                            String redisKeyPrefix, String redisUri) {
        this(name, expectedInsertions, falsePositiveProbability, redisKeyPrefix, redisUri, DEFAULT_RETRY_COUNT);
    }

    /**
     * 构造函数
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     * @param redisKeyPrefix           Redis键前缀
     * @param redisUri                 Redis连接URI
     * @param retryCount               重试次数
     */
    public RedisBloomFilter(String name, long expectedInsertions, double falsePositiveProbability,
                            String redisKeyPrefix, String redisUri, int retryCount) {
        this(name, expectedInsertions, falsePositiveProbability, redisKeyPrefix, redisUri, retryCount,
                DEFAULT_CONNECTION_TIMEOUT, DEFAULT_COMMAND_TIMEOUT);
    }

    /**
     * 构造函数
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量（必须 &gt; 0）
     * @param falsePositiveProbability 期望误判率（必须在 (0, 1) 开区间内）
     * @param redisKeyPrefix           Redis键前缀
     * @param redisUri                 Redis连接URI
     * @param retryCount               重试次数
     * @param connectionTimeout        连接超时时间（毫秒）
     * @param commandTimeout           命令超时时间（毫秒）
     */
    public RedisBloomFilter(String name, long expectedInsertions, double falsePositiveProbability,
                            String redisKeyPrefix, String redisUri, int retryCount,
                            long connectionTimeout, long commandTimeout) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Bloom filter name must not be blank");
        }
        if (expectedInsertions <= 0) {
            throw new IllegalArgumentException("expectedInsertions must be positive: " + expectedInsertions);
        }
        if (falsePositiveProbability <= 0.0 || falsePositiveProbability >= 1.0) {
            throw new IllegalArgumentException("falsePositiveProbability must be in (0, 1): " + falsePositiveProbability);
        }
        this.name = name;
        this.expectedInsertions = expectedInsertions;
        this.falsePositiveProbability = falsePositiveProbability;
        this.redisKey = redisKeyPrefix + ":" + name;
        this.redisUri = redisUri;
        this.retryCount = Math.max(1, retryCount);
        this.connectionTimeout = connectionTimeout;
        this.commandTimeout = commandTimeout;

        // 计算布隆过滤器的位大小和哈希函数数量（含溢出保护）
        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveProbability);
        this.hashFunctions = optimalHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * 计算最优的位大小：m = ceil(-n * ln p / (ln2)^2)，带 int 溢出保护
     *
     * @param n 预期插入元素数量
     * @param p 期望误判率
     * @return 最优位大小
     */
    private static int optimalBitSize(long n, double p) {
        double numBits = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        return (int) Math.min(Math.ceil(numBits), MAX_BIT_SIZE);
    }

    /**
     * 计算最优的哈希函数数量：k = round(m / n * ln2)，上限 {@value #MAX_HASH_FUNCTIONS}
     *
     * @param n 预期插入元素数量
     * @param m 位大小
     * @return 最优哈希函数数量
     */
    private static int optimalHashFunctions(long n, int m) {
        double k = Math.round((double) m / n * Math.log(2));
        return (int) Math.max(1, Math.min(k, MAX_HASH_FUNCTIONS));
    }

    /**
     * 计算元素的 k 个哈希位位置。
     * 单次 128-bit MurmurHash3 得到 (h1, h2)，double hashing 派生：
     * {@code position(i) = (h1 + i * h2) mod m}，保证位分布均匀且只需一次哈希。
     *
     * @param element 元素
     * @return 哈希位位置数组（长度 = hashFunctions）
     */
    private long[] hashPositions(T element) {
        HashCode hashCode = Hashing.murmur3_128().hashBytes(toBytes(element));
        byte[] bytes = hashCode.asBytes();
        long h1 = longAt(bytes, 0);
        long h2 = longAt(bytes, 8);
        long[] positions = new long[hashFunctions];
        long combined = h1;
        for (int i = 0; i < hashFunctions; i++) {
            // & Long.MAX_VALUE 使结果非负，规避 Math.abs(Integer.MIN_VALUE) 溢出缺陷
            positions[i] = (combined & Long.MAX_VALUE) % bitSize;
            combined += h2;
        }
        return positions;
    }

    private static long longAt(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i >= 0; i--) {
            value = (value << 8) | (bytes[offset + i] & 0xFFL);
        }
        return value;
    }

    /**
     * 元素转字节。String / Number / Boolean 走 UTF-8 字节快速路径，
     * 其他可序列化对象走 Java 序列化（与 {@link GuavaBloomFilter} 的字节来源一致）。
     */
    private byte[] toBytes(T element) {
        if (element instanceof String s) {
            return s.getBytes(StandardCharsets.UTF_8);
        }
        if (element instanceof Number || element instanceof Boolean) {
            return element.toString().getBytes(StandardCharsets.UTF_8);
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(element);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Element is not serializable: " + element.getClass(), e);
        }
    }

    @Override
    public boolean put(T element) {
        long startTime = System.nanoTime();
        boolean changed = executeWithRetry("put element", () -> {
            long[] positions = hashPositions(element);
            StatefulRedisConnection<String, String> connection = connection();
            // pipeline：k 条 SETBIT 单次网络往返
            List<RedisFuture<Long>> futures = new ArrayList<>(positions.length);
            for (long position : positions) {
                futures.add(connection.async().setbit(redisKey, position, 1));
            }
            awaitAll(futures);
            // 任一位由 0 变 1，说明元素是首次加入（与 BloomFilter 接口语义一致：已存在返回 false）
            boolean bitsChanged = false;
            for (RedisFuture<Long> future : futures) {
                if (longValue(future) == 1L) {
                    bitsChanged = true;
                }
            }
            return bitsChanged;
        });
        totalTimeNanos.add(System.nanoTime() - startTime);
        putCount.increment();
        return changed;
    }

    @Override
    public boolean mightContain(T element) {
        long startTime = System.nanoTime();
        boolean result = executeWithRetry("check element", () -> {
            long[] positions = hashPositions(element);
            StatefulRedisConnection<String, String> connection = connection();
            // pipeline：k 条 GETBIT 单次网络往返
            List<RedisFuture<Long>> futures = new ArrayList<>(positions.length);
            for (long position : positions) {
                futures.add(connection.async().getbit(redisKey, position));
            }
            awaitAll(futures);
            // 任一位为 0，元素一定不存在
            for (RedisFuture<Long> future : futures) {
                if (longValue(future) == 0L) {
                    return false;
                }
            }
            return true; // 所有位都为1，元素可能存在
        });
        totalTimeNanos.add(System.nanoTime() - startTime);
        getCount.increment();
        return result;
    }

    @Override
    public void clear() {
        long startTime = System.nanoTime();
        executeWithRetry("clear bloom filter", () -> {
            StatefulRedisConnection<String, String> connection = connection();
            connection.sync().del(redisKey);
            return null;
        });
        totalTimeNanos.add(System.nanoTime() - startTime);
        clearCount.increment();
    }

    /**
     * 理论误判率（构造时的期望值 p）
     */
    public double getExpectedFpp() {
        return falsePositiveProbability;
    }

    /**
     * 估算当前实际误判率：p ≈ (置位位数 / bitSize) ^ k。
     * 插入数量接近 expectedInsertions 时约等于期望误判率，超出后持续上升（提示需要扩容重建）。
     */
    public double getEstimatedFpp() {
        Long bitsSet = executeWithRetry("count bits", () ->
                connection().sync().bitcount(redisKey));
        if (bitsSet == null || bitsSet <= 0) {
            return 0.0;
        }
        double fillRatio = Math.min(1.0, (double) bitsSet / bitSize);
        return Math.pow(fillRatio, hashFunctions);
    }

    /**
     * 获取缓存的 Redis 连接（失效时经由 RedisConnectionManager 重建）
     */
    private StatefulRedisConnection<String, String> connection() {
        StatefulRedisConnection<String, String> connection = this.cachedConnection;
        if (connection == null || !connection.isOpen()) {
            connection = RedisConnectionManager.getInstance().getStatefulConnection(redisUri);
            this.cachedConnection = connection;
        }
        return connection;
    }

    /**
     * pipeline 提交后统一等待结果（超时按 commandTimeout）
     */
    private void awaitAll(List<? extends RedisFuture<?>> futures) {
        boolean completed = LettuceFutures.awaitAll(commandTimeout, TimeUnit.MILLISECONDS,
                futures.toArray(new RedisFuture[0]));
        if (!completed) {
            throw new IllegalStateException("Bloom filter pipeline did not complete within " + commandTimeout + " ms");
        }
    }

    private static long longValue(RedisFuture<Long> future) {
        Long value = safeGet(future);
        return value == null ? 0L : value;
    }

    private static <V> V safeGet(RedisFuture<V> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read pipeline result", e);
        }
    }

    /**
     * 带重试的执行模板（指数退避）
     *
     * @param actionDesc 操作描述（用于日志）
     * @param action     实际操作
     * @param <R>        返回类型
     * @return 操作结果
     */
    private <R> R executeWithRetry(String actionDesc, java.util.function.Supplier<R> action) {
        int attempt = 0;
        Exception last = null;
        while (attempt < retryCount) {
            try {
                return action.get();
            } catch (Exception e) {
                last = e;
                attempt++;
                if (attempt >= retryCount) {
                    break;
                }
                handleRetry(attempt, e);
            }
        }
        throw new RuntimeException("Failed to " + actionDesc + " after " + retryCount + " attempts: "
                + (last == null ? "unknown" : last.getMessage()), last);
    }

    /**
     * 处理重试逻辑
     *
     * @param attempt 当前重试次数
     * @param e       异常信息
     */
    private void handleRetry(int attempt, Exception e) {
        // 指数退避策略
        long delay = RETRY_DELAY_MS * (1L << (attempt - 1));
        log.warn("Attempt {} failed: {}, retrying in {} ms", attempt, e.getMessage(), delay);
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", ie);
        }
    }

    /**
     * 关闭布隆过滤器（连接由 RedisConnectionManager 统一管理，此处仅输出性能统计）
     */
    public void close() {
        log.info("RedisBloomFilter Performance Stats:\n{}", getStats());
    }

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间（毫秒）
     */
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 获取命令超时时间
     *
     * @return 命令超时时间（毫秒）
     */
    public long getCommandTimeout() {
        return commandTimeout;
    }

    /**
     * 获取性能统计信息
     *
     * @return 性能统计信息字符串
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("RedisBloomFilter Stats for '").append(name).append("':\n");
        sb.append("  Expected Insertions: ").append(expectedInsertions).append("\n");
        sb.append("  Expected FPP: ").append(falsePositiveProbability).append("\n");
        sb.append("  Bit Size: ").append(bitSize).append("\n");
        sb.append("  Hash Functions: ").append(hashFunctions).append("\n");
        sb.append("  Put Operations: ").append(putCount.sum()).append("\n");
        sb.append("  Get Operations: ").append(getCount.sum()).append("\n");
        sb.append("  Clear Operations: ").append(clearCount.sum()).append("\n");
        sb.append("  Total Time: ").append(totalTimeNanos.sum() / 1_000_000).append(" ms\n");
        long totalOps = putCount.sum() + getCount.sum() + clearCount.sum();
        if (totalOps > 0) {
            sb.append("  Average Operation Time: ")
                    .append(totalTimeNanos.sum() / totalOps)
                    .append(" ns\n");
        }
        return sb.toString();
    }
}
