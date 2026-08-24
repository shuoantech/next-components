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

package com.qiwumind.next.components.compute.core;



import com.qiwumind.next.components.compute.core.constants.TrialAssetConstants;
import com.qiwumind.next.components.compute.core.constants.TrialInnerConstants;
import com.qiwumind.next.components.compute.core.dto.*;
import com.qiwumind.next.components.compute.core.enums.*;
import com.qiwumind.next.components.compute.core.util.DateUtil;
import com.qiwumind.next.components.compute.core.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 类ComputeAbstractService.java的实现描述：抽象的试算业务处理类<br>
 * 子类中calcCore 方法可以抽取出来,但是为了不可预见的灵活性,放在子类处理,不提供抽象方法。
 */
@Slf4j
public abstract class ComputeAbstractService implements ComputeService {

    /**
     * 默认12个月
     */
    protected final static BigDecimal MONTH = new BigDecimal("12");

    /**
     * 计算过程的中精度
     */
    protected final static Integer PRECISION = 20;

    /**
     * 默认一年360天
     */
    protected final static Integer A_YEAR_DAYS = 360;

    /**
     *
     */
    protected final static Integer A_MONTH_DAYS = 30;

    /**
     * 核心计算逻辑
     *
     * @param resp
     * @param calcDto
     * @param rate
     * @param needDateFlag
     * @return
     */
    public abstract ArrayList<ComputeInstallmentResp.InstallmentData> calcCore(ComputeInstallmentResp resp,
                                                                               ComputeConfigDTO calcDto,
                                                                               BigDecimal rate,
                                                                               Boolean needDateFlag);

    /**
     * 资金端核心计算逻辑
     *
     * @param resp
     * @param rate
     * @param calcDto
     * @return
     */
    public abstract ArrayList<FundComputeTrialResp.FundInstallmentData> calcCore(FundComputeTrialResp resp,
                                                                                 BigDecimal rate,
                                                                                 FundComputeConfigDTO calcDto);

    /**
     * 简单试算
     */
    public abstract ArrayList<ComputeSimpleResp.SimpleInstallmentData> calcCore(ComputeSimpleReq req,
                                                                                ComputeSimpleResp resp,
                                                                                BigDecimal rate);

    /**
     * 简单试算入口,使用这个基本就可以
     *
     * @param req
     * @return
     */
    public ArrayList<ComputeSimpleResp.SimpleInstallmentData> calc(ComputeSimpleReq req, ComputeSimpleResp resp) {
        return this.calcCore(req, resp, this.getPeriodsRate(req));
    }
    /**
     * 执行方法(资金端)
     */
    @Override
    public ArrayList<ComputeInstallmentResp.InstallmentData> calc(ComputeInstallmentResp resp, ComputeConfigDTO calcDto) {
        return this.calcCore(resp, calcDto, this.getPeriodsRate(calcDto), this.needSetDateFlag(calcDto));
    }

    /**
     * 资金端试算
     *
     * @param resp
     * @param calcDto
     * @return
     */
    public ArrayList<FundComputeTrialResp.FundInstallmentData> calc(FundComputeTrialResp resp, FundComputeConfigDTO calcDto) {
        return this.calcCore(resp, calcDto.getFundRate(), calcDto);
    }

    /**
     * 是否需要设置日期
     *
     * @param calcDto
     * @return
     */
    protected Boolean needSetDateFlag(ComputeConfigDTO calcDto) {
        Boolean needSetDateFlag = false;
        if (calcDto.getTemplateVersion() == null || calcDto.getTemplateVersion() < TrialAssetConstants.TEMPLATE_VERSION) {
            needSetDateFlag = true;
        }
        return needSetDateFlag;
    }

