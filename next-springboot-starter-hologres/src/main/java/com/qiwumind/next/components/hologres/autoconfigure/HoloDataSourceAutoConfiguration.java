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

import javax.sql.DataSource;

import com.qiwumind.next.components.hologres.core.api.CmdClientBean;
import com.qiwumind.next.components.hologres.core.api.HoloDataSourceFactory;
import com.qiwumind.next.components.hologres.core.api.QueryClientBean;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Hologres 数据源自动配置。
 * <p>
 * 通过 {@code next.hologres.enabled=true}（默认启用）激活。
 * 禁用方式：{@code next.hologres.enabled=false}
 * <p>
 * 自动注册以下 Bean：
 * <ul>
 *   <li>{@code holoDataSource} - Hologres 数据源（HikariCP/Druid）</li>
 *   <li>{@code queryClientBean} - 查询客户端（聚合查询、列表查询、元数据查询）</li>
 *   <li>{@code cmdClientBean} - 命令客户端（批量写入、删除、导出）</li>
 * </ul>
 *
 */
@AutoConfiguration
@EnableConfigurationProperties(DataSourceConfiguration.class)
@ConditionalOnProperty(prefix = SystemConstants.Prefix.HOLOGRES, name = "enabled", havingValue = "true")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Slf4j
public class HoloDataSourceAutoConfiguration {

    private final DataSourceConfiguration dataSourceConfiguration;

    public HoloDataSourceAutoConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean(value = "holoDataSource")
    @ConditionalOnMissingBean(name = "holoDataSource")
    public DataSource holoDataSource() {
        try {
            DataSource holoDataSource = HoloDataSourceFactory.createDataSource(dataSourceConfiguration);
            log.info("Hologres 数据源初始化成功");
            return holoDataSource;
        } catch (Exception e) {
            log.error("Hologres 数据源初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Hologres 数据源初始化失败: " + e.getMessage(), e);
        }
    }

    @Bean(value = "queryClientBean")
    @ConditionalOnMissingBean(name = "queryClientBean")
    public QueryClientBean queryClientBean(@Qualifier("holoDataSource") DataSource holoDataSource) {
        log.info("Hologres 查询客户端初始化成功");
        return new QueryClientBean(holoDataSource);
    }

    @Bean(value = "cmdClientBean")
    @ConditionalOnMissingBean(name = "cmdClientBean")
    public CmdClientBean cmdClientBean(@Qualifier("holoDataSource") DataSource holoDataSource) {
        log.info("Hologres 命令客户端初始化成功");
        return new CmdClientBean(holoDataSource);
    }

}
