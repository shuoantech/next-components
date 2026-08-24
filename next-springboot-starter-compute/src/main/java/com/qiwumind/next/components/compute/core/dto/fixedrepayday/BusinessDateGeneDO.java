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

package com.qiwumind.next.components.compute.core.dto.fixedrepayday;



import com.qiwumind.next.components.compute.core.enums.GraceTypeEnum;
import com.qiwumind.next.components.compute.core.enums.InterestCalcWayEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import com.qiwumind.next.components.compute.core.enums.StepTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 还款计划日期生成要素实体
 */
@Getter
@Setter
@ToString
public class BusinessDateGeneDO {
    /**
     * 试算日期
     */
    private Date        trialDate;

    /**
     * 分期期数，第几期
     */
    private Integer     installmentNo;

    /**
     * 步长类型,D为日,M为月,Y为年
     */
    private StepTypeEnum stepType;

    /**
     * 步长量度
     */
    private Integer      stepValue;

    /**
     * 宽限期类型
     */
    private GraceTypeEnum graceType;

    /**
     * 宽限期
     */
    private Integer      graceTimeDay;

    /**
     * 计息方式
     */
    private RepayWayEnum repayWayEnum;

    /**
     * 利息计算方式
     */
    private InterestCalcWayEnum interestCalcWayEnum;

    /**
     * 报案日计算值
     */
    private Integer      calcReportValue;

    /**
     * 第一期约定还款日
     */
    private Date         firstRepayDate;
}
