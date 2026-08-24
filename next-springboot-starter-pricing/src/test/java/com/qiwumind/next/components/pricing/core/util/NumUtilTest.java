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

package com.qiwumind.next.components.pricing.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * \1 单元测试。
 */
public class NumUtilTest {

    // ==================== toLong(Long) ====================

    @Test
    public void testToLong_normal() {
        assertEquals(100L, NumUtil.toLong(100L));
    }

    @Test
    public void testToLong_null() {
        assertEquals(0L, NumUtil.toLong((Long) null));
    }

    @Test
    public void testToLong_zero() {
        assertEquals(0L, NumUtil.toLong(0L));
    }

    @Test
    public void testToLong_negative() {
        assertEquals(-100L, NumUtil.toLong(-100L));
    }

    // ==================== toLong(Integer) ====================

    @Test
    public void testToLong_Integer_normal() {
        assertEquals(100L, NumUtil.toLong(Integer.valueOf(100)));
    }

    @Test
    public void testToLong_Integer_null() {
        assertEquals(0L, NumUtil.toLong((Integer) null));
    }

    // ==================== toInt(Integer) ====================

    @Test
    public void testToInt_normal() {
        assertEquals(100, NumUtil.toInt(100));
    }

    @Test
    public void testToInt_null() {
        assertEquals(0, NumUtil.toInt(null));
    }

    // ==================== toInt(Integer, int) ====================

    @Test
    public void testToInt_defaultValue_normal() {
        assertEquals(100, NumUtil.toInt(100, -1));
    }

    @Test
    public void testToInt_defaultValue_null() {
        assertEquals(-1, NumUtil.toInt(null, -1));
    }

    // ==================== toLong(String) ====================

    @Test
    public void testToLong_String_normal() {
        assertEquals(12345L, NumUtil.toLong("12345"));
    }

    @Test
    public void testToLong_String_null() {
        assertEquals(0L, NumUtil.toLong((String) null));
    }

    @Test
    public void testToLong_String_empty() {
        assertEquals(0L, NumUtil.toLong(""));
    }

    @Test
    public void testToLong_String_invalid() {
        assertEquals(0L, NumUtil.toLong("abc"));
    }

    @Test
    public void testToLong_String_negative() {
        assertEquals(-100L, NumUtil.toLong("-100"));
    }

    @Test
    public void testToLong_String_maxValue() {
        assertEquals(Long.MAX_VALUE, NumUtil.toLong(String.valueOf(Long.MAX_VALUE)));
    }
}
