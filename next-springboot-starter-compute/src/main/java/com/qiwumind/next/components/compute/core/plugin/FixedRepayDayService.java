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



import com.qiwumind.next.components.compute.core.dto.ComputeDTO;
import com.qiwumind.next.components.compute.core.dto.GeneRepayDateConfigDTO;
import com.qiwumind.next.components.compute.core.dto.fixedrepayday.FirstRepayDayRuleDO;
import com.qiwumind.next.components.compute.core.dto.fixedrepayday.FixedRepayDayDO;
import com.qiwumind.next.components.compute.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

/**
 * @program: 20200324
 * @Date: 2020/3/14 15:59
 * @Author: HN
 * @Description:  固定还款日计算
 */
@Slf4j
public class FixedRepayDayService {

    /**
     * 生成首期约定还款日 - 月末平移
     *
     * @param fixedRepayDay
     * @param inputDate
     * @param moveDays
     * @param miniDays
     */
    public FixedRepayDayDO endMonthMove(Integer fixedRepayDay, Date inputDate, int moveDays, int miniDays) {
        Date firstRepayDate; //第一期约定还款日

        //调用方未传入固定还款日，则由试算生成
        if (fixedRepayDay == null) {
            //获取当前传入日期的天
            fixedRepayDay = DateUtil.getDay(inputDate, Calendar.DAY_OF_MONTH);

            //固定还款日在[29,30]，则平移
            if (fixedRepayDay >= 29 && fixedRepayDay <= 31) {
                fixedRepayDay -= moveDays;
            }
        }

        //获得第一期约定还款日
        firstRepayDate = calcByMiniDays(inputDate, fixedRepayDay, miniDays);

        FixedRepayDayDO dto = new FixedRepayDayDO();
        dto.setFixedRepayDay(fixedRepayDay);
        dto.setFirstRepayDate(firstRepayDate);
        return dto;

    }

    /**
     * TODO 0714版本之后可删 获取第一个约定还款日 - 固定平移到一天
     *
     * @return
     */
    public FixedRepayDayDO generate(ComputeDTO config) {
        Integer fixedRepayDay;
        //        fixedRepayDay = config.getFixedRepayDay();

        //step.1 计算固定还款日
        int addMonth = 1; //首期约定还款日落到哪月，默认1即下月
        //        if (fixedRepayDay == null) { //如果没有传入固定还款日
        FirstRepayDayRuleDO firstRepayDayRuleDO = config.getFirstRepayDayRuleDO();
        //获取当前传入日期的天
        fixedRepayDay = DateUtil.getDay(config.getTrialDate(), Calendar.DAY_OF_MONTH);

        //固定还款日在[26, 31] 平移到25
        int start, end;
        start = firstRepayDayRuleDO.getPeriod()[0];
        end = firstRepayDayRuleDO.getPeriod()[1];
        if (end < start) { //区间跨月
            if (fixedRepayDay >= start && fixedRepayDay <= 31) { //首期约定还款日次月
                fixedRepayDay = firstRepayDayRuleDO.getFixedRepayDay();
            } else if (fixedRepayDay >= 1 && fixedRepayDay <= end) { //首期约定还款日同月
                fixedRepayDay = firstRepayDayRuleDO.getFixedRepayDay();
                addMonth = 0;
            }
        } else { //区间顺序递增
            if (fixedRepayDay >= start && fixedRepayDay <= end) { //首期约定还款日次月
                fixedRepayDay = firstRepayDayRuleDO.getFixedRepayDay();
            }
        }
        //        }

        //step.2 计算第一期约定还款日
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(config.getTrialDate());
        calendar.set(Calendar.DAY_OF_MONTH, fixedRepayDay);
        Date firstRepayDate = DateUtil.addMonths(calendar.getTime(), addMonth);

        //step.3 设值
        FixedRepayDayDO dto = new FixedRepayDayDO();
        dto.setFixedRepayDay(fixedRepayDay);
        dto.setFirstRepayDate(firstRepayDate);
        return dto;
    }

    /**
     * 获取第一个约定还款日 - 固定平移到一天 固定还款日在[26, 1] 平移到25
     *
     * @return
     */
    public FixedRepayDayDO manyDaysFixed(Date trialDate, Integer userFixedRepayDay, GeneRepayDateConfigDTO configDTO) {

        int trialDay = DateUtil.getDay(trialDate, Calendar.DAY_OF_MONTH); //获取当前传入日期的天
        //1.计算固定还款日
        Integer repayDay = trialDay; //默认取试算当天
        if (userFixedRepayDay == null) {
            if (configDTO.getEndDay() < configDTO.getStartDay()) { //区间跨月
                if (trialDay >= configDTO.getStartDay() && trialDay <= 31) { //首期约定还款日次月
                    repayDay = configDTO.getConfigFixedRepayDay();
                } else if (trialDay >= 1 && trialDay <= configDTO.getEndDay()) { //首期约定还款日同月
                    repayDay = configDTO.getConfigFixedRepayDay();
                }
            } else { //区间顺序递增
                if (trialDay >= configDTO.getStartDay() && trialDay <= configDTO.getEndDay()) { //首期约定还款日次月
                    repayDay = configDTO.getConfigFixedRepayDay();
                }
            }
        } else {
            repayDay = userFixedRepayDay;
        }

        //2.计算在哪个月
        Date firstRepayDate;
        if (configDTO.getMiniDays() == null) { //无第一期最小天数
            int addMonth = 1; //首期约定还款日落到哪月，默认1即下月
            if (configDTO.getEndDay() < configDTO.getStartDay()) { //区间跨月
                if (trialDay >= 1 && trialDay <= configDTO.getEndDay()) { //首期约定还款日同月
                    addMonth = 0;
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trialDate);
            calendar.set(Calendar.DAY_OF_MONTH, repayDay);
            firstRepayDate = DateUtil.addMonths(calendar.getTime(), addMonth);
        } else {
            firstRepayDate = calcByMiniDays(trialDate, repayDay, configDTO.getMiniDays());
        }

        //step.4 设值
        FixedRepayDayDO dto = new FixedRepayDayDO();
        dto.setFixedRepayDay(repayDay);
        dto.setFirstRepayDate(firstRepayDate);
        return dto;
    }

    /**
     * 根据第一期最小天数，获取第一个约定还款日
     *
     * @param trialDate
     * @param fixedRepayDay
     * @param miniDays
     * @return
     */
    public Date calcByMiniDays(Date trialDate, int fixedRepayDay, int miniDays) {
        //得到试算日期当月的固定还款日当天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialDate);
        calendar.set(Calendar.DAY_OF_MONTH, fixedRepayDay);
        Date firstRepayDate = calendar.getTime();

        //计算相距天数 eg. 固定还款日5号，当日27号借款，getDays() < 0，平移到下月5号，如果getDays() < miniDays，则平移到满足条件
        while (DateUtil.getDays(firstRepayDate, trialDate) < miniDays) {
            calendar.add(Calendar.MONTH, 1);
            firstRepayDate = calendar.getTime();
        }

        return firstRepayDate;
    }

    /**
     * 承保要求在一年内，如果第一期大于一个自然月，则被校验
     *
     * @param trialDate
     * @param firstRepayDate
     * @return
     */
    public boolean beyondPolicyPeriod(Date trialDate, Date firstRepayDate) {
        //得到第一期约定还款日的上月同自然日
        Date lastDate = DateUtil.addMonths(firstRepayDate, -1);

        return DateUtil.compare(trialDate, lastDate) < 0 ? true : false;
    }


}
