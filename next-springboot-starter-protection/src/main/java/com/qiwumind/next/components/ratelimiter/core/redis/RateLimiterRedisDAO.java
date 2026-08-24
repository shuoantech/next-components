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

package com.qiwumind.next.components.ratelimiter.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流 Redis DAO
 * @author qiwumind
 */
@AllArgsConstructor
public class RateLimiterRedisDAO {

    /**
     * 限流操作
     * KEY 格式：rate_limiter:%s // 参数为 uuid
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String RATE_LIMITER = "rate_limiter:%s";

    private final RedissonClient redissonClient;

    public Boolean tryAcquire(String key, int count, int time, TimeUnit timeUnit) {
        // 1. 获得 RRateLimiter，并设置 rate 速率
        RRateLimiter rateLimiter = getRRateLimiter(key, count, time, timeUnit);
        // 2. 尝试获取 1 个
        return rateLimiter.tryAcquire();
    }

    private static String formatKey(String key) {
        return String.format(RATE_LIMITER, key);
    }

    private RRateLimiter getRRateLimiter(String key, long count, int time, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        long rateInterval = timeUnit.toSeconds(time);
        Duration duration = Duration.ofSeconds(rateInterval);
        // 1. 如果不存在，设置 rate 速率
        RateLimiterConfig config = rateLimiter.getConfig();
        if (config == null) {
            rateLimiter.trySetRate(RateType.OVERALL, count, rateInterval, RateIntervalUnit.SECONDS);
            // 原因参见 https://t.zsxq.com/lcR0W
            rateLimiter.expire(duration);
            return rateLimiter;
        }
        // 2. 如果存在，并且配置相同，则直接返回
        if (config.getRateType() == RateType.OVERALL
                && Objects.equals(config.getRate(), count)
                && Objects.equals(config.getRateInterval(), TimeUnit.SECONDS.toMillis(rateInterval))) {
            return rateLimiter;
        }
        // 3. 如果存在，并且配置不同，则进行新建
        rateLimiter.setRate(RateType.OVERALL, count, rateInterval, RateIntervalUnit.SECONDS);
        // 原因参见 https://t.zsxq.com/lcR0W
        rateLimiter.expire(duration);
        return rateLimiter;
    }

}
