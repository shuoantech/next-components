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

package com.qiwumind.next.components.redis.core.cache;

import com.google.common.base.Preconditions;
import com.qiwumind.next.components.redis.core.JedisPoolManager;
import com.qiwumind.next.components.redis.core.constants.RedisConstant;
import com.qiwumind.next.components.common.util.string.StringUtil;
import com.qiwumind.next.components.common.util.serializer.SerializationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.function.Consumer;

/**
 * 使用Jedis 操作
 * <p>
 * 所有方法均通过 try-with-resources 从连接池借出并归还连接，异常时连接也能正确归还，避免连接泄漏。
 */
public class JedisCache {
    private Logger logger = LoggerFactory.getLogger(JedisCache.class);
    private JedisPoolManager poolManager;
    /**
     * 最大扫描次数（防止无限循环）
     */
    private static final int MAX_SCAN_ITERATIONS = 100000;
    /**
     * 默认每次扫描数量
     */
    private static final int DEFAULT_COUNT = 100;

    public JedisCache(JedisPoolManager poolManage) {
        this.poolManager = poolManage;
    }


    public JedisPoolManager getPoolManager() {
        return this.poolManager;
    }

    public void setPoolManager(JedisPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    /**
     * 多线程处理redis 回调方法，可以结合 FutureTaskWorker 使用
     *
     * @param callback
     * @return T
     */
    public <T, R> R multiCall(T t, RedisCallBack<T, R> callback) {
        try (Jedis jedis = this.poolManager.getJedis()) {
            return callback.call(t, jedis);
        }
    }

    @FunctionalInterface
    public interface RedisCallBack<T, R> {
        R call(T t, Jedis jedis);
    }

    /**
     * 关闭jedis
     *
     * @param jedis
     */
    public void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 关闭jedis
     *
     * @param jedisPool
     */
    public void close(final JedisPool jedisPool) {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }


    /**
     * 获取环境key
     *
     * @param key
     * @return
     */
    public String getKey(final String key) {
        final StringBuilder builder = new StringBuilder();
        final String env = StringUtils.isBlank(this.poolManager.getEnv()) ? "PRD" : this.poolManager.getEnv();
        builder.append(env).append(RedisConstant.COLON).append(key);
        return builder.toString();
    }


    // ==================== 基础 SCAN 操作 ====================

    /**
     * 获取所有匹配的 key（使用 SCAN）
     *
     * @param pattern 匹配模式，如 "user:*", "cache:session:*"
     * @return key 集合
     */
    public Set<String> keys(String pattern) {
        return keys(pattern, DEFAULT_COUNT);
    }

    /**
     * 获取所有匹配的 key（自定义每次扫描数量）
     *
     * @param pattern 匹配模式
     * @param count   每次扫描数量（建议 100-1000）
     */
    public Set<String> keys(String pattern, int count) {
        Set<String> keys = new HashSet<>();
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams params = new ScanParams().match(pattern).count(count);
        int iteration = 0;
        try (Jedis jedis = this.poolManager.getJedis()) {
            do {
                // 防止无限循环
                if (iteration++ > MAX_SCAN_ITERATIONS) {
                    logger.warn("SCAN 超过最大迭代次数 {}, pattern: {}", MAX_SCAN_ITERATIONS, pattern);
                    break;
                }
                ScanResult<String> result = jedis.scan(cursor, params);
                keys.addAll(result.getResult());
                cursor = result.getCursor();

            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        }

        logger.debug("SCAN 完成, pattern: {}, 共获取 {} 个key, 迭代次数: {}",
                pattern, keys.size(), iteration);
        return keys;
    }

    /**
     * 批量删除匹配的 key（使用 SCAN）
     *
     * @param pattern 匹配模式
     * @return 删除的数量
     */
    public long delByPattern(String pattern) {
        return delByPattern(pattern, DEFAULT_COUNT);
    }

    /**
     * 批量删除匹配的 key
     *
     * @param pattern   匹配模式
     * @param batchSize 每批删除数量
     * @return 删除的数量
     */
    public long delByPattern(String pattern, int batchSize) {
        List<String> batch = new ArrayList<>(batchSize);
        long[] deletedCount = {0};

        scanKeys(pattern, batchSize, key -> {
            batch.add(key);
            if (batch.size() >= batchSize) {
                try (Jedis jedis = this.poolManager.getJedis()) {
                    deletedCount[0] += jedis.del(batch.toArray(new String[0]));
                }
                batch.clear();
            }
        });

        // 删除最后一批
        if (!batch.isEmpty()) {
            try (Jedis jedis = this.poolManager.getJedis()) {
                deletedCount[0] += jedis.del(batch.toArray(new String[0]));
            }
        }

        logger.info("批量删除完成, pattern: {}, 删除数量: {}", pattern, deletedCount[0]);
        return deletedCount[0];
    }

    // ==================== 流式处理（推荐，内存友好）====================

    /**
     * 流式处理所有匹配的 key（不一次性加载到内存）
     *
     * @param pattern   匹配模式
     * @param processor key 处理器
     */
    public void scanKeys(String pattern, Consumer<String> processor) {
        scanKeys(pattern, DEFAULT_COUNT, processor);
    }

    /**
     * 流式处理所有匹配的 key
     *
     * @param pattern   匹配模式
     * @param count     每次扫描数量
     * @param processor key 处理器
     */
    public void scanKeys(String pattern, int count, Consumer<String> processor) {
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams params = new ScanParams().match(pattern).count(count);
        int iteration = 0;
        long processedCount = 0;
        try (Jedis jedis = this.poolManager.getJedis()) {
            do {
                if (iteration++ > MAX_SCAN_ITERATIONS) {
                    logger.warn("SCAN 超过最大迭代次数 {}, pattern: {}", MAX_SCAN_ITERATIONS, pattern);
                    break;
                }

                ScanResult<String> result = jedis.scan(cursor, params);
                List<String> keys = result.getResult();
                // 处理这批 keys
                for (String key : keys) {
                    try {
                        processor.accept(key);
                        processedCount++;
                    } catch (Exception e) {
                        logger.error("处理 key 失败: {}", key, e);
                    }
                }

                cursor = result.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        }

        logger.info("SCAN 流式处理完成, pattern: {}, 共处理 {} 个key", pattern, processedCount);
    }

// ==============   string type =================

    /**
     * @param dbIndex
     * @param key
     * @param value
     * @param expireTime
     */
    public <T> void setValueByKey(final int dbIndex, final String key, final T value, final int expireTime) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            jedis.select(dbIndex);//需要调用一个方法，设置使用第几个database
            jedis.set(tmpKey, SerializationUtils.getStrFromObj(value));
            if (expireTime > 0) {
                jedis.expire(tmpKey, expireTime);
            }
        }
    }

    /**
     * string type
     *
     * @param key
     * @param value
     * @param expireTime 0 表示不过期
     */
    public <T> void setValueByKey(final String key, final T value, final int expireTime) {
        final String tmpKey = getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            Preconditions.checkNotNull(jedis, "jedis不能为空");
            jedis.set(tmpKey, SerializationUtils.getStrFromObj(value));
            if (expireTime > 0) {
                jedis.expire(tmpKey, expireTime);
            }
        }
    }

