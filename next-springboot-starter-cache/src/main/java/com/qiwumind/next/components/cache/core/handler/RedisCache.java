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

package com.qiwumind.next.components.cache.core.handler;


import com.qiwumind.next.components.cache.core.Cache;
import com.qiwumind.next.components.cache.core.annotations.CacheQuery;
import com.qiwumind.next.components.cache.core.annotations.InCache;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

import java.time.Duration;
import java.util.Random;


/**
 * 类描述：
 *
 * @author KS.Li 2021年10月11日 下午2:11:56
 */
@Slf4j
@CacheQuery
@InCache
public class RedisCache implements Cache, Ordered {

    private JedisCache jedisCache;
    private int expireTime;

    public RedisCache(JedisCache jedisCache, int expireTime) {
        this.jedisCache = jedisCache;
        this.expireTime = expireTime;
    }

    @Override
    public int getOrder() {
        return 2;
    }


    @Override
    public String queryCache(String key) {
        key = buildCacheKey("redis", key);
        String keyValue = jedisCache.getValueByKey(key);
        if (StringUtils.isNotBlank(keyValue)) {
            log.info("***use redis value={} ***", keyValue);
            return keyValue;
        }
        return null;
    }

    @Override
    public Boolean queryCacheExist(String key) {
        key = buildCacheKey("redis", key);
        return jedisCache.exists(key);
    }

    @Override
    public Boolean cache(String key, String value) {
        key = buildCacheKey("redis", key);
        jedisCache.setValueByKey(key, value, expireTime);
        log.info("*** add redis key:{} vlaue={}***", key, value);
        return true;
    }

    @Override
    public Boolean del(String key) {
        key = buildCacheKey("redis", key);
        jedisCache.del(key);
        return true;
    }

    private String buildCacheKey(String prefix, String key) {
        return String.format("%s:%s", prefix, key);
    }

    /**
     * 生成随机TTL，防止缓存雪崩
     */
    private Duration generateRandomTTL(int minMinutes, int maxMinutes) {
        Random random = new Random();
        int randomMinutes = minMinutes + random.nextInt(maxMinutes - minMinutes + 1);
        return Duration.ofMinutes(randomMinutes);
    }
}
