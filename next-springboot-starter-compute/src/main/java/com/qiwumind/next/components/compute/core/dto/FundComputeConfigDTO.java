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



import com.qiwumind.next.components.compute.core.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类FundComputeConfigDTO.java的实现描述：资金端试算DTO
 * 
 */
@Setter
@Getter
public class FundComputeConfigDTO extends ComputeDTO {

    /**
     * 还款日计算基类
     */
    protected RepayCalcDateDTO         repayCalcDateDTO;

    /**
     * 试算日期 <br>
     */
    protected Date                  fundTrialDate;

    /**
     * 特殊逻辑加工后试算日期 <br>
     */
    protected Date                  specialTrialDate;

    /**
     * 取资金配置平台时间
     */
    protected Date                  getFundConfigDate;

//    /**
//     * 获取配置时间
//     */
//    private Date                    getConfigDate;

    /**
     * 按日计息时候,头尾计算方式
     */
    protected TailEnum tailEnum;

    /**
     * 宽限期类型
     */
    protected GraceTypeEnum graceDateType;

    /**
     * 宽限期值
     */
    protected Integer               graceValue;

    /**
     * 尾差计算方式
     */
    protected TaildifferenceEnum tailDifferenceType   = TaildifferenceEnum.LAST_PERIOD;

    /**
     * 资金方约定还款日是否为特殊规则
     */
    protected Boolean               supportRepayDateSpecialLogic = false;

    /**
     * 计算利息方式:按月计息,按日计息<br>
     * 只适用月利随本清场景
     */
    protected InterestCalcWayEnum interestCalcWayEnum;

        /**
     * 还款计划类型
     */
    private RepayPlanGeneTypeEnum generateType;

}
