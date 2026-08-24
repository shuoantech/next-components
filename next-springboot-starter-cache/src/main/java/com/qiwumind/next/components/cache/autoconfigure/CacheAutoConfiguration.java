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

package com.qiwumind.next.components.cache.autoconfigure;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiwumind.next.components.cache.core.CacheChain;
import com.qiwumind.next.components.cache.core.EnhancedCache;
import com.qiwumind.next.components.cache.core.handler.CaffeineCache;
import com.qiwumind.next.components.cache.core.handler.RedisCache;
import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@EnableConfigurationProperties(value = {CacheConfiguration.class})
@Configuration
@ConditionalOnProperty(prefix = SystemConstants.Prefix.CACHE, name = "cache-open", havingValue = "true")
public class CacheAutoConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);
    private CacheConfiguration cacheConfiguration;

    public CacheAutoConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }


    public CaffeineCache caffeineCache() {
        Cache<String, String> rowcellcache = Caffeine.newBuilder()
                // 设置过期时间
                .expireAfterWrite(cacheConfiguration.getExpireAfterWrite(), TimeUnit.SECONDS)
                // 设置访问过期时间
                .expireAfterAccess(cacheConfiguration.getExpireAfterAccess(), TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(cacheConfiguration.getInitialCapacity())
                // 缓存的最大条数
                .maximumSize(cacheConfiguration.getMaximumSize())
                .build();
        log.info("****** load Caffeine  Cache*********");
        return new CaffeineCache(rowcellcache);
    }

    @Bean(name = "redisCache")
    @ConditionalOnMissingBean(RedisCache.class)
    public RedisCache redisCache(JedisCache jedisCache) {
        return new RedisCache(jedisCache, cacheConfiguration.getRedisExpireTime());
    }

    @Bean(name = "cacheChain")
    @ConditionalOnMissingBean(CacheChain.class)
    public CacheChain cacheChain(RedisCache redisCache) {
        List<com.qiwumind.next.components.cache.core.Cache> chain = new ArrayList<>();
        chain.add(caffeineCache());
        chain.add(redisCache);
        return new CacheChain(chain);
    }


    @Bean(name = "cacheQueryService")
    @ConditionalOnMissingBean(EnhancedCache.class)
    public EnhancedCache enhancedCache(CacheChain cacheChain) {

        return new EnhancedCache(cacheChain);
    }


}
