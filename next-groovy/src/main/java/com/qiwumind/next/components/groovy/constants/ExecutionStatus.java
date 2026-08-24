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

package com.qiwumind.next.components.groovy.constants;



import lombok.Getter;
import lombok.ToString;

/**
 * 执行状态
 */
@Getter
@ToString
public enum ExecutionStatus {
    /**
     * 执行失败
     */
    FAILED("500", "执行失败"),
    /**
     * 执行成功
     */
    SUCCESS("200", "执行成功"),
    /**
     * 没有找到脚本
     */
    NO_SCRIPT("4004", "没有找到groovy脚本"),
    /**
     * 参数有误
     */
    PARAM_ERROR("3003", "没有找到groovy脚本");

    /**
     * 编码
     */
    private String code;
    /**
     * 含义
     */
    private String meaning;

    ExecutionStatus(String code, String meaning) {
        this.code = code;
        this.meaning = meaning;
    }
}
