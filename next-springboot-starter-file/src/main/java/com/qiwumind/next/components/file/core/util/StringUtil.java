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

package com.qiwumind.next.components.file.core.util;



import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * String工具类
 *
 * @package common.util
 * @Title StringUtil.java
 * @version 1.0
 */

public class StringUtil {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(final Object pObj) {
        if (pObj == null || "".equals(pObj)) {
            return true;
        }
        
        if (pObj instanceof String) {
            if (((String) pObj).length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否为NotEmpty(!null或元素>0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static boolean isNotEmpty(final Object pObj) {
        return !isEmpty(pObj);
    }

    /**
     * 获取6位验证码
     *
     * @return
     */
    public static String getCaptcha() {
        return getRandomnumber(6);
    }

    /**
     * 获取length位随机码
     *
     * @return
     */
    public static String getRandomnumber(final int length) {
        final StringBuffer sb = new StringBuffer();
        final SecureRandom r = new SecureRandom();
        for (int i = 0; i < length; i++) {
            final int sum = r.nextInt(10);
            sb.append(sum);
        }
        return sb.toString();
    }

    /**
     * 如果object是null,返回"",否则返回该值
     *
     * @param object 对象
     * @return 返回字符串String类型
     */
    public static String getString(final Object object) {
        String str = "";
        if (object != null) {
            str = object.toString();
        }
        return str;
    }

    /**
     * 如果object是null,返回0,,否则返回该值
     *
     * @param object object
     * @return
     */
    public static long getLong(final Object object) {
        long lon = 0;
        if (object != null) {
            lon = Long.valueOf(getString(object));
        }
        return lon;
    }

    /**
     * @param object 数据
     * @param digit 精度<br>
     *            小数的位数
     * @param roundingMode 舍入模式
     * @return 类型
     */
    public static BigDecimal getBigDecimal(final Object object, final int digit, final RoundingMode roundingMode) {
        String str = getString(object);
        if (str == null || "".equals(str.trim())) {
            str = "0";
        }
        BigDecimal bigDecimal = new BigDecimal(str);
        bigDecimal = bigDecimal.setScale(digit, roundingMode);
        return bigDecimal;
    }

    /**
     * 默认采用截取
     *
     * @param object
     * @param digit 精度<br>
     *            小数的位数
     * @return
     */
    public static BigDecimal getBigDecimal(final Object object, final int digit) {
        String str = getString(object);
        if (str == null || "".equals(str.trim())) {
            str = "0";
        }
        BigDecimal bigDecimal = new BigDecimal(str);
        bigDecimal = bigDecimal.setScale(digit, RoundingMode.FLOOR);
        return bigDecimal;
    }

    /**
     * @param object object
     * @return 如果object是null,返回0,,否则返回该值
     */
    public static int getInteger(final Object object) {
        int in = 0;
        if (object != null) {
            in = Integer.valueOf(getString(object));
        }
        return in;
    }

    /**
     * @param list
     * @return
     */
    public static List<Integer> getInteger(final List<String> list) {
        final List<Integer> res = new ArrayList<Integer>();
        CollectionUtils.collect(list, new Transformer() {
            @Override
            public Object transform(final Object o) {
                return getInteger(o);
            }
        }, res);
        return res;
    }

    /**
     * @param list
     * @return
     */
    public static String getString(final Integer[] list) {
        final StringBuffer id = new StringBuffer();
        for (int i = 0; i < list.length; i++) {
            id.append(list[i].toString() + ",");
        }
        return id.toString();
    }

    /**
     * 对字符串加密
     *
     * @param password 字符串
     * @param algorithm 加密方式 MD5，SHA等
     * @return
     */
    public static String encodePassword(final String password, final String algorithm) {
        final byte[] unencodePassword = password.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            logger.info("",e);
            return password;
        }
        md.reset();
        md.update(unencodePassword);
        final byte[] encodePassword = md.digest();
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < encodePassword.length; i++) {
            if ((encodePassword[i] & 0xff) < (0x10)) {
                buf.append("0");
            }
            buf.append(Long.toString(encodePassword[i] & 0xff, 16));
        }
        return buf.toString();
    }
}