    /**
     * 取得利率
     * @return
     */
    protected BigDecimal getPeriodsRate(ComputeSimpleReq req) {
        BigDecimal rate = req.getRate();
        if (req.getRateType() == RateEnum.PERIODS_RATE || req.getRateType() == RateEnum.MONTH_RATE) {
            rate = req.getRate();
        } else if (req.getRateType() == RateEnum.YEAR_RATE) {
            if (req.getInstallmentStep().contains(TrialInnerConstants.M)) {
                rate = MoneyUtil.divide(req.getRate(), MONTH, PRECISION);
            } else if (req.getInstallmentStep().contains(TrialInnerConstants.D)) {
                rate = MoneyUtil.divide(req.getRate(), A_YEAR_DAYS, PRECISION);
            }
        }
        log.info("rate:{}", rate);
        return rate;
    }

    /**
     * 取得资金端期利率
     *
     * @param calcDto
     * @return
     */
    protected BigDecimal getPeriodsRate(ComputeConfigDTO calcDto) {
        BigDecimal rate = BigDecimal.ZERO;
        if (calcDto.getFundRateType() == RateEnum.PERIODS_RATE || calcDto.getFundRateType() == RateEnum.MONTH_RATE) {
            rate = calcDto.getRate();
        } else if (calcDto.getFundRateType() == RateEnum.YEAR_RATE) {
            rate = MoneyUtil.divide(calcDto.getRate(), MONTH, PRECISION);
            if (calcDto.getInstallmentStep().contains(TrialInnerConstants.M)) {
                int value = Integer.valueOf(calcDto.getInstallmentStep().replace(TrialInnerConstants.M, ""));
                if (value > 1) {
                    rate = MoneyUtil.multiply(rate, value);
                }
            }
        }
        return rate;
    }

    /**
     * 等额本息（固定还款日）取得资金端期利率，转化成日利率
     *
     * @return
     */
    protected BigDecimal getPeriodsRate(RateEnum rateEnum, BigDecimal rate) {
        BigDecimal assetRate = BigDecimal.ZERO;
        if (rateEnum == RateEnum.MONTH_RATE) {
            assetRate = MoneyUtil.divide(rate, A_MONTH_DAYS, PRECISION);
        } else if (rateEnum == RateEnum.YEAR_RATE) {
            assetRate = MoneyUtil.divide(rate, A_YEAR_DAYS, PRECISION);
        } else if (rateEnum == RateEnum.DAILY_RATE) {
            assetRate = rate;
        }
        assetRate = assetRate.setScale(8, BigDecimal.ROUND_HALF_UP);
        log.info("daily rate = {}", assetRate);
        return assetRate;
    }

