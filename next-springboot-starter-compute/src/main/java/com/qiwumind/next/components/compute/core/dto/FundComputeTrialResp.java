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



import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import com.qiwumind.next.components.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类FundComputeResp.java的实现描述：资金端借款试算结果Resp
 */
@Setter
@Getter
public class FundComputeTrialResp extends BaseResp {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2170078132742079139L;

    /**
     *
     */
    public FundComputeTrialResp() {
        super();
        this.fundInstallmentDataList = new ArrayList<FundInstallmentData>();
    }

    /**
     *
     */
//    public FundComputeTrialResp(String reqNo, ResultCode resultCode) {
//        super(reqNo, resultCode);
//        this.fundInstallmentDataList = new ArrayList<FundInstallmentData>();
//    }

    /**
     * 试算日期 <br>
     */
    private Date fundTrialDate;

    /**
     * 还款方式<br>
     */
    private RepayWayEnum repayWay;

    /**
     * 分期总期数 <br>
     */
    private Integer totalInstallmentNo;

    /**
     * 日利率
     */
    private RateDTO dailyRateDTO;

    /**
     * 月利率
     */
    private RateDTO  monthRateDTO;

    /**
     * 年化利率
     */
    private RateDTO yearRateDTO;

    /**
     * 分期信息列表
     */
    private List<FundInstallmentData> fundInstallmentDataList;

    /**
     * 类FundComputeTrialResp.java的实现描述：data
     */
    @Getter
    @Setter
    public static class FundInstallmentData extends BaseDTO {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1889235908350870582L;

        /**
         * 本金
         */
        private BigDecimal principal;

        /**
         * 利息
         */
        private BigDecimal interest;

        /**
         * 期数 <br>
         */
        private Integer installmentNo;

        /**
         * 约定还款日
         */
        private Date agreeRepayDate;

        /**
         * 最晚还款日
         */
        private Date latestRepayDate;

        /**
         * 提前还款截止日
         */
        private Date forbidEarlyRepayDate;


        /**
         * 约定报案日
         */
        private Date              agreedReportDate;

    }


    /**
     * 固定还款日 <br>
     */
    private Integer                   fixedRepayDay;

    /**
     * 总金额
     */
    private BigDecimal                sumAmount = BigDecimal.ZERO;

    /**
     * 总本金
     */
    private BigDecimal                sumPrincipal = BigDecimal.ZERO;

    /**
     * 总利息
     */
    private BigDecimal                sumInterest = BigDecimal.ZERO;

}
