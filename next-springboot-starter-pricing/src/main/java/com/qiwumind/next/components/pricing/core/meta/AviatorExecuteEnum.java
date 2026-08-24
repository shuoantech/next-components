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

package com.qiwumind.next.components.pricing.core.meta;

/**
 * Aviator 执行模式 - 确定当前处于规则执行的哪个阶段。
 * <p>
 * 每个 Aviator 函数根据此枚举决定：
 * - 仅验证（AVAILABLE_CHECK）
 * - 仅计算（ACTION）
 * - 验证并计算（CHECK_AND_ACTION）
 */
public enum AviatorExecuteEnum {

    /** 阶段 1：仅可用性检查 - 只验证不应用变更 */
    AVAILABLE_CHECK(1, "可用性判断"),

    /** 阶段 2：仅执行 - 应用计算 */
    ACTION(2, "作用"),

    /** 阶段 3：检查并执行 - 一次性验证并计算 */
    CHECK_AND_ACTION(3, "检查并作用");

    private final int code;
    private final String desc;

    AviatorExecuteEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }
}
