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

package com.qiwumind.next.components.compute.core.constants;



/**
 * 类TrialConstants.java的实现描述：试算系统常量,用于对接产品平台各种常量
 *
 * @author   2017年3月6日 上午10:30:26
 */
public class TrialInnerConstants {

    /**
     * 点
     */
    public final static Integer MAX_INSTALLMENTNO = 24;

    /**
     * 
     */
    public final static String  INS_PARAM_CHECK_FAIL               = "20000";

    /**
     * 点
     */
    public final static Integer MIN_INSTALLMENTNO = 1;
    /**
     * 点
     */
    public final static String  POINT             = ".";

    /**
     * Y
     */
    public final static String Y = "Y";

    /**
     * N
     */
    public final static String N = "N";

    /**
     * 还款日计算方式
     */
    public final static String REPAYMENTDAY = "REPAYMENT_DAY";

    /**
     * 还款日:每月方式,父节点---->还款日计算
     */
    public final static String TYPE_MONTH = "TYPE_MONTH";

    /**
     * 还款日:D+日方式,父节点---->还款日计算
     */
    public final static String TYPE_DAY = "TYPE_DAY";

    /**
     * 还款日:D+日方式的天数,父节点---->还款日计算
     */
    public final static String CONSUMEDAY = "REPAYMENT_DAY.TYPE_DAY.DATE";

    /**
     * value : M D <br>
     * 还款日: D+日/月 方式的天数,父节点---->还款日计算
     */
    public final static String DATETYPE = "REPAYMENT_DAY.TYPE_DAY.DATE_TYPE";

    /**
     * 还款日:每月方式-第几月开始计算<br>
     * 【父节点:---->每月方式---->还款日计算】
     */
    public final static String REPAY_TYPE_DAY = "REPAYMENT_DAY.TYPE_MONTH.DAY_OF_MONTH";

    /**
     * 还款日:每月方式-每月多少号<br>
     * 【父节点:---->每月方式---->还款日计算】
     */
    public final static String REPAY_TYPE_MONTH = "REPAYMENT_DAY.TYPE_MONTH.MONTH";

    /**
     * 计费方式
     */
    public final static String JXFS = "JXFS";

    /**
     * 随借随还--->父节点为计费方式:JFFS
     */
    public final static String DAILY_INTEREST = "JXFS.BORROW_WITH_REPAY";

    /**
     * 等额本息--->父节点为计费方式:JFFS
     */
    public final static String EQUAL_INSTALLMENT = "JXFS.EQ_CAPITAL_AND_INTEREST";

    /**
     * 等本等息--->父节点为计费方式:JFFS
     */
    public final static String EQUAL_INTEREST = "JXFS.EQ_INTEREST";

    /**
     * 等额本金--->父节点为计费方式:JFFS
     */
    public final static String EQUAL_PRINCIPAL = "JXFS.EQ_CAPITAL";

    /**
     * 先息后本--->父节点为计费方式:JFFS
     */
    public final static String MONTH_INTEREST = "JXFS.FIRST_INTEREST";

    /**
     * 一次性还本付息--->父节点为计费方式:JFFS
     */
    public final static String FULL_PAYMENT = "JXFS.ALL_INTEREST";

    /**
     * 等本等息--->父节点为计费方式:JFFS
     */
    public final static String EQINTEREST = "EQ_INTEREST";

    /**
     * 等额本金--->父节点为计费方式:JFFS
     */
    public final static String EQCAPITAL = "EQ_CAPITAL";

    /**
     * 等额等息--->父节点为计费方式:JFFS
     */
    public final static String EQCAPINTEREST = "EQ_CAPITAL_AND_INTEREST";

    /**
     * 先息后本--->父节点为计费方式:JFFS
     */
    public final static String FIRSTINTEREST = "FIRST_INTEREST";

    /**
     * 一次性还本付息--->父节点为计费方式:JFFS
     */
    public final static String ALLINTERES = "ALL_INTEREST";

    /**
     * 随借随还--->父节点为计费方式:JFFS
     */
    public final static String BORROWWITHREPAY = "BORROW_WITH_REPAY";

    /**
     * 宽限期类型: T为工作日 D为自然日
     */
    public final static String GRACETIME_TYPE = "GRACETIME_TYPE";

    /**
     * 宽限期天数
     */
    public final static String GRACETIME_DAY = "GRACETIME_DAY";

