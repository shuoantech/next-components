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



import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.redis.autoconfigure.RedisConfiguration;
import com.qiwumind.next.components.redis.core.bloomfilter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(value = {BloomFilterConfiguration.class, RedisConfiguration.class})
@Configuration
@ConditionalOnProperty(prefix = SystemConstants.Prefix.BLOOM_FILTER, name = "enable", havingValue = "true")
public class BloomFilterAutoConfiguration {


    private final Logger log = LoggerFactory.getLogger(BloomFilterAutoConfiguration.class);
    private BloomFilterConfiguration bloomFilterConfiguration;
    private RedisConfiguration redisConfiguration;

    public BloomFilterAutoConfiguration(BloomFilterConfiguration bloomFilterConfiguration, RedisConfiguration redisConfiguration) {
        this.bloomFilterConfiguration = bloomFilterConfiguration;
        this.redisConfiguration = redisConfiguration;
    }

    @Bean(name = "guavaBloomFilter")
    @ConditionalOnMissingBean(GuavaBloomFilter.class)
    public <T> BloomFilter guavaBloomFilter() {

        BloomFilter<T> guavaBloomFilter = BloomFilterManager.INSTANCE.createGuavaBloomFilter("gbf"
                , bloomFilterConfiguration.getExpectedInsertions()
                , bloomFilterConfiguration.getFalsePositiveProbability());

        return guavaBloomFilter;
    }

    /**
     * 一般建议使用时采取获取单例较好，key是可变的
     * @param redisConfiguration
     * @return
     * @param <T>
     */
    @Bean(name = "redisBloomFilter")
    @ConditionalOnMissingBean(RedisBloomFilter.class)
    public <T> BloomFilter redisBloomFilter(RedisConfiguration redisConfiguration) {

        BloomFilter<T> redisBloomFilter = BloomFilterManager.INSTANCE.createRedisBloomFilter("rbf"
                , bloomFilterConfiguration.getExpectedInsertions()
                , bloomFilterConfiguration.getFalsePositiveProbability()
                , "default", redisConfiguration.getHost());
        log.info(" *** default redisBloomFilter *** ");
        return redisBloomFilter;
    }


}
