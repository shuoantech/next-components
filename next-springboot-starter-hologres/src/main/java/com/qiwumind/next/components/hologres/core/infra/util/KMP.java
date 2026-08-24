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

package com.qiwumind.next.components.hologres.core.infra.util;

/**
 * 字符串搜索工具（KMP 算法实现）。
 *
 * @author liks
 */
public class KMP {

    private KMP() {
    }

    /**
     * KMP 搜索：在 source 中查找 pattern 首次出现的位置。
     *
     * @param source  源字符串
     * @param pattern 模式串
     * @return 匹配位置索引，未找到返回 -1
     */
    public static int kmpSearch(String source, String pattern) {
        char[] s = source.toCharArray();
        char[] t = pattern.toCharArray();
        int[] next = nextArray(pattern);
        int i = 0, j = 0;
        while (i < s.length && j < t.length) {
            if (j == -1 || s[i] == t[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        return j == t.length ? i - t.length : -1;
    }

    /**
     * 计算 KMP 算法的 next 数组（优化版）。
     */
    private static int[] nextArray(String target) {
        char[] t = target.toCharArray();
        int[] next = new int[t.length];
        next[0] = -1;
        int k = -1;
        int j = 0;
        while (j < next.length - 1) {
            if (k == -1 || t[j] == t[k]) {
                k++;
                j++;
                if (t[j] != t[k]) {
                    next[j] = k;
                } else {
                    // 优化：避免 p[j] == p[next[j]] 的情况，递归取值
                    next[j] = next[k];
                }
            } else {
                k = next[k];
            }
        }
        return next;
    }
}