    /**
     * 设置日期方法
     *
     * @param calcDto
     * @param data
     * @param installmentNo
     * @return
     */
    protected ComputeInstallmentResp.InstallmentData setDate(ComputeConfigDTO calcDto, ComputeInstallmentResp.InstallmentData data, Integer installmentNo) {
        RepayCalcDateDTO calcDateDto = calcDto.getRepayCalcDateDTO();
        Date latestRepayDate = null;//最晚还款日 = 约定还款日+宽限期
        Date agreeRepayDate = null;//约定还款日
        Date beforeAgreedRepayDate = null;//约定还款日
        Date firstAgreedRepayDate = calcDateDto.getFirstAgreedRepayDate();//第一期约定还款日
        Date trialDate = DateUtil.formatDateYYYMMDD(calcDto.getTrialDate());//试算日
        Date billDate = null;//账单日
//        Date firstBillDate = calcDto.getKMSCalcDateDTO().getFirstBillDate();//第一期账单日

        //还款日期计算量度
        Integer calcValue = calcDateDto.getBaseValue();
        //宽限日天数
        Integer graceTimeDay = calcDateDto.getGraceTimeDay();
        //步长计算量度
        int stepValue = calcDateDto.getStepValue();
        // 如果利随本清或者随借随还产品,只有一期
        if (calcDto.getFundRepayWay() == RepayWayEnum.FULL_PAYMENT
                || calcDto.getFundRepayWay() == RepayWayEnum.DAILY_INTEREST) {
            if (calcDateDto.getStepType() == StepTypeEnum.MONTH) {
                agreeRepayDate = DateUtil.addMonths(trialDate, stepValue);
            } else if (calcDateDto.getStepType() == StepTypeEnum.DAY) {
                agreeRepayDate = DateUtil.addDays(trialDate, stepValue);
            }
            calcDateDto.setFirstAgreedRepayDate(agreeRepayDate);
            beforeAgreedRepayDate = trialDate;
        } else {
            //如果第一期或者只有一期
            if (installmentNo == 1) {
                //如果月方式
                if (calcDateDto.getRepayDayTypeEnum() == RepayDayTypeEnum.TYPE_MONTH) {
                    if (TrialInnerConstants.KMS.equals(calcDto.getTypeCode())) {
                        // 卡模式 暂时不考虑
                      /*
                        KMSCalcDateDTO kmsCalc = calcDto.getKMSCalcDateDTO();
                        Integer startOfMonth = kmsCalc.getBillDayOfPerMonth();
                        firstBillDate = DateUtil.addDays(DateUtil.getMonthFirstDay(trialDate), startOfMonth - 1);
                        Integer value = DateUtil.compare(firstBillDate, trialDate);
                        if (value > 0) {
                            billDate = firstBillDate;
                        } else if (value <= 0) {
                            firstBillDate = DateUtil.addMonths(firstBillDate, 1);
                            billDate = firstBillDate;
                        }
                        kmsCalc.setFirstBillDate(firstBillDate);
                        */
                    }
                    Integer startOfMonth = calcDateDto.getStartOfMonth();//第几个月后开始
                    Integer dayOfMonth = calcDateDto.getDayOfMonth();//月的第几天
                    if (startOfMonth == 0) {
                        firstAgreedRepayDate = DateUtil.addDays(DateUtil.getMonthFirstDay(trialDate), dayOfMonth - 1);
                        if (TrialInnerConstants.KMS.equals(calcDto.getTypeCode())) {
                            firstAgreedRepayDate = DateUtil.addMonths(
                                    DateUtil.addDays(DateUtil.getMonthFirstDay(trialDate), dayOfMonth - 1), 1);
                        } else {
                            if (DateUtil.compare(firstAgreedRepayDate, trialDate) <= 0) {
                                firstAgreedRepayDate = DateUtil.addMonths(firstAgreedRepayDate, 1);
                            }
                        }
                    } else {
                        firstAgreedRepayDate = DateUtil.addMonths(
                                DateUtil.addDays(DateUtil.getMonthFirstDay(trialDate), dayOfMonth - 1), startOfMonth);
                    }
                } else if (calcDateDto.getRepayDayTypeEnum() == RepayDayTypeEnum.TYPE_DAY) {
                    if (TrialInnerConstants.D.equals(calcDateDto.getDateType())) {
                        firstAgreedRepayDate = DateUtil.addDays(trialDate, calcValue);
                    } else if (TrialInnerConstants.M.equals(calcDateDto.getDateType())) {
                        firstAgreedRepayDate = DateUtil.addMonths(trialDate, calcValue);
                    }
                }
                calcDateDto.setFirstAgreedRepayDate(firstAgreedRepayDate);
                agreeRepayDate = firstAgreedRepayDate;
                beforeAgreedRepayDate = trialDate;

            } else {
                //期数*步长
                Integer calc = Integer.valueOf(MoneyUtil.multiply(installmentNo, stepValue).toString());
                Integer forbid = Integer.valueOf(MoneyUtil.multiply(installmentNo - 1, stepValue).toString());
                if (calcDateDto.getStepType() == StepTypeEnum.DAY) {
                    agreeRepayDate = DateUtil.addDays(trialDate, calc);
                    beforeAgreedRepayDate = DateUtil.addDays(trialDate, forbid);
                } else if (calcDateDto.getStepType() == StepTypeEnum.MONTH) {
                    agreeRepayDate = DateUtil.addMonths(trialDate, calc);
                    beforeAgreedRepayDate = DateUtil.addMonths(trialDate, forbid);
                }
              /*
               if (TrialInnerConstants.KMS.equals(calcDto.getTypeCode())) {
                    billDate = firstBillDate;
                    billDate = DateUtil.addMonths(firstBillDate, calc);
                }*/
            }
        }
        //设置最晚还款日
        if (calcDateDto.getGraceType() == GraceTypeEnum.NATURAL_DAYS) {
            latestRepayDate = DateUtil.addDays(agreeRepayDate, graceTimeDay);
        } else if (calcDateDto.getGraceType() == GraceTypeEnum.WEEKDAYS) {
            if (graceTimeDay > 0) {
                //对接日历平台
               /*
               ResultBase<Date> resultDate = holidayApiService.getWorkDay(
                        DateUtil.formatDateByYYYYMMDD(agreeRepayDate), graceTimeDay);
                log.info("resultDate:{}", resultDate);
                latestRepayDate = resultDate.getValue();
                */
            } else {
                latestRepayDate = agreeRepayDate;
            }
        } else {
            latestRepayDate = agreeRepayDate;
        }
        Date agreedReportDate = DateUtil.addDays(latestRepayDate, calcDateDto.getCalcReportValue());
        //提前还款截止日为上个约定还款日,如果不存在,设置为试算日期
        data.setForbidEarlyRepayDate(DateUtil.getOneDayEnd(beforeAgreedRepayDate != null ? beforeAgreedRepayDate : trialDate));
        data.setAgreeRepayDate(DateUtil.getOneDayEnd(agreeRepayDate));
        data.setLatestRepayDate(DateUtil.getOneDayEnd(latestRepayDate));
        data.setAgreedReportDate(DateUtil.getOneDayEnd(agreedReportDate));
        data.setBillDate(DateUtil.getOneDayEnd(billDate));
        //        log.info("beforeAgreedRepayDate:{},agreeRepayDate:{},latestRepayDate:{},agreedReportDate:{},billDate:{}", beforeAgreedRepayDate,agreeRepayDate, latestRepayDate, agreedReportDate, billDate);
        return data;
    }

