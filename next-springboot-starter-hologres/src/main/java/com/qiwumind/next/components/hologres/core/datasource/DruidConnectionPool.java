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

import com.alibaba.druid.pool.DruidDataSource;
import com.qiwumind.next.components.hologres.core.infra.config.DataSourceConfig;

/**
 * Druid 连接池实现。
 */
public class DruidConnectionPool extends ConnectionPool {

    public DruidConnectionPool(DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);
    }

    @Override
    protected DataSource createConnectionPool() {
        DataSourceConfig config = getDataSourceConfig();
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(config.getUrl());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setMaxWait(config.getConnectionTimeoutMilliseconds());
        ds.setMinEvictableIdleTimeMillis(config.getIdleTimeoutMilliseconds());
        ds.setMaxEvictableIdleTimeMillis(config.getMaxLifetimeMilliseconds());
        ds.setMaxActive(config.getMaxPoolSize());
        ds.setMinIdle(config.getMinPoolSize());
        ds.setKeepAlive(config.isKeepAlive());
        ds.setValidationQuery(config.getValidationQuery());
        return ds;
    }

    @Override
    public void close() {
        ((DruidDataSource) getDataSource()).close();
    }

    @Override
    public boolean setMinIdleSize(Integer size) {
        ((DruidDataSource) getDataSource()).setMinIdle(size);
        return true;
    }

    @Override
    public boolean setMaxPoolSize(Integer size) {
        ((DruidDataSource) getDataSource()).setMaxActive(size);
        return true;
    }

    @Override
    public boolean setConnectionTimeOut(Long time) {
        ((DruidDataSource) getDataSource()).setMaxWait(time);
        return true;
    }

    @Override
    public boolean setMaxLifeTime(Long time) {
        ((DruidDataSource) getDataSource()).setMaxEvictableIdleTimeMillis(time);
        return true;
    }

    @Override
    public boolean setIdleTimeOut(Long time) {
        ((DruidDataSource) getDataSource()).setMinEvictableIdleTimeMillis(time);
        return true;
    }
}
