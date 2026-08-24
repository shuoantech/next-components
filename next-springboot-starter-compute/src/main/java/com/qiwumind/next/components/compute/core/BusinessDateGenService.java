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



import com.qiwumind.next.components.compute.core.dto.fixedrepayday.BusinessDateGeneDO;
import com.qiwumind.next.components.compute.core.dto.fixedrepayday.BusinessDateGeneResultDO;
import com.qiwumind.next.components.compute.core.enums.GraceTypeEnum;
import com.qiwumind.next.components.compute.core.enums.InterestCalcWayEnum;
import com.qiwumind.next.components.compute.core.enums.RepayWayEnum;
import com.qiwumind.next.components.compute.core.enums.StepTypeEnum;
import com.qiwumind.next.components.compute.core.util.DateUtil;
import com.qiwumind.next.components.compute.core.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @program: 20200324
 * @Date: 2020/3/14 17:14
 * @Author: HN
 * @Description: 还款计划时间生成服务
 */
@Slf4j
public class BusinessDateGenService {

    /**
     * 计算还款日期 - 通用<br>
     *  规则：<br>
     *      提前还款截止日 = 第一期为放款日期，后续期数为上一期约定还款日<br>
     *      约定还款日 = 固定划款日模式为固定日期，非固定还款日为提前还款截止日 + 步长<br>
     *      最晚还款日 = 约定还款日+宽限期<br>
     *      约定保安日 = 最晚还款日 + 约定报案日计算值（目前都是1）<br>
     *      未支持资产端等待期和资金端特殊还款日<br>
     */
    public BusinessDateGeneResultDO getDate(BusinessDateGeneDO geneDO) {
        //业务日期
        Date beforeAgreedRepayDate = null;
        Date agreeRepayDate = null;
        Date latestRepayDate = null;
        Date agreedReportDate = null;
        Date trialDate = geneDO.getTrialDate();

        if (geneDO.getRepayWayEnum() == RepayWayEnum.DAILY_INTEREST
                || geneDO.getRepayWayEnum() == RepayWayEnum.FULL_PAYMENT) {
            beforeAgreedRepayDate = trialDate;
            if (geneDO.getStepType() == StepTypeEnum.DAY) {
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addDays(trialDate, geneDO.getStepValue()));
            } else if (geneDO.getStepType() == StepTypeEnum.MONTH) {
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addMonths(trialDate, geneDO.getStepValue()));
            }
        } else if (geneDO.getRepayWayEnum() == RepayWayEnum.EQUAL_AMOUNT_DAILY
                || (geneDO.getRepayWayEnum() == RepayWayEnum.EQUAL_INSTALLMENT
                && geneDO.getInterestCalcWayEnum() == InterestCalcWayEnum.CALC_BY_DAY)) {
            Integer forbid = MoneyUtil.multiply(geneDO.getInstallmentNo() - 1, geneDO.getStepValue()).intValue();
            if (geneDO.getInstallmentNo() == 1) {
                beforeAgreedRepayDate = DateUtil.addMonths(trialDate, forbid);
                agreeRepayDate = geneDO.getFirstRepayDate();
            } else {
                beforeAgreedRepayDate = DateUtil.addMonths(geneDO.getFirstRepayDate(), forbid - 1);
                agreeRepayDate = DateUtil.addMonths(geneDO.getFirstRepayDate(), forbid);
            }
        } else {
            Integer forbid = MoneyUtil.multiply(geneDO.getInstallmentNo() - 1, geneDO.getStepValue()).intValue();
            if (geneDO.getStepType() == StepTypeEnum.DAY) {
                beforeAgreedRepayDate = DateUtil.addDays(trialDate, forbid);
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addDays(trialDate,
                        MoneyUtil.multiply(geneDO.getInstallmentNo(), geneDO.getStepValue()).intValue()));
            } else if (geneDO.getStepType() == StepTypeEnum.MONTH) {
                beforeAgreedRepayDate = DateUtil.addMonths(trialDate, forbid);
                agreeRepayDate = DateUtil.getOneDayEnd(DateUtil.addMonths(trialDate,
                        MoneyUtil.multiply(geneDO.getInstallmentNo(), geneDO.getStepValue()).intValue()));
            }
        }

        //设置最晚还款日
        if (geneDO.getGraceType() == GraceTypeEnum.NATURAL_DAYS) {
            latestRepayDate = DateUtil.addDays(agreeRepayDate, geneDO.getGraceTimeDay());
        } else if (geneDO.getGraceType() == GraceTypeEnum.WEEKDAYS) {
            if (geneDO.getGraceTimeDay() > 0) {
                //对接日历平台
              /*
                ResultBase<Date> resultDate = holidayApiService
                        .getWorkDay(DateUtil.formatDateByYYYYMMDD(agreeRepayDate), geneDO.getGraceTimeDay());
                log.info("resultDate:{}", com.zhongan.creditcore.common.dto.BaseDto.toJson(resultDate));
                latestRepayDate = resultDate.getValue();
                */
            } else {
                latestRepayDate = agreeRepayDate;
            }
        } else {
            latestRepayDate = agreeRepayDate;
        }

        //最晚还款日 == 约定还款日
        agreedReportDate = DateUtil.addDays(latestRepayDate, geneDO.getCalcReportValue());

        //设值
        BusinessDateGeneResultDO resultDO = new BusinessDateGeneResultDO();
        resultDO.setInstallmentNo(geneDO.getInstallmentNo());
        resultDO.setLatestRepayDate(DateUtil.getOneDayEnd(latestRepayDate));
        resultDO.setAgreeRepayDate(DateUtil.getOneDayEnd(agreeRepayDate));
        resultDO.setForbidEarlyRepayDate(DateUtil.getOneDayEnd(beforeAgreedRepayDate));
        resultDO.setAgreedReportDate(DateUtil.getOneDayEnd(agreedReportDate));
        return resultDO;
    }
}
