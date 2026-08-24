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



import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiwumind.next.components.compute.core.enums.RateEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 类ComputeInstallmentReq.java的实现描述：分期试算Request
 */
@Getter
@Setter
public class ComputeSimpleReq extends BaseReq {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8510523490969177865L;

    /**
     * 金额 <br>
     * 必填
     */
    private BigDecimal        amount;

    /**
     * 利率类型<br>
     * 必填
     */
    private RateEnum rateType;

    /**
     * 利率,如果不为空<br>
     * 必填
     */
    private BigDecimal        rate;

    /**
     * 还款方式<br>
     * 注:还款方式<br>
     * 必填
     */
    private RepayWayEnum repayWayEnum;

    /**
     * 分期期数 <br>
     * 必填
     */
    private Integer           installmentNo;

    /**
     * 分期步长 <br>
     * 实例: 20D 或者 3M<br>
     * 说明:D表示天,M表示月 暂支持持 N+D,或者N+M方式<br>
     * 必填
     */
    private String            installmentStep;

    /**
     * 试算日期 <br>
     * 必填
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date              trialDate;

}
