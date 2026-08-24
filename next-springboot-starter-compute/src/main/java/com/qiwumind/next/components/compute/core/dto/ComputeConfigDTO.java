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
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 *
 */
@Setter
@Getter
public class ComputeConfigDTO extends ComputeDTO {

    /**
     * 产品类型
     */
    private String             typeCode;

    /**
     * 模板版本号(为了兼容老产品配置,作为分流标志)
     */
    private Integer            templateVersion;

    /**
     * 利率（资金端）
     */
    private BigDecimal         rate               = BigDecimal.ZERO;

    /**
     * 利率（资产端）
     */
    private BigDecimal         assetRate          = BigDecimal.ZERO;

    /**
     * 贷款服务费费率(资产端)
     */
    private BigDecimal         chargeRate         = BigDecimal.ZERO;

    /**
     * 贷款服务费类型(资产端)
     */
    private ChargeRateEnum chargeRateType;

    /**
     * 还款方式(资产端)
     */
    private RepayWayEnum repayWayEnum;

    /**
     * 利率类型（资产）
     */
    private RateEnum assetRateType;

    /**
     * 服务费计算方式
     */
    private CalcChargeWayEnum calcChargeWay;

    /**
     * 尾差计算方式
     */
    private TaildifferenceEnum tailDifferenceType = TaildifferenceEnum.LAST_PERIOD;

    /**
     * 当期不满一期或者超过一期是否按日计息,默认为否
     */
    private Boolean            dailyInterestFalg  = false;
    /**
     * 计算标准version
     */
    private Integer            version;
    /**
     * 保费费率<br>
     */
    private BigDecimal         policyRate;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
