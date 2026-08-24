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



import javax.sql.DataSource;

import com.qiwumind.next.components.starrocks.core.infra.config.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnectionPool extends ConnectionPool {
    public HikariConnectionPool(DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);
    }

    @Override
    public DataSource createConnectionPool() {
        DataSourceConfig config = this.getDataSourceConfig();
        HikariConfig hikariconfig = new HikariConfig();
//        hikariconfig.setDriverClassName("org.postgresql.Driver");
        hikariconfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariconfig.setJdbcUrl(config.getUrl());
        hikariconfig.setUsername(config.getUsername());
        hikariconfig.setPassword(config.getPassword());
        hikariconfig.setConnectionTimeout(config.getConnectionTimeoutMilliseconds());
        hikariconfig.setIdleTimeout(config.getIdleTimeoutMilliseconds());
        hikariconfig.setMaxLifetime(config.getMaxLifetimeMilliseconds());
        hikariconfig.setMaximumPoolSize(config.getMaxPoolSize());
        hikariconfig.setMinimumIdle(config.getMinPoolSize());

        // 优化 StarRocks 连接参数
        hikariconfig.addDataSourceProperty("useUnicode", "true");
        hikariconfig.addDataSourceProperty("characterEncoding", "UTF-8");
        hikariconfig.addDataSourceProperty("useSSL", "false");
        hikariconfig.addDataSourceProperty("serverTimezone", "Asia/Shanghai");
        hikariconfig.addDataSourceProperty("connectTimeout", "5000");
        hikariconfig.addDataSourceProperty("socketTimeout", "300000");


        return new HikariDataSource(hikariconfig);
    }




    @Override
    public void close() {
        ((HikariDataSource) this.getDataSource()).close();
    }

    @Override
    public boolean setMinIdleSize(Integer size) {
        ((HikariDataSource) this.getDataSource()).setMinimumIdle(size);
        return true;
    }

    @Override
    public boolean setMaxPoolSize(Integer size) {
        ((HikariDataSource) this.getDataSource()).setMaximumPoolSize(size);
        return true;
    }

    @Override
    public boolean setConnectionTimeOut(Integer time) {
        ((HikariDataSource) this.getDataSource()).setConnectionTimeout(time);
        return true;
    }

    @Override
    public boolean setMaxLifeTime(Integer time) {
        ((HikariDataSource) this.getDataSource()).setMaxLifetime(time);
        return true;
    }

    @Override
    public boolean setIdleTimeOut(Integer time) {
        ((HikariDataSource) this.getDataSource()).setIdleTimeout(time);
        return true;
    }
}
