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



import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于阿里云Redis的分布式布隆过滤器实现
 * 使用Lettuce作为Redis客户端
 * 使用RedisConnectionManager管理连接
 * 使用管道（pipeline）减少网络往返时间
 * 优化了连接管理、哈希计算和错误处理
 */
public class RedisBloomFilter<T> implements BloomFilter<T> {

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

    // 哈希结果缓存，减少重复计算
    private final Map<T, long[]> hashCache = new ConcurrentHashMap<>();

    // 轻量级性能统计
    private long putCount = 0;
    private long getCount = 0;
    private long clearCount = 0;
    private long totalTimeNanos = 0;

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
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     * @param redisKeyPrefix           Redis键前缀
     * @param redisUri                 Redis连接URI
     * @param retryCount               重试次数
     * @param connectionTimeout        连接超时时间（毫秒）
     * @param commandTimeout           命令超时时间（毫秒）
     */
    public RedisBloomFilter(String name, long expectedInsertions, double falsePositiveProbability,
                            String redisKeyPrefix, String redisUri, int retryCount,
                            long connectionTimeout, long commandTimeout) {
        this.name = name;
        this.expectedInsertions = expectedInsertions;
        this.falsePositiveProbability = falsePositiveProbability;
        this.redisKey = redisKeyPrefix + ":" + name;
        this.redisUri = redisUri;
        this.retryCount = retryCount;
        this.connectionTimeout = connectionTimeout;
        this.commandTimeout = commandTimeout;

        // 计算布隆过滤器的位大小和哈希函数数量
        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveProbability);
        this.hashFunctions = optimalHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * 计算最优的位大小
     *
     * @param n 预期插入元素数量
     * @param p 期望误判率
     * @return 最优位大小
     */
    private int optimalBitSize(long n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算最优的哈希函数数量
     *
     * @param n 预期插入元素数量
     * @param m 位大小
     * @return 最优哈希函数数量
     */
    private int optimalHashFunctions(long n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * 计算元素的哈希值数组
     * 使用缓存减少重复计算
     *
     * @param element 元素
     * @return 哈希值数组
     */
    private long[] getHashValues(T element) {
        return hashCache.computeIfAbsent(element, this::computeHashValues);
    }

    /**
     * 计算元素的哈希值数组
     *
     * @param element 元素
     * @return 哈希值数组
     */
    private long[] computeHashValues(T element) {
        long[] hashes = new long[hashFunctions];
        for (int i = 0; i < hashFunctions; i++) {
            String value = element.toString() + i;
            hashes[i] = Math.abs(murmurHash3(value)) % bitSize;
        }
        return hashes;
    }

    /**
     * MurmurHash3算法的简化实现
     * 比SHA-256更快，适合布隆过滤器使用
     *
     * @param value 要哈希的值
     * @return 哈希值
     */
    private long murmurHash3(String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        int length = data.length;
        int h1 = 0;
        int k1 = 0;

        // 处理数据块
        int i = 0;
        while (length >= 4) {
            k1 = (data[i] & 0xFF) | ((data[i + 1] & 0xFF) << 8) |
                    ((data[i + 2] & 0xFF) << 16) | ((data[i + 3] & 0xFF) << 24);

            k1 *= 0xcc9e2d51;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= 0x1b873593;

            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;

            i += 4;
            length -= 4;
        }

        // 处理剩余字节
        k1 = 0;
        switch (length) {
            case 3:
                k1 ^= (data[i + 2] & 0xFF) << 16;
            case 2:
                k1 ^= (data[i + 1] & 0xFF) << 8;
            case 1:
                k1 ^= (data[i] & 0xFF);
                k1 *= 0xcc9e2d51;
                k1 = Integer.rotateLeft(k1, 15);
                k1 *= 0x1b873593;
                h1 ^= k1;
        }

        // 最终混合
        h1 ^= data.length;
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1 & 0xFFFFFFFFL;
    }

    @Override
    public boolean put(T element) {
        long startTime = System.nanoTime();
        int attempt = 0;
        boolean success = false;
        while (attempt < retryCount) {
            try {
                success = doPut(element);
                break;
            } catch (Exception e) {
                attempt++;
                if (attempt >= retryCount) {
                    throw new RuntimeException("Failed to put element after " + retryCount + " attempts: " + e.getMessage(), e);
                }
                handleRetry(attempt, e);
            }
        }

        long endTime = System.nanoTime();
        totalTimeNanos += (endTime - startTime);
        putCount++;

        return success;
    }

    /**
     * 实际执行put操作
     * @param element 要添加的元素
     * @return 是否成功添加
     */
    private boolean doPut(T element) {
        boolean added = false;
        // 使用RedisConnectionManager获取StatefulRedisConnection
        StatefulRedisConnection<String, String> connection =
                RedisConnectionManager.getInstance().getStatefulConnection(redisUri);
        // 使用管道批量执行SETBIT命令
        RedisCommands<String, String> pipeline = connection.sync();
        // 获取缓存的哈希值
        long[] hashes = getHashValues(element);
        // 计算所有哈希位置并添加到管道
        for (long position : hashes) {
            pipeline.setbit(redisKey, position, 1);
        }
        // 执行管道命令并获取结果
        for (Object result : pipeline.exec()) {
            if (result instanceof Long && (Long) result == 0) {
                added = true;
            }
        }

        return added;
    }

    @Override
    public boolean mightContain(T element) {
        long startTime = System.nanoTime();
        int attempt = 0;
        boolean result = false;

        while (attempt < retryCount) {
            try {
                result = doMightContain(element);
                break;
            } catch (Exception e) {
                attempt++;
                if (attempt >= retryCount) {
                    throw new RuntimeException("Failed to check element after " + retryCount + " attempts: " + e.getMessage(), e);
                }
                handleRetry(attempt, e);
            }
        }

        long endTime = System.nanoTime();
        totalTimeNanos += (endTime - startTime);
        getCount++;

        return result;
    }

    /**
     * 实际执行mightContain操作
     * @param element 要检查的元素
     * @return 是否可能存在
     */
    private boolean doMightContain(T element) {
        // 使用RedisConnectionManager获取StatefulRedisConnection
        StatefulRedisConnection<String, String> connection = RedisConnectionManager.getInstance().getStatefulConnection(redisUri);
        // 使用管道批量执行GETBIT命令
        RedisCommands<String, String> pipeline = connection.sync();
        // 获取缓存的哈希值
        long[] hashes = getHashValues(element);
        // 计算所有哈希位置并添加到管道
        for (long position : hashes) {
            pipeline.getbit(redisKey, position);
        }
        // 执行管道命令并获取结果
        for (Object result : pipeline.exec()) {
            if (result instanceof Long && (Long) result == 0) {
                return false;
            }
        }
        return true; // 所有位都为1，元素可能存在
    }

    @Override
    public void clear() {
        long startTime = System.nanoTime();
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                doClear();
                // 清除哈希缓存
                hashCache.clear();
                break;
            } catch (Exception e) {
                attempt++;
                if (attempt >= retryCount) {
                    throw new RuntimeException("Failed to clear bloom filter after " + retryCount + " attempts: " + e.getMessage(), e);
                }
                handleRetry(attempt, e);
            }
        }

        long endTime = System.nanoTime();
        totalTimeNanos += (endTime - startTime);
        clearCount++;
    }

