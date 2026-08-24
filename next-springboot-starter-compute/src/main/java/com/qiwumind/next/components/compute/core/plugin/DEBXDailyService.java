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



import com.qiwumind.next.components.compute.core.BusinessDateGenService;
import com.qiwumind.next.components.compute.core.ComputeAbstractService;
import com.qiwumind.next.components.compute.core.dto.*;
import com.qiwumind.next.components.compute.core.dto.fixedrepayday.BusinessDateGeneDO;
import com.qiwumind.next.components.compute.core.dto.fixedrepayday.BusinessDateGeneResultDO;
import com.qiwumind.next.components.compute.core.util.DateUtil;
import com.qiwumind.next.components.compute.core.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 类DEBXService.java的实现描述：等额本息（每日计息算法）
 */

@Slf4j
public class DEBXDailyService extends ComputeAbstractService {

    private final BusinessDateGenService businessDateGenService;

    public DEBXDailyService(BusinessDateGenService businessDateGenService) {
        this.businessDateGenService = businessDateGenService;
    }

    public ArrayList<InstallmentTrialResp .InstallmentData> calcInstallment(FundComputeConfigDTO calcDto) {
        ArrayList<InstallmentTrialResp.InstallmentData> list = new ArrayList<>();
        ArrayList<FundComputeTrialResp.FundInstallmentData> fundList = fundCalcInstallment(calcDto);
        if (fundList != null && fundList.size() > 0) {
            for (FundComputeTrialResp.FundInstallmentData fundInstallmentData : fundList) {
                InstallmentTrialResp.InstallmentData installmentData = new InstallmentTrialResp.InstallmentData();
                installmentData.setPrincipal(fundInstallmentData.getPrincipal());
                installmentData.setInterest(fundInstallmentData.getInterest());
                installmentData.setInstallmentNo(fundInstallmentData.getInstallmentNo());
                installmentData.setForbidEarlyRepayDate(fundInstallmentData.getForbidEarlyRepayDate());
                installmentData.setAgreeRepayDate(fundInstallmentData.getAgreeRepayDate());
                installmentData.setLatestRepayDate(fundInstallmentData.getLatestRepayDate());
                installmentData.setAgreedReportDate(fundInstallmentData.getAgreedReportDate());
                list.add(installmentData);
            }
        }
        return list;

    }

    /**
     * 分期试算 - 通用
     *
     * @param calcDto
     * @return
     */
    public ArrayList<FundComputeTrialResp.FundInstallmentData> fundCalcInstallment(FundComputeConfigDTO calcDto) {
        BigDecimal amount = calcDto.getAmount();//本金
        BigDecimal rate = this.getPeriodsRate(calcDto.getFundRateType(), calcDto.getFundRate());//日利率
        Date loanDate = calcDto.getTrialDate();//借款日期
        int installmentNo = calcDto.getInstallmentNo(); //期数

        //step.1 计算出每月的还款日
        ArrayList<FundComputeTrialResp.FundInstallmentData> list = new ArrayList<>();
        for (int i = 1; i <= installmentNo; i++) {
            FundComputeTrialResp.FundInstallmentData data = new FundComputeTrialResp.FundInstallmentData();
            data.setInstallmentNo(i);

            BusinessDateGeneDO geneDO = new BusinessDateGeneDO();
            geneDO.setTrialDate(calcDto.getTrialDate());
            geneDO.setInstallmentNo(i);
            geneDO.setStepType(calcDto.getRepayCalcDateDTO().getStepType());
            geneDO.setStepValue(calcDto.getRepayCalcDateDTO().getStepValue());
            geneDO.setGraceType(calcDto.getRepayCalcDateDTO().getGraceType());
            geneDO.setGraceTimeDay(calcDto.getRepayCalcDateDTO().getGraceTimeDay());
            geneDO.setRepayWayEnum(calcDto.getFundRepayWay());
            geneDO.setInterestCalcWayEnum(calcDto.getInterestCalcWayEnum());
            geneDO.setCalcReportValue(1);
            geneDO.setFirstRepayDate(calcDto.getRepayCalcDateDTO().getFirstAgreedRepayDate());

            BusinessDateGeneResultDO resultDO = businessDateGenService.getDate(geneDO);

            data.setForbidEarlyRepayDate(resultDO.getForbidEarlyRepayDate());
            data.setAgreeRepayDate(resultDO.getAgreeRepayDate());
            data.setLatestRepayDate(resultDO.getLatestRepayDate());
            data.setAgreedReportDate(resultDO.getAgreedReportDate());
            list.add(data);
        }

        //step.2 计算每期计息天数
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 1; i <= list.size(); i++) {
            Date startDate, endDate;
            FundComputeTrialResp.FundInstallmentData data = list.get(i - 1);
            endDate = data.getAgreeRepayDate();
            if (i == 1) {
                startDate = loanDate;
            } else {
                startDate = list.get(i - 2).getAgreeRepayDate();
            }
            int days = DateUtil.getDays(endDate, startDate);
            log.info("第{}期{}天", i, days);
            map.put(data.getInstallmentNo(), days);
        }

