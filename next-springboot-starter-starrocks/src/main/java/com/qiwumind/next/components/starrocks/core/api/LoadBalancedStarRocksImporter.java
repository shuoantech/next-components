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



import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import com.qiwumind.next.components.starrocks.autoconfigure.StreamLoadConfigProperties;
import com.qiwumind.next.components.starrocks.core.dto.StreamLoadResponse;
import com.qiwumind.next.components.starrocks.core.infra.config.FELoadBalancer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * 导入相关操作 - 通过FE的8030端口
 * 支持负载均衡的StarRocks导入器
 */
public abstract class LoadBalancedStarRocksImporter<T> {
    protected static final Logger logger = LoggerFactory.getLogger(LoadBalancedStarRocksImporter.class);

    protected final FELoadBalancer loadBalancer;

    protected StarRocksClusterProperties starRocksProperties;
    protected StreamLoadConfigProperties streamLoadConfig;


    protected CloseableHttpClient httpClient;

    public LoadBalancedStarRocksImporter(FELoadBalancer loadBalancer, StarRocksClusterProperties starRocksProperties,
                                         StreamLoadConfigProperties streamLoadConfig) {
        this.loadBalancer = loadBalancer;
        this.starRocksProperties = starRocksProperties;
        this.streamLoadConfig = streamLoadConfig;

        initHttpClient();
    }

    private void initHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(60000)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(20)
                .setMaxConnPerRoute(10)
                .build();
    }


    /**
     * 负载均衡导入CSV文件
     */
    public StreamLoadResponse executeStreamLoadWithLB(T csv, String table,
                                                      FELoadBalancer.SelectionStrategy strategy) {
        FELoadBalancer.FENode selectedNode = null;
        int retryCount = 0;
        int maxRetries = loadBalancer.getAvailableNodes().size(); // 最大重试次数等于可用节点数
        while (retryCount <= maxRetries) {
            try {
                // 选择FE节点
                selectedNode = loadBalancer.selectOptimalNode(strategy);
                logger.info("选择FE节点: {} (策略: {})", selectedNode.getName(), strategy);
                // 执行导入
                StreamLoadResponse response = executeStreamLoad(selectedNode, csv, table);
                if (response.isSuccess()) {
                    logger.info("导入成功- 节点: {}", selectedNode.getName());
                    return response;
                } else {
                    // 导入失败，可能是节点问题，记录并重试
                    logger.warn("导入失败- 节点: {}, 错误: {}",
                            selectedNode.getName(), response.getMessage());
                    retryCount++;
                }

            } catch (Exception e) {
                logger.error("导入异常- 节点: {}, 错误: {}",
                        selectedNode != null ? selectedNode.getName() : "unknown", e.getMessage());
                retryCount++;
            }
        }
        return StreamLoadResponse.failure("所有FE节点导入失败，重试次数: " + retryCount);
    }

    protected abstract StreamLoadResponse executeStreamLoad(FELoadBalancer.FENode node, T csv, String table) throws IOException;


    protected String basicAuthHeader(String username, String password) {
        final String tobeEncode = username + ":" + password;
        byte[] encoded = Base64.encodeBase64(tobeEncode.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }


    protected void setLoadHeaders(HttpPut httpPut) {
        httpPut.setHeader("format", "csv");
        httpPut.setHeader("max_filter_ratio", String.valueOf(streamLoadConfig.getMaxFilterRatio()));
        httpPut.setHeader("column_separator", streamLoadConfig.getColumnSeparator());

        httpPut.setHeader(HttpHeaders.EXPECT, "100-continue");
        httpPut.setHeader(HttpHeaders.AUTHORIZATION,
                basicAuthHeader(starRocksProperties.getUsername(), starRocksProperties.getPassword()));
        // the label header is optional, not necessary
        // use label header can ensure at most once semantics
        httpPut.setHeader("label", generateLabel("java"));

        // 指定列名
        if (StringUtils.isNotBlank(streamLoadConfig.getColumns())) {
            httpPut.setHeader("columns", streamLoadConfig.getColumns());
        }else {
            logger.warn(" httpPut head columns is empty ");
        }
        if (!httpPut.containsHeader("timeout")) {
            httpPut.setHeader("timeout", "60");
        }


    }

    /**
     * 生成唯一标签
     */
    public String generateLabel(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取负载均衡器状态
     */
    public void printLoadBalancerStatus() {
        System.out.println("=== FE负载均衡器状态 ===");
        System.out.println("可用节点: " + loadBalancer.getAvailableNodes().size() +
                "/" + loadBalancer.getAllNodeStatus().size());
        Map<String, FELoadBalancer.FENodeStatus> statusMap = loadBalancer.getAllNodeStatus();
        for (Map.Entry<String, FELoadBalancer.FENodeStatus> entry : statusMap.entrySet()) {
            FELoadBalancer.FENodeStatus status = entry.getValue();
            System.out.printf("节点 %s: 健康=%s, 响应时间=%dms, 成功率=%.1f%%%n",
                    entry.getKey(), status.isHealthy(), status.getResponseTime(),
                    status.getSuccessRate());
        }
    }

    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("关闭HTTP客户端失败", e);
            }
        }
    }


}
