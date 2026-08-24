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



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = SystemConstants.Prefix.StarRocks.CONFIG)
public class StreamLoadConfigProperties {

    private double maxFilterRatio = 0.1;
    private String columnSeparator = ",";
    private int skipHeader = 0;
    private String columns;
    private String where;
    private Map<String, String> extraHeaders = new HashMap<>();

    public StreamLoadConfigProperties() {
        // 设置常用参数
        extraHeaders.put("strip_outer_array", "true");
        extraHeaders.put("ignore_json_size", "true");
    }

    // Builder模式
    public static class Builder {
        private StreamLoadConfigProperties config = new StreamLoadConfigProperties();

        public StreamLoadConfigProperties.Builder maxFilterRatio(double ratio) {
            config.maxFilterRatio = ratio;
            return this;
        }

        public StreamLoadConfigProperties.Builder columnSeparator(String separator) {
            config.columnSeparator = separator;
            return this;
        }

        public StreamLoadConfigProperties.Builder columns(String columns) {
            config.columns = columns;
            return this;
        }

        public StreamLoadConfigProperties.Builder skipHeader(int skipHeader) {
            config.skipHeader = skipHeader;
            return this;
        }

        public StreamLoadConfigProperties.Builder where(String where) {
            config.where = where;
            return this;
        }

        public StreamLoadConfigProperties.Builder addHeader(String key, String value) {
            config.extraHeaders.put(key, value);
            return this;
        }

        public StreamLoadConfigProperties build() {
            return config;
        }
    }

    // Getter方法
    public double getMaxFilterRatio() {
        return maxFilterRatio;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public String getColumns() {
        return columns;
    }

    public String getWhere() {
        return where;
    }

    public Map<String, String> getExtraHeaders() {
        return extraHeaders;
    }

    public int getSkipHeader() {
        return skipHeader;
    }

}
