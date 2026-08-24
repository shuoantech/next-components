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

import com.qiwumind.next.components.hologres.core.infra.config.DataSourceConfig;

/**
 * Hologres 连接池抽象基类。
 * <p>
 * 支持 HikariCP 和 Druid 两种连接池实现。
 * 通过工厂方法 {@link #create(DataSourceConfig)} 根据配置自动选择。
 *
 * @author KS.Li
 */
public abstract class ConnectionPool implements AutoCloseable {

    private DataSourceConfig dataSourceConfig;
    private DataSource dataSource;

    /**
     * 工厂方法：根据配置创建对应的连接池实现。
     */
    public static ConnectionPool create(DataSourceConfig dataSourceConfig) {
        return switch (dataSourceConfig.getPool()) {
            case HikariCP -> new HikariConnectionPool(dataSourceConfig);
            case DruidCP -> new DruidConnectionPool(dataSourceConfig);
        };
    }

    public ConnectionPool(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSource = createConnectionPool();
    }

    /**
     * 动态更新连接池参数（无需重建 DataSource）。
     * <p>
     * 只有连接 URL、用户名、密码、连接池类型相同时才允许热更新。
     */
    public boolean updateConnectionPool(DataSourceConfig newConfig) {
        if (!dataSourceConfig.getUrl().equals(newConfig.getUrl())
                || !dataSourceConfig.getUsername().equals(newConfig.getUsername())
                || !dataSourceConfig.getPassword().equals(newConfig.getPassword())
                || !dataSourceConfig.getPool().equals(newConfig.getPool())) {
            return false;
        }

        if (!dataSourceConfig.getMinPoolSize().equals(newConfig.getMinPoolSize())) {
            setMinIdleSize(newConfig.getMinPoolSize());
        }
        if (!dataSourceConfig.getMaxPoolSize().equals(newConfig.getMaxPoolSize())) {
            setMaxPoolSize(newConfig.getMaxPoolSize());
        }
        if (!dataSourceConfig.getConnectionTimeoutMilliseconds()
                .equals(newConfig.getConnectionTimeoutMilliseconds())) {
            setConnectionTimeOut(newConfig.getConnectionTimeoutMilliseconds());
        }
        if (!dataSourceConfig.getIdleTimeoutMilliseconds()
                .equals(newConfig.getIdleTimeoutMilliseconds())) {
            setIdleTimeOut(newConfig.getIdleTimeoutMilliseconds());
        }
        if (!dataSourceConfig.getMaxLifetimeMilliseconds()
                .equals(newConfig.getMaxLifetimeMilliseconds())) {
            setMaxLifeTime(newConfig.getMaxLifetimeMilliseconds());
        }

        this.dataSourceConfig = newConfig;
        return true;
    }

    /**
     * 创建连接池的实现（子类实现）。
     */
    protected abstract DataSource createConnectionPool();

    @Override
    public abstract void close();

    public abstract boolean setMinIdleSize(Integer size);

    public abstract boolean setMaxPoolSize(Integer size);

    public abstract boolean setConnectionTimeOut(Long time);

    public abstract boolean setMaxLifeTime(Long time);

    public abstract boolean setIdleTimeOut(Long time);

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