    /**
     * 尾差计息方式:LAST_PERIOD(尾差记末期) FIRST_PERIOD(尾差记首期)
     */
    public final static String TAILDIFFERENCE_TYPE = "TAIL_DIFFERENCE_TYPE";

    /**
     * 工作日标志
     */
    public final static String T = "T";

    /**
     * T(尾差记末期)
     */
    public final static String LAST_PERIOD = "LAST_PERIOD";

    /**
     * F(尾差记首期)
     */
    public final static String FIRST_PERIOD = "FIRST_PERIOD";

    /**
     * 月数表示字符
     */
    public final static String M = "M";

    /**
     * 天数表示字符
     */
    public final static String D = "D";

    /**
     * 一个月单位
     */
    public final static String ONO_MONTH = "1M";

    /**
     * 每日计息服务费率
     */
    public final static String SERVICE_CHARGE_RATE_DAILY_INTEREST = "JXFS.BORROW_WITH_REPAY.SERVICE_RATE";

    /**
     * 等本等息服务费费率
     */
    public final static String SERVICE_CHARGE_RATE_EQ_INTEREST = "SERVICE_FEE_JXFS.EQ_INTEREST.M";

    /**
     * 服务费费率
     */
    public final static String SERVICE_CHARGE_RATE = "SERVICE_FEE_JXFS";

    /**
     * 提前结清违约金
     */
    public final static String EARLY_SETTLEMENT_TYPE = "ADVANCE_SETTLE_PENALTY";

    /**
     * 提前结清违约金不减免
     */
    public final static String NO = "NO";

    /**
     * 提前结清下一期利息作为提前结清违约金
     */
    public final static String DED_LAST_INTEREST_FO_EARLY = "NEXT_PEROID_INTEREST";

    /**
     * 一次性手续费率(父节点为随借随还)
     */
    public final static String ONE_FEE = "JXFS.BORROW_WITH_REPAY.SERVICE_RATE";

    /**
     * 日利率(父节点为随借随还)
     */
    public final static String DAILY_RATE = "JXFS.BORROW_WITH_REPAY.DAY_RATE";

    /**
     * 是否支持指定金额还款
     */
    public final static String IS_SUPPORT_REPAY_BY_AMOUNT = "IS_SUPPORT_SPECIFIED_AMOUNT_REPAYMENT";

    /**
     * 还款冲销逻辑key值RepaymentLogic
     */
    public final static String REPAYMENT_LOGIC = "REPAYMENT_LOGIC";

    /**
     * 是否支持提前还款key值
     */
    public final static String IS_SUPPORT_PREPAYMENT = "IS_SUPPORT_PREPAYMENT";

    /**
     * 还款/退款冲销逻辑:按金额冲销
     */
    public final static String LOGIC_BY_AMOUNT = "BY_AMOUNT";

    /**
     * 还款/退款冲销逻辑:按顺序冲销
     */
    public final static String LOGIC_BY_SEQUENCE = "BY_SEQUENCE";

    /**
     * 还款利息减免规则key值
     */
    public final static String REPAYMENT_INTEREST_WAIVER_RULE = "INTEREST_WAIVER_RULE";

    /**
     * 还款服务费减免规则key值
     */
    public final static String REPAYMENT_CHARGE_WAIVER_RULE = "CHARGE_WAIVER_RULE";

    /**
     * 还款利息减免规则key值,基于资金方维度
     */
    public final static String EARLY_REPAY_MODE = "EARLY_REPAY_MODE";

    /**
     * 还款利息减免规则key值,基于资金方维度
     */
    public final static String EARLY_REPAY_MODE_ALL_REDUCE = "EARLY_REPAY_MODE_ALL_REDUCE";

    /**
     * 还款利息减免规则key值,基于资金方维度
     */
    public final static String EARLY_REPAY_MODE_ALL_NOT_REDUCE = "EARLY_REPAY_MODE_ALL_NOT_REDUCE";

    /**
     * 还款利息减免规则key值,基于资金方维度
     */
    public final static String EARLY_REPAY_MODE_NOW_REDUCE = "EARLY_REPAY_MODE_NOW_REDUCE";

    /**
     * 还款利息减免规则key值,基于资金方维度
     */
    public final static String EARLY_REPAY_MODE_PART_REDUCE = "EARLY_REPAY_MODE_PART_REDUCE";

    /**
     * 还款利息减免规则key值,ALL_REDUCE基于资金方维度
     */
    public final static String ALL_REDUCE = "ALL_REDUCE";

