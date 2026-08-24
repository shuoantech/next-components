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
 * BE节点功能说明
 */
public class BEExplanation {
    /**
     * BE主要职责：
     * 1. 数据存储 - 实际存储表数据
     * 2. 查询执行 - 执行查询计划
     * 3. 数据压缩 - 列式存储压缩
     * 4. 数据导入 - 接收并处理数据导入
     * 5. 数据副本 - 数据多副本管理
     */
    
    // BE服务端口
    public static class BEPorts {
        public static final int BE_PORT = 9060;       // 心跳、任务执行
        public static final int WEB_PORT = 8040;      // Web界面
        public static final int BRPC_PORT = 8060;     // 内部RPC通信
        public static final int HTTP_PORT = 8040;     // HTTP服务
    }
}
