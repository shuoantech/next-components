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

package com.qiwumind.next.components.redis.core.retry;



import com.github.rholder.retry.BlockStrategy;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自旋锁的实现, 不响应线程中断
 */
@Slf4j
@NoArgsConstructor
public class SpinBlockStrategy implements BlockStrategy {

    @Override
    public void block(final long sleepTime) throws InterruptedException {
        final long start = System.currentTimeMillis();
        long end = start;
        log.info("[SpinBlockStrategy]...begin wait.");
        while (end - start <= sleepTime) {
            end = System.currentTimeMillis();
        }
        log.info("[SpinBlockStrategy]...end wait.duration={} ms", end - start);
    }
}
