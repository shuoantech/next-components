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

package com.qiwumind.next.components.redis.core.lock;



import lombok.RequiredArgsConstructor;

/**
 * @ClassName RedisDebounceService
 * @Date 2024/9/18 10:05
 * @Version 1.0
 */
@RequiredArgsConstructor
public class RedisDebounceService {
    private final LockService lockService;

    private static final String DEBOUNCE_PREFIX = "debounce:";

    // 尝试获取分布式锁
    public boolean acquireLock(String key, long debounceInterval) {
        Boolean result =  lockService.tryLock(DEBOUNCE_PREFIX + key, "true",debounceInterval);
        return Boolean.TRUE.equals(result);
    }

    // 释放分布式锁
    public void releaseLock(String key) {
        lockService.releaseLock(key,"true");
    }

}
