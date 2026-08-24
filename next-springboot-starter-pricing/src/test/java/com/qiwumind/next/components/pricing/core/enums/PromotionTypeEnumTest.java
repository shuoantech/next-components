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

package com.qiwumind.next.components.pricing.core.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class PromotionTypeEnumTest {

    @Test
    public void testGetEnumByCode_valid() {
        assertEquals(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT,
                PromotionTypeEnum.getEnumByCode(101));
        assertEquals(PromotionTypeEnum.LADDER_FULL_X_CENT_GIFT,
                PromotionTypeEnum.getEnumByCode(201));
        assertEquals(PromotionTypeEnum.FREE_SHIPPING,
                PromotionTypeEnum.getEnumByCode(301));
        assertEquals(PromotionTypeEnum.DAN_PIN_SET_PRICE,
                PromotionTypeEnum.getEnumByCode(401));
    }

    @Test
    public void testGetEnumByCode_null() {
        assertNull(PromotionTypeEnum.getEnumByCode(null));
    }

    @Test
    public void testGetEnumByCode_negative() {
        assertNull(PromotionTypeEnum.getEnumByCode(-1));
    }

    @Test
    public void testGetEnumByCode_nonexistent() {
        assertNull(PromotionTypeEnum.getEnumByCode(9999));
    }

    @Test
    public void testIsGift_allFourTypes() {
        assertTrue(PromotionTypeEnum.LADDER_FULL_X_CENT_GIFT.isGift());
        assertTrue(PromotionTypeEnum.LADDER_FULL_X_NUM_GIFT.isGift());
        assertTrue(PromotionTypeEnum.PRE_FULL_X_CENT_GIFT.isGift());
        assertTrue(PromotionTypeEnum.PRE_FULL_X_NUM_GIFT.isGift());
        assertFalse(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.isGift());
    }

    @Test
    public void testIsShipping_allSixTypes() {
        assertTrue(PromotionTypeEnum.FREE_SHIPPING.isShipping());
        assertTrue(PromotionTypeEnum.FULL_X_CENT_FREE_SHIPPING.isShipping());
        assertTrue(PromotionTypeEnum.FULL_X_NUM_FREE_SHIPPING.isShipping());
        assertTrue(PromotionTypeEnum.SHIPPING_OFF_N_CENT.isShipping());
        assertTrue(PromotionTypeEnum.FULL_X_CENT_SHIPPING_OFF_N_CENT.isShipping());
        assertTrue(PromotionTypeEnum.FULL_X_NUM_SHIPPING_OFF_N_CENT.isShipping());
        assertFalse(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.isShipping());
    }

    @Test
    public void testIsSinglePromotion() {
        assertTrue(PromotionTypeEnum.DAN_PIN_SET_PRICE.isSinglePromotion());
        assertTrue(PromotionTypeEnum.DAN_PIN_ZHI_JIAN.isSinglePromotion());
        assertTrue(PromotionTypeEnum.DAN_PIN_ZHE_KOU.isSinglePromotion());
        assertTrue(PromotionTypeEnum.TAO_CAN_SET_PRICE.isSinglePromotion());
        assertTrue(PromotionTypeEnum.TAO_CAN_ZHI_JIAN.isSinglePromotion());
        assertFalse(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.isSinglePromotion());
    }

    @Test
    public void testIsAttrPromotion() {
        assertTrue(PromotionTypeEnum.SPEC_FREE.isAttrPromotion());
        assertTrue(PromotionTypeEnum.SPEC_OFF.isAttrPromotion());
        assertTrue(PromotionTypeEnum.ACCESSORIES_FREE.isAttrPromotion());
        assertTrue(PromotionTypeEnum.ACCESSORIES_OFF.isAttrPromotion());
        assertFalse(PromotionTypeEnum.DAN_PIN_ZHE_KOU.isAttrPromotion());
    }

    @Test
    public void testIsPosPromotion() {
        assertTrue(PromotionTypeEnum.POS_SET_DIFF_PRICE.isPosPromotion());
        assertTrue(PromotionTypeEnum.POS_DISCOUNT.isPosPromotion());
        assertTrue(PromotionTypeEnum.POS_OFF.isPosPromotion());
        assertTrue(PromotionTypeEnum.POS_PER_FULL_X_CENT_OFF_N_CENT.isPosPromotion());
        assertFalse(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.isPosPromotion());
        assertFalse(PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST.isPosPromotion());
    }

    @Test
    public void testIsMemberRightsPromotion() {
        assertTrue(PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST.isMemberRightsPromotion());
        assertFalse(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.isMemberRightsPromotion());
    }

    @Test
    public void testPriority() {
        // Single-item and attr promotions = 1
        assertEquals(1, PromotionTypeEnum.DAN_PIN_ZHE_KOU.priority());
        assertEquals(1, PromotionTypeEnum.SPEC_FREE.priority());
        assertEquals(1, PromotionTypeEnum.TAO_CAN_SET_PRICE.priority());

        // Free N items = 2
        assertEquals(2, PromotionTypeEnum.LADDER_FULL_X_NUM_FREE_N_NUM.priority());

        // Multi-item full-reduction = 3
        assertEquals(3, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.priority());

        // Discount one item = 3 (code 106 matches code range 101-110 before individual check)
        assertEquals(3, PromotionTypeEnum.PER_FULL_X_NUM_DISCOUNT_ONE_NUM.priority());
        assertEquals(3, PromotionTypeEnum.FULL_X_NUM_DISCOUNT_ONE_NUM.priority());

        // Gift = 5
        assertEquals(5, PromotionTypeEnum.LADDER_FULL_X_CENT_GIFT.priority());

        // Shipping = 6
        assertEquals(6, PromotionTypeEnum.FREE_SHIPPING.priority());

        // Default = 100
        assertEquals(100, PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST.priority());
    }

    @Test
    public void testGetCode() {
        assertEquals(101, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.getCode());
        assertEquals(901, PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST.getCode());
    }

    @Test
    public void testGetBigCode() {
        assertEquals(10, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT.getBigCode());
        assertEquals(20, PromotionTypeEnum.LADDER_FULL_X_CENT_GIFT.getBigCode());
        assertEquals(40, PromotionTypeEnum.DAN_PIN_ZHE_KOU.getBigCode());
        assertEquals(90, PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST.getBigCode());
    }
}