    /**
     * 还款利息减免规则key值,ALL_NOT_REDUCE基于资金方维度
     */
    public final static String ALL_NOT_REDUCE = "ALL_NOT_REDUCE";

    /**
     * 还款利息减免规则key值,NOW_REDUCE基于资金方维度
     */
    public final static String NOW_REDUCE = "NOW_REDUCE";

    /**
     * 还款利息减免规则key值,PART_REDUCE基于资金方维度
     */
    public final static String PART_REDUCE = "PART_REDUCE";

    /**
     * 还款/退款 利息减免规则:全部减免
     */
    public final static String INTEREST_ALL_DERATE = "ALL_DERATE";

    /**
     * 还款/退款 利息减免规则:全部不减免
     */
    public final static String INTEREST_ALL_NOT_DERATE = "ALL_NOT_DERATE";

    /**
     * 还款/退款 利息减免规则:部分减免
     */
    public final static String INTEREST_PART_DERATE = "PART_DERATE";

    /**
     * 还款/退款 利息计算到天
     */
    public final static String AFTER_TODAY_PART_DERATE = "AFTER_TODAY_PART_DERATE";

    /**
     * 还款利息减免规则,减免N期key值
     */
    public final static String REPAYMENT_INTEREST_PART_DERATE_N = "INTEREST_WAIVER_RULE.PART_DERATE.N";

    /**
     * 还款服务费减免规则,减免N期key值
     */
    public final static String CHARGE_WAIVER_RULE_PART_DERATE_N = "CHARGE_WAIVER_RULE.PART_DERATE.N";

    /**
     * 提前结清场景日利率
     */
    public final static String EARLY_SETTLEMENT_DAILY_INTEREST = "INTEREST_WAIVER_RULE.AFTER_TODAY_PART_DERATE.PER";

    /**
     * 是否支持退款
     */
    public final static String IS_SUPPORT_REFUND = "IS_SUPPORT_REFUND";

    /**
     * 退款冲销逻辑
     */
    public final static String REFUND_WRITEOFF_LOGIC = "IS_SUPPORT_REFUND.Y.REFUND_WRITEOFF_LOGIC";

    /**
     * 退款利息减免规则
     */
    public final static String REFUND_INTEREST_LESS_RULE = "IS_SUPPORT_REFUND.Y.REFUND_INTEREST_LESS_RULE";

    /**
     * 是否支持犹豫期退款
     */
    public final static String IS_SUPPORT_REFUND_HESITATE = "IS_SUPPORT_REFUND.Y.IS_SUPPORT_REFUND_HESITATE";

    /**
     * 犹豫期时效
     */
    public final static String DURATION = "IS_SUPPORT_REFUND.Y.IS_SUPPORT_REFUND_HESITATE.Y.DURATION";

    /**
     * 溢缴款处理规则
     */
    public final static String PAYMENT_PROCESSING_RULES = "IS_SUPPORT_REFUND.Y.PAYMENT_PROCESSING_RULES";

    /**
     * 不冲销其他订单
     */
    public final static String NOT_REVERSE_OTHER_ORDER = "NOT_REVERSE_OTHER_ORDER";

    /**
     * 冲销其他订单
     */
    public final static String REVERSE_OTHER_ORDER = "REVERSE_OTHER_ORDER";

    /**
     * 退款部分减免-期数
     */
    public final static String REFUND_INTEREST_PART_DERATE = "IS_SUPPORT_REFUND.Y.REFUND_INTEREST_LESS_RULE.PART_DERATE.AMOUNT";

    /**
     * 产品平台现金贷固定常量
     */
    public final static String XJD = "XJD";

    /**
     * fund固定常量
     */
    public final static String FUND = "fund";

    /**
     * 产品平台消费分期固定常量
     */
    public final static String XFFQ = "XFFQ";

    /**
     * 产品平台卡模式固定常量
     */
    public final static String KMS = "KMS";

    /**
     * 卡模式账单日
     */
    public final static String DEBT_DAY = "DEBT_DAY";

    /**
     * 卡模式账单日/还款日:每月几号
     */
    public final static String DAY_OF_MONTH = "DAY_OF_MONTH";

    /**
     * 卡模式账单日:每月几号的value
     */
    public final static String DAY_OF_MONTH_VALUE = "DEBT_DAY.DAY_OF_MONTH.DATE";

    /**
     * 卡模式还款日配置key
     */
    public final static String MONTH_REPAYMENT_DATE = "MONTH_REPAYMENT_DATE";

