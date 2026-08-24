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

package com.qiwumind.next.components.pricing.core.bo;

import com.qiwumind.next.components.pricing.core.enums.ActivityEnum;
import com.qiwumind.next.components.pricing.core.enums.PromotionTypeEnum;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class ActivityTypeBOTest {

    @Test
    public void testConstructor_withActivityEnum() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.COUPON);
        assertEquals(ActivityEnum.COUPON, type.getActivityEnum());
        assertNull(type.getPromotionTypeEnum());
    }

    @Test
    public void testConstructor_withBoth() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT);
        assertEquals(ActivityEnum.PROMOTION, type.getActivityEnum());
        assertEquals(PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT, type.getPromotionTypeEnum());
    }

    @Test
    public void testIsPromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION);
        assertTrue(type.isPromotion());
    }

    @Test
    public void testIsPromotion_false() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.COUPON);
        assertFalse(type.isPromotion());
    }

    @Test
    public void testIsCoupon_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.COUPON);
        assertTrue(type.isCoupon());
    }

    @Test
    public void testIsCoupon_false() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION);
        assertFalse(type.isCoupon());
    }

    @Test
    public void testIsCard() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.CARD);
        assertTrue(type.isCard());
        assertFalse(type.isCoupon());
        assertFalse(type.isPromotion());
    }

    @Test
    public void testIsStrategy() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.STRATEGY);
        assertTrue(type.isStrategy());
    }

    @Test
    public void testIsGiftPromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.LADDER_FULL_X_CENT_GIFT);
        assertTrue(type.isGiftPromotion());
    }

    @Test
    public void testIsGiftPromotion_false_noPromotionType() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION);
        assertFalse(type.isGiftPromotion());
    }

    @Test
    public void testIsGiftPromotion_false_nonGift() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT);
        assertFalse(type.isGiftPromotion());
    }

    @Test
    public void testIsShippingPromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.FREE_SHIPPING);
        assertTrue(type.isShippingPromotion());
    }

    @Test
    public void testIsShippingPromotion_false() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT);
        assertFalse(type.isShippingPromotion());
    }

    @Test
    public void testIsSinglePromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.DAN_PIN_ZHE_KOU);
        assertTrue(type.isSinglePromotion());
    }

    @Test
    public void testIsAttrPromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.SPEC_FREE);
        assertTrue(type.isAttrPromotion());
    }

    @Test
    public void testIsMemberRightsPromotion_true() {
        ActivityTypeBO type = new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.MEMBER_RIGHTS_BREAKFIRST);
        assertTrue(type.isMemberRightsPromotion());
    }
}
