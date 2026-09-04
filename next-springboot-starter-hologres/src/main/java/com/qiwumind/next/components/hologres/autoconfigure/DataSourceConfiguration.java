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

package com.qiwumind.next.components.hologres.autoconfigure;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.validation.annotation.Validated;

/**
 * Hologres 数据源配置属性。
 * <p>
 * 前缀：{@code qiwumind.hologres}
 *
 * <pre>{@code
 * next:
 *   hologres:
 *     enabled: true
 *     username: your_username
 *     pwd: your_password
 *     max-pool-size: 20
 *     idle-timeout: 120s
 * }</pre>
 *
 * @author KS.Li
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = SystemConstants.Prefix.HOLOGRES)
public class DataSourceConfiguration {

    /**
     * 连接用户名
     */
    private String username;
    /**
     * 连接密码
     */
    private String pwd;
    private String database;
    /**
     * 连接池最大连接数
     */
    private Integer maxPoolSize;
    /**
     * 空闲连接回收超时（支持 Duration 格式，如 120s、2m）
     */
    private Duration idleTimeout;
    private HoloConfig holoConfig;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor
    public static class HoloConfig {
        private String url;
        private int port = 80;
    }

    /**
     * 获取空闲超时毫秒数，兼容旧配置。
     */
    public Long getIdleTimeoutMilliseconds() {
        return idleTimeout != null ? idleTimeout.toMillis() : null;
    }

}
