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

package com.qiwumind.next.components.compute.core.util;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 类MoneyUtil.java的实现描述：金额操作工具类
 */
public class MoneyUtil {

    /**
     * 标度（小数位数）
     */
    public static final int SCALE = 10;

    /**
     * 金钱显示标度（小数位数）
     */
    public static final int MONEYSHOWSCALE = 2;

    /**
     * 利率显示标度（小数位数）
     */
    public static final int INTERESTRATESHOWSCALE = 4;

    /**
     * 精度
     */
    public static final int PRECISION = 30;

    /**
     * 保存舍入规则
     */
    public static final RoundingMode SAVEROUNDINGMODE = RoundingMode.HALF_UP;

    /**
     * 是否舍去小数点最后的零
     */
    public static boolean STRIPTRAILINGZEROS = true;


    /**
     * 每年天数
     */
    public static final String YEARDAYS = "360";

    /**
     * 每年月数
     */
    public static final String YEARMOTHS = "12";

    /**
     * 每月天数
     */
    public static final String MOTHDAYS = "30";

    /**
     * 数字“1”
     */
    public static final BigDecimal ONE = new BigDecimal("1");

    /**
     * 数字“100”
     */
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * 数字“0.01”
     */
    public static final BigDecimal ONEHUNDREDTH = new BigDecimal("0.01");

    private MoneyUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 单元为“元”的金额转化成单位为“分”<br>
     * 金额 10.32 ——》1032
     *
     * @param yuan 单元为“元”的金额
     * @return
     */
    public static String toCent(final String yuan) {
        return String.valueOf(new Money(yuan).getCent());
    }

    /**
     * 分转换为元
     *
     * @param fen
     * @return
     */
    public static BigDecimal fen2Yuan(final BigDecimal fen) {
        if (fen == null) {
            return null;
        }
        return fen.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 大写数字
     */
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    /**
     * 整数部分的单位
     */
    private static final String[] IUNIT = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾",
            "佰", "仟"};
    /**
     * 小数部分的单位
     */
    private static final String[] DUNIT = {"角", "分", "厘"};

    /**
     * 得到大写金额。
     */
    public static String toChinese(String str) {
        str = str.replaceAll(",", "");// 去掉","
        String integerStr;// 整数部分数字
        String decimalStr;// 小数部分数字

        // 初始化：分离整数部分和小数部分
        if (str.indexOf(".") > 0) {
            integerStr = str.substring(0, str.indexOf("."));
            decimalStr = str.substring(str.indexOf(".") + 1);
        } else if (str.indexOf(".") == 0) {
            integerStr = "";
            decimalStr = str.substring(1);
        } else {
            integerStr = str;
            decimalStr = "";
        }
        // integerStr去掉首0，不必去掉decimalStr的尾0(超出部分舍去)
        if (!integerStr.equals("")) {
            integerStr = Long.toString(Long.parseLong(integerStr));
            if (integerStr.equals("0")) {
                integerStr = "";
            }
        }
        // overflow超出处理能力，直接返回
        if (integerStr.length() > IUNIT.length) {
            System.out.println(str + ":超出处理能力");
            return str;
        }

        int[] integers = toArray(integerStr);// 整数部分数字
        boolean isMust5 = isMust5(integerStr);// 设置万单位
        int[] decimals = toArray(decimalStr);// 小数部分数字
        return getChineseInteger(integers, isMust5) + getChineseDecimal(decimals);
    }

