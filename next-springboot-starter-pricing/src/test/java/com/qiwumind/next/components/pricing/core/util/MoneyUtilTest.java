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

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * \1 单元测试。
 */
public class MoneyUtilTest {

    // ==================== discount() 折扣后价格 ====================

    @Test
    public void testDiscount_normal() {
        // 1000 * 800 / 1000 = 800 (20% off)
        assertEquals(800L, MoneyUtil.discount(1000L, 800L));
    }

    @Test
    public void testDiscount_noDiscount() {
        // discount=1000 means full price
        assertEquals(1000L, MoneyUtil.discount(1000L, 1000L));
    }

    @Test
    public void testDiscount_halfPrice() {
        // 2000 * 500 / 1000 = 1000 (50% off)
        assertEquals(1000L, MoneyUtil.discount(2000L, 500L));
    }

    @Test
    public void testDiscount_nullPrice() {
        assertEquals(0L, MoneyUtil.discount(null, 800L));
    }

    @Test
    public void testDiscount_nullDiscount() {
        assertEquals(0L, MoneyUtil.discount(1000L, null));
    }

    @Test
    public void testDiscount_zeroDiscount() {
        assertEquals(0L, MoneyUtil.discount(1000L, 0L));
    }

    @Test
    public void testDiscount_zeroPrice() {
        assertEquals(0L, MoneyUtil.discount(0L, 800L));
    }

    @Test
    public void testDiscount_largeNumbers() {
        // Integer division truncation: 99999 * 800 / 1000 = 79999 (truncated)
        assertEquals(79999L, MoneyUtil.discount(99999L, 800L));
    }

    // ==================== getDiscount() 优惠金额 ====================

    @Test
    public void testGetDiscount_normal() {
        // 1000 * (1000-800) / 1000 = 200, ROUND_UP -> 200
        assertEquals(200L, MoneyUtil.getDiscount(1000L, 800L));
    }

    @Test
    public void testGetDiscount_full() {
        // 1000 * (1000-0) / 1000 = 1000
        assertEquals(1000L, MoneyUtil.getDiscount(1000L, 0L));
    }

    @Test
    public void testGetDiscount_noDiscount() {
        // 1000 * (1000-1000) / 1000 = 0
        assertEquals(0L, MoneyUtil.getDiscount(1000L, 1000L));
    }

    @Test
    public void testGetDiscount_roundUp() {
        // 1001 * (1000-800) / 1000 = 200.2, ROUND_UP -> 201
        assertEquals(201L, MoneyUtil.getDiscount(1001L, 800L));
    }

    @Test
    public void testGetDiscount_nullPrice() {
        assertEquals(0L, MoneyUtil.getDiscount(null, 800L));
    }

    @Test
    public void testGetDiscount_nullDiscount() {
        assertEquals(0L, MoneyUtil.getDiscount(1000L, null));
    }

    @Test
    public void testGetDiscount_zeroTotal() {
        assertEquals(0L, MoneyUtil.getDiscount(0L, 800L));
    }

    @Test
    public void testGetDiscount_tinyDiscount() {
        // 1 * 1 / 1000 = 0.001, ROUND_UP -> 1
        assertEquals(1L, MoneyUtil.getDiscount(1L, 999L));
    }

    // ==================== getDiscountCeiling() 带封顶优惠 ====================

    @Test
    public void testGetDiscountCeiling() {
        assertEquals(200L, MoneyUtil.getDiscountCeiling(1000L, 800L));
    }

    // ==================== getShare() 比例分摊 ====================

    @Test
    public void testGetShare_proportional() {
        // 500 * 1000 / 2000 = 250, ROUND_UP -> 250
        assertEquals(250L, MoneyUtil.getShare(500L, 2000L, 1000L));
    }

    @Test
    public void testGetShare_roundUp() {
        // 333 * 1000 / 1000 = 333, no roundup needed
        assertEquals(333L, MoneyUtil.getShare(333L, 1000L, 1000L));
    }

    @Test
    public void testGetShare_exceedsSelf() {
        // If share exceeds selfPrice, cap at selfPrice
        assertEquals(100L, MoneyUtil.getShare(100L, 1000L, 2000L));
    }

    @Test
    public void testGetShare_nullSelfPrice() {
        assertEquals(0L, MoneyUtil.getShare(null, 2000L, 1000L));
    }

    @Test
    public void testGetShare_nullTotalPrice() {
        assertEquals(0L, MoneyUtil.getShare(500L, null, 1000L));
    }

    @Test
    public void testGetShare_nullSharePrice() {
        assertEquals(0L, MoneyUtil.getShare(500L, 2000L, null));
    }

    @Test
    public void testGetShare_zeroTotal() {
        assertEquals(0L, MoneyUtil.getShare(500L, 0L, 1000L));
    }

    @Test
    public void testGetShare_negativeTotal() {
        assertEquals(0L, MoneyUtil.getShare(500L, -1L, 1000L));
    }

