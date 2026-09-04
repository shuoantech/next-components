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
import com.google.common.hash.PrimitiveSink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 基于Guava的内存布隆过滤器实现
 * 使用Guava库提供的BloomFilter实现（底层为 128-bit MurmurHash3 + double hashing，位分布均匀）
 * <p>
 * 优化要点：
 * <ul>
 *   <li>{@link Funnel} 只创建一次并在 {@link #clear()} 重建过滤器时复用（原来每次 clear 都重新 new）</li>
 *   <li>String / Number / Boolean 元素走 UTF-8 字节快速路径，避免每次 put/mightContain 触发
 *       Java 序列化（ObjectOutputStream 创建与写出开销大）；其他对象回落到 Java 序列化</li>
 *   <li>过滤器实例使用 volatile，保证 clear 替换实例后其他线程立即可见</li>
 * </ul>
 */
public class GuavaBloomFilter<T> implements BloomFilter<T> {

    private final long expectedInsertions;
    private final double falsePositiveProbability;
    /** 元素转字节的 Funnel（只创建一次，复用） */
    private final Funnel<T> funnel;
    private volatile com.google.common.hash.BloomFilter<T> guavaBloomFilter;

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
        this.funnel = createFunnel();
        this.guavaBloomFilter = com.google.common.hash.BloomFilter.create(
                funnel,
                expectedInsertions,
                falsePositiveProbability
        );
    }

    /**
     * 创建通用 Funnel：常见标量类型走 UTF-8 字节快速路径，其余对象走 Java 序列化
     *
     * @return 通用Funnel实例
     */
    @SuppressWarnings("unchecked")
    private <E> Funnel<E> createFunnel() {
        return (Funnel<E>) new FastFunnel();
    }

    /**
     * 通用 Funnel 实现：String/Number/Boolean 直接写入 UTF-8 字节，其他可序列化对象走 Java 序列化
     */
    private static final class FastFunnel implements Funnel<Object> {
        private static final long serialVersionUID = 1L;

        @Override
        public void funnel(Object from, PrimitiveSink into) {
            if (from instanceof String s) {
                into.putBytes(s.getBytes(StandardCharsets.UTF_8));
            } else if (from instanceof Number || from instanceof Boolean) {
                into.putBytes(from.toString().getBytes(StandardCharsets.UTF_8));
            } else {
                into.putBytes(serialize(from));
            }
        }

        private static byte[] serialize(Object value) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(value);
                oos.flush();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new IllegalArgumentException("Element is not serializable: " + value.getClass(), e);
            }
        }
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
        // Guava的BloomFilter没有直接的clear方法，通过替换实例实现clear（funnel 复用）
        guavaBloomFilter = com.google.common.hash.BloomFilter.create(
                funnel,
                expectedInsertions,
                falsePositiveProbability
        );
    }
}
