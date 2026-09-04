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

package com.qiwumind.next.components.hologres.core.infra.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Hologres 数据源配置。
 *
 * @author KS.Li
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataSourceConfig {

    private String url;
    private String username;
    private String password;
    private String dbname;
    private DataSourceType pool;
    private Integer minPoolSize = 1;
    private Integer maxPoolSize = 20;
    private String validationQuery = "select 1";
    /** 连接超时毫秒数 */
    private Long connectionTimeoutMilliseconds = 60000L;
    /** 连接最大存活时间毫秒数 */
    private Long maxLifetimeMilliseconds = 900000L;
    /** 空闲连接回收超时毫秒数 */
    private Long idleTimeoutMilliseconds = 120000L;
    private boolean keepAlive = true;

    /**
     * 判断是否需要重新加载（URL/用户名/密码不同时需要重建连接）。
     */
    public boolean needReload(DataSourceConfig newConfig) {
        return !url.equals(newConfig.url)
                || !username.equals(newConfig.username)
                || !password.equals(newConfig.password);
    }

    @Override
    public String toString() {
        return "DataSourceConfig[url=%s, username=%s, password=******, dbname=%s, pool=%s, min=%d, max=%d, connTimeout=%dms, maxLife=%dms, idleTimeout=%dms, keepAlive=%b]"
                .formatted(url, username, dbname, pool, minPoolSize, maxPoolSize,
                        connectionTimeoutMilliseconds, maxLifetimeMilliseconds, idleTimeoutMilliseconds, keepAlive);
    }

    /**
     * 连接池类型枚举。
     */
    public enum DataSourceType {
        DruidCP,
        HikariCP
    }
}