    /**
     * 实际执行clear操作
     */
    private void doClear() {
        RedisCommands<String, String> commands = RedisConnectionManager.getInstance().getConnection(redisUri);
        // 使用Redis的DEL命令删除键
        commands.del(redisKey);
    }

    /**
     * 处理重试逻辑
     *
     * @param attempt 当前重试次数
     * @param e       异常信息
     */
    private void handleRetry(int attempt, Exception e) {
        // 记录重试信息
        System.err.println("Attempt " + attempt + " failed: " + e.getMessage() + ", retrying...");

        // 指数退避策略
        long delay = RETRY_DELAY_MS * (1L << (attempt - 1));
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", ie);
        }
    }

    /**
     * 关闭Redis连接
     * 注意：由于使用了连接池，这里不关闭连接，而是由连接池管理
     */
    public void close() {
        // 打印性能统计信息
        System.out.println("RedisBloomFilter Performance Stats:");
        System.out.println("  Name: " + name);
        System.out.println("  Put Operations: " + putCount);
        System.out.println("  Get Operations: " + getCount);
        System.out.println("  Clear Operations: " + clearCount);
        System.out.println("  Total Time: " + (totalTimeNanos / 1_000_000) + " ms");
        if (putCount + getCount + clearCount > 0) {
            System.out.println("  Average Operation Time: " +
                    (totalTimeNanos / (putCount + getCount + clearCount)) + " ns");
        }
        System.out.println("  Hash Cache Size: " + hashCache.size());

        // 清除哈希缓存
        hashCache.clear();

        // 由于使用了RedisConnectionManager，连接由连接池管理
        // 这里可以不做任何操作，或者根据需要关闭特定连接
        // RedisConnectionManager.INSTANCE.closeConnection(redisUri);
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
        sb.append("  False Positive Probability: ").append(falsePositiveProbability).append("\n");
        sb.append("  Bit Size: ").append(bitSize).append("\n");
        sb.append("  Hash Functions: ").append(hashFunctions).append("\n");
        sb.append("  Put Operations: ").append(putCount).append("\n");
        sb.append("  Get Operations: ").append(getCount).append("\n");
        sb.append("  Clear Operations: ").append(clearCount).append("\n");
        sb.append("  Total Time: ").append((totalTimeNanos / 1_000_000)).append(" ms\n");
        if (putCount + getCount + clearCount > 0) {
            sb.append("  Average Operation Time: ")
                    .append((totalTimeNanos / (putCount + getCount + clearCount)))
                    .append(" ns\n");
        }
        sb.append("  Hash Cache Size: ").append(hashCache.size()).append("\n");
        return sb.toString();
    }
}
