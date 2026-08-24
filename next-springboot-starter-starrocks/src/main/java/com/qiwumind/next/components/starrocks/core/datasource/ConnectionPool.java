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

package com.qiwumind.next.components.starrocks.core.datasource;



import com.qiwumind.next.components.starrocks.core.infra.config.DataSourceConfig;

import javax.sql.DataSource;


public abstract class ConnectionPool implements AutoCloseable {

    private DataSourceConfig dataSourceConfig;
    private DataSource       dataSource;

    public static ConnectionPool create(DataSourceConfig dataSourceConfig) {
        switch (dataSourceConfig.getPool()) {
            case HikariCP:
                return new HikariConnectionPool(dataSourceConfig);
//            case DruidCP:
//                return new DruidConnectionPool(dataSourceConfig);
            default:
                throw new RuntimeException(String.format("DataSource Type [%s] is not supported!",
                        dataSourceConfig.getPool()));
        }
    }

    public ConnectionPool(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSource = this.createConnectionPool();
    }

    public boolean updateConnectionPool(DataSourceConfig newDataSourceConfig) {
        if (!this.dataSourceConfig.getUrl().equals(newDataSourceConfig.getUrl())) {
            return false;
        } else if (!this.dataSourceConfig.getUsername().equals(newDataSourceConfig.getUsername())) {
            return false;
        } else if (!this.dataSourceConfig.getPassword().equals(newDataSourceConfig.getPassword())) {
            return false;
        } else if (!this.dataSourceConfig.getPool().equals(newDataSourceConfig.getPool())) {
            return false;
        } else {
            if (!this.dataSourceConfig.getMinPoolSize().equals(newDataSourceConfig.getMinPoolSize())) {
                this.setMinIdleSize(newDataSourceConfig.getMinPoolSize());
            }

            if (!this.dataSourceConfig.getMaxPoolSize().equals(newDataSourceConfig.getMaxPoolSize())) {
                this.setMaxPoolSize(newDataSourceConfig.getMaxPoolSize());
            }

            if (!this.dataSourceConfig.getConnectionTimeoutMilliseconds().equals(
                    newDataSourceConfig.getConnectionTimeoutMilliseconds())) {
                this.setConnectionTimeOut(newDataSourceConfig.getConnectionTimeoutMilliseconds());
            }

            if (!this.dataSourceConfig.getIdleTimeoutMilliseconds().equals(
                    newDataSourceConfig.getIdleTimeoutMilliseconds())) {
                this.setIdleTimeOut(newDataSourceConfig.getIdleTimeoutMilliseconds());
            }

            if (!this.dataSourceConfig.getMaxLifetimeMilliseconds().equals(
                    newDataSourceConfig.getMaxLifetimeMilliseconds())) {
                this.setMaxLifeTime(newDataSourceConfig.getMaxLifetimeMilliseconds());
            }

            this.dataSourceConfig = newDataSourceConfig;
            return true;
        }
    }

    public abstract DataSource createConnectionPool();

    @Override
    public abstract void close();

    public abstract boolean setMinIdleSize(Integer var1);

    public abstract boolean setMaxPoolSize(Integer var1);

    public abstract boolean setConnectionTimeOut(Integer var1);

    public abstract boolean setMaxLifeTime(Integer var1);

    public abstract boolean setIdleTimeOut(Integer var1);

    public DataSourceConfig getDataSourceConfig() {
        return this.dataSourceConfig;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ConnectionPool)) {
            return false;
        } else {
            ConnectionPool other = (ConnectionPool) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$dataSourceConfig = this.getDataSourceConfig();
                Object other$dataSourceConfig = other.getDataSourceConfig();
                if (this$dataSourceConfig == null) {
                    if (other$dataSourceConfig != null) {
                        return false;
                    }
                } else if (!this$dataSourceConfig.equals(other$dataSourceConfig)) {
                    return false;
                }

                Object this$dataSource = this.getDataSource();
                Object other$dataSource = other.getDataSource();
                if (this$dataSource == null) {
                    if (other$dataSource != null) {
                        return false;
                    }
                } else if (!this$dataSource.equals(other$dataSource)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConnectionPool;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $dataSourceConfig = this.getDataSourceConfig();
        result = result * 59 + ($dataSourceConfig == null ? 43 : $dataSourceConfig.hashCode());
        Object $dataSource = this.getDataSource();
        result = result * 59 + ($dataSource == null ? 43 : $dataSource.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConnectionPool(dataSourceConfig=" + this.getDataSourceConfig() + ", dataSource=" + this.getDataSource()
                + ")";
    }
}
