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



import com.google.common.hash.Funnel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 基于Guava的内存布隆过滤器实现
 * 使用Guava库提供的BloomFilter实现
 */
public class GuavaBloomFilter<T> implements BloomFilter<T> {

    private final long expectedInsertions;
    private final double falsePositiveProbability;
    private com.google.common.hash.BloomFilter<T> guavaBloomFilter;

    /**
     * 构造函数
     *
     * @param name                     布隆过滤器名称
     * @param expectedInsertions       预期插入元素数量
     * @param falsePositiveProbability 期望误判率
     */
    public GuavaBloomFilter(String name, long expectedInsertions, double falsePositiveProbability) {
        this.expectedInsertions = expectedInsertions;
        this.falsePositiveProbability = falsePositiveProbability;

        // 使用自定义的Funnel实现替代Funnels.universalFunnel()
        Funnel<T> funnel = createUniversalFunnel();

        // 使用Guava的BloomFilter.create方法创建布隆过滤器
        this.guavaBloomFilter = com.google.common.hash.BloomFilter.create(
                funnel,
                expectedInsertions,
                falsePositiveProbability
        );
    }

    /**
     * 创建通用的Funnel实现
     *
     * @return 通用Funnel实例
     */
    private Funnel<T> createUniversalFunnel() {

        return (from, into) -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(from);
                oos.flush();
                byte[] bytes = baos.toByteArray();
                into.putBytes(bytes);
                oos.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to serialize object", e);
            }
        };
    }

    @Override
    public boolean put(T element) {
        return guavaBloomFilter.put(element);
    }

    @Override
    public boolean mightContain(T element) {
        return guavaBloomFilter.mightContain(element);
    }

    @Override
    public void clear() {
        // Guava的BloomFilter没有直接的clear方法，需要重新创建
        // 这里我们通过替换实例来实现clear功能
        Funnel<T> funnel = createUniversalFunnel();
        guavaBloomFilter = com.google.common.hash.BloomFilter.create(
                funnel,
                expectedInsertions,
                falsePositiveProbability
        );
    }
}
