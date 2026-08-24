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

package com.qiwumind.next.components.starrocks.core.api;


import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import com.qiwumind.next.components.starrocks.core.datasource.ConnectionPool;
import com.qiwumind.next.components.starrocks.core.infra.config.DataSourceConfig;
import com.qiwumind.next.components.starrocks.core.infra.config.StarRocksClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


public class StarRocksDataSourceFactory {
    static Logger logger = LoggerFactory.getLogger(StarRocksDataSourceFactory.class);

    private static volatile ConnectionPool connectionPool;

    public static DataSource createDataSource(StarRocksClusterProperties clusterProperties) {
        // 1 配置加载
        StarRocksClientConfig holoClientConfig = new StarRocksClientConfig();
        holoClientConfig.init(clusterProperties);
        DataSourceConfig dataSourceConfig = holoClientConfig.getConfig();
        logger.info(" StarRocks  dataSourceConfig ={}", BaseDTO.toJson(dataSourceConfig));
        // 3 连接，保留 ConnectionPool 引用以便关闭
        connectionPool = ConnectionPool.create(dataSourceConfig);
        return connectionPool.getDataSource();
    }

    /**
     * 关闭连接池，释放资源
     */
    public static void close() {
        if (connectionPool != null) {
            connectionPool.close();
            connectionPool = null;
        }
    }
}
