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

package com.qiwumind.next.components.hologres.core.api;

import javax.sql.DataSource;

import com.qiwumind.next.components.hologres.autoconfigure.DataSourceConfiguration;
import com.qiwumind.next.components.hologres.core.datasource.ConnectionPool;
import com.qiwumind.next.components.hologres.core.infra.config.DataSourceConfig;
import com.qiwumind.next.components.hologres.core.infra.config.HoloClientConfig;
import com.qiwumind.next.components.hologres.core.infra.util.EnvUtil;
import com.qiwumind.next.components.hologres.core.infra.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Hologres 数据源工厂。
 * <p>
 * 提供多种方式创建 Hologres 数据源：
 * <ul>
 *   <li>通过环境变量 + 用户名密码</li>
 *   <li>通过 DataSourceConfiguration 配置</li>
 * </ul>
 *
 * @author KS.Li
 */
@Slf4j
public class HoloDataSourceFactory {

    private HoloDataSourceFactory() {}


    /**
     * 根据 DataSourceConfiguration 创建数据源。
     */
    public static DataSource createDataSource(DataSourceConfiguration dataSourceConfiguration) {
        HoloClientConfig holoClientConfig = new HoloClientConfig(EnvUtil.getEnv());
        holoClientConfig.init(dataSourceConfiguration);
        DataSourceConfig config = holoClientConfig.getConfig();
        log.info("Hologres 数据源配置 = {}", JsonUtil.object2Json(config));
        return ConnectionPool.create(config).getDataSource();
    }
}
