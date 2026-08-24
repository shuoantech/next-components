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



import com.qiwumind.next.components.common.util.serializer.SerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 使用springframework.data.redis 操作，与sping集成方便
 *
 * @author likuisheng 2017年12月1日 下午4:09:02
 */
@Getter
@Setter
@ToString
public class RedisTemplateCache {
    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplateCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 存入obj
     *
     * @param obj
     * @param keyId
     */
    public <T> void set(final String keyId, final T obj) {
        redisTemplate.opsForValue().set(keyId, obj);

    }

    public <T> void set(String k, T obj, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(k, obj, timeout, timeUnit);
    }

    /**
     * 取 obj
     *
     * @param keyId
     */
    public <T> T get(final String keyId) {
        return (T) redisTemplate.opsForValue().get(keyId);
    }


    /**
     * 存入hash对象数据
     *
     * @param obj
     * @param keyId
     * @param hashKey
     */
    public <T> void addHash(final String keyId, final String hashKey, final T obj) {
        final HashOperations<String, String, String> opsForHash = this.redisTemplate.opsForHash();
        opsForHash.put(keyId, hashKey, SerializationUtils.getStrFromObj(obj));
    }

    /**
     * 存入hash对象数据
     *
     * @param obj
     * @param keyId
     * @param hashKey
     */
    public <T> void addHash(final String keyId, final String hashKey, final T obj, final int timeont) {
        final HashOperations<String, String, String> opsForHash = this.redisTemplate.opsForHash();
        opsForHash.put(keyId, hashKey, SerializationUtils.getStrFromObj(obj));
        this.redisTemplate.expire(keyId, timeont, TimeUnit.MILLISECONDS);//设置超时时间10秒 第三个参数控制时间单位，详情查看TimeUnit
    }

    /**
     * 获得hash数据 entries(K key)
     *
     * @param <T>
     * @param keyId
     */
    public <T> Map<String, T> getHashEntries(final String keyId) {
        final HashOperations<String, String, T> opsForHash = this.redisTemplate.opsForHash();
        return opsForHash.entries(keyId);
    }

    /**
     * 获得hash数据
     *
     * @param <T>
     * @param keyId
     * @param hashKey
     */
    public <T> T getHash(final String keyId, final String hashKey) {
        final HashOperations<String, String, String> opsForHash = this.redisTemplate.opsForHash();
        String res = opsForHash.get(keyId, hashKey);
        return StringUtils.isBlank(res) ? null : (T) SerializationUtils.getObjFromStr(res);
    }

    /**
     * 更新hash数据
     *
     * @param <T>
     * @param obj
     * @param keyId
     * @param hashKey
     */
    public <T> void modifyHash(final T obj, final String keyId, final String hashKey) {
        final HashOperations<String, String, String> opsForHash = this.redisTemplate.opsForHash();
        final String result = opsForHash.get(keyId, hashKey);
        if (result != null) {
            opsForHash.put(keyId, hashKey, SerializationUtils.getStrFromObj(obj));
        }
    }

    /**
     * 删除hash数据
     *
     * @param <T>
     * @param keyId
     * @param hashKeys
     */
    public <T> void delHash(final String keyId, final String... hashKeys) {
        final HashOperations<String, String, String> opsForHash = this.redisTemplate.opsForHash();
        List<String> stringList = List.of(hashKeys);
        // 转换回 Object[] 但保留顺序特性
        Object[] objectArray = stringList.toArray();
        opsForHash.delete(keyId, objectArray);
    }

    /**
     * 存入集合对象数据
     *
     * @param list
     * @param keyId
     */
    public <T> void addList(final List<T> list, final String keyId) {
        final ListOperations<String, T> listOperations = (ListOperations<String, T>) this.redisTemplate.opsForList();
        //添加到缓存中 rightPush 依次有右边添加  leftPush依次由左边添加
        listOperations.rightPushAll(keyId, list);
    }

    /**
     * 存入单个对象数据到集合
     *
     * @param obj
     * @param keyId
     */
    public <T> void addList(final T obj, final String keyId) {
        final ListOperations<String, T> listOperations = (ListOperations<String, T>) this.redisTemplate.opsForList();
        //添加到缓存中 rightPush 依次有右边添加  leftPush依次由左边添加
        listOperations.rightPush(keyId, obj);
    }

    /**
     * 分页查询数据 倒序
     *
     * @param keyId
     * @param num       页码，从第一页开始
     * @param pageCount 每页条数
     * @return
     */
    public <T> List<T> findPageList(final String keyId, final Long num, final Long pageCount) {
        final ListOperations<String, T> listOperations = (ListOperations<String, T>) this.redisTemplate.opsForList();
        final Long startCount = (num - 1) * pageCount;
        //获得所有当前可以的所有信息 获得list集合 【 0 ，-1 代表所有值】
        return listOperations.range(keyId, startCount, startCount + pageCount - 1);
    }

    /**
     * 查询list 的key数量
     *
     * @param keyId
     * @return
     */
    public <T> long listCount(final String keyId) {
        final ListOperations<String, T> listOperations = (ListOperations<String, T>) this.redisTemplate.opsForList();
        return listOperations.size(keyId);
    }

