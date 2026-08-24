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

package com.qiwumind.next.components.pricing.core.engine;

import com.qiwumind.next.components.pricing.core.bo.*;
import com.qiwumind.next.components.pricing.core.enums.ActivityEnum;
import com.qiwumind.next.components.pricing.core.enums.PromotionTypeEnum;
import com.qiwumind.next.components.pricing.core.util.MoneyUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Integration test for ComputeService - the main pricing computation orchestration.
 * <p>
 * Tests end-to-end pricing scenarios including:
 * <ul>
 *   <li>No activities (base case)</li>
 *   <li>Coupon only</li>
 *   <li>Promotion only</li>
 *   <li>Coupon + Promotion combined</li>
 * </ul>
 */
public class ComputeServiceTest {

    private ComputeService service;
    private PriceBO priceBO;
    private List<ActivityBO> activityList;

    @Before
    public void setUp() {
        service = new ComputeService();

        // Build test goods: Coffee(1500) x1 + Tea(1000) x2 = 3500 cents total
        List<GoodsBO> goodsList = new ArrayList<>();

        GoodsBO coffee = new GoodsBO();
        coffee.setItemId(1L);
        coffee.setItemName("Coffee");
        coffee.setSaleAmount(1500L);
        coffee.setNum(1);
        coffee.setEqIndex(0);
        goodsList.add(coffee);

        GoodsBO tea1 = new GoodsBO();
        tea1.setItemId(2L);
        tea1.setItemName("Tea");
        tea1.setSaleAmount(1000L);
        tea1.setNum(1);
        tea1.setEqIndex(1);
        goodsList.add(tea1);

        GoodsBO tea2 = new GoodsBO();
        tea2.setItemId(2L);
        tea2.setItemName("Tea");
        tea2.setSaleAmount(1000L);
        tea2.setNum(1);
        tea2.setEqIndex(2);
        goodsList.add(tea2);

        priceBO = new PriceBO();
        priceBO.setGoodsList(goodsList);
        priceBO.setCurrentTime(System.currentTimeMillis());
        priceBO.setChannel(1);
        priceBO.setShippingFee(500L);

        activityList = new ArrayList<>();
    }

    // ==================== Base Case: No Activities ====================

    @Test
    public void testCompute_noActivities() {
        ComputeRespBO result = service.compute(priceBO, activityList);

        assertEquals(Long.valueOf(3500L), result.getTotalAmount());
        assertEquals(Long.valueOf(4000L), result.getPayAmount()); // 3500 + 500 shipping
        assertEquals(Long.valueOf(0L), result.getTotalDiscount());
        assertTrue(result.getUsedCouponList().isEmpty());
        assertTrue(result.getUsedPromotionList().isEmpty());
    }

    // ==================== Coupon Only ====================

    @Test
    public void testCompute_couponOnly() {
        ActivityBO coupon = createCoupon(
                "COUPON_001", "满20减3券", 2000L, 300L, 300L);
        activityList.add(coupon);

        ComputeRespBO result = service.compute(priceBO, activityList);

        assertEquals(Long.valueOf(3500L), result.getTotalAmount());
        // 3500 - 300 coupon discount + 500 shipping = 3700
        assertEquals(Long.valueOf(3700L), result.getPayAmount());
        assertEquals(Long.valueOf(300L), result.getTotalDiscount());
        assertEquals(1, result.getUsedCouponList().size());
        assertTrue(result.getUsedPromotionList().isEmpty());
    }

    @Test
    public void testCompute_coupon_tooLarge() {
        // Coupon face amount exceeds order total
        ActivityBO coupon = createCoupon(
                "COUPON_BIG", "大额券", 2000L, 10000L, 10000L);
        activityList.add(coupon);

        ComputeRespBO result = service.compute(priceBO, activityList);

        // Large face amount coupon should not be auto-recommended
        assertTrue(result.getUsedCouponList().isEmpty());
        assertEquals(Long.valueOf(4000L), result.getPayAmount()); // 3500 + 500 shipping
    }

    // ==================== Promotion Only ====================

    @Test
    public void testCompute_promotionOnly_fullReduction() {
        ActivityBO promotion = createPromotion(
                "PROMO_001", "满25减5", 2500L, 500L);
        activityList.add(promotion);

        ComputeRespBO result = service.compute(priceBO, activityList);

        assertEquals(Long.valueOf(3500L), result.getTotalAmount());
        // 3500 - 500 promotion + 500 shipping = 3500
        assertEquals(Long.valueOf(3500L), result.getPayAmount());
        assertEquals(Long.valueOf(500L), result.getTotalDiscount());
        assertEquals(1, result.getUsedPromotionList().size());
        assertTrue(result.getUsedCouponList().isEmpty());
    }

    // ==================== Coupon + Promotion Combined ====================

    @Test
    public void testCompute_couponAndPromotion() {
        // The classic demo scenario:
        // Coffee(1500) + Tea(1000) x2 = 3500 total
        // Coupon: full 2000, off 300
        // Promotion: full 2500, off 500
        // Coupon runs first, then promotion

        ActivityBO coupon = createCoupon(
                "COUPON_001", "满20减3券", 2000L, 300L, 300L);
        activityList.add(coupon);

        ActivityBO promotion = createPromotion(
                "PROMO_001", "满25减5", 2500L, 500L);
        activityList.add(promotion);

        ComputeRespBO result = service.compute(priceBO, activityList);

        assertEquals(Long.valueOf(3500L), result.getTotalAmount());
        // 3500 - 300 (coupon) - 500 (promotion) + 500 (shipping) = 3200
        assertEquals(Long.valueOf(3200L), result.getPayAmount());
        assertEquals(Long.valueOf(800L), result.getTotalDiscount());
        assertEquals(1, result.getUsedCouponList().size());
        assertEquals(1, result.getUsedPromotionList().size());
    }

