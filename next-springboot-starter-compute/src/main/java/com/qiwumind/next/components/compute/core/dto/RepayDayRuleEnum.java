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

package com.qiwumind.next.components.compute.core.dto;



import lombok.Getter;

/**
 *
 */
@Getter
public enum RepayDayRuleEnum {

    SAME_WITH_FUND("SAME_WITH_FUND", "同资金方"),
    END_MONTH_MOVE("END_MONTH_MOVE", "月末平移"),
    FIXED_MANY_DAYS("FIXED_MANY_DAYS", "多日固定"),;

    /**
     * code
     */
    private String code;

    /**
     * message
     */
    private String message;

    /**
     * 构造方法
     *
     * @param code
     * @param message
     */
    RepayDayRuleEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
