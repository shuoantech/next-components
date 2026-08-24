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

package com.qiwumind.next.components.pricing.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;

/**
 * 定价引擎的配置属性。
 * <p>
 * 所有属性均以 {@code next.pricing} 为前缀。
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * next:
 *   pricing:
 *     enabled: true
 *     aviator-cache-expressions: true
 * }</pre>
 */
@Setter
@Getter
@ConfigurationProperties(prefix = SystemConstants.Prefix.PRICING)
public class PricingProperties {

    /** 配置属性前缀 */
    public static final String PREFIX = SystemConstants.Prefix.PRICING;

    /** 是否启用定价引擎自动配置。默认：true */
    private boolean enabled = true;

    /** 是否缓存编译后的 Aviator 表达式以提升性能。默认：true */
    private boolean aviatorCacheExpressions = true;
}
