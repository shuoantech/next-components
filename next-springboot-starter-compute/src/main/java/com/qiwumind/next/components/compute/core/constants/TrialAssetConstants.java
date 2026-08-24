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
 * 类AssetConstants.java的实现描述：资产端配置常量
 * 
 * @author songguoxian 2017年6月13日 下午3:36:25
 */
public class TrialAssetConstants {

    /**
     * 模板版本号<br>
     * 兼容老产品,业务分流标记
     */
    public final static Integer TEMPLATE_VERSION                                = 2;

    /**
     * 资产端计息方式
     */
    public final static String  INTEREST_MODE                                   = "INTEREST_MODE";

    /**
     * 当期不满一期或者超过一期是否按日计算利息
     */
    public final static String  CAL_INTEREST_BY_DAY                             = "CAL_INTEREST_BY_DAY";

    /**
     * 计算方式选项：标准，个性化
     */
    public final static String  CAL_OPTIONS                                     = "CAL_OPTIONS";

    /**
     * 标准
     */
    public final static String  STANDARD                                        = "STANDARD";

    /**
     * 个性化
     */
    public final static String  PERSONALIZATION                                 = "PERSONALIZATION";

    /**
     * 个性化利率key
     */
    public final static String  PERSONIZATION_RATE_TABLE                        = "PERSONALIZATION.PERSONIZATION_RATE_TABLE";

    /**
     * 标准费率表
     */
    public final static String  STANDARD_RATE_TABLE                             = "STANDARD.STANDARD_RATE_TABLE";

    /**
     * 选项
     */
    public final static String  FIELDS                                          = "FIELDS";

    /**
     * 标准计息：前缀
     */
    public final static String  FIELDS_PERIODS                                  = ".FIELDS.";

    /**
     * 费率生效时间
     */
    public final static String  RATE_EFFECTIVE_DATE                             = "RATE_EFFECTIVE_DATE";

    /**
     * 费率生效时间
     */
    public final static String  RATE_INVALID_DATE                               = "RATE_INVALID_DATE";

    /**
     * 服务费由前置传入
     */
    public final static String  PRE_PASS_IN                                     = "PRE_PASS_IN";

    /**
     * 服务费:固定比例
     */
    public final static String  FIXED_RATE                                      = "FIXED_RATE";

    /**
     * 服务费:资产端与资金端差额
     */
    public final static String  ASSET_SUB_FUND                                  = "ASSET_SUB_FUND";

    /**
     * 服务费:等于保费
     */
    public final static String  EQUAL_POLICY_FEE                                = "EQUAL_POLICY_FEE";

    /**
     * 随借随还利率key
     */
    public final static String  DAY_INTEREST_RATE                               = "DAY_INTEREST_RATE";

    /**
     * 服务费父节点key值
     */
    public final static String  FIXED_RATE_KEY                                  = "SERVICE_FEE_JXFS.FIXED_RATE.CAL_OPTIONS";

    /**
     * 服务费固定key值
     */
    public final static String  SERVICE_FEE_JXFS                                = "SERVICE_FEE_JXFS.FIXED_RATE.CAL_OPTIONS.STANDARD.STANDARD_RATE_TABLE";

    /**
     * 服务费
     */
    public final static String  SERVICE_RATE                                    = "SERVICE_RATE";

    /**
     * month
     */
    public final static String  MONTH                                           = "MONTH";

    /**
     * day
     */
    public final static String  DAY                                             = "DAY";

    /**
     * 利随本清查询key值
     */
    public final static String  ALL_INTEREST_BY_TIME_CALCULATE                  = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_MONTH_FILEDS              = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_DAY_FILEDS                = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.DAY.DAY_RATE_TABLE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_MONTH_RATE_TABLE          = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.M";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_DAY_RATE_TABLE            = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.DAY.DAY_RATE_TABLE.FIELDS.DAY_RATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_PERSONALIZATION_FILEDS             = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.PERSONALIZATION.BY_TIME_CALCULATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_MONTH_EFFECTIVE_DATE_KEY  = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.RATE_EFFECTIVE_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_DAY_EFFECTIVE_DATE_KEY    = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.DAY.DAY_RATE_TABLE.FIELDS.RATE_EFFECTIVE_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_PERSONALIZATION_EFFECTIVE_DATE_KEY = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.PERSONALIZATION.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.RATE_EFFECTIVE_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_MONTH_INVALID_DATE_KEY    = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.RATE_INVALID_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_STANDARD_DAY_INVALID_DATE_KEY      = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.STANDARD.BY_TIME_CALCULATE.DAY.DAY_RATE_TABLE.FIELDS.RATE_INVALID_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_PERSONALIZATION_INVALID_DATE_KEY   = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.PERSONALIZATION.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.RATE_INVALID_DATE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_PERSONALIZATION_MONTH              = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.PERSONALIZATION.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE";

    /**
     * 
     */
    public final static String  ALL_INTEREST_PERSONALIZATION_MONTH_RATE_KEY     = "INTEREST_MODE.ALL_INTEREST.CAL_OPTIONS.PERSONALIZATION.BY_TIME_CALCULATE.MONTH.MONTH_RATE_TABLE.FIELDS.RATE_CONFIG";

}