    /**
     * 整数部分和小数部分转换为数组，从高位至低位
     */
    private static int[] toArray(String number) {
        int[] array = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            array[i] = Integer.parseInt(number.substring(i, i + 1));
        }
        return array;
    }

    /**
     * 得到中文金额的整数部分。
     */
    private static String getChineseInteger(int[] integers, boolean isMust5) {
        StringBuffer chineseInteger = new StringBuffer("");
        int length = integers.length;
        for (int i = 0; i < length; i++) {
            // 0出现在关键位置：1234(万)5678(亿)9012(万)3456(元)
            // 特殊情况：10(拾元、壹拾元、壹拾万元、拾万元)
            String key = "";
            if (integers[i] == 0) {
                if ((length - i) == 13) // 万(亿)(必填)
                    key = IUNIT[4];
                else if ((length - i) == 9) // 亿(必填)
                    key = IUNIT[8];
                else if ((length - i) == 5 && isMust5) // 万(不必填)
                    key = IUNIT[4];
                else if ((length - i) == 1) // 元(必填)
                    key = IUNIT[0];
                // 0遇非0时补零，不包含最后一位
                if ((length - i) > 1 && integers[i + 1] != 0)
                    key += NUMBERS[0];
            }
            chineseInteger.append(integers[i] == 0 ? key : (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
        }
        return chineseInteger.toString();
    }

    /**
     * 得到中文金额的小数部分。
     */
    private static String getChineseDecimal(int[] decimals) {
        StringBuffer chineseDecimal = new StringBuffer("");
        for (int i = 0; i < decimals.length; i++) {
            // 舍去3位小数之后的
            if (i == 3)
                break;
            chineseDecimal.append(decimals[i] == 0 ? "" : (NUMBERS[decimals[i]] + DUNIT[i]));
        }
        return chineseDecimal.toString();
    }

    /**
     * 判断第5位数字的单位"万"是否应加。
     */
    private static boolean isMust5(String integerStr) {
        int length = integerStr.length();
        if (length > 4) {
            String subInteger = "";
            if (length > 8) {
                // 取得从低位数，第5到第8位的字串
                subInteger = integerStr.substring(length - 8, length - 4);
            } else {
                subInteger = integerStr.substring(0, length - 4);
            }
            return Integer.parseInt(subInteger) > 0;
        } else {
            return false;
        }
    }

    /**
     * 把null当作0处理
     */
    public static BigDecimal null2Zero(Number amount) {
        if (amount == null)
            return BigDecimal.ZERO;

        if (amount instanceof BigDecimal) {
            return (BigDecimal) amount;
        } else {
            return new BigDecimal(amount.toString());
        }
    }

    /**
     * 判断金额是否为“零”值
     */
    public static boolean isZero(Number amount) {
        return null2Zero(amount).compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 判断金额是否大于零
     */
    public static boolean greaterThanZero(Number amount) {
        return null2Zero(amount).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 取两个数字的最大值
     */
    public static BigDecimal max(Number amount1, Number amount2) {
        if (null2Zero(amount1).compareTo(null2Zero(amount2)) > 0) {
            return null2Zero(amount1);
        } else {
            return null2Zero(amount2);
        }
    }

    /**
     * 判断金额是否小于零
     */
    public static boolean lessThanZero(Number amount) {
        return null2Zero(amount).compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 两个金额比较
     */
    public static int compare(Number m1, Number m2) {
        return null2Zero(m1).compareTo(null2Zero(m2));
    }

    /**
     * 多个金额相加，至少2个参数
     */
    public static BigDecimal add(Number amount1, Number amount2, Number... amounts) {
        BigDecimal result = null2Zero(amount1).add(null2Zero(amount2));
        for (Number amount : amounts) {
            result = result.add(null2Zero(amount));
        }
        return result;
    }

    /**
     * 多个金额相乘，至少2个参数，不处理精度
     */
    public static BigDecimal multiply(Number amount1, Number amount2, Number... amounts) {
        BigDecimal result = null2Zero(amount1).multiply(null2Zero(amount2));
        for (Number amount : amounts) {
            result = result.multiply(null2Zero(amount));
        }
        return result;
    }

    /**
     * 多个金额相乘，至少2个参数，四舍五入保留两位小数
     */
    public static BigDecimal multiplyWithScale(Number amount1, Number amount2, Number... amounts) {
        BigDecimal result = null2Zero(amount1).multiply(null2Zero(amount2));
        for (Number amount : amounts) {
            result = result.multiply(null2Zero(amount));
        }
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result;
    }

    /**
     * 金额相减
     *
     * @param amount1
     * @param amount2
     * @return
     */
    public static BigDecimal subtract(Number amount1, Number amount2) {
        return null2Zero(amount1).subtract(null2Zero(amount2));
    }

    /**
     * 对两个金额做除法（不丢失精度），四舍五入保留指定位数的小数
     *
     * @param src
     * @param dest
     * @param scale 保留位数
     */
    public static BigDecimal divide(Number src, Number dest, int scale) {
        BigDecimal srcDeci = null2Zero(src);
        BigDecimal destDeci = null2Zero(dest);
        return srcDeci.divide(destDeci, scale, RoundingMode.HALF_UP);
    }

    /**
     * 金额单位元转分
     */
    public static BigDecimal yuan2Fen(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.movePointRight(2).setScale(0, BigDecimal.ROUND_DOWN);
    }

    /**
     * 金额单位分转元
     */
    public static BigDecimal fen2yuan(BigDecimal amount) {
        return null2Zero(amount).movePointLeft(2).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 检查是否是正确的资金金额
     *
     * @param val
     * @return
     */
    public static boolean checkValidAmount(BigDecimal val) {
        if (null == val) {
            return true;
        }

        BigDecimal result = val.setScale(2, RoundingMode.DOWN);
        return result.compareTo(val) == 0;
    }

    /**
     * NPV公式，用于计算现值PV，保留6位小数，四舍五入
     *
     * @param perNCF     每期的净现金流(除最后一期外)
     * @param perRate    收益率
     * @param periods    期数
     * @param lastPerNCF 最后一期的净现金流
     * @return
     */
    public static BigDecimal calPv(BigDecimal perNCF, BigDecimal perRate, int periods, BigDecimal lastPerNCF) {
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 1; i <= periods - 1; i++) {
            result = result.add(perNCF.divide(perRate.add(BigDecimal.ONE).pow(i), 8, RoundingMode.DOWN));
        }
        result = result.add(lastPerNCF.divide(perRate.add(BigDecimal.ONE).pow(periods), 8, RoundingMode.DOWN));
        result = result.divide(new BigDecimal(1), 6, RoundingMode.HALF_UP);
        return result;
    }

    /**
     * PMT公式，用于计算每期净现金流NCF，保留6位小数，四舍五入
     *
     * @param firstFlow 期初现金流出
     * @param perRate   收益率
     * @param periods   期数
     * @return
     */
    public static BigDecimal calEachNcf(BigDecimal firstFlow, BigDecimal perRate, int periods) {
        BigDecimal divisor = firstFlow.multiply(perRate.add(BigDecimal.ONE).pow(periods));
        BigDecimal dividend = BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < periods; i++) {
            dividend = dividend.add(perRate.add(BigDecimal.ONE).pow(i));
        }
        result = divisor.divide(dividend, 6, RoundingMode.HALF_UP);
        return result;
    }

    /**
     * 计算第N期的终值（前提：前几期每期的净现金流必须相等）,保留6位小数，四舍五入
     *
     * @param PV         未来收益的现值
     * @param preEachNcf 前几期每期的净现金流
     * @param perRate    收益率
     * @param periods    当前的期数
     * @return
     */
    public static BigDecimal calFvByPV(BigDecimal PV, BigDecimal preEachNcf, BigDecimal perRate, int periods) {
        BigDecimal result = PV.multiply(perRate.add(BigDecimal.ONE).pow(periods));
        for (int i = 1; i < periods; i++) {
            result = result.subtract(preEachNcf.multiply(perRate.add(BigDecimal.ONE).pow(i)));
        }
        result = result.divide(new BigDecimal(1), 6, RoundingMode.HALF_UP);
        return result;
    }

    /**
     * 检查分期金额是否合法
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public static boolean checkPeriodAmount(BigDecimal arg0, Integer arg1) {

        if (arg1 > 0) {
            BigDecimal periodAmount = divide(arg0, new BigDecimal(arg1), 2);

            return (periodAmount.compareTo(BigDecimal.ZERO) > 0);
        }

        return true;
    }

    /**
     * 金额精度检查
     *
     * @param val
     * @param size
     * @return
     */
    public static boolean checkAmount(BigDecimal val, int size) {

        if (val == null || val.toString().split("\\.").length <= 1) {

            return false;
        }

        if (val.toString().split("\\.")[1].length() == size) {
            return true;
        }

        return false;
    }

    /**
     * 解析百分数
     *
     * @param percent
     * @return
     */

    public static BigDecimal parsePercent(String percent, Integer mode) {
        return StringUtils.isEmpty(percent) ? BigDecimal.ZERO : new BigDecimal(percent).movePointLeft(2).setScale(2,
                mode);
    }

    /**
     * 解析费率
     *
     * @param percent
     * @return
     */
    public static BigDecimal parseRate(String percent, Integer scale, Integer mode) {
        return StringUtils.isEmpty(percent) ? BigDecimal.ZERO : new BigDecimal(percent).movePointLeft(2).setScale(scale,
                mode);
    }

    public static BigDecimal parseRate(String percent) {
        return parseRate(percent, 8, BigDecimal.ROUND_HALF_UP);
    }


    public static BigDecimal newBigDecimal(String str) {
        return (str == null || str.trim().isEmpty()) ? BigDecimal.ZERO : new BigDecimal(str);
    }

    /**
     * <p>Description: 加法返回格式化结果数字</p>
     *
     * @param addend
     * @param augend
     * @return
     */
    public static BigDecimal add(BigDecimal addend, BigDecimal augend) {
        return formatMoney(addend.add(augend));
    }

    /**
     * <p>Description: 加法返回格式化结果数字</p>
     *
     * @param addend
     * @param augend
     * @return
     */
    public static BigDecimal add(String addend, String augend) {
        BigDecimal decimalAddend = newBigDecimal(addend);
        BigDecimal decimalAugend = newBigDecimal(augend);
        return formatMoney(decimalAddend.add(decimalAugend));
    }

    /**
     * <p>Description: 加法返回格式化结果字符串</p>
     *
     * @param addend
     * @param augend
     * @return
     */
    public static String addToString(BigDecimal addend, BigDecimal augend) {
        return formatToString(addend.add(augend));
    }

    /**
     * <p>Description: 加法返回格式化结果字符串</p>
     *
     * @param addend
     * @param augend
     * @return
     */
    public static String addToString(String addend, String augend) {
        BigDecimal decimalAddend = newBigDecimal(addend);
        BigDecimal decimalAugend = newBigDecimal(augend);
        return formatToString(decimalAddend.add(decimalAugend));
    }

    /**
     * <p>Description: 减法返回格式化结果数字</p>
     *
     * @param minuend
     * @param subtrahend
     * @return
     */
    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
        return formatMoney(minuend.subtract(subtrahend));
    }

    /**
     * <p>Description: 减法返回格式化结果数字</p>
     *
     * @param minuend
     * @param subtrahend
     * @return
     */
    public static BigDecimal subtract(String minuend, String subtrahend) {
        BigDecimal decimalMinuend = newBigDecimal(minuend);
        BigDecimal decimalSubtrahend = newBigDecimal(subtrahend);
        return formatMoney(decimalMinuend.subtract(decimalSubtrahend));
    }

    /**
     * <p>Description: 减法返回格式化结果字符串</p>
     *
     * @param minuend
     * @param subtrahend
     * @return
     */
    public static String subtractToString(BigDecimal minuend, BigDecimal subtrahend) {
        return formatToString(minuend.subtract(subtrahend));
    }

    /**
     * <p>Description: 减法返回格式化结果字符串</p>
     *
     * @param minuend
     * @param subtrahend
     * @return
     */
    public static String subtractToString(String minuend, String subtrahend) {
        BigDecimal decimalMinuend = newBigDecimal(minuend);
        BigDecimal decimalSubtrahend = newBigDecimal(subtrahend);
        return formatToString(decimalMinuend.subtract(decimalSubtrahend));
    }

    /**
     * <p>Description: 乘法返回格式化结果数字</p>
     *
     * @param multiplier
     * @param multiplicand
     * @return
     */
    public static BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand) {
        return formatMoney(multiplier.multiply(multiplicand));
    }

    /**
     * <p>Description: 乘法返回格式化结果数字</p>
     *
     * @param multiplier
     * @param multiplicand
     * @return
     */
    public static BigDecimal multiply(String multiplier, String multiplicand) {
        BigDecimal decimalMultiplier = newBigDecimal(multiplier);
        BigDecimal decimalMultiplicand = newBigDecimal(multiplicand);
        return formatMoney(decimalMultiplier.multiply(decimalMultiplicand));
    }

    /**
     * <p>Description: 乘法返回格式化结果字符串</p>
     *
     * @param multiplier
     * @param multiplicand
     * @return
     */
    public static String multiplyToString(BigDecimal multiplier, BigDecimal multiplicand) {
        return formatToString(multiplier.multiply(multiplicand));
    }

    /**
     * <p>Description: 乘法返回格式化结果字符串</p>
     *
     * @param multiplier
     * @param multiplicand
     * @return
     */
    public static String multiplyToString(String multiplier, String multiplicand) {
        BigDecimal decimalMultiplier = newBigDecimal(multiplier);
        BigDecimal decimalMultiplicand = newBigDecimal(multiplicand);
        return formatToString(decimalMultiplier.multiply(decimalMultiplicand));
    }

    /**
     * <p>Description: 除法 返回格式化结果数字</p>
     *
     * @param pidend
     * @param pisor
     * @return
     */
    public static BigDecimal pide(BigDecimal pidend, BigDecimal pisor) {
        return formatMoney(pidend.divide(pisor));
    }

    /**
     * <p>Description: 除法返回格式化结果数字</p>
     *
     * @param pidend
     * @param pisor
     * @return
     */
    public static BigDecimal divide(String pidend, String pisor) {
        BigDecimal decimalpidend = new BigDecimal(pidend);
        BigDecimal decimalpisor = new BigDecimal(pisor);
        return formatMoney(decimalpidend.divide(decimalpisor));
    }

    /**
     * <p>Description: 除法返回格式化结果字符串</p>
     *
     * @param pidend
     * @param pisor
     * @return
     */
    public static String pideToString(BigDecimal pidend, BigDecimal pisor) {
        return formatToString(formatMoney(pidend.divide(pisor)));
    }


    /**
     * <p>Description: 按既定小数位数格式化金额保存</p>
     *
     * @param result
     * @return
     */
    public static BigDecimal formatMoney(BigDecimal result) {
        return result.setScale(SCALE, SAVEROUNDINGMODE);
    }

    /**
     * <p>Description: 按既定小数位数格式化金额显示</p>
     *
     * @param resultStr 要格式化的数
     * @param multiple  乘以的倍数
     * @return
     */
    public static String formatMoneyToShow(String resultStr, BigDecimal multiple) {
        BigDecimal result = newBigDecimal(resultStr);
        return formatToString(formatMoneyToShow(result, multiple));
    }

    /**
     * <p>Description: 按既定小数位数格式化金额显示</p>
     *
     * @param result   要格式化的数
     * @param multiple 乘以的倍数
     * @return
     */
    public static BigDecimal formatMoneyToShow(BigDecimal result, BigDecimal multiple) {
        return result.multiply(multiple).setScale(MONEYSHOWSCALE, SAVEROUNDINGMODE);
    }

    /**
     * <p>Description: 按既定小数位数格式化利率显示</p>
     *
     * @param result   要格式化的数
     * @param multiple 乘以的倍数
     * @return
     */
    public static BigDecimal formatInterestRateToShow(BigDecimal result, BigDecimal multiple) {
        return result.multiply(multiple).setScale(INTERESTRATESHOWSCALE, SAVEROUNDINGMODE);
    }

    /**
     * <p>Description: 按既定小数位数格式化显示</p>
     *
     * @param result 要格式化的数
     * @param scale  显示标度（小数位数）
     * @return
     */
    public static BigDecimal formatToShow(BigDecimal result, int scale) {
        return result.setScale(scale, SAVEROUNDINGMODE);
    }

    /**
     * <p>Description: 格式化为字符串，进行去零不去零操作</p>
     *
     * @param result
     * @return
     */
    public static String formatToString(BigDecimal result) {
        if (result == null) {
            return "";
        } else {
            return STRIPTRAILINGZEROS ? result.stripTrailingZeros().toPlainString() : result.toPlainString();
        }
    }

    /**
     * <p>Description: 按既定小数位数格式化为货币格式</p>
     *
     * @param result
     * @return
     */
    public static String formatToCurrency(BigDecimal result) {
        BigDecimal temp = result.divide(HUNDRED, SAVEROUNDINGMODE);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        return numberFormat.format(STRIPTRAILINGZEROS ? temp.stripTrailingZeros() : temp);
    }

    public static String formatToPercent(BigDecimal result) {
        BigDecimal temp = result.divide(HUNDRED, SAVEROUNDINGMODE);
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        return numberFormat.format(STRIPTRAILINGZEROS ? temp.stripTrailingZeros() : temp);
    }

    /**
     * <p>Description:格式化数字为千分位显示； </p>
     *
     * @param text
     * @return
     */
    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0.00");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }
}
