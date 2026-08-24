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



import com.qiwumind.next.components.compute.core.dto.fixedrepayday.FirstRepayDayRuleDO;
import com.qiwumind.next.components.compute.core.enums.InterestCalcWayEnum;
import com.qiwumind.next.components.compute.core.enums.RateEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 资金和资产试算都需要的值
 */
@Setter
@Getter
public class ComputeDTO extends ConfigDTO {

    /**
     * 资金编码<br>
     */
    private String fundCode;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品类型
     */
    private String typeCode;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 期数
     */
    private Integer installmentNo;

    /**
     * 步长
     */
    private String installmentStep;

    /**
     * 还款方式(资金端)
     */
    private RepayWayEnum fundRepayWay;

    /**
     * 利息计算方式
     */
    private InterestCalcWayEnum interestCalcWayEnum;

    /**
     * 还款日生成规则
     */
    private RepayDayRuleEnum repayDayRuleEnum;

    /**
     * 资金端利率
     */
    private BigDecimal fundRate = BigDecimal.ZERO;

    /**
     * 利率类型(资金)
     */
    private RateEnum fundRateType;

    /**
     * 还款日计算基类
     */
    private RepayCalcDateDTO repayCalcDateDTO;

    /**
     * 固定还款日
     */
    private Integer fixedRepayDay;

    /**
     * 第一期最小天数
     */
    private Integer miniDays;

    /**
     * 平移天数
     */
    private Integer moveDays;

    /**
     * 是否同资金端
     */
    private boolean equalFund;

    /**
     * 固定还款日生成规则
     */
    private FirstRepayDayRuleDO firstRepayDayRuleDO;

    /**
     * 第一期约定还款日生成规则
     */
    private GeneRepayDateConfigDTO geneRepayDateConfigDTO;

}