    /**
     * 卡模式还款日配置:每月几号
     */
    public final static String REPAYDAY_OF_MONTH_VALUE = "MONTH_REPAYMENT_DATE.DAY_OF_MONTH.DATE";

    /**
     * 固定服务费费率
     */
    public final static String FIXED_SERVICE_RATE = "SERVICE_FEE_JXFS.FIXED_RATE.SERVICE_RATE";

    /**
     * 提前结清违约金配置
     */
    public final static String ADVANCE_SETTLE_PENALTY = "ADVANCE_SETTLE_PENALTY";

    /**
     * 提前结清违约金配置收取下期利息
     */
    public final static String NEXT_PEROID_INTEREST = "NEXT_PEROID_INTEREST";

    /**
     * 提前结清违约金配置收取剩余本金乘以费率
     */
    public final static String CAPITAL_MULTIPLY_RATE = "CAPITAL_MULTIPLY_RATE";

    /**
     * 提前结清违约金配置收取剩余本息生意费率息
     */
    public final static String CAPITAL_INTEREST_MULTIPLY_RATE = "CAPITAL_INTEREST_MULTIPLY_RATE";

    /**
     * 资金配置
     */
    public final static String  NEW_FUND_EDITABLE                  = "NEW_FUND_EDITABLE";

    /**
     * 资金配置根节点
     */
    public final static String  NEW_FUND_EDITABLE_FIELDS           = "NEW_FUND_EDITABLE.FIELDS.";

    /**
     * 资金配置页-资金编码
     */
    public final static String  FUND_TYPE                          = "FUND_TYPE";

    /**
     * 选项
     */
    public final static String  FIELDS                             = "FIELDS";

    /**
     * 信保产品编码
     */
    public final static String  INSURANCE_PRODUCT_CODE             = "INSURANCE_PRODUCT_CODE";

    /**
     * 信保灰度类型-提前结清试算
     */
    public final static String  INSURANCE_EARLY_SETTLEMENT_TRIAL         = "EARLY_SETTLEMENT_TRIAL";

    /**
     * 是否投保
     */
    public final static String  IS_INSURE                          = "IS_INSURE";

    /**
     * 年化费率
     */
    public final static String  YEAR_RATE                          = "YEAR_RATE";

    /**
     * 固定费率
     */
    public final static String  FIXED_RATE                         = "FIXED_RATE";

    /**
     * 还款计划第一期最小天数
     */
    public final static String  FIRST_INSTALMENT_MINI_DAYS       = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST_FRD.FIRST_INSTALMENT_MINI_DAYS";

    /**
     * 如果第一期约定还款日为29、30、31天，则向前平移天数
     */
    public final static String  MOVE_DAYS                         = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST_FRD.MOVE_DAYS";

    /**
     * 利息计算方式
     */
    public final static String  INTEREST_CALC_WAY                  = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY";

    /**
     * 资金方列表
     */
    public final static String  FUND_LIST                          = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.FUND_LIST";

    /**
     * 还款计划第一期最小天数（按日计息）
     */
    public final static String  FIRST_INSTALMENT_MINI_DAYS_BY_DAY  = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.FIRST_INSTALLMENT_MINI_DAYS";

    /**
     * 还款日平移规则
     */
    public final static String  REPAY_DAY_RULES                    = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.REPAY_DAY_RULES";

    /**
     * 如果第一期约定还款日为29、30、31天，则向前平移天数（按日计息）
     */
    public final static String  MOVE_DAYS_BY_DAY                   = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.REPAY_DAY_RULES.END_MONTH_MOVE.MOVE_DAYS";

    /**
     * 多日固定 - 起始日
     */
    public final static String  FIXED_MANY_DAYS_START_DAY          = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.REPAY_DAY_RULES.FIXED_MANY_DAYS.START_DAY";

    /**
     * 多日固定 - 截止日
     */
    public final static String  FIXED_MANY_DAYS_END_DAY            = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.REPAY_DAY_RULES.FIXED_MANY_DAYS.END_DAY";

    /**
     * 多日固定 - 固定日
     */
    public final static String  FIXED_MANY_DAYS_FIXED_DAY          = "INTEREST_MODE.EQ_CAPITAL_AND_INTEREST.INTEREST_CALC_WAY.BY_DAY.REPAY_DAY_RULES.FIXED_MANY_DAYS.FIXED_DAY";

}
