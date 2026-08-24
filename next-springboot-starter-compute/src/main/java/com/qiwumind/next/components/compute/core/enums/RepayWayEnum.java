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

package com.qiwumind.next.components.compute.core.enums;




import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
public enum RepayWayEnum {
    MONTH_INTEREST(1, "先息后本(月付利息)"),

    FULL_PAYMENT(2, "利随本清(趸交)"),

    EQUAL_INSTALLMENT(3, "等额本息"),

    EQUAL_PRINCIPAL(4, "等额本金"),

    EQUAL_INTEREST(5, "等本等息"),

    QUARTERLY_INTEREST(6, "先息后本(季付利息)"),

    DAILY_INTEREST(7, "按日计息"),

    EQUAL_AMOUNT_DAILY(8, "等额还款(按日计息)");

    private int code;

    private String message;

    /**
     * 带参构造器
     *
     * @param code
     * @param message
     */
    private RepayWayEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
