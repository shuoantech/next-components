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


public class CommonConstants {
    public static final String NEXT_NAME = "Next Service";
    public static final String NEXT_VERSION_NAME = "next.version";
    public static final String NEXT_VERSION_DEFAULT_VALUE = "3.5.15";

    public static final String NACOS_VERSION_NAME = "nacos.version";
    public static final String NACOS_VERSION_VALUE = "2.3.2";

    public static final String LOGGER_VERSION_NAME = "next.logger.version";
    public static final String SPRING_BOOT_FRAMEWORK = "SpringBoot Framework";
    public static final String SPRING_BOOT_FRAMEWORK_VERSION = NEXT_VERSION_DEFAULT_VALUE;

    public static final String FRAMEWORK = "Framework";

    public static final String APPLICATION = "Application";
    public static final String VERSION = "Version";

    public static final String ENV_PROPERTIES_SUFFIX = ".meta";
    public static final String SEPARATE_COLON = ":";
    public static final String SEPARATE_COMMA = ",";

    public static final String METADATA = "metadata";
    public static final String SUB_ENV = "env";

    public static final String DEPLOY_ENV = "DEPLOY_ENV";
    public static final String DEPLOY_ENV_LOCAL = "local";

    public static final String APP_ID = "app.id";

    public static final String SPRING_APPLICATION_NAME = "spring.application.name";
    public static final String APPLICATION_CONTEXT_PATH = "server.servlet.context-path";

    public static final String GITLAB = "Gitlab";
    public static final String DOCS = "Docs";
    public static final String GITLAB_VALUE = "https://gitee.com/xiaobaoinfo/next-components-v2";
    public static final String DOCS_VALUE = "https://gitee.com/xiaobaoinfo/next-components-v2";


    public static final String GLOBAL = "spring.global";

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

    /**
     * 未知
     */
    public static final String UN_KNOW = "未知";
    /**
     *
     */
    public static final String STRING_BLANK = "";
    /**
     *
     */
    public static final String SUCCESS_CODE = "000000";
    /**
     *
     */
    public static final String SUCCESS_MSG = "成功";
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
    public static final String TMP_DIR = "/alidata1/admin/tmp/";
    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";
}
