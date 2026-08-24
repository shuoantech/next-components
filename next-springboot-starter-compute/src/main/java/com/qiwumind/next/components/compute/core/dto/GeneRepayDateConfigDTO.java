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
import lombok.Setter;
import lombok.ToString;

/**
 */

@Setter
@Getter
@ToString
public class GeneRepayDateConfigDTO {

    /**
     * 还款日规则<br>
     */
    private RepayDayRuleEnum repayDayRuleEnum;

    /**
     * 平移天数
     */
    private Integer          moveDays;

    /**
     * 第一期天数
     */
    private Integer          miniDays;

    /**
     * 多日固定 - 起始日期
     */
    private Integer          startDay;

    /**
     * 多日固定 - 截止日期
     */
    private Integer          endDay;

    /**
     * 多日固定 - 固定日期
     */
    private Integer          configFixedRepayDay;
}
