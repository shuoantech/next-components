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



/**
 * StarRocks连接配置
 */
public class StarRocksConfig {
    // 查询配置
    private final String queryHost;
    private final int queryPort;  // MySQL协议端口，默认9030
    
    // 导入配置
    private final String streamLoadHost;
    private final int streamLoadPort;  // HTTP协议端口，默认8030
    
    // 通用配置
    private final String database;
    private final String username;
    private final String password;
    private final int connectTimeout;
    private final int socketTimeout;
    
    private StarRocksConfig(Builder builder) {
        this.queryHost = builder.queryHost;
        this.queryPort = builder.queryPort;
        this.streamLoadHost = builder.streamLoadHost;
        this.streamLoadPort = builder.streamLoadPort;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.connectTimeout = builder.connectTimeout;
        this.socketTimeout = builder.socketTimeout;
    }
    
    public static class Builder {
        private String queryHost;
        private int queryPort = 9030;
        private String streamLoadHost;
        private int streamLoadPort = 8030;
        private String database;
        private String username;
        private String password;
        private int connectTimeout = 30000;
        private int socketTimeout = 60000;
        
        public Builder queryHost(String queryHost) {
            this.queryHost = queryHost;
            return this;
        }
        
        public Builder queryPort(int queryPort) {
            this.queryPort = queryPort;
            return this;
        }
        
        public Builder streamLoadHost(String streamLoadHost) {
            this.streamLoadHost = streamLoadHost;
            return this;
        }
        
        public Builder streamLoadPort(int streamLoadPort) {
            this.streamLoadPort = streamLoadPort;
            return this;
        }
        
        public Builder database(String database) {
            this.database = database;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        public Builder socketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }
        
        public StarRocksConfig build() {
            // 如果没有单独设置导入主机，使用查询主机
            if (streamLoadHost == null) {
                this.streamLoadHost = queryHost;
            }
            return new StarRocksConfig(this);
        }
    }
    
    // Getters
    public String getQueryHost() { return queryHost; }
    public int getQueryPort() { return queryPort; }
    public String getStreamLoadHost() { return streamLoadHost; }
    public int getStreamLoadPort() { return streamLoadPort; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getConnectTimeout() { return connectTimeout; }
    public int getSocketTimeout() { return socketTimeout; }
    
    public String getQueryJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", queryHost, queryPort, database);
    }
    
    public String getStreamLoadUrl(String table) {
        return String.format("http://%s:%d/api/%s/%s/_stream_load", 
                streamLoadHost, streamLoadPort, database, table);
    }
    
    @Override
    public String toString() {
        return String.format("StarRocksConfig{query=%s:%d, streamLoad=%s:%d, database=%s}", 
                queryHost, queryPort, streamLoadHost, streamLoadPort, database);
    }
}
