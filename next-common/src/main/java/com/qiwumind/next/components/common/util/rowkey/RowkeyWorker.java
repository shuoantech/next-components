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

package com.qiwumind.next.components.common.util.rowkey;



import java.security.MessageDigest;
import java.util.Date;

import java.util.HexFormat;

public class RowkeyWorker {
    /**
     * rowkey 前缀
     *
     * @param certNo 身份证号
     */
    public static String rowkeyPrefix(String certNo) {
        return rowkeySalt(certNo, 20) + md5Hash16(certNo);
    }

    /**
     * 分区salt 2位 +hash(certNo) 16位 +时间19位,长度37位
     *
     * @param certNo 身份证号
     * @param date 申请时间，具体最好到秒
     * @return 分区salt+hash(certNo)+时间
     */
    public static String rowkey(String certNo, Date date) {
        return rowkeySalt(certNo, 20) + md5Hash16(certNo) + (Long.MAX_VALUE - date.getTime());
    }

    /**
     * 分区salt 2位 +hash(certNo) 16位 +主键14位,长度32位
     *
     * @param certNo 身份证号
     * @param id
     * @return 分区salt+hash(certNo)+主键
     */
    public static String rowkey(String certNo, long id) {
        return rowkeySalt(certNo, 20) + md5Hash16(certNo) + format(14, id);
    }

    /**
     * @Description:加密-16位小写
     * @author:liuyc
     * @time:2016年5月23日 上午11:15:33
     */
    public static String md5Hash16(String encryptStr) {
        return md5Hash(encryptStr).substring(8, 24);
    }

    /**
     * 动态分区
     *
     * @param key
     * @param region 分区数
     * @return Integer
     */
    public static String rowkeySalt(String key, int region) {
        long saltAsInt = Math.abs((long)key.hashCode()) % region;
        int charsInSalt = (int) (Logarithm.log(region - 1, 10) + 1);
        return format(charsInSalt, saltAsInt);
    }

    public static String format(int charsInSalt, long value) {
        return String.format("%0" + charsInSalt + "d", value);
    }

    public static String md5Hash(String encryptStr) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(encryptStr.getBytes());
            byte[] digiest = messageDigest.digest();
            encryptStr = HexFormat.of().withUpperCase().formatHex(digiest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }
}
