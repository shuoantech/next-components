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

package com.qiwumind.next.components.hologres.core.datasource;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.qiwumind.next.components.hologres.core.infra.config.DataSourceConfig;

/**
 * HikariCP 连接池实现。
 */
public class HikariConnectionPool extends ConnectionPool {

    public HikariConnectionPool(DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);
    }

    @Override
    protected DataSource createConnectionPool() {
        DataSourceConfig config = getDataSourceConfig();
        HikariConfig hc = new HikariConfig();
        hc.setDriverClassName("org.postgresql.Driver");
        hc.setJdbcUrl(config.getUrl());
        hc.setUsername(config.getUsername());
        hc.setPassword(config.getPassword());
        hc.setConnectionTimeout(config.getConnectionTimeoutMilliseconds());
        hc.setIdleTimeout(config.getIdleTimeoutMilliseconds());
        hc.setMaxLifetime(config.getMaxLifetimeMilliseconds());
        hc.setMaximumPoolSize(config.getMaxPoolSize());
        hc.setMinimumIdle(config.getMinPoolSize());
        return new HikariDataSource(hc);
    }

    @Override
    public void close() {
        ((HikariDataSource) getDataSource()).close();
    }

    @Override
    public boolean setMinIdleSize(Integer size) {
        ((HikariDataSource) getDataSource()).setMinimumIdle(size);
        return true;
    }

    @Override
    public boolean setMaxPoolSize(Integer size) {
        ((HikariDataSource) getDataSource()).setMaximumPoolSize(size);
        return true;
    }

    @Override
    public boolean setConnectionTimeOut(Long time) {
        ((HikariDataSource) getDataSource()).setConnectionTimeout(time);
        return true;
    }

    @Override
    public boolean setMaxLifeTime(Long time) {
        ((HikariDataSource) getDataSource()).setMaxLifetime(time);
        return true;
    }

    @Override
    public boolean setIdleTimeOut(Long time) {
        ((HikariDataSource) getDataSource()).setIdleTimeout(time);
        return true;
    }
}
