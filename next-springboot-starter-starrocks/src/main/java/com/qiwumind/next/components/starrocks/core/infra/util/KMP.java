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

package com.qiwumind.next.components.starrocks.core.infra.util;



/**
 * 类KMP.java的实现描述
 *
 * @author liks 2020年6月17日 下午2:35:04
 */
public class KMP {
    public static void main(final String[] args) {
        String source = "zaxd-gemini-mics/TCJQ/ZYTC/chargeback/20200603/test/OVERDUE_20200603000000.csv__f54fdc63a9e142c4b1595eb44fe5995f";
        source = " select adfaf,sfsdf,ff from table hargeback/20200603/test/OVERDUE_20200603000000.csv__f54fdc63a9e142c4b1595eb44fe5995f";

        final String target = "select";
        System.out.println("匹配成功，下标为：" + KmpSearch(source, target));
        System.out.println("匹配成功，下标为：" + naiveMatch(source, target));

        System.out.println("匹配成功，下标为：" + source.substring(0, naiveMatch(source, target)));


    }

    /**
     * @param source
     * @param pattern
     * @return
     */
    public static int naiveMatch(final String source, final String pattern) {
        int i = 0, j = 0;
        while (i < source.length() && j < pattern.length()) {
            if (source.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else {
                i = (i - j) + 1;
                j = 0;
            }
        }
        if (j >= pattern.length()) {
            return i - pattern.length();
        } else {
            return -1;
        }
    }

    /**
     * @param source
     * @param target
     * @return
     */
    public static int KmpSearch(final String source, final String target) {
        // 转为字符型数组
        final char[] s = source.toCharArray();
        final char[] t = target.toCharArray();
        // 获取next数组
        final int[] next = next(target);
        int i = 0;// 主串下标
        int j = 0;// 模式串下标
        while (i < s.length && j < t.length) {
            if (j == -1 || s[i] == t[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }

        }
        if (j == t.length) {
            return i - t.length;// 返回模式串在主串中的头下标
        } else {
            return -1;
        }
    }

    /**
     * next数组优化版
     *
     * @param target
     * @return
     */
    public static int[] next(final String target) {
        final char[] t = target.toCharArray();
        final int[] next = new int[t.length];
        next[0] = -1;
        int k = -1;
        int j = 0;
        while (j < next.length - 1) {
            if (k == -1 || t[j] == t[k]) {
                k++;
                j++;
                // ===============
                // 较优化前的next数组求法，改变在以下四行代码。
                if (t[j] != t[k]) {
                    next[j] = k;// 优化前只有这一行。
                } else {
                    // 优化后因为不能出现p[j] = p[ next[j ]]，所以当出现时需要继续递归。
                    next[j] = next[k];
                }
                // ===============
            } else {
                k = next[k];
            }
        }
        return next;
    }

}
