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

package com.qiwumind.next.components.groovy.entity;



import com.qiwumind.next.components.groovy.constants.ExecutionStatus;

import lombok.Data;

/**
 * 脚本执行结果
 *
 * @author wenpan 2022/09/18 12:44
 */
@Data
public class EngineExecutorResult {

    /**
     * 执行状态
     */
    private ExecutionStatus executionStatus;

    /**
     * 返回内容
     */
    private Object          context;

    /**
     * 异常信息
     */
    private Throwable       exception;

    /**
     * 自定义异常描述
     */
    private String          errorMessage;

    /**
     * 获取context为指定的类型
     */
    public <T> T context() {
        return (T) this.context;
    }

    private EngineExecutorResult(ExecutionStatus executionStatus, String errorMessage) {
        this.executionStatus = executionStatus;
        this.errorMessage = errorMessage;
    }

    private EngineExecutorResult(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    private EngineExecutorResult(ExecutionStatus executionStatus, Throwable exception) {
        this.executionStatus = executionStatus;
        this.exception = exception;
    }

    private <T> EngineExecutorResult(ExecutionStatus executionStatus, T context) {
        this.executionStatus = executionStatus;
        this.context = context;
    }

    /**
     * 执行失败
     *
     * @param exception 异常信息
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     */
    public static EngineExecutorResult failed(Throwable exception) {
        return new EngineExecutorResult(ExecutionStatus.FAILED, exception);
    }

    /**
     * 执行失败
     *
     * @param errorMessage 异常信息
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author wenpan 2022/9/18 12:54 下午
     */
    public static EngineExecutorResult failed(String errorMessage) {
        return new EngineExecutorResult(ExecutionStatus.PARAM_ERROR, errorMessage);
    }

    /**
     * 执行成功
     *
     * @param context 内容
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author wenpan 2022/9/18 12:55 下午
     */
    public static <T> EngineExecutorResult success(T context) {
        return success(ExecutionStatus.SUCCESS, context);
    }

    /**
     * 执行成功
     *
     * @param context 内容
     * @param status 执行状态
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author 2022/9/18 12:55 下午
     */
    public static <T> EngineExecutorResult success(ExecutionStatus status, T context) {
        return new EngineExecutorResult(status, context);
    }
}
