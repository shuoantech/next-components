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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class DiscountBOTest {

    @Test
    public void testDefaultConstructor() {
        DiscountBO discount = new DiscountBO();
        assertEquals(Long.valueOf(0L), discount.getDiscount());
        assertNull(discount.getActivityEnum());
        assertNull(discount.getCode());
        assertNull(discount.getInnerLogicUniqueCode());
    }

    @Test
    public void testConstructorFromActivity() {
        ActivityBaseBO activity = new ActivityBaseBO();
        activity.setActivityEnum(ActivityEnum.COUPON);
        activity.setCode("COUPON_001");
        activity.setInnerLogicUniqueCode("UNIQUE_001");

        DiscountBO discount = new DiscountBO(activity);

        assertEquals(ActivityEnum.COUPON, discount.getActivityEnum());
        assertEquals("COUPON_001", discount.getCode());
        assertEquals("UNIQUE_001", discount.getInnerLogicUniqueCode());
        assertEquals(Long.valueOf(0L), discount.getDiscount());
    }

    @Test
    public void testSetters() {
        DiscountBO discount = new DiscountBO();
        discount.setActivityEnum(ActivityEnum.PROMOTION);
        discount.setCode("PROMO_001");
        discount.setInnerLogicUniqueCode("INNER_001");
        discount.setDiscount(500L);
        discount.setActivityName("满25减5");

        assertEquals(ActivityEnum.PROMOTION, discount.getActivityEnum());
        assertEquals("PROMO_001", discount.getCode());
        assertEquals("INNER_001", discount.getInnerLogicUniqueCode());
        assertEquals(Long.valueOf(500L), discount.getDiscount());
        assertEquals("满25减5", discount.getActivityName());
    }

    @Test
    public void testFromActivityBO() {
        ActivityBO activity = new ActivityBO();
        activity.setActivityEnum(ActivityEnum.PROMOTION);
        activity.setCode("PROMO_002");
        activity.setInnerLogicUniqueCode("UNIQUE_002");

        DiscountBO discount = new DiscountBO(activity);

        assertEquals(ActivityEnum.PROMOTION, discount.getActivityEnum());
        assertEquals("PROMO_002", discount.getCode());
        assertEquals("UNIQUE_002", discount.getInnerLogicUniqueCode());
    }
}
