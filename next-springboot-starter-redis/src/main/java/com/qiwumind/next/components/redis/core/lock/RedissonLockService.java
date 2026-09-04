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

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁实现，底层基于 Redisson（与 protection 包的 @Lock4j 共用同一套 RedissonClient）。
 * 由原 RedisTemplateLockService / JedisLockService 两套自研 SET-NX 实现统一收敛而来，
 * 消除重复，并修复 releaseLock 未校验持有者直接 delete 会误删他人锁的问题。
 */
public class RedissonLockService implements LockService {

    private final RedissonClient redissonClient;

    public RedissonLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁
     * @param value      请求标识（Redisson 内部已用 UUID+threadId 标识持有者，此参数仅保留兼容，不用于解锁校验）
     * @param expireTime 超期时间（毫秒），获取成功后锁在此时间后自动过期
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(final String lockKey, final String value, final long expireTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // waitTime=0：获取不到立即返回；leaseTime=expireTime：成功后自动过期（等同原 SET NX PX）
            return lock.tryLock(0, expireTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放分布式锁（仅当当前线程持有该锁时才解锁，避免误删他人锁）
     *
     * @param lockKey 锁
     * @param value   请求标识
     * @return 是否释放成功
     */
    @Override
    public boolean releaseLock(final String lockKey, final String value) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            return true;
        }
        return false;
    }

}