    /**
     * string type
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueByKey(final String key) {
        final String tmpKey = getKey(key);
        final String result;
        try (Jedis jedis = this.poolManager.getJedis()) {
            Preconditions.checkNotNull(jedis, "jedis不能为空");
            result = jedis.get(tmpKey);
        }
        if (StringUtils.isBlank(result)) {
            return null;
        }
        return (T) SerializationUtils.getObjFromStr(result);
    }

    /**
     * @param dbIndex
     * @param key
     * @return
     */
    public String getValueByKey(final int dbIndex, final String key) {
        final String tmpKey = this.getKey(key);
        final String result;
        try (Jedis jedis = this.poolManager.getJedis()) {
            jedis.select(dbIndex);
            result = jedis.get(tmpKey);
        }
        if (StringUtils.isBlank(result)) {
            return null;
        }
        return StringUtil.getString(SerializationUtils.getObjFromStr(result));
    }


// ==============   Sorted Set  type =================

    /**
     * 返回指定位置的集合元素,0为第一个元素，-1为最后一个元素
     *
     * @param <T>
     * @param key
     * @param start 开始查询的位置元素
     * @param end   结束查询的位置元素
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zrange(final String key, final int start, final int end) {
        final String tmpKey = this.getKey(key);
        final List<String> set;
        try (Jedis jedis = this.poolManager.getJedis()) {
            set = jedis.zrange(tmpKey, start, end);
        }
        // 使用 LinkedHashSet 保持 Sorted Set 的排序顺序
        final Set<T> result = new LinkedHashSet<>();
        if (CollectionUtils.isNotEmpty(set)) {
            for (String item : set) {
                result.add((T) SerializationUtils.getObjFromStr(item));
            }
        }
        return result;
    }

    /**
     * 从有序集合中删除成员
     *
     * @param key
     * @param value
     * @return 返回1成功
     */
    public long zrem(final String key, final String value) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.zrem(tmpKey, SerializationUtils.getStrFromObj(value));
        }
    }

    /**
     * 获取给定区间的元素，原始按照权重由高到低排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zrevrange(final String key, final int start, final int end) {
        final String tmpKey = this.getKey(key);
        final List<String> set;
        try (Jedis jedis = this.poolManager.getJedis()) {
            set = jedis.zrevrange(tmpKey, start, end);
        }
        // 使用 LinkedHashSet 保持 Sorted Set 的排序顺序
        final Set<T> result = new LinkedHashSet<>();
        if (CollectionUtils.isNotEmpty(set)) {
            for (String item : set) {
                result.add((T) SerializationUtils.getObjFromStr(item));
            }
        }
        return result;
    }


    /* ==================hash 结构=========================== */

    /**
     * 添加对应关系，如果对应关系已存在，则覆盖
     *
     * @param <T>
     * @param key
     * @param value 对应关系
     * @return 状态，成功返回OK
     */
    public <T> Long hset(final String key, final String mapKey, final T value) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.hset(tmpKey, mapKey, SerializationUtils.getStrFromObj(value));
        }
    }

    /**
     * 添加对应关系，如果对应关系已存在，则覆盖
     *
     * @param <T>
     * @param key
     * @param value 对应关系
     * @return 状态，成功返回OK
     */
    public <T> Long hset(final String key, final String mapKey, final T value, final int timeout) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            final Long result = jedis.hset(tmpKey, mapKey, SerializationUtils.getStrFromObj(value));
            jedis.expire(tmpKey, timeout);
            return result;
        }
    }

    public Long hsetMap(final String key, Map<String, String> map) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.hset(tmpKey, map);
        }
    }

    /**
     * hashmap中取 对应的对象值
     *
     * @param key
     * @param fields
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T hget(final String key, final String fields) {
        final String tmpKey = this.getKey(key);
        final String str;
        try (Jedis jedis = this.poolManager.getJedis()) {
            str = jedis.hget(tmpKey, fields);
        }
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return (T) SerializationUtils.getObjFromStr(str);
    }

    /**
     * 删除hashmap的 mapkey
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hdel(final String key, final String... fields) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.hdel(tmpKey, fields);
        }
    }

    /**
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(final String key) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.hgetAll(tmpKey);
        }
    }


    /* ==================List 结构=========================== */

    /**
     * 向List头部追加记录
     *
     * @param key
     * @param value
     * @return 记录总数
     */
    public <T> long rpush(final String key, final T value) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.rpush(tmpKey, SerializationUtils.getStrFromObj(value));
        }
    }

    /**
     * 删除
     *
     * @param key
     * @return
     */
    public long del(final String key) {
        final String tmpKey = this.getKey(key);
        this.logger.info("***del key={}***", tmpKey);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.del(tmpKey);
        }
    }


    /**
     * @param dbIndex
     * @param key
     */
    public void deleteByKey(final int dbIndex, final String key) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            jedis.select(dbIndex);
            jedis.del(tmpKey);
        }
    }

    /**
     * 是否存在KEY
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        final String tmpnewKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.exists(tmpnewKey);
        }
    }

    /**
     * 重命名KEY
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public String rename(final String oldKey, final String newKey) {
        final String tmpKey = this.getKey(oldKey);
        final String tmpnewKey = this.getKey(newKey);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.rename(tmpKey, tmpnewKey);
        }
    }

    /**
     * 设置失效时间 秒
     *
     * @param key
     * @param seconds
     */
    public void expire(final String key, final int seconds) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            jedis.expire(tmpKey, seconds);
        }
    }

    /**
     * 删除失效时间
     *
     * @param key
     */
    public void persist(final String key) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            jedis.persist(tmpKey);
        }
    }

    /**
     * 返回指定key序列值
     *
     * @param key
     * @return
     */
    public long incr(final String key) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.incr(tmpKey);
        }
    }

    /**
     * @param key
     * @param field
     * @param time
     * @return
     */
    public String setex(final String key, final String field, final int time) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.setex(tmpKey, time, field);
        }
    }

    /**
     * Redis HyperLogLog的使用 HyperLogLog与布隆过滤器都是针对大数据统计存储应用场景下的知名算法。
     * pfadd/pfcount/pfmerge
     *
     * @param key
     * @param elements
     * @return
     */
    public Long pfadd(final String key, final String[] elements) {
        final String tmpKey = this.getKey(key);
        try (Jedis jedis = this.poolManager.getJedis()) {
            return jedis.pfadd(tmpKey, elements);
        }
    }

}