    /**
     * 简单试算设置日期值
     *
     * @param req
     * @param dto
     * @param installmentNo   期数
     */
    protected void setDate(ComputeSimpleReq req, ComputeSimpleResp.SimpleInstallmentData dto, int installmentNo) {
        if (req.getRepayWayEnum() == RepayWayEnum.DAILY_INTEREST || req.getRepayWayEnum() == RepayWayEnum.FULL_PAYMENT) {
            if (req.getInstallmentStep().contains(TrialInnerConstants.D)) {
                int days = Integer.valueOf(req.getInstallmentStep().replace(TrialInnerConstants.D, ""));
                dto.setAgreeRepayDate(DateUtil.getOneDayEnd(DateUtil.addDays(req.getTrialDate(), days)));
            } else if (req.getInstallmentStep().contains(TrialInnerConstants.M)) {
                int months = Integer.valueOf(req.getInstallmentStep().replace(TrialInnerConstants.M, ""));
                dto.setAgreeRepayDate(DateUtil.getOneDayEnd(DateUtil.addMonths(req.getTrialDate(), months)));
            }
        } else {
            if (req.getInstallmentStep().contains(TrialInnerConstants.D)) {
                int days = Integer.valueOf(req.getInstallmentStep().replace(TrialInnerConstants.D, ""));
                Integer calc = Integer.valueOf(MoneyUtil.multiply(installmentNo, days).toString());
                dto.setAgreeRepayDate(DateUtil.getOneDayEnd(DateUtil.addDays(req.getTrialDate(), calc)));
            } else if (req.getInstallmentStep().contains(TrialInnerConstants.M)) {
                int months = Integer.valueOf(req.getInstallmentStep().replace(TrialInnerConstants.M, ""));
                Integer calc = Integer.valueOf(MoneyUtil.multiply(installmentNo, months).toString());
                dto.setAgreeRepayDate(DateUtil.getOneDayEnd(DateUtil.addMonths(req.getTrialDate(), calc)));
            }
        }
    }

