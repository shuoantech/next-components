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
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Data
@Configuration
@ConfigurationProperties(prefix = SystemConstants.Prefix.StarRocks.CLUSTER)
public class StarRocksClusterProperties {

    /**
     * FE 节点列表 (格式: host:port,host:port,host:port)
     */
//    private String feNodes = "10.32.29.0:9030,fe2:9030,fe3:9030";
//    private String feNodes = "10.32.29.0:9030";

    // 查询配置 // MySQL协议端口，默认9030
    private   String queryNodes;
    // 导入配置 // HTTP协议端口，默认8030
    private   String streamLoadNodes;


    //=============== 通用配置
    /**
     * 数据库名称
     */
    private String database = "demo";

    /**
     * 用户名
     */
    private String username = "root";

    /**
     * 密码
     */
    private String password = "Wy@SR888";

    /**
     * 连接池最大连接数
     */
    private int maxPoolSize = 20;
    /**
     * 连接池最小空闲连接
     */
    private int minIdle = 5;
    /**
     * 连接超时时间(ms)
     */
    private int connectionTimeout = 30000;
    /**
     * 空闲超时时间(ms)
     */
    private int idleTimeout = 600000;
    /**
     * 读取超时时间（毫秒）
     */
    private int socketTimeout = 60000;

    private String env;
}