    /**
     * 分页查询数据 倒序 ，索引start<=index<=end的元素子集，倒序
     *
     * @param keyId
     * @param index     第几页 从第一页开始
     * @param pageCount 每页数量
     * @return
     */
    public <T> Set<T> findPageZset(final String keyId, final Long index, final Long pageCount) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        final Long startCount = (index - 1) * pageCount;
        //获得所有当前可以的所有信息 获得list集合 【 0 ，-1 代表所有值】
        return zset.range(keyId, startCount, startCount + pageCount - 1);
    }

    /**
     * 查询所有
     *
     * @param keyId
     * @return
     */
    public <T> Set<T> findAllZset(final String keyId) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        //获得所有当前可以的所有信息 获得list集合 【 0 ，-1 代表所有值】
        return zset.range(keyId, 0, -1);
    }

    /**
     * 根据score 查询set中某对象
     *
     * @param keyId
     * @param score
     * @return
     */
    public <T> T findZsetWithScore(final String keyId, final double score) {
        final Set<T> set = this.findZsetWithScore(keyId, score, score);
        if (set.iterator().hasNext()) {
            return set.iterator().next();
        }
        return null;
    }

    /**
     * 根据score 查询set中某对象
     *
     * @param keyId
     * @param minscore
     * @param maxscore
     * @return
     */
    public <T> Set<T> findZsetWithScore(final String keyId, final double minscore, final double maxscore) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        return zset.rangeByScore(keyId, minscore, maxscore);

    }

    /**
     * 查询list 中键为K的集合元素个数
     *
     * @param keyId
     * @return
     */
    public <T> long zsetCount(final String keyId) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        return zset.size(keyId);
    }

    /**
     * zset有序集合中添加对象，score为当前时间（score可以相同）
     *
     * @param key
     * @param obj 保存对象
     */
    public <T> void addZset(final String key, final T obj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        zset.add(key, obj, System.currentTimeMillis());
    }

    /**
     * set中添加对象
     *
     * @param key
     * @param value
     */
    public <T> void addSet(final String key, final T[] value) {
        final SetOperations<String, T> set = (SetOperations<String, T>) this.redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 取差集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public <T> Set<T> diffSet(final String key, final String otherKey) {
        final SetOperations<String, T> set = (SetOperations<String, T>) this.redisTemplate.opsForSet();
        return set.difference(key, otherKey);
    }

    /**
     * zset集合中添加对象，
     *
     * @param key
     * @param obj   保存对象
     * @param score 分数
     */
    public <T> void addZsetWithScore(final String key, final T obj, final double score) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        this.delZSetWithScore(key, score);
        zset.add(key, obj, score);
    }

    /**
     * zset集合中添加对象，score为当前时间（score可以相同）
     *
     * @param keyId
     * @param list  保存对象
     */
    public <T> void addListZSet(final String keyId, final List<T> list) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        for (final T obj : list) {
            zset.add(keyId, obj, System.currentTimeMillis());
        }
    }

    /**
     * 修改zset集合里某对象,建议使用 modifyZSetWithScore()
     *
     * @param keyId
     * @param oldObj
     * @param newObj
     */
    public <T> void modifyZSet(final String keyId, final T oldObj, final T newObj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        if (oldObj != null) {
            final Long index = zset.rank(keyId, oldObj);
            if (index != null) {
                zset.remove(keyId, oldObj);
            }
        }
        zset.add(keyId, newObj, System.currentTimeMillis());
    }

    /**
     * 修改zset集合里某对象
     *
     * @param keyId
     * @param score  修改前的对象的分数
     * @param newObj 修改后的对象
     */
    public <T> void modifyZSetWithScore(final String keyId, final double score, final T newObj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        final long count = zset.removeRangeByScore(keyId, score, score);
        if (count > 0) {
            zset.add(keyId, newObj, score);
        }
    }

    /**
     * 删除zset集合里某对象
     *
     * @param keyId
     * @param obj
     */
    public <T> void delZSet(final String keyId, final T obj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        if (obj != null) {
            final Long index = zset.rank(keyId, obj);
            if (index != null) {
                zset.remove(keyId, obj);
            }
        }
    }

    /**
     * 删除zset集合里某对象
     *
     * @param keyId
     * @param score 对象的分数
     * @return
     */
    public <T> Long delZSetWithScore(final String keyId, final double score) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        return zset.removeRangeByScore(keyId, score, score);
    }

    /**
     * @param keyId
     * @param score
     * @return
     */
    public <T> T queryZSetWithScore(final String keyId, final double score) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        final Set<T> set = zset.rangeByScore(keyId, score, score);
        if (!set.isEmpty()) {
            return set.iterator().next();
        }
        return null;
    }

    /**
     * 查询索引： zset键为K的集合，value为obj的元素索引，正序
     *
     * @param keyId
     * @param obj
     * @return
     */
    public <T> Long findZSetIndex(final String keyId, final T obj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        return zset.rank(keyId, obj);
    }

    /**
     * 删除，zset键为K的集合，value为obj的元素
     *
     * @param keyId
     * @param obj
     * @return
     */
    public <T> void remove(final String keyId, final T obj) {
        final ZSetOperations<String, T> zset = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
        final Long index = zset.rank(keyId, obj);
        if (index != null) {
            zset.remove(keyId, obj);
        }
    }

    /**
     * 删除redis的某个key
     *
     * @param keyId
     */
    public Boolean delete(final String keyId) {
        return this.redisTemplate.delete(keyId);
    }

    /**
     * @param key
     * @return
     */
    public <T> boolean exist(final String key) {
        return this.redisTemplate.hasKey(key);
    }

}