    /**
     * 资金端试算日期设置值
     *
     * @param calcDto
     * @param dto
     * @param installmentNo
     */
    protected void setDate(FundComputeConfigDTO calcDto, FundComputeTrialResp.FundInstallmentData dto, int installmentNo) {
        //int graceValue = calcDto.getGraceValue();
        Date trialDate = calcDto.getTrialDate();

        Date agreeRepayDate = null;
        // Date latestRepayDate = null;//最晚还款日 = 约定还款日+宽限期
        Date beforeAgreedRepayDate = null;//上个约定还款日
        //步长计算量度
        int stepValue = calcDto.getRepayCalcDateDTO().getStepValue();

        if (calcDto.getFundRepayWay() == RepayWayEnum.DAILY_INTEREST
                || calcDto.getFundRepayWay() == RepayWayEnum.FULL_PAYMENT) {
            if (calcDto.getInstallmentStep().contains(TrialInnerConstants.D)) {
                int days = Integer.valueOf(calcDto.getInstallmentStep().replace(TrialInnerConstants.D, ""));
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addDays(trialDate, days));
            } else if (calcDto.getInstallmentStep().contains(TrialInnerConstants.M)) {
                int months = Integer.valueOf(calcDto.getInstallmentStep().replace(TrialInnerConstants.M, ""));
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addMonths(trialDate, months));
            }
            beforeAgreedRepayDate = trialDate;
        } else {
            Integer forbid = Integer.valueOf(MoneyUtil.multiply(installmentNo - 1, stepValue).toString());
            if (calcDto.getInstallmentStep().contains(TrialInnerConstants.D)) {
                int days = Integer.valueOf(calcDto.getInstallmentStep().replace(TrialInnerConstants.D, ""));
                Integer calc = Integer.valueOf(MoneyUtil.multiply(installmentNo, days).toString());
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addDays(trialDate, calc));
                beforeAgreedRepayDate = DateUtil.addDays(trialDate, forbid);
            } else if (calcDto.getInstallmentStep().contains(TrialInnerConstants.M)) {
                int months = Integer.valueOf(calcDto.getInstallmentStep().replace(TrialInnerConstants.M, ""));
                Integer calc = Integer.valueOf(MoneyUtil.multiply(installmentNo, months).toString());
                if (calcDto.getSupportRepayDateSpecialLogic()) {
                    Date firstAgreedRepayDate = calcDto.getRepayCalcDateDTO().getFirstAgreedRepayDate();
                    if (installmentNo == 1) {
                        agreeRepayDate = firstAgreedRepayDate;
                        beforeAgreedRepayDate = DateUtil.addMonths(trialDate, forbid);
                    } else {
                        agreeRepayDate = DateUtil.addMonths(firstAgreedRepayDate, forbid);
                        beforeAgreedRepayDate = DateUtil.addMonths(firstAgreedRepayDate, forbid - 1);
                    }
                } else {
                    agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addMonths(trialDate, calc));
                    beforeAgreedRepayDate = DateUtil.addMonths(trialDate, forbid);
                }
            }
        }
        //设置最晚还款日
        //        if (calcDto.getGraceDateType() == com.zhongan.creditcore.common.enums.GraceTypeEnum.NATURAL_DAYS) {
        //            latestRepayDate = DateUtil.addDays(agreeRepayDate, graceValue);
        //        } else if (calcDto.getGraceDateType() == com.zhongan.creditcore.common.enums.GraceTypeEnum.WEEKDAYS) {
        //            if (graceValue > 0) {
        //                //对接日历平台
        //                ResultBase<Date> resultDate = holidayApiService.getWorkDay(DateUtil.formatDateByYYYYMMDD(agreeRepayDate), graceValue);
        //                log.info("resultDate:{}", resultDate);
        //                latestRepayDate = resultDate.getValue();
        //            } else {
        //                latestRepayDate = agreeRepayDate;
        //            }
        //        } else {
        //            latestRepayDate = agreeRepayDate;
        //        }
        dto.setAgreeRepayDate(DateUtil.getOneDayEnd(agreeRepayDate));
        dto.setForbidEarlyRepayDate(DateUtil.getOneDayEnd(beforeAgreedRepayDate));
        dto.setLatestRepayDate(dto.getAgreeRepayDate());//暂定最晚还款日等于约定还款日
    }
}
