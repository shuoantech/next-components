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



import com.qiwumind.next.components.compute.core.enums.GraceTypeEnum;
import com.qiwumind.next.components.compute.core.enums.RepayDayTypeEnum;
import com.qiwumind.next.components.compute.core.enums.StepTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * 类RepayCalcDateDto.java的实现描述：还款日配置信息类
 */
@Setter
@Getter
public class RepayCalcDateDTO {

    /**
     * 每月多少号<br>
     * 适用于每月方式
     */
    private Integer dayOfMonth;

    /**
     * 首期第几个月开始<br>
     * 适用于每月方式
     */
    private Integer startOfMonth;

    /**
     * DateType量度<br>
     * 适用于D+ 日期方式,和DateType组合成 N天,或者N月方式,列:10D , 9M
     */
    private Integer baseValue;

    /**
     * 单位：D 或者 M <br>
     * 适用于D+ 日期方式,和baseValue组合成 N天,或者N月方式,列:10D , 9M
     */
    private String DateType;

    /**
     * 还款日方式
     */
    private RepayDayTypeEnum repayDayTypeEnum;

    /**
     * 第一次约定还款日
     */
    private Date firstAgreedRepayDate;

    /**
     * 约定还款日距离约定报案日间隔(天数)
     */
    private Integer calcReportValue;

    /**
     * 提前还款截止日步长(默认1个月)
     */
    private String earlyRepayDeadline;

    /**
     * 步长类型,D为日,M为月,Y为年
     */
    private StepTypeEnum stepType;

    /**
     * 步长量度
     */
    private Integer stepValue;

    /**
     * 宽限期(天数)
     */
    private Integer graceTimeDay;

    /**
     * 宽限期类型,D为工作日,T为自然日
     */
    private GraceTypeEnum graceType;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