        //step.3 计算每期应还本息之和
        BigDecimal fenzi = BigDecimal.ONE;

        for (int i = 1; i <= list.size(); i++) {
            fenzi = MoneyUtil.multiply(fenzi, MoneyUtil.add(1, MoneyUtil.multiply(rate, map.get(i)).setScale(PRECISION)))
                    .setScale(PRECISION, RoundingMode.HALF_UP);
        }
        fenzi = MoneyUtil.multiply(amount, fenzi).setScale(PRECISION, RoundingMode.HALF_UP);

        BigDecimal fenmu = BigDecimal.ONE;
        for (int i = 2; i <= list.size(); i++) {
            BigDecimal v = BigDecimal.ONE;
            for (int j = i; j <= list.size(); j++) {
                v = MoneyUtil.multiply(v,
                                MoneyUtil.add(1, MoneyUtil.multiply(rate, map.get(j)).setScale(PRECISION, RoundingMode.HALF_UP)))
                        .setScale(PRECISION, RoundingMode.HALF_UP);
            }
            fenmu = MoneyUtil.add(fenmu, v);
        }

        BigDecimal eachIntallmentAmout = MoneyUtil.divide(fenzi, fenmu, 2);
        log.info("每期应还金额 = {}", eachIntallmentAmout);

        //step.4 设置每一期的本金和利息
        BigDecimal interest, principal, restPrincipal = amount;
        for (int i = 1; i <= list.size(); i++) {
            interest = MoneyUtil.multiplyWithScale(restPrincipal, rate, map.get(i));
            if (i == list.size()) { //最后一期本金等于剩余本金
                principal = restPrincipal;
            } else {
                principal = MoneyUtil.subtract(eachIntallmentAmout, interest);
                restPrincipal = MoneyUtil.subtract(restPrincipal, principal);
            }

            //如果利率足够大，计算利息也会很大，算出来的本金可能小于0。破坏了等额还款
            if (principal.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("请求参数异常，计算本金小于0");
            }
            FundComputeTrialResp.FundInstallmentData data = list.get(i - 1);
            data.setPrincipal(principal);
            data.setInterest(interest);
        }

        return list;
    }


    @Override
    public ArrayList<ComputeInstallmentResp.InstallmentData> calcCore(ComputeInstallmentResp resp, ComputeConfigDTO calcDto, BigDecimal rate, Boolean needDateFlag) {
        return null;
    }

    @Override
    public ArrayList<FundComputeTrialResp.FundInstallmentData> calcCore(FundComputeTrialResp resp, BigDecimal rate, FundComputeConfigDTO calcDto) {
        return null;
    }

    @Override
    public ArrayList<ComputeSimpleResp.SimpleInstallmentData> calcCore(ComputeSimpleReq req, ComputeSimpleResp resp, BigDecimal rate) {
        return null;
    }
}
