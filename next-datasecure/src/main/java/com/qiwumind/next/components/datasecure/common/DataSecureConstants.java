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

package com.qiwumind.next.components.datasecure.common;



import java.text.SimpleDateFormat;

/**
 * 类DataSecureConstants.java的实现描述：常量类
 * 
 */
public class DataSecureConstants {
    /** 标识符(半角逗号) */
    public final static String IDENTIFIER_A_COMMA = ",";

    /** 标识符 (*号) */
    public static final String IDENTIFIER_STAR = "*";

    /** 标识符 (@号) */
    public static final String IDENTIFIER_AT = "@";

    /** 双引号(") */
    public static final String DOUBLE_QUOTATION = "\"";

    /** 组JSON用(":") */
    public static final String MARK_JSON = "\":\"";

    /** 左中括号([) */
    public static final char LEFT_BRACKET = '[';

    /** 右中括号([) */
    public static final char RIGHT_BRACKET = ']';

    /** 左大括号({) */
    public static final String LEFT_BRACE = "{";

    /** 等于号 */
    public static final String MARK_EQUAL = "=";

    /**
     * 手机号正则表达式:
     * <P>
     * 13[0-9], 14[5,7], 15[0, 1, 2, 3, 5, 6, 7, 8, 9], 17[0, 1, 6, 7, 8],
     * 18[0-9]
     * </p>
     * <P>
     * 移动号段:
     * 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,
     * 184,187,188
     * </p>
     * <P>
     * 联通号段: 130,131,132,145,152,155,156,170,171,176,185,186
     * </p>
     * <P>
     * 电信号段: 133,134,153,170,177,180,181,189
     * </p>
     */
    public final static String REGEX_PHONENO  = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$";
    /** 时分秒格式(HHmmss)正则 */
    public final static String REGEX_HHMMSS   = "^(?:[01]\\d|2[0-3])(?:[0-5]\\d){2}$";
    /** 年月日格式(YYYYMMDD)正则 */
    public final static String REGEX_YYYYMMDD = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
    /** JSON格式key:value正则 */
    // public final static String REGEX_JSON     = "\"({0})\":\"([^\"]+?)\"";
    public final static String REGEX_JSON     = "\\\\*\"({0})\\\\*\":\\\\*\"([^\"\\\\]+)\\\\*\"";

    // 等于符表示的正则表达式
    public final static String REGEX_EQUAL = "([^,\\s\\d-\\[\\]&?{}][\\s*\\w]+?)=([^,\"\\*\\[\\]{}<]\\s*\\w+)";

    /** list-->json 正则 */
    public final static String REGEX_LIST = "\\\\*\"([^\"\\\\]+)\\\\*\"";

    // 数据库记录中是否加密这段名
    public final static String IS_ENCRYPT       = "IS_ENCRYPT";
    // 加密存储开关情况(1:开，0:关）
    public final static String IS_ENCRYPT_OPEN  = "1";
    // 加密存储开关情况(1:开，0:关）
    public final static String IS_ENCRYPT_CLOSE = "0";

    public final static ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
}
