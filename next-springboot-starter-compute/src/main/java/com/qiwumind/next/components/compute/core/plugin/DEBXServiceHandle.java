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

//package com.qiwumind.next.components.compute.core.plugin;
//
//import com.google.common.collect.Lists;
//import com.qiwumind.next.components.compute.core.util.ComputeUtil;
//import com.qiwumind.next.components.compute.core.util.MoneyUtil;
//import org.apache.commons.lang3.time.DateUtils;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * 等额本息计算
// *
// * @author 2017年4月17日 下午8:48:03
// */
//public class DEBXServiceHandle   {
//
//    /**
//     * <p>
//     * &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
//     * &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;贷款本金×月利率×（1＋月利率）＾还款月数 <br/>
//     * 每月还款总额= --------------------------------------------- <br/>
//     * &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
//     * &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;（1＋月利率）＾还款月数－1 <br/>
//     * <br/>
//     * <br/>
//     * Description: 每月还款总额。〔贷款本金×月利率×（1＋月利率）＾还款月数〕÷〔（1＋月利率）＾还款月数－1〕
//     * </p>
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param count 期数
//     * @return
//     */
//    public BigDecimal monthlyRepayment(BigDecimal principal, BigDecimal monthlyInterestRate, int count) {
//        //（1＋月利率）＾还款月数
//        BigDecimal temp = monthlyInterestRate.add(MoneyUtil.ONE).pow(count);
//
//        return principal.multiply(monthlyInterestRate).multiply(temp).divide(temp.subtract(MoneyUtil.ONE),
//                MoneyUtil.SCALE, MoneyUtil.SAVEROUNDINGMODE);
//    }
//
//    /**
//     * <p>
//     * Description: 还款总利息。贷款总利息=每月还款额*分期数-贷款总额
//     * </p>
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param installmentNo 还款期数
//     * @return
//     */
//    public BigDecimal totalInterest(BigDecimal principal, BigDecimal monthlyInterestRate, int installmentNo) {
//        BigDecimal totalPrincipal = this.totalPrincipal(principal, monthlyInterestRate, installmentNo);
//
//        return totalPrincipal.subtract(principal);
//    }
//
//    /**
//     * 还款总金额（含有本金+利息）
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param installmentNo 还款期数
//     * @return
//     */
//    public BigDecimal totalPrincipal(BigDecimal principal, BigDecimal monthlyInterestRate, int installmentNo) {
//        //每月还款额
//        BigDecimal monthlyRepayment = this.monthlyRepayment(principal, monthlyInterestRate, installmentNo);
//
//        return new BigDecimal(installmentNo).multiply(monthlyRepayment);
//    }
//
//    /**
//     * <p>
//     * Description: 月还款本金。已经精确到分位，未做单位换算
//     * </p>
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param monthlyRepayment 月还款额
//     * @param number 当前期数
//     * @return
//     */
//    public BigDecimal monthlyPrincipal(BigDecimal principal, BigDecimal monthlyInterestRate,
//                                       BigDecimal monthlyRepayment, int number) {
//        BigDecimal monthlyInterest = this.monthlyInterest(principal, monthlyInterestRate, monthlyRepayment, number);
//        //月还款额-月还款利息
//        return monthlyRepayment.subtract(monthlyInterest).setScale(MoneyUtil.MONEYSHOWSCALE,
//                MoneyUtil.SAVEROUNDINGMODE);
//    }
//
//    /**
//     * <p>
//     * 月还款利息。（贷款本金×月利率-月还款额）*（1+月利率)^（当前期数-1）+月还款额
//     * </p>
//     * 等同于 每月应还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param monthlyRepayment 月还款额
//     * @param number 当前期数
//     * @return
//     */
//    public BigDecimal monthlyInterest(BigDecimal principal, BigDecimal monthlyInterestRate, BigDecimal monthlyRepayment,
//                                      int number) {
//        //（1+月利率)^（当前期数-1）
//        BigDecimal temp = monthlyInterestRate.add(MoneyUtil.ONE).pow(number - 1);
//        return principal.multiply(monthlyInterestRate).subtract(monthlyRepayment).multiply(temp).add(monthlyRepayment);
//    }
//
//    /**
//     * <p>
//     * 每月应还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
//     * </p>
//     * 等同于 月还款利息。（贷款本金×月利率-月还款额）*（1+月利率)^（当前期数-1）+月还款额
//     *
//     * @param principal 贷款本金
//     * @param monthlyInterestRate 月利率
//     * @param monthlyRepayment 月还款额
//     * @param number 当前期数
//     * @return
//     */
//    public BigDecimal monthlyInterest(BigDecimal principal, BigDecimal monthlyInterestRate, int installmentNo,
//                                      int currentinstallmentNo) {
//
//        BigDecimal temp = monthlyInterestRate.add(MoneyUtil.ONE).pow(installmentNo);
//        BigDecimal temp2 = monthlyInterestRate.add(MoneyUtil.ONE).pow(currentinstallmentNo - 1);
//
//        return principal.multiply(monthlyInterestRate).multiply(temp.subtract(temp2))
//                .divide(temp.subtract(MoneyUtil.ONE), 2, MoneyUtil.SAVEROUNDINGMODE);
//    }
//
//    /**
//     * <p>
//     * 月还款本金=月还款总额-月还款利息 。 已经精确到分位，未做单位换算
//     * </p>
//     * 等同于 每月应还本金=贷款本金×月利率×(1 +月利率)^(还款月序号-1)÷〔(1 +月利率)^还款月数-1〕
//     *
//     * @param monthlyRepayment 月还款总额
//     * @param monthInterest 月还款利息
//     * @return
//     */
//    public BigDecimal monthPrincipal(BigDecimal monthlyRepayment, BigDecimal monthInterest) {
//        //月还款总额-月还款利息
//        return monthlyRepayment.subtract(monthInterest).setScale(MoneyUtil.MONEYSHOWSCALE,
//                MoneyUtil.SAVEROUNDINGMODE);
//    }
//
//    /**
//     * <p>
//     * 每月应还本金=贷款本金×月利率×(1 +月利率)^(还款月序号-1)÷〔(1 +月利率)^还款月数-1〕 。 已经精确到分位，未做单位换算
//     * </p>
//     * 等同于 月还款本金=月还款总额-月还款利息
//     *
//     * @param monthlyRepayment 月还款总额
//     * @param monthInterest 月还款利息
//     * @return
//     */
//    public BigDecimal monthPrincipal(BigDecimal principal, BigDecimal monthlyInterestRate, int installmentNo,
//                                     int currentinstallmentNo) {
//        //月还款总额-月还款利息
//
//        BigDecimal temp = monthlyInterestRate.add(MoneyUtil.ONE).pow(installmentNo);
//        BigDecimal tempcurrentmonth = monthlyInterestRate.add(MoneyUtil.ONE).pow(currentinstallmentNo - 1);
//
//        return principal.multiply(monthlyInterestRate).multiply(tempcurrentmonth).divide(temp.subtract(MoneyUtil.ONE),
//                2, MoneyUtil.SAVEROUNDINGMODE);
//
//    }
//
//    /**
//     * * 等额本息算法 计算公式
//     *
//     * @param loanAmt 借款金额
//     * @param expire 借款期限
//     * @param rate 利率
//     * @param loanDate 借款日期 (用于计算到期还款日)
//     * @param step 单期步长
//     * @param stepUnit 步长单位(D, M, Y)
//     * @param insterestStartDate 起息日
//     * @return 返回值出去之后, 调用者需要自行补足 repay plan的上下文,如:补足,资产ID,生成还款计划ID等
//     */
//    @Override
//    public List<RepayPlanDTO.RepayPlan> acpiPlanGenerator(final BigDecimal loanAmt, final int expire,
//                                                          final BigDecimal rate, final Date loanDate, final int step,
//                                                          final String stepUnit, final Date insterestStartDate) {
//
//        final List<RepayPlanDTO.RepayPlan> res = Lists.newArrayList();
//        Date nextDate = loanDate;
//        //起息日
//        Date interestStartDate = DateUtils.addDays(insterestStartDate, 0);
//        Date stopInterestDate = null;
//
//        /** 计算每月还款额 */
//        BigDecimal repayAmt = this.monthlyRepayment(loanAmt, rate, expire);
//        //四舍五入
//        repayAmt = repayAmt.setScale(2, MoneyUtil.SAVEROUNDINGMODE);
//
//        for (int i = 1; i <= expire; i++) {
//            /** 计算当期利息 */
//            final BigDecimal interest = this.monthlyInterest(loanAmt, rate, repayAmt, i);
//            /** 计算当期本金 */
//            final BigDecimal principal = repayAmt.subtract(interest);
//            /** 计算当期到期还款日 */
//            nextDate = ComputeUtil.getAgreementRepayDate(loanDate, nextDate, stepUnit, step);
//            stopInterestDate = DateUtils.addDays(nextDate, -1);
//            final RepayPlanDTO.RepayPlan repayPlan = new RepayPlanDTO.RepayPlan();
//            repayPlan.setActualRepaymentDate(null);
//            repayPlan.setLoanOrderNo(null);
//            repayPlan.setPaidInterest(null);
//            repayPlan.setPaidPenalty(null);
//            repayPlan.setPaidPrincipal(null);
//            repayPlan.setPenalty(null);
//            repayPlan.setRepaymentPlanNo(null);
//            repayPlan.setRepayStatus(null);
//            repayPlan.setStopInterestDate(stopInterestDate);
//            repayPlan.setAgreedRepaymentDate(nextDate);
//            repayPlan.setInterest(interest);
//            repayPlan.setPrincipal(principal);
//            repayPlan.setInstallmentNo(i);
//            repayPlan.setTotalInstallmentNo(expire);
//            //起息日=上一期约定还款日
//            repayPlan.setInterestStartDate(interestStartDate);
//            interestStartDate = DateUtils.addDays(nextDate, 0);
//            res.add(repayPlan);
//        }
//
//        //处理精度
//        final RoundAccuracyService accuracy = new RoundAccuracyService(2, loanAmt);
//        accuracy.dealAccuracy(res);
//        return res;
//    }
//
//}
