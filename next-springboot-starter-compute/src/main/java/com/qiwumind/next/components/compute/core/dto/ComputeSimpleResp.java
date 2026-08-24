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



import com.qiwumind.next.components.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类ComputeInstallmentResp.java的实现描述：分期试算Response
 * 
 */
@Getter
@Setter
public class ComputeSimpleResp extends BaseResp {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1509373679483933684L;

    /**
     * 
     */
    public ComputeSimpleResp() {
        super();
        this.simpleInstallmentDataList = new ArrayList<SimpleInstallmentData>();
    }

    /**
     * 
     */
//    public ComputeSimpleResp(String reqNo, ResultCode resultCode) {
//        super(reqNo, resultCode);
//        this.simpleInstallmentDataList = new ArrayList<SimpleInstallmentData>();
//    }

    /**
     * 试算日期 <br>
     */
    private Date                        trialDate;

    /**
     * 分期信息列表
     */
    private List<SimpleInstallmentData> simpleInstallmentDataList;

    /**
     * 类ComputeSimpleResp.java的实现描述：data
     * 
     */
    @Getter
    @Setter
    public static class SimpleInstallmentData extends BaseDTO {

        /**
         * 
         */
        private static final long serialVersionUID = -4698083426885039652L;

        /**
         * 当前第几期
         */
        private Integer           installmentNo;

        /**
         * 分期本金
         */
        private BigDecimal        principal;

        /**
         * 分期利息
         */
        private BigDecimal        interest;

        /**
         * 贷款服务费
         */
        private BigDecimal        charge           = BigDecimal.ZERO;

        /**
         * 约定还款日
         */
        private Date              agreeRepayDate;
    }
}
