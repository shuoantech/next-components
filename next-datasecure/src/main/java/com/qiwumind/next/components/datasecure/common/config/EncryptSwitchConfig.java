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

package com.qiwumind.next.components.datasecure.common.config;



/**
 * 类EncryptSwitchConfig.java的实现描述：数据库加密开关
 * 
 * @author Jun 2, 2017 11:37:05 AM
 */
public class EncryptSwitchConfig {
    /**
     * 上线临时方案，增加一个字段做判断，控制是否进行加解密操作 正式上线后，都做加解密操作，标志可去掉 默认是打开的
     */
    private static boolean encryptFlag = true;

    /**
     * 通过hsf http调用应用系统开关，来进行直接控制
     * 
     * @param bflag
     */
    public static void setEncryptFlag(boolean bflag) {
        encryptFlag = bflag;
    }

    /**
     * 得到目前是否加密标志
     * 
     * @return
     */
    public static boolean getEncryptFlag() {
        return encryptFlag;
    }
}
