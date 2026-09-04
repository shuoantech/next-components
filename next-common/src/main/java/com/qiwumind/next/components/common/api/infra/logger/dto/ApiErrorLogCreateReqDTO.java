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

package com.qiwumind.next.components.common.api.infra.logger.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API 错误日志
 *
 * @author 3070
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ApiErrorLogCreateReqDTO {

    /**
     * 链路编号
     */
    private String traceId;
    /**
     * 账号编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 应用名
     */
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    /**
     * 请求方法名
     */
    @NotNull(message = "http 请求方法不能为空")
    private String requestMethod;
    /**
     * 访问地址
     */
    @NotNull(message = "访问地址不能为空")
    private String requestUrl;
    /**
     * 请求参数
     */
    @NotNull(message = "请求参数不能为空")
    private String requestParams;
    /**
     * 用户 IP
     */
    @NotNull(message = "ip 不能为空")
    private String userIp;
    /**
     * 浏览器 UA
     */
    @NotNull(message = "User-Agent 不能为空")
    private String userAgent;

    /**
     * 异常时间
     */
    @NotNull(message = "异常时间不能为空")
    private LocalDateTime exceptionTime;
    /**
     * 异常名
     */
    @NotNull(message = "异常名不能为空")
    private String exceptionName;
    /**
     * 异常发生的类全名
     */
    @NotNull(message = "异常发生的类全名不能为空")
    private String exceptionClassName;
    /**
     * 异常发生的类文件
     */
    @NotNull(message = "异常发生的类文件不能为空")
    private String exceptionFileName;
    /**
     * 异常发生的方法名
     */
    @NotNull(message = "异常发生的方法名不能为空")
    private String exceptionMethodName;
    /**
     * 异常发生的方法所在行
     */
    @NotNull(message = "异常发生的方法所在行不能为空")
    private Integer exceptionLineNumber;
    /**
     * 异常的栈轨迹异常的栈轨迹
     */
    @NotNull(message = "异常的栈轨迹不能为空")
    private String exceptionStackTrace;
    /**
     * 异常导致的根消息
     */
    @NotNull(message = "异常导致的根消息不能为空")
    private String exceptionRootCauseMessage;
    /**
     * 异常导致的消息
     */
    @NotNull(message = "异常导致的消息不能为空")
    private String exceptionMessage;


}
