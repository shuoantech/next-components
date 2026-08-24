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



import com.qiwumind.next.components.compute.core.ComputeAbstractService;
import com.qiwumind.next.components.compute.core.dto.*;

import com.qiwumind.next.components.compute.core.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * 类DEBXService.java的实现描述：等额本息
 *
 * @author   2017年4月17日 下午8:48:03
 */
@Slf4j
public class DEBXService extends ComputeAbstractService {

    /**
     * 等额本息
     */
    @Override
    public ArrayList<ComputeInstallmentResp.InstallmentData>calcCore(ComputeInstallmentResp resp, ComputeConfigDTO calcDto, BigDecimal rate,
                                                                      Boolean needDateFlag) {
        log.info("DEBXService,calcCore,calcDto:{},rate:{},needDateFlag:{}", calcDto.toString(), rate, needDateFlag);
        BigDecimal amount = calcDto.getAmount();
        Integer insNo = calcDto.getInstallmentNo();
        BigDecimal chargeRate = calcDto.getChargeRate();
        BigDecimal perCharge = MoneyUtil.multiplyWithScale(amount, chargeRate);
        ArrayList<ComputeInstallmentResp.InstallmentData> list = new ArrayList<ComputeInstallmentResp.InstallmentData>();
        // 每月本息总金额  = 〔本贷款本金 × 月利率  × (1 + 月利率)^还款月数〕÷〔(1 + 月利率)^还款月数 - 1〕
        BigDecimal perMonth = BigDecimal.ZERO;
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            perMonth = MoneyUtil.divide(amount, insNo, 2);
        } else {
            BigDecimal calcNum = MoneyUtil.multiply(amount, rate,
                            (MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo)).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(PRECISION, RoundingMode.HALF_UP);

            BigDecimal calcNum2 = MoneyUtil.subtract((MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo).setScale(
                    PRECISION, RoundingMode.HALF_UP)).setScale(PRECISION, RoundingMode.HALF_UP), BigDecimal.ONE);
            perMonth = MoneyUtil.divide(calcNum, calcNum2, 2);
        }
        log.info("每月应还总额:{}", perMonth);
        //已计本金
        BigDecimal sumCapital = amount;

        for (int i = 1; i <= insNo; i++) {
            ComputeInstallmentResp.InstallmentData dto = new ComputeInstallmentResp.InstallmentData();
            BigDecimal monthInterest = BigDecimal.ZERO;
            // (本金  × 利率 - 每月还款总额) × ( 1 + 利率)^(期数-1) + 每月还款总额
            BigDecimal calcAmount = MoneyUtil.multiply(MoneyUtil.subtract(MoneyUtil.multiply(amount, rate), perMonth),
                            (MoneyUtil.add(rate, BigDecimal.ONE)).pow(i - 1).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            monthInterest = MoneyUtil.add(calcAmount, perMonth);
            dto.setInstallmentNo(i);
            dto.setPrincipal(MoneyUtil.subtract(perMonth, monthInterest));
            if (i < insNo) {
                sumCapital = MoneyUtil.subtract(sumCapital, dto.getPrincipal());
            }
            if (i == insNo) {//所有本金尾差放在最后一期
                dto.setPrincipal(sumCapital);
            }
            dto.setCharge(perCharge);
            // 每月应还利息 = 等额本金 - 已还本金
            dto.setInterest(monthInterest);
            if (needDateFlag) {
                this.setDate(calcDto, dto, i);
            }
            list.add(dto);
        }
        return list;
    }

    @Override
    public ArrayList<FundComputeTrialResp.FundInstallmentData> calcCore(FundComputeTrialResp resp, BigDecimal rate,
                                                                        FundComputeConfigDTO calcDto) {
        BigDecimal amount2 = calcDto.getAmount();
        Integer insNo2 = calcDto.getInstallmentNo();
        ArrayList<FundComputeTrialResp.FundInstallmentData> list2 = new ArrayList<FundComputeTrialResp.FundInstallmentData>();

        // 每月本息总金额  = 〔本贷款本金 × 月利率  × (1 + 月利率)^还款月数〕÷〔(1 + 月利率)^还款月数 - 1〕
        BigDecimal perMonth2 = BigDecimal.ZERO;
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            perMonth2 = MoneyUtil.divide(amount2, insNo2, 2);
        } else {
            BigDecimal calcNum = MoneyUtil.multiply(amount2, rate,
                            (MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo2)).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(PRECISION, RoundingMode.HALF_UP);

            BigDecimal calcNum2 = MoneyUtil.subtract((MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo2).setScale(
                    PRECISION, RoundingMode.HALF_UP)).setScale(PRECISION, RoundingMode.HALF_UP), BigDecimal.ONE);
            perMonth2 = MoneyUtil.divide(calcNum, calcNum2, 2);
        }
        log.info("每月应还总额:{}", perMonth2);
        ///已计本金
        BigDecimal sumCapital = amount2;

        for (int i = 1; i <= insNo2; i++) {
            FundComputeTrialResp.FundInstallmentData dto2 = new FundComputeTrialResp.FundInstallmentData();
            BigDecimal monthInterest = BigDecimal.ZERO;
            // (本金  × 利率 - 每月还款总额) × ( 1 + 利率)^(期数-1) + 每月还款总额
            BigDecimal calcAmount = MoneyUtil.multiply(
                            MoneyUtil.subtract(MoneyUtil.multiply(amount2, rate), perMonth2),
                            (MoneyUtil.add(rate, BigDecimal.ONE)).pow(i - 1).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            monthInterest = MoneyUtil.add(calcAmount, perMonth2);
            //log.info("monthInterest:{}", monthInterest);
            dto2.setInstallmentNo(i);
            dto2.setPrincipal(MoneyUtil.subtract(perMonth2, monthInterest));
            if (i < insNo2) {
                sumCapital = MoneyUtil.subtract(sumCapital, dto2.getPrincipal());
            }
            if (i == insNo2) {//所有本金尾差放在最后一期
                dto2.setPrincipal(sumCapital);
            }
            // 每月应还利息 = 等额本金 - 已还本金
            dto2.setInterest(monthInterest);
            this.setDate(calcDto, dto2, i);
            list2.add(dto2);
        }
        return list2;
    }

    @Override
    public ArrayList<ComputeSimpleResp.SimpleInstallmentData> calcCore(ComputeSimpleReq req, ComputeSimpleResp resp, BigDecimal rate) {
        BigDecimal amount3 = req.getAmount();
        Integer insNo3 = req.getInstallmentNo();
        ArrayList<ComputeSimpleResp.SimpleInstallmentData> list3 = new ArrayList<ComputeSimpleResp.SimpleInstallmentData>();

        // 每月本息总金额  = 〔本贷款本金 × 月利率  × (1 + 月利率)^还款月数〕÷〔(1 + 月利率)^还款月数 - 1〕
        BigDecimal perMonth3 = BigDecimal.ZERO;
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            perMonth3 = MoneyUtil.divide(amount3, insNo3, 2);
        } else {
            BigDecimal calcNum = MoneyUtil.multiply(amount3, rate,
                            (MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo3)).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(PRECISION, RoundingMode.HALF_UP);

            BigDecimal calcNum2 = MoneyUtil.subtract((MoneyUtil.add(rate, BigDecimal.ONE).pow(insNo3).setScale(
                    PRECISION, RoundingMode.HALF_UP)).setScale(PRECISION, RoundingMode.HALF_UP), BigDecimal.ONE);
            perMonth3 = MoneyUtil.divide(calcNum, calcNum2, 2);
        }
        log.info("每月应还总额:{}", perMonth3);
        //已计本金
        BigDecimal sumCapital = amount3;

        for (int i = 1; i <= insNo3; i++) {
            ComputeSimpleResp.SimpleInstallmentData dto = new ComputeSimpleResp.SimpleInstallmentData();
            BigDecimal monthInterest = BigDecimal.ZERO;
            // (本金  × 利率 - 每月还款总额) × ( 1 + 利率)^(期数-1) + 每月还款总额
            BigDecimal calcAmount = MoneyUtil.multiply(
                            MoneyUtil.subtract(MoneyUtil.multiply(amount3, rate), perMonth3),
                            (MoneyUtil.add(rate, BigDecimal.ONE)).pow(i - 1).setScale(PRECISION, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            monthInterest = MoneyUtil.add(calcAmount, perMonth3);
            log.info("monthInterest:{}", monthInterest);
            dto.setInstallmentNo(i);
            dto.setPrincipal(MoneyUtil.subtract(perMonth3, monthInterest));
            if (i < insNo3) {
                sumCapital = MoneyUtil.subtract(sumCapital, dto.getPrincipal());
            }
            if (i == insNo3) {//所有本金尾差放在最后一期
                dto.setPrincipal(sumCapital);
            }
            // 每月应还利息 = 等额本金 - 已还本金
            dto.setInterest(monthInterest);
            this.setDate(req, dto, i);
            list3.add(dto);
        }
        return list3;
    }


}
