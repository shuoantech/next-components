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

package com.qiwumind.next.components.starrocks.autoconfigure;



import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.starrocks.core.api.StarRocksDataSourceFactory;
import com.qiwumind.next.components.starrocks.core.api.StarRocksQueryBean;
import com.qiwumind.next.components.starrocks.core.api.handler.ContentStarRocksImporter;
import com.qiwumind.next.components.starrocks.core.api.handler.FileStarRocksImporter;
import com.qiwumind.next.components.starrocks.core.infra.config.FELoadBalancer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
@EnableConfigurationProperties(value = {StarRocksClusterProperties.class, StreamLoadConfigProperties.class})
@ConditionalOnProperty(prefix = SystemConstants.GLOBAL, name = "starrocks-open", havingValue = "true")
public class StarRocksDataSourceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(StarRocksDataSourceAutoConfiguration.class);
    private StarRocksClusterProperties clusterProperties;
    private StreamLoadConfigProperties streamLoadConfig;

    public StarRocksDataSourceAutoConfiguration(StarRocksClusterProperties clusterProperties,
                                                StreamLoadConfigProperties streamLoadConfig, FELoadBalancer loadBalancer) {
        this.clusterProperties = clusterProperties;
        this.streamLoadConfig = streamLoadConfig;
    }



    /**
     * 创建支持故障转移的数据源
     */
    @Bean("starRocksDataSource")
    @Primary
    public DataSource starRocksDataSource() {
        DataSource dataSource = StarRocksDataSourceFactory.createDataSource(clusterProperties);
        logger.info("*** load starRocksDataSource end  ***");
        return dataSource;
    }

    @Bean(value = "starRocksQueryBean")
    @ConditionalOnMissingBean(name = "starRocksQueryBean")
    public StarRocksQueryBean starRocksQueryBean() {
        DataSource starRocksDataSource = this.starRocksDataSource();
        StarRocksQueryBean queryClientBean = new StarRocksQueryBean(starRocksDataSource);
        logger.info("*** load starRocksQueryBean end ***");
        return queryClientBean;
    }



    @Bean(value = "queryFeLoadBalancer", initMethod = "init",destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "queryFeLoadBalancer")
    public FELoadBalancer queryFeLoadBalancer() {
        String[] queryNodes = clusterProperties.getQueryNodes().split(",");
        // 1. 配置FE节点列表
        List<FELoadBalancer.FENode> feNodes = this.nodeStarRocks(queryNodes);
        FELoadBalancer loadBalancer = new FELoadBalancer(feNodes);
        logger.info("*** load loadBalancer end ***");
        return loadBalancer;
    }

    @Bean(value = "streamLoadNodesFeLoadBalancer", initMethod = "init",destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "streamLoadNodesFeLoadBalancer")
    public FELoadBalancer streamLoadNodesFeLoadBalancer() {
        String[] queryNodes = clusterProperties.getStreamLoadNodes().split(",");
        // 1. 配置FE节点列表
        List<FELoadBalancer.FENode> feNodes = this.nodeStarRocks(queryNodes);
        FELoadBalancer loadBalancer = new FELoadBalancer(feNodes);
        logger.info("*** load streamLoadNodesFeLoadBalancer end ***");
        return loadBalancer;
    }

    private List<FELoadBalancer.FENode> nodeStarRocks(String[] feNodesstr) {
        List<FELoadBalancer.FENode> feNodes = new ArrayList<>();
        int size = feNodesstr.length;
        for (int i = 1; i <= size; i++) {
            String[] n = feNodesstr[i - 1].split(":");
            if (n.length < 2) {
                logger.warn("FE 节点配置格式错误，跳过: {}", feNodesstr[i - 1]);
                continue;
            }
            feNodes.add(new FELoadBalancer.FENode(n[0], Integer.valueOf(n[1]), "FE-" + i, 1));
        }
        return feNodes;
    }


    /**
     *
     */
    @Bean("contentStarRocksImporter")
    @Primary
    public ContentStarRocksImporter contentStarRocksImporter() {
        ContentStarRocksImporter starRocksStreamLoad = new ContentStarRocksImporter(streamLoadNodesFeLoadBalancer(), clusterProperties, streamLoadConfig);
        logger.info("*** load contentStarRocksImporter end  ***");
        return starRocksStreamLoad;
    }

    /**
     */
    @Bean("fileStarRocksImporter")
    @Primary
    public FileStarRocksImporter fileStarRocksImporter() {
        FileStarRocksImporter starRocksStreamLoad = new FileStarRocksImporter(streamLoadNodesFeLoadBalancer(), clusterProperties, streamLoadConfig);
        logger.info("*** load fileStarRocksImporter end  ***");
        return starRocksStreamLoad;
    }

    public static void main(String[] args) throws SQLException {
        StarRocksClusterProperties clusterProperties = new StarRocksClusterProperties();
        clusterProperties.setQueryNodes("10.96.83.234:9030,10.96.83.235:9030,10.96.83.236:9030");
        clusterProperties.setPassword("SR@1105Y");
        clusterProperties.setDatabase("dwd");
        clusterProperties.setEnv("test");
        DataSource dataSource = StarRocksDataSourceFactory.createDataSource(clusterProperties);
        StarRocksQueryBean queryClientBean = new StarRocksQueryBean(dataSource);
        List<Map<String, Object>> map = queryClientBean.listQuery("SELECT part_date,event,distinct_id,event_time from user_operate_behavior_detail  where part_date='2025-12-03' and H_lib='js' limit 2 ");
        System.out.println(map);


        System.out.println("MDZFB_500".startsWith("ZFBHB"));
    }



}
