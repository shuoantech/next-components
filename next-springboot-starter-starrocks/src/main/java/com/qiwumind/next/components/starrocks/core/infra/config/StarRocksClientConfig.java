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



import com.qiwumind.next.components.starrocks.autoconfigure.StarRocksClusterProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public class StarRocksClientConfig {


    private DataSourceConfig config;

    private Env srEnv;

    public StarRocksClientConfig() {

    }


    public DataSourceConfig getConfig() {
        return config;
    }

    public void setConfig(DataSourceConfig config) {
        this.config = config;
    }


    public void init(StarRocksClusterProperties clusterProperties) {
        String runEnv = clusterProperties.getEnv();
        if (StringUtils.isBlank(runEnv)) {
            throw new RuntimeException("env 不允许空");
        }
        srEnv = Env.getEnv(runEnv);
        String url = buildFailoverJdbcUrl(clusterProperties);
        // 测试及预发环境，限制最大连接数为5个
        if (srEnv == Env.TEST) {
            Integer maxPoolSize = clusterProperties.getMaxPoolSize();
            if (maxPoolSize == null || maxPoolSize > 5) {
                clusterProperties.setMaxPoolSize(5);
            }
        }
        config = new DataSourceConfig(url, clusterProperties.getUsername(),
                clusterProperties.getPassword(), clusterProperties.getMaxPoolSize(),
                clusterProperties.getIdleTimeout());
    }

    /**
     * 构建支持故障转移的 JDBC URL
     * 格式: jdbc:mysql:loadbalance://host1:port,host2:port,host3:port/database
     */
    private String buildFailoverJdbcUrl(StarRocksClusterProperties clusterProperties) {
        String[] feNodes = clusterProperties.getQueryNodes().split(",");
        if (feNodes == null) {
            throw new RuntimeException("Nodes 不允许空");
        }
        if (srEnv == Env.TEST || srEnv == Env.PRE) {//单台
            String url = "jdbc:mysql://%s/%s";
            url = String.format(url, feNodes[0], clusterProperties.getDatabase());
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql:loadbalance://");
        for (int i = 0; i < feNodes.length; i++) {
            if (i > 0) {
                urlBuilder.append(",");
            }
            urlBuilder.append(feNodes[i].trim());
        }
        urlBuilder.append("/").append(clusterProperties.getDatabase());
        urlBuilder.append("?loadBalanceAutoCommitStatementThreshold=5");
        urlBuilder.append("&loadBalanceHostRemovalGracePeriod=15000");
        urlBuilder.append("&loadBalanceBlacklistTimeout=60000");
        urlBuilder.append("&retriesAllDown=10");
        urlBuilder.append("&ha.enableJMX=true");

        return urlBuilder.toString();
    }


    public static enum Env {
        TEST(new String[]{"dev", "test", "sit", "test1", "test2", "test3", "test4", "test5"}),
        PRE(new String[]{"uat", "pre"}),
        PRD(new String[]{"prod", "prd"});

        private String[] env;
//        private String user;
//        private String apikey;

        public String[] getEnv() {
            return env;
        }

        public void setEnv(String[] env) {
            this.env = env;
        }

//        public String getUser() {
//            return user;
//        }
//
//        public void setUser(String user) {
//            this.user = user;
//        }
//
//        public String getApikey() {
//            return apikey;
//        }
//
//        public void setApikey(String apikey) {
//            this.apikey = apikey;
//        }

        private Env(String[] env) {
            this.env = env;
        }

        public static Env getEnv(String env) {
            if (StringUtils.isEmpty(env)) {
                return null;
            }
            return Stream.of(Env.values())
                    .filter(e -> Stream.of(e.getEnv()).anyMatch(predicate -> env.contains(predicate))).findFirst()
                    .orElse(null);
        }
    }
}
