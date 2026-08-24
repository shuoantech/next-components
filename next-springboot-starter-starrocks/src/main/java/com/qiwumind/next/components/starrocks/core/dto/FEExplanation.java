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

package com.qiwumind.next.components.starrocks.core.dto;



/**
 * FE节点功能说明
 */
public class FEExplanation {
    /**
     * FE主要职责：
     * 1. 元数据管理 - 表结构、分区、副本等信息
     * 2. 查询解析和规划 - SQL解析、生成执行计划
     * 3. 集群管理 - 节点管理、负载均衡
     * 4. 客户端连接 - 提供MySQL协议接口
     * 5. 导入协调 - 协调数据导入流程
     */
    
    // FE服务端口
    public static class FEPorts {
        public static final int QUERY_PORT = 9030;    // MySQL协议，用于查询
        public static final int HTTP_PORT = 8030;     // HTTP协议，用于导入
        public static final int RPC_PORT = 9020;      // 内部通信
    }
}
