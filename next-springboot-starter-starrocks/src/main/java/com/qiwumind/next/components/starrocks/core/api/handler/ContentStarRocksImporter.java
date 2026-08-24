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



// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import com.qiwumind.next.components.starrocks.autoconfigure.StreamLoadConfigProperties;
import com.qiwumind.next.components.starrocks.core.api.LoadBalancedStarRocksImporter;
import com.qiwumind.next.components.starrocks.core.dto.StreamLoadResponse;
import com.qiwumind.next.components.starrocks.core.infra.config.FELoadBalancer;
import com.qiwumind.next.components.starrocks.core.infra.util.CsvReaderUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This class is a java demo for starrocks stream load
 * <p>
 * The pom.xml dependency:
 *
 * <dependency>
 * <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId>
 * <version>4.5.3</version>
 * </dependency>
 * <p>
 * How to use:
 * <p>
 * 1 create a table in starrocks with any mysql client
 * <p>
 * CREATE TABLE `stream_test` (
 * `id` bigint(20) COMMENT "",
 * `id2` bigint(20) COMMENT "",
 * `username` varchar(32) COMMENT ""
 * ) ENGINE=OLAP
 * DUPLICATE KEY(`id`)
 * DISTRIBUTED BY HASH(`id`) BUCKETS 20;
 * <p>
 * <p>
 * 2 change the StarRocks cluster, db, user config in this class
 * <p>
 * 3 run this class, you should see the following output:
 * <p>
 * {
 * "TxnId": 27,
 * "Label": "39c25a5c-7000-496e-a98e-348a264c81de",
 * "Status": "Success",
 * "Message": "OK",
 * "NumberTotalRows": 10,
 * "NumberLoadedRows": 10,
 * "NumberFilteredRows": 0,
 * "NumberUnselectedRows": 0,
 * "LoadBytes": 50,
 * "LoadTimeMs": 151
 * }
 * <p>
 * Attention:
 * <p>
 * 1 wrong dependency version(such as 4.4) of httpclient may cause shaded.org.apache.http.ProtocolException
 * Caused by: shaded.org.apache.http.ProtocolException: Content-Length header already present
 * at shaded.org.apache.http.protocol.RequestContent.process(RequestContent.java:96)
 * at shaded.org.apache.http.protocol.ImmutableHttpProcessor.process(ImmutableHttpProcessor.java:132)
 * at shaded.org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:182)
 * at shaded.org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:88)
 * at shaded.org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
 * at shaded.org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:184)
 * <p>
 * 2 run this class more than once, the status code for http response is still ok, and you will see
 * the following output:
 * <p>
 * {
 * "TxnId": -1,
 * "Label": "39c25a5c-7000-496e-a98e-348a264c81de",
 * "Status": "Label Already Exists",
 * "ExistingJobStatus": "FINISHED",
 * "Message": "Label [39c25a5c-7000-496e-a98e-348a264c81de"] has already been used.",
 * "NumberTotalRows": 0,
 * "NumberLoadedRows": 0,
 * "NumberFilteredRows": 0,
 * "NumberUnselectedRows": 0,
 * "LoadBytes": 0,
 * "LoadTimeMs": 0
 * }
 * 3 when the response statusCode is 200, that doesn't mean your stream load is ok, there may be still
 * some stream problem unless you see the output with 'ok' message
 */
public class ContentStarRocksImporter extends LoadBalancedStarRocksImporter<String> {


    public ContentStarRocksImporter(FELoadBalancer loadBalancer, StarRocksClusterProperties starRocksProperties, StreamLoadConfigProperties streamLoadConfig) {
        super(loadBalancer, starRocksProperties, streamLoadConfig);
    }

    @Override
    protected StreamLoadResponse executeStreamLoad(FELoadBalancer.FENode node, String csv, String table) throws IOException {
        String url = String.format("http://%s:%d/api/%s/%s/_stream_load",
                node.getHost(), 8040, starRocksProperties.getDatabase(), table);
        HttpPut httpPut = new HttpPut(url);
        setLoadHeaders(httpPut);
        try {
            StringEntity entity = new StringEntity(csv, "UTF-8");
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
                logger.info("节点 {} 响应状态: {}, 响应体: {} 响应时间: {}ms", node.getName(), statusCode, loadResult, responseTime);

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

    /**
     * 从网络地址下载csv文件导入到starrocks，适用于数据量不太大情况下
     *
     * @param httpCsvUrl
     * @param table
     * @throws Exception
     */
    public StreamLoadResponse importStarRocks(String httpCsvUrl, String table) throws Exception {
        // 解析文件数据导入
        URL url = new URL(httpCsvUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();

            String tmpresult = CsvReaderUtil.readCsv2Str(inputStream, streamLoadConfig.getColumnSeparator(), streamLoadConfig.getSkipHeader());
            if (StringUtils.isBlank(tmpresult)) {
                logger.info("查无数据");
                return StreamLoadResponse.failure("查无数据 ");
            }
            String loadData = tmpresult;

            StreamLoadResponse response = this.executeStreamLoadWithLB(loadData, table, FELoadBalancer.SelectionStrategy.RANDOM);
            return response;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("关闭 InputStream 失败", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private StringBuilder getStringBuilder(List<String[]> listSize) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] list : listSize) {
            int size = list.length;
            String oneRow = "";
            for (int i = 1; i <= size; i++) {
                if (i != size) {
                    oneRow = oneRow + list[i - 1] + "\t";
                } else {
                    oneRow = oneRow + list[i - 1] + "\n";
                }
            }
            stringBuilder.append(oneRow);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder;
    }

}
