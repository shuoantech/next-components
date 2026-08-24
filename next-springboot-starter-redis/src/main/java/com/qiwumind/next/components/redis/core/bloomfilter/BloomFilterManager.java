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



/**
 * 单独使用时，如果集成sringboot 建议用springboot的single模式即可
 * 布隆过滤器工厂实现类
 * 使用枚举实现单例模式，确保线程安全且懒加载
 */
public enum BloomFilterManager {

    /**
     * 单例实例
     */
    INSTANCE;

    /**
     * 创建基于Guava的内存布隆过滤器
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     * @param <T>                      元素类型
     * @return Guava内存布隆过滤器实例
     */
    public <T> BloomFilter<T> createGuavaBloomFilter(String name, long expectedInsertions, double falsePositiveProbability) {
        return new GuavaBloomFilter<>(name, expectedInsertions, falsePositiveProbability);
    }

    /**
     * 创建基于Redis的分布式布隆过滤器（带自定义Redis连接URI）
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     * @param redisKeyPrefix           Redis键前缀
     * @param redisUri                 Redis连接URI
     * @param <T>                      元素类型
     * @return Redis分布式布隆过滤器实例
     */
    public <T> BloomFilter<T> createRedisBloomFilter(String name, long expectedInsertions, double falsePositiveProbability,
                                                     String redisKeyPrefix, String redisUri) {
        return new RedisBloomFilter<>(name, expectedInsertions, falsePositiveProbability, redisKeyPrefix, redisUri);
    }



}
