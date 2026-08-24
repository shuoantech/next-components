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



import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

public class RedisTypeSafeClient {
    private Jedis jedis;

    public RedisTypeSafeClient(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 自动处理类型冲突的 List 操作
     */
    public Long typeSafeListPush(String key, String... values) {
        try {
            return jedis.lpush(key, values);
        } catch (JedisDataException e) {
            if (e.getMessage().contains("WRONGTYPE")) {
                System.out.println("自动转换键类型: " + key);
                jedis.del(key);
                return jedis.lpush(key, values);
            }
            throw e;
        }
    }

    /**
     * 自动处理类型冲突的 Hash 操作
     */
    public Long typeSafeHashSet(String key, String field, String value) {
        try {
            return jedis.hset(key, field, value);
        } catch (JedisDataException e) {
            if (e.getMessage().contains("WRONGTYPE")) {
                System.out.println("自动转换键类型: " + key);
                jedis.del(key);
                return jedis.hset(key, field, value);
            }
            throw e;
        }
    }

    /**
     * 获取数据（自动识别类型）
     */
    public Object typeSafeGet(String key) {
        String type = jedis.type(key);

        switch (type) {
            case "string":
                return jedis.get(key);
            case "list":
                return jedis.lrange(key, 0, -1);
            case "hash":
                return jedis.hgetAll(key);
            case "set":
                return jedis.smembers(key);
            case "zset":
                return jedis.zrangeWithScores(key, 0, -1);
            default:
                return null;
        }
    }
}
