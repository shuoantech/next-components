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

package com.qiwumind.next.components.starrocks.core.infra.config;



public class DataSourceConfig {
    private String url;
    private String username;
    private String password;
    private String dbname;
    private final DataSourceType pool;
    private Integer minPoolSize = 1;
    private Integer maxPoolSize = 20;

    private String validationQuery = "select 1";
    /**
     * 连接超时毫秒数
     */
    private Integer connectionTimeoutMilliseconds = 60000;
    /**
     * 连接最大存活时间毫秒数
     */
    private Integer maxLifetimeMilliseconds = 900000;
    /**
     * 空闲连接回收超时毫秒数
     */
    private Integer idleTimeoutMilliseconds = 120000;
    private boolean keepAlive = true;

    public boolean needReload(DataSourceConfig newDataSourceConfig) {
        if (newDataSourceConfig == null) {
            return false;
        }
        boolean isSameDataSource = java.util.Objects.equals(this.url, newDataSourceConfig.getUrl())
                && java.util.Objects.equals(this.username, newDataSourceConfig.getUsername())
                && java.util.Objects.equals(this.password, newDataSourceConfig.getPassword());
        return !isSameDataSource;
    }

    public DataSourceConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.pool = DataSourceType.HikariCP;
    }

    public DataSourceConfig(String url, String username, String password, Integer maxPoolSize,
                            Integer idleTimeoutMilliseconds) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.pool = DataSourceType.HikariCP;

        if (maxPoolSize != null && maxPoolSize > 0) {
            this.maxPoolSize = maxPoolSize;
        }

        if (idleTimeoutMilliseconds != null && idleTimeoutMilliseconds > 0) {
            this.idleTimeoutMilliseconds = idleTimeoutMilliseconds;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbname == null) ? 0 : dbname.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((pool == null) ? 0 : pool.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataSourceConfig other = (DataSourceConfig) obj;
        if (dbname == null) {
            if (other.dbname != null) {
                return false;
            }
        } else if (!dbname.equals(other.dbname)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (pool != other.pool) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    public DataSourceConfig(String url, String username, String password, DataSourceType pool,
                            Integer minPoolSize, Integer maxPoolSize, Integer connectionTimeoutMilliseconds,
                            Integer maxLifetimeMilliseconds, Integer idleTimeoutMilliseconds, boolean keepAlive) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.pool = pool;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
        this.maxLifetimeMilliseconds = maxLifetimeMilliseconds;
        this.idleTimeoutMilliseconds = idleTimeoutMilliseconds;
        this.keepAlive = keepAlive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public Integer getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(Integer minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public Integer getConnectionTimeoutMilliseconds() {
        return connectionTimeoutMilliseconds;
    }

    public void setConnectionTimeoutMilliseconds(Integer connectionTimeoutMilliseconds) {
        this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
    }

    public Integer getMaxLifetimeMilliseconds() {
        return maxLifetimeMilliseconds;
    }

    public void setMaxLifetimeMilliseconds(Integer maxLifetimeMilliseconds) {
        this.maxLifetimeMilliseconds = maxLifetimeMilliseconds;
    }

    public Integer getIdleTimeoutMilliseconds() {
        return idleTimeoutMilliseconds;
    }

    public void setIdleTimeoutMilliseconds(Integer idleTimeoutMilliseconds) {
        this.idleTimeoutMilliseconds = idleTimeoutMilliseconds;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public DataSourceType getPool() {
        return pool;
    }

    @Override
    public String toString() {
        return "DataSourceConfig [url=" + url + ", username=" + username + ", password=" + password + ", dbname="
                + dbname + ", pool=" + pool + ", minPoolSize=" + minPoolSize + ", maxPoolSize=" + maxPoolSize
                + ", connectionTimeoutMilliseconds=" + connectionTimeoutMilliseconds + ", maxLifetimeMilliseconds="
                + maxLifetimeMilliseconds + ", idleTimeoutMilliseconds=" + idleTimeoutMilliseconds + ", keepAlive="
                + keepAlive + "]";
    }

    public static enum DataSourceType {
//        DruidCP,
        HikariCP;

        private DataSourceType() {
        }
    }
}