    // ==================== Multiple Coupons (Auto Recommend) ====================

    @Test
    public void testCompute_autoRecommendBestCoupon() {
        // Coupon A: off 500 ("满30减5")
        ActivityBO couponA = createCoupon(
                "COUPON_A", "满30减5券", 3000L, 500L, 500L);
        // Coupon B: off 300 ("满20减3")
        ActivityBO couponB = createCoupon(
                "COUPON_B", "满20减3券", 2000L, 300L, 300L);

        activityList.add(couponA);
        activityList.add(couponB);

        ComputeRespBO result = service.compute(priceBO, activityList);

        // Best coupon (A: off 500) should be used
        assertEquals(1, result.getUsedCouponList().size());
        ActivityBO usedCoupon = result.getUsedCouponList().get(0);
        assertEquals("COUPON_A", usedCoupon.getCode());
    }

    // ==================== Edge Cases ====================

    @Test
    public void testCompute_nullActivityList() {
        // Should not crash with null list
        ComputeRespBO result = service.compute(priceBO, null);
        assertNotNull(result);
    }

    @Test
    public void testCompute_emptyGoodsList() {
        PriceBO emptyPrice = new PriceBO();
        emptyPrice.setGoodsList(new ArrayList<>());
        emptyPrice.setShippingFee(500L);
        emptyPrice.setCurrentTime(System.currentTimeMillis());

        ComputeRespBO result = service.compute(emptyPrice, activityList);

        assertEquals(Long.valueOf(0L), result.getTotalAmount());
        assertEquals(Long.valueOf(500L), result.getPayAmount());
        assertEquals(Long.valueOf(0L), result.getTotalDiscount());
    }

    @Test
    public void testCompute_unselectedGoods() {
        // Unselect the coffee
        priceBO.getGoodsList().get(0).setSelected(false);

        ActivityBO coupon = createCoupon(
                "COUPON_001", "满20减3券", 2000L, 300L, 300L);
        activityList.add(coupon);

        ComputeRespBO result = service.compute(priceBO, activityList);

        // Only Tea x2 = 2000 total, coupon with offNCent=300 applies
        assertEquals(Long.valueOf(2000L), result.getTotalAmount());
        // Discount should be positive (coupon applied)
        assertTrue("Coupon should apply some discount", result.getTotalDiscount() > 0);
        assertTrue("Discount should not exceed 300", result.getTotalDiscount() <= 300);
        // Pay amount = 2000 - discount + 500(shipping)
        assertEquals(Long.valueOf(2000L - result.getTotalDiscount() + 500L), result.getPayAmount());
    }

    @Test
    public void testCompute_initializeComputeAmount() {
        // If computeAmount is null, it should be initialized from saleAmount
        priceBO.getGoodsList().get(0).setComputeAmount(null);

        ComputeRespBO result = service.compute(priceBO, activityList);

        // Should still work - computeAmount was initialized
        assertEquals(Long.valueOf(3500L), result.getTotalAmount());
    }

    // ==================== Helper Methods ====================

    private ActivityBO createCoupon(String code, String name, Long threshold, Long offNCent, Long faceAmount) {
        ActivityBO coupon = new ActivityBO();
        coupon.setCode(code);
        coupon.setName(name);
        coupon.setActivityType(new ActivityTypeBO(ActivityEnum.COUPON));
        coupon.setInnerLogicUniqueCode(code);
        coupon.setStartTime(System.currentTimeMillis() - 86400000L);
        coupon.setEndTime(System.currentTimeMillis() + 86400000L);
        coupon.setCreateTime(System.currentTimeMillis() - 172800000L);
        coupon.setFaceAmount(faceAmount);
        coupon.setCheckExpression(
                "TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum)");
        coupon.setComputeExpression(
                "TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum) && OffNCentFunction(activity, price, executeEnum)");

        Map<String, Object> meta = new HashMap<>();
        meta.put("offNCent", offNCent);
        meta.put("thresholdAmount", threshold);
        coupon.setMetaMap(meta);

        return coupon;
    }

    private ActivityBO createPromotion(String code, String name, Long threshold, Long offNCent) {
        ActivityBO promotion = new ActivityBO();
        promotion.setCode(code);
        promotion.setName(name);
        promotion.setActivityType(new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT));
        promotion.setInnerLogicUniqueCode(code);
        promotion.setStartTime(System.currentTimeMillis() - 86400000L);
        promotion.setEndTime(System.currentTimeMillis() + 86400000L);
        promotion.setCheckExpression(
                "TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum)");
        promotion.setComputeExpression(
                "TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum) && OffNCentFunction(activity, price, executeEnum)");

        Map<String, Object> meta = new HashMap<>();
        meta.put("offNCent", offNCent);
        meta.put("thresholdAmount", threshold);
        promotion.setMetaMap(meta);

        return promotion;
    }
}
