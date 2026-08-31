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

package com.qiwumind.next.components.common.constant;


import java.nio.charset.Charset;

/**
 * 类CommonConstant.java的实现描述：通用常量类
 */
public class SystemConstants {

    /**
     * N
     */
    public static final String IS_DELETED_N = "N";
    /**
     * Y
     */
    public static final String IS_DELETED_Y = "Y";
    /**
     * SYSTEM
     */
    public static final String SYSTEM = "SYSTEM";
    /**
     * 系统用户的主目录
     */
    public static final String LOCAL_PATH = System.getProperty("user.home").concat("/");
    public static final String CF = "cf";
    /**
     * 未知
     */
    public static final String UN_KNOW = "未知";
    public static final String TMP_DIR = "/alidata1/admin/tmp/";
    public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
    /**
     *
     */
    public static final int ZERO = 0;
    public static final String ZERO_STR = "0";
    public static final String ONE_STR = "1";

    public static final String STRING_BLANK = "";
    public static final String SUCCESS_CODE = "000000";
    public static final String SUCCESS_MSG = "成功";

    public static final String FAIL_CODE = "400000";
    public static final String FAIL_MSG = "失败";
    public static final String NOT_EXSIT_CODE = "300000";
    public static final String NOT_EXSIT_MSG = "不存在";
    public static final String AYSNC_CODE = "200000";
    public static final String AYSNC_MSG = "异步处理中";

    public static final String SYSTEM_ERROR_CODE = "999999";
    public static final String SYSTEM_ERROR_MSG = "异常";

    public static final String ILLEGAL_ARGUMENT_CODE = "900000";
    public static final String ILLEGAL_ARGUMENT_MSG = "无效参数";

    public static final String PROCESSING_CODE = "100000";
    public static final String PROCESSING_MSG = "处理中";
    public static final String REQUEST_FREQUENCY_FAST_CODE = "110000";
    public static final String REQUEST_FREQUENCY_FAST_MSG = "查询频率太频繁";
    public static final String DELIMITER = ":";
    /**
     * 支付信息导出ContentType
     */
    public static final String EXPORT_CONTENTTYPE = "application/octet-stream; charset=utf-8";

    /**
     * 支付信息导出HeaderKey
     */
    public static final String EXPORT_HEADER_KEY = "Content-Disposition";
    /**
     * 支付信息导出HeaderValue
     */
    public static final String EXPORT_HEADER_VAL = "attachment; filename=loanOrderExport.xls";


    // ... 基础常量保持不变 ...
    public static final String GLOBAL = "next";
    public static final String POINT = ".";

    /**
     * 配置前缀命名空间（所有以 next. 开头的配置）
     */
    public static final class Prefix {
        private static final String ROOT = GLOBAL + POINT;
        // ===== 基础配置 =====

        public static final String COMPUTE = ROOT + "compute";
        public static final String CONTEXT = ROOT + "context";
        public static final String TENANT = ROOT + "tenant";
        public static final String DATA_PERMISSION = ROOT + "data-permission";
        public static final String QUARTZ = ROOT + "quartz";
        public static final String TRACER = ROOT + "tracer";
        public static final String LICENSE = ROOT + "license";
        public static final String SECURITY = ROOT + "security";
        public static final String CACHE = ROOT + "cache";
        public static final String REDIS = ROOT + "redis";
        public static final String WEB = ROOT + "web";
        public static final String PDF = ROOT + "pdf";
        public static final String XSS = ROOT + "xss";
        public static final String OPENAPI = ROOT + "openapi";
        public static final String METRICS = ROOT + "metrics";
        public static final String ACCESS_LOG = ROOT + "access-log";
        public static final String BLOOM_FILTER = ROOT + "bloom-filter";
        public static final String WEBSOCKET = ROOT + "websocket";
        public static final String PRICING = ROOT + "pricing";
        public static final String API_ENCRYPT = ROOT + "api-encrypt";
        public static final String HOLOGRES = ROOT + "hologres";  // 修复拼写

        // ===== Groovy 引擎 =====
        public static final class Groovy {
            private static final String GROOVY_ROOT = ROOT + "groovy.";
            public static final String ENGINE = GROOVY_ROOT + "engine";
            public static final String CLASSPATH_LOADER = ENGINE + ".classpath-loader";
            public static final String REDIS_LOADER = ENGINE + ".redis-loader";
            public static final String MYSQL_LOADER = ENGINE + ".mysql-loader";
        }

        // ===== StarRocks =====
        public static final class StarRocks {
            private static final String SR_ROOT = ROOT + "starrocks.";
            public static final String CLUSTER = SR_ROOT + "cluster";
            public static final String CONFIG = SR_ROOT + "config";
        }

        public static final class Sequence {
            public static final String SEQUENCE_SINGLE = ROOT + "sequence";

        }
        public static final class Crypto {
            public static final String CRYTO_ROOT = ROOT + "crypto.";

            public static final String CRYTO = CRYTO_ROOT + "db";

        }

        // ===== 非 GLOBAL 体系 =====
        public static final class ThirdParty {

            public static final String WECHAT_MP = "wechat.mp";
            public static final String WECHAT_PAY = "wechat.pay";

        }
    }


}