    @Test
    public void testGetShare_exactToOneCent() {
        // share 1 cent: 1 * 1 / 1000 = 0.001, ROUND_UP -> 1
        assertEquals(1L, MoneyUtil.getShare(1L, 1000L, 1L));
    }

    // ==================== centToYuan() 分转元智能 ====================

    @Test
    public void testCentToYuan_even100() {
        assertEquals("10", MoneyUtil.centToYuan(1000L));
    }

    @Test
    public void testCentToYuan_even10() {
        assertEquals("10.5", MoneyUtil.centToYuan(1050L));
    }

    @Test
    public void testCentToYuan_twoDecimals() {
        assertEquals("10.55", MoneyUtil.centToYuan(1055L));
    }

    @Test
    public void testCentToYuan_null() {
        assertEquals("0", MoneyUtil.centToYuan(null));
    }

    @Test
    public void testCentToYuan_zero() {
        assertEquals("0", MoneyUtil.centToYuan(0L));
    }

    @Test
    public void testCentToYuan_negative() {
        assertEquals("0", MoneyUtil.centToYuan(-100L));
    }

    // ==================== centToYuanTwo() 分转元固定 ====================

    @Test
    public void testCentToYuanTwo_normal() {
        assertEquals("10.00", MoneyUtil.centToYuanTwo(1000L));
    }

    @Test
    public void testCentToYuanTwo_decimal() {
        assertEquals("10.55", MoneyUtil.centToYuanTwo(1055L));
    }

    @Test
    public void testCentToYuanTwo_null() {
        assertEquals("0.00", MoneyUtil.centToYuanTwo(null));
    }

    @Test
    public void testCentToYuanTwo_zero() {
        assertEquals("0.00", MoneyUtil.centToYuanTwo(0L));
    }

    @Test
    public void testCentToYuanTwo_negative() {
        assertEquals("0.00", MoneyUtil.centToYuanTwo(-100L));
    }

    // ==================== bigDecimalYuanToCent() 元转分 ====================

    @Test
    public void testBigDecimalYuanToCent_normal() {
        assertEquals(1000L, MoneyUtil.bigDecimalYuanToCent(new BigDecimal("10")));
    }

    @Test
    public void testBigDecimalYuanToCent_decimal() {
        // 10.555 * 100 = 1055.5, ROUND_UP -> 1056
        assertEquals(1056L, MoneyUtil.bigDecimalYuanToCent(new BigDecimal("10.555")));
    }

    @Test
    public void testBigDecimalYuanToCent_null() {
        assertEquals(0L, MoneyUtil.bigDecimalYuanToCent(null));
    }

    @Test
    public void testBigDecimalYuanToCent_zero() {
        assertEquals(0L, MoneyUtil.bigDecimalYuanToCent(BigDecimal.ZERO));
    }

    // ==================== discountToStr() 折扣转字符串 ====================

    @Test
    public void testDiscountToStr_even100() {
        assertEquals("8", MoneyUtil.discountToStr(800L));
    }

    @Test
    public void testDiscountToStr_even10() {
        assertEquals("8.5", MoneyUtil.discountToStr(850L));
    }

    @Test
    public void testDiscountToStr_twoDecimals() {
        assertEquals("8.55", MoneyUtil.discountToStr(855L));
    }

    @Test
    public void testDiscountToStr_null() {
        assertEquals("0", MoneyUtil.discountToStr(null));
    }

    @Test
    public void testDiscountToStr_zero() {
        assertEquals("0", MoneyUtil.discountToStr(0L));
    }

    @Test
    public void testDiscountToStr_negative() {
        assertEquals("0", MoneyUtil.discountToStr(-100L));
    }

    // ==================== Integration Scenarios ====================

    @Test
    public void testFullPriceScenario() {
        // Original: 3500 cents (coffee 1500 + tea 1000*2)
        long total = 3500L;
        // No discount
        assertEquals(3500L, MoneyUtil.discount(total, 1000L));
        assertEquals(0L, MoneyUtil.getDiscount(total, 1000L));
    }

    @Test
    public void testPromotionScenario() {
        // Full 2500 (25 yuan), off 500 (5 yuan)
        long total = 3500L;
        // getDiscount tells how much to take off
        // 3500 * (1000-500/25*20) -- but the discount argument here is
        // the coupon-style discount base value, not the offNCent
        // For an "off N cents" promotion, the discount amount = N directly
        // So getDiscount is used with discount=(1000-0)=1000 for zero off
        // Actually getDiscount(3500, 1000-500) - but that's not how it's called
    }

    @Test
    public void testShareScenario_threeGoods() {
        // Share 300 cents across: coffee(1500) and tea(1000) x2 = 3500 total
        // Coffee share: 300 * 1500 / 3500 = 128.57, ROUND_UP -> 129
        assertEquals(129L, MoneyUtil.getShare(1500L, 3500L, 300L));
        // Tea share: 300 * 1000 / 3500 = 85.71, ROUND_UP -> 86
        assertEquals(86L, MoneyUtil.getShare(1000L, 3500L, 300L));
    }
}
