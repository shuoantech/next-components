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



import com.qiwumind.next.components.compute.core.enums.ChargeRateEnum;
import com.qiwumind.next.components.compute.core.enums.RateEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.common.result.BaseResultType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分期试算Response
 */
@Getter
@Setter
public class ComputeInstallmentResp extends BaseResp {
    /**
     *
     */
    public ComputeInstallmentResp() {
        super();
        this.installmentDataList = new ArrayList<InstallmentData>();
    }


    public ComputeInstallmentResp(String reqNo, BaseResultType resultType) {
        super(reqNo, resultType);
        this.installmentDataList = new ArrayList<InstallmentData>();
    }

    /**
     * 试算日期 <br>
     */
    private Date trialDate;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 资金利率类型
     */
    private RateEnum rateType;

    /**
     * 利率
     */
    private BigDecimal rate = BigDecimal.ZERO;

    /**
     * 资产端利率类型
     */
    private RateEnum assetRateType;

    /**
     * 资产端利率
     */
    private BigDecimal assetRate = BigDecimal.ZERO;

    /**
     * 资产端贷款服务费费率
     */
    private BigDecimal chargeRate = BigDecimal.ZERO;

    /**
     * 资产端贷款服务费费率类型
     */
    private ChargeRateEnum chargeRateType;

    /**
     * 还款方式(资产端)
     */
    private RepayWayEnum repayWayEnum;

    /**
     * 还款方式(资金端)
     */
    private RepayWayEnum fundRepayWayEnum;

    /**
     * 分期信息列表
     */
    private List<InstallmentData> installmentDataList;

    /**
     * 类ComputeInstallmentResp.java的实现描述：data
     */
    @Getter
    @Setter
    public static class InstallmentData extends BaseDTO {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = -2126791171658064913L;

        /**
         * 当前第几期
         */
        private Integer installmentNo;

        /**
         * 分期本金
         */
        private BigDecimal principal;

        /**
         * 分期利息
         */
        private BigDecimal interest;

        /**
         * 贷款服务费
         */
        private BigDecimal charge;

        /**
         * 优惠分期本金<br>
         * 非必填
         */
        private BigDecimal freePrincipal;

        /**
         * 优惠分期利息<br>
         * 非必填
         */
        private BigDecimal freeInterest;

        /**
         * 优惠分期手续费<br>
         * 非必填
         */
        private BigDecimal freeCharge;

        /**
         * 约定还款日
         */
        private Date agreeRepayDate;

        /**
         * 最晚还款日
         */
        private Date latestRepayDate;

        /**
         * 约定报案日
         */
        private Date agreedReportDate;

        /**
         * 提前还款截止日
         */
        private Date forbidEarlyRepayDate;

        /**
         * 账单日(此属性在卡模式中才会有值)
         */
        private Date billDate;
    }

}
