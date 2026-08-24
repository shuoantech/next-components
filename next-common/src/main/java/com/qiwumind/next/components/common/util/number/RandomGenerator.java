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

package com.qiwumind.next.components.common.util.number;



import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {
    /**
     * 生成[0,100]之间均匀分布的随机小数
     * 精度：双精度浮点数
     * totalWeight = 100
     */
    public static double randomInclusive( Double totalWeight) {
        // ThreadLocalRandom提供了更好的性能和线程安全性
        return ThreadLocalRandom.current().nextDouble(0, totalWeight + Double.MIN_VALUE);
        // 使用 100 + Double.MIN_VALUE 确保能生成100
    }
    
    /**
     * 生成[0,100]之间均匀分布的随机小数（指定精度）
     * @param decimalPlaces 小数点后位数
     */
    public static double randomInclusive(int decimalPlaces) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long max = (long) Math.pow(10, decimalPlaces);
        long value = random.nextLong((100L * max) + 1); // +1确保包含100
        return value / (double) max;
    }
}
