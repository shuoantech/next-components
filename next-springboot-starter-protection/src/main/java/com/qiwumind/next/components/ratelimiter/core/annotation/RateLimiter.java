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

package com.qiwumind.next.components.ratelimiter.core.annotation;

import com.qiwumind.next.components.common.exception.enums.GlobalErrorCodeConstants;
import com.qiwumind.next.components.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.qiwumind.next.components.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import com.qiwumind.next.components.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import com.qiwumind.next.components.ratelimiter.core.keyresolver.impl.DefaultRateLimiterKeyResolver;
import com.qiwumind.next.components.ratelimiter.core.keyresolver.impl.ServerNodeRateLimiterKeyResolver;
import com.qiwumind.next.components.ratelimiter.core.keyresolver.impl.UserRateLimiterKeyResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * @author qiwumind
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 限流的时间，默认为 1 秒
     */
    int time() default 1;
    /**
     * 时间单位，默认为 SECONDS 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 提示信息，请求过快的提示
     * @see GlobalErrorCodeConstants#TOO_MANY_REQUESTS
     */
    String message() default ""; // 为空时，使用 TOO_MANY_REQUESTS 错误提示

    /**
     * 使用的 Key 解析器
     * @see DefaultRateLimiterKeyResolver 全局级别
     * @see UserRateLimiterKeyResolver 用户 ID 级别
     * @see ClientIpRateLimiterKeyResolver 用户 IP 级别
     * @see ServerNodeRateLimiterKeyResolver 服务器 Node 级别
     * @see ExpressionIdempotentKeyResolver 自定义表达式，通过 {@link #keyArg()} 计算
     */
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;
    /**
     * 使用的 Key 参数
     */
    String keyArg() default "";

}
