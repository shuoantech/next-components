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

package com.qiwumind.next.components.signature.core.annotation;

import com.qiwumind.next.components.common.exception.enums.GlobalErrorCodeConstants;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * HTTP API 签名注解
 * @author qiwumind Zhougang
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignature {

    /**
     * 同一个请求多长时间内有效 默认 60 秒
     */
    int timeout() default 60;

    /**
     * 时间单位，默认为 SECONDS 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // ========================== 签名参数 ==========================

    /**
     * 提示信息，签名失败的提示
     * @see GlobalErrorCodeConstants#BAD_REQUEST
     */
    String message() default "签名不正确"; // 为空时，使用 BAD_REQUEST 错误提示

    /**
     * 签名字段：appId 应用ID
     */
    String appId() default "appId";

    /**
     * 签名字段：timestamp 时间戳
     */
    String timestamp() default "timestamp";

    /**
     * 签名字段：nonce 随机数，10 位以上
     */
    String nonce() default "nonce";

    /**
     * sign 客户端签名
     */
    String sign() default "sign";

}
