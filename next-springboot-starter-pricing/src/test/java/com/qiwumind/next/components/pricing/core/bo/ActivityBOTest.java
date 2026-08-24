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
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class ActivityBOTest {

    private ActivityBO activity;
    private PriceBO priceBO;

    @Before
    public void setUp() {
        activity = new ActivityBO();
        activity.setCode("PROMO_001");
        activity.setName("满25减5");
        activity.setActivityType(new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT));
        activity.setInnerLogicUniqueCode("PROMO_001");
        activity.setStartTime(System.currentTimeMillis() - 86400000L); // Yesterday
        activity.setEndTime(System.currentTimeMillis() + 86400000L);   // Tomorrow
        activity.setFaceAmount(500L);

        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("offNCent", 500L);
        metaMap.put("thresholdAmount", 2500L);
        activity.setMetaMap(metaMap);

        priceBO = new PriceBO();
        priceBO.setCurrentTime(System.currentTimeMillis());
    }

    @Test
    public void testGetActivityEnum_fromSuper() {
        activity.setActivityEnum(ActivityEnum.COUPON);
        assertEquals(ActivityEnum.COUPON, activity.getActivityEnum());
    }

    @Test
    public void testGetActivityEnum_fromActivityType() {
        // When super's activityEnum is null, delegate to activityType
        assertEquals(ActivityEnum.PROMOTION, activity.getActivityEnum());
    }

    @Test
    public void testGetActivityEnum_nullBoth() {
        ActivityBO empty = new ActivityBO();
        assertNull(empty.getActivityEnum());
    }

    @Test
    public void testActivityBaseInfoCheckAndMatch_withinTime() {
        assertTrue(activity.activityBaseInfoCheckAndMatch(priceBO));
    }

    @Test
    public void testActivityBaseInfoCheckAndMatch_beforeStart() {
        activity.setStartTime(System.currentTimeMillis() + 86400000L); // Tomorrow
        assertFalse(activity.activityBaseInfoCheckAndMatch(priceBO));
    }

    @Test
    public void testActivityBaseInfoCheckAndMatch_afterEnd() {
        activity.setEndTime(System.currentTimeMillis() - 86400000L); // Yesterday
        assertFalse(activity.activityBaseInfoCheckAndMatch(priceBO));
    }

    @Test
    public void testActivityBaseInfoCheckAndMatch_nullTime() {
        activity.setStartTime(null);
        activity.setEndTime(null);
        assertTrue(activity.activityBaseInfoCheckAndMatch(priceBO));
    }

    @Test
    public void testActivityBaseInfoCheckAndMatch_nullCurrentTime() {
        priceBO.setCurrentTime(null);
        assertTrue(activity.activityBaseInfoCheckAndMatch(priceBO));
    }

    @Test
    public void testCouponBaseInfoCheck() {
        assertTrue(activity.couponBaseInfoCheck(priceBO));
    }

    @Test
    public void testCouponInitAllBuyGoods() {
        // Should not throw exceptions
        activity.couponInitAllBuyGoods(priceBO);
    }

    @Test
    public void testMetaMapAccess() {
        Map<String, Object> meta = activity.getMetaMap();
        assertNotNull(meta);
        assertEquals(500L, meta.get("offNCent"));
        assertEquals(2500L, meta.get("thresholdAmount"));
    }

    @Test
    public void testDiscountAmount_defaultZero() {
        assertEquals(Long.valueOf(0L), activity.getDiscountAmount());
    }

    @Test
    public void testSelected_defaultFalse() {
        assertFalse(activity.isSelected());
    }

    @Test
    public void testSelected_setTrue() {
        activity.setSelected(true);
        assertTrue(activity.isSelected());
    }

    @Test
    public void testAddEqIndex() {
        assertNull(activity.getEqIndexList());
        activity.addEqIndex(0);
        assertNotNull(activity.getEqIndexList());
        assertEquals(1, activity.getEqIndexList().size());
        assertTrue(activity.getEqIndexList().contains(0));

        // Duplicate should not be added
        activity.addEqIndex(0);
        assertEquals(1, activity.getEqIndexList().size());
    }
}
