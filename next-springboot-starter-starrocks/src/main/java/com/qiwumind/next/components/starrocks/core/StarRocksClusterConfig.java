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

package com.qiwumind.next.components.starrocks.core;



import java.util.List;

/**
 * StarRocks集群配置
 */
public class StarRocksClusterConfig {
    private final List<FENode> feNodes;
    private final List<BENode> beNodes;

    public StarRocksClusterConfig(List<FENode> feNodes, List<BENode> beNodes) {
        this.feNodes = feNodes;
        this.beNodes = beNodes;
    }
    
    /**
     * FE节点配置
     */
    public static class FENode {
        private final String host;
        private final int queryPort;      // 9030 - 查询
        private final int httpPort;       // 8030 - 导入
        private final String role;        // LEADER, FOLLOWER, OBSERVER
        
        public FENode(String host, int queryPort, int httpPort, String role) {
            this.host = host;
            this.queryPort = queryPort;
            this.httpPort = httpPort;
            this.role = role;
        }
        
        public String getQueryUrl() {
            return String.format("jdbc:mysql://%s:%d", host, queryPort);
        }
        
        public String getStreamLoadUrl(String database, String table) {
            return String.format("http://%s:%d/api/%s/%s/_stream_load", 
                    host, httpPort, database, table);
        }
    }
    
    /**
     * BE节点配置
     */
    public static class BENode {
        private final String host;
        private final int bePort;         // 9060 - BE服务端口
        private final int httpPort;       // 8040 - Web界面
        private final String status;      // alive, dead
        
        public BENode(String host, int bePort, int httpPort) {
            this.host = host;
            this.bePort = bePort;
            this.httpPort = httpPort;
            this.status = "alive";
        }
        
        public String getWebUrl() {
            return String.format("http://%s:%d", host, httpPort);
        }
    }
}
