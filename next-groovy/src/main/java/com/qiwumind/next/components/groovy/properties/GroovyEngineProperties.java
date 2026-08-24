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

package com.qiwumind.next.components.groovy.properties;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;

/**
 * 配置文件
 */
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = SystemConstants.Prefix.Groovy.ENGINE)
public class GroovyEngineProperties {

    public static final String PREFIX = SystemConstants.Prefix.Groovy.ENGINE;

    /**
     * 轮询检查脚本变更时间周期，单位：秒
     */
    private Long pollingCycle = 300L;

    /**
     * 初次轮询检查脚本变更延时时间L
     */
    private Long initialDelay = 0L;

    /**
     * 是否开启groovy脚本引擎功能，默认不开启
     */
    private boolean enable = false;

    /**
     * 本地缓存失效时间(单位：分钟)，默认600分钟
     */
    private Long cacheExpireAfterWrite = 600L;

    /**
     * 本地缓存初始容量，默认100
     */
    private Integer cacheInitialCapacity = 100;

    /**
     * 本地缓存最大容量，默认500
     */
    private Long cacheMaximumSize = 500L;
}
