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

package com.qiwumind.next.components.starrocks.core.api.handler;



import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import com.qiwumind.next.components.starrocks.autoconfigure.StreamLoadConfigProperties;
import com.qiwumind.next.components.starrocks.core.api.LoadBalancedStarRocksImporter;
import com.qiwumind.next.components.starrocks.core.dto.StreamLoadResponse;
import com.qiwumind.next.components.starrocks.core.infra.config.FELoadBalancer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 支持负载均衡的StarRocks导入器
 */
public class FileStarRocksImporter extends LoadBalancedStarRocksImporter<File> {

    public FileStarRocksImporter(FELoadBalancer loadBalancer, StarRocksClusterProperties starRocksProperties, StreamLoadConfigProperties streamLoadConfig) {
        super(loadBalancer, starRocksProperties, streamLoadConfig);
    }

    @Override
    protected StreamLoadResponse executeStreamLoad(FELoadBalancer.FENode node, File csv, String table) throws IOException {
        String url = String.format("http://%s:%d/api/%s/%s/_stream_load",
                node.getHost(), node.getHttpPort(), starRocksProperties.getDatabase(), table);
        HttpPut httpPut = new HttpPut(url);
        setLoadHeaders(httpPut);
        try {
            FileEntity entity = new FileEntity(csv, ContentType.create("text/plain", StandardCharsets.UTF_8));
            httpPut.setEntity(entity);
            long startTime = System.currentTimeMillis();
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                String loadResult = "";
                if (response.getEntity() != null) {
                    loadResult = EntityUtils.toString(response.getEntity());
                }
                final int statusCode = response.getStatusLine().getStatusCode();
                // statusCode 200 just indicates that starrocks be service is ok, not stream load
                // you should see the output content to find whether stream load is success
                if (statusCode != 200) {
                    throw new IOException(
                            String.format("Stream load failed, statusCode=%s load result=%s", statusCode, loadResult));
                }
                long responseTime = System.currentTimeMillis() - startTime;
                logger.debug("节点 {} 响应状态: {}, 响应体: {} 响应时间: {}ms", node.getName(), statusCode, loadResult, responseTime);

                StreamLoadResponse result = BaseDTO.fromJson(loadResult, StreamLoadResponse.class);
                logger.info("  导入总行数: {}, 加载行数: {}, 过滤行数: {}, 耗时: {}ms",
                        result.getNumberTotalRows(), result.getNumberLoadedRows(),
                        result.getNumberFilteredRows(), result.getLoadTimeMs());
                return result;
            }
        } finally {
            httpPut.releaseConnection();
        }
    }


}
