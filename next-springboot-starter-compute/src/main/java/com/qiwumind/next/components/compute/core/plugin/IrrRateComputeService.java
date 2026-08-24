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

package com.qiwumind.next.components.compute.core.plugin;

import com.google.common.collect.Lists;
import com.qiwumind.next.components.compute.core.IRateComputeService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class IrrRateComputeService implements IRateComputeService {
    private static final int SCALE = 20;  // 计算精度
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 计算IRR，约数 返回小数一般
     *
     * @param everyPlanToltalAmount 【第一个元素为本金的负数，后面为每期(每月)应还金额（本金+利息）】
     * @return
     */
    @Override
    public BigDecimal calculate(BigDecimal totalAmount, List<BigDecimal> everyPlanToltalAmount) {
        BigDecimal monthIrr = monthIrr(totalAmount, everyPlanToltalAmount);
        return monthIrr.multiply(new BigDecimal("12"));
    }

    /**
     * 按照百分比返回前端，含%
     *
     * @param totalAmount
     * @param everyPlanToltalAmount
     * @return
     */
    public String calaulateYearIrr(BigDecimal totalAmount, List<BigDecimal> everyPlanToltalAmount) {
        DecimalFormat df = new DecimalFormat("0.00%");
        BigDecimal irr = calculate(totalAmount, everyPlanToltalAmount);
        return df.format(irr);


    }

    public BigDecimal monthIrr(BigDecimal totalAmount, List<BigDecimal> everyPlanToltalAmount) {
        List<BigDecimal> cashFlow = Lists.newArrayList();
        cashFlow.add(totalAmount);
        cashFlow.addAll(everyPlanToltalAmount);
        return this.irr(cashFlow);
    }

    /**
     * 计算IRR，约数
     *
     * @param cashFlow 【第一个元素为本金的负数，后面为每期应还金额（本金+利息）】
     * @return
     */
    public BigDecimal irr(List<BigDecimal> cashFlow) {
        if (cashFlow == null || cashFlow.size() < 2) {
            return BigDecimal.ZERO;
        }
        BigDecimal flowOut = cashFlow.get(0);
        BigDecimal minValue = BigDecimal.ZERO;
        BigDecimal maxValue = BigDecimal.ONE;
        BigDecimal testValue = BigDecimal.ZERO;

        int LOOPNUM = 1000;
        /**
         * 最小差异
         */
        final BigDecimal MINDIF = new BigDecimal("0.00001");

        while (LOOPNUM > 0) {
            // testValue = (minValue + maxValue) / 2
            testValue = minValue.add(maxValue)
                    .divide(new BigDecimal("2"), SCALE, ROUNDING_MODE);
            // 计算 NPV
            BigDecimal npv = this.NPVWithBigDecimalRate(cashFlow, testValue);
            // target = -flowOut
            BigDecimal target = flowOut.negate();
            // 判断 |npv - target| < MINDIF
            if (npv.add(target).abs().compareTo(MINDIF) < 0) {
                break;
            }
            // 判断 |flowOut| > npv
            else if (flowOut.abs().compareTo(npv) > 0) {
                maxValue = testValue;
            } else {
                minValue = testValue;
            }

            LOOPNUM--;
        }
        return testValue;
    }

    /**
     * 净现值（Net Present Value, NPV)
     * 使用 BigDecimal 计算，避免精度丢失
     *
     * @param flowInArr 现金流列表
     * @param rate      折现率
     * @return NPV 值（BigDecimal 类型）
     */
    // 如果需要更高精度的版本，可以提供一个完全使用 BigDecimal 的重载方法
    public BigDecimal NPVWithBigDecimalRate(List<BigDecimal> flowInArr, BigDecimal rate) {
        BigDecimal npv = BigDecimal.ZERO;
        BigDecimal ratePlusOne = BigDecimal.ONE.add(rate);
        for (int i = 1; i < flowInArr.size(); i++) {
            // 计算分母: (1 + rate)^i
            BigDecimal denominator = ratePlusOne.pow(i, new MathContext(SCALE, ROUNDING_MODE));
            // 计算: flowInArr[i] / (1+rate)^i
            BigDecimal term = flowInArr.get(i).divide(denominator, SCALE, ROUNDING_MODE);
            npv = npv.add(term);
        }
        return npv;
    }
}
