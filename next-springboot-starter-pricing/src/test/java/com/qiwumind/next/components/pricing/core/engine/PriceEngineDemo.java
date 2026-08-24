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
import com.qiwumind.next.components.pricing.core.engine.ComputeService;
import com.qiwumind.next.components.pricing.core.enums.ActivityEnum;
import com.qiwumind.next.components.pricing.core.enums.PromotionTypeEnum;
import com.qiwumind.next.components.pricing.core.util.MoneyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo: Shows how to use the price engine to compute a simple order.
 *
 * Scenario:
 * - 2 goods: Coffee (1500 cents = 15 yuan) x1, Tea (1000 cents = 10 yuan) x2
 * - 1 promotion: "满25元减5元" (full 25 yuan, off 5 yuan)
 * - 1 coupon: "满20元减3元" (full 20 yuan, off 3 yuan)
 *
 * Expected result:
 * - Total original: 1500 + 1000*2 = 3500 cents (35 yuan)
 * - Coupon: off 300 cents (3 yuan) -> pay 3200
 * - Promotion: full 2500 (25 yuan) -> off 500 cents (5 yuan) -> pay 2700
 */
public class PriceEngineDemo {

    public static void main(String[] args) {
        // 1. Build goods (one GoodsBO per unit - matches original system design)
        List<GoodsBO> goodsList = new ArrayList<>();

        GoodsBO coffee = new GoodsBO();
        coffee.setItemId(1L);
        coffee.setItemName("Coffee");
        coffee.setSaleAmount(1500L);
        coffee.setComputeAmount(1500L);
        coffee.setNum(1);
        coffee.setEqIndex(0);
        goodsList.add(coffee);

        GoodsBO tea1 = new GoodsBO();
        tea1.setItemId(2L);
        tea1.setItemName("Tea");
        tea1.setSaleAmount(1000L);
        tea1.setComputeAmount(1000L);
        tea1.setNum(1);
        tea1.setEqIndex(1);
        goodsList.add(tea1);

        GoodsBO tea2 = new GoodsBO();
        tea2.setItemId(2L);
        tea2.setItemName("Tea");
        tea2.setSaleAmount(1000L);
        tea2.setComputeAmount(1000L);
        tea2.setNum(1);
        tea2.setEqIndex(2);
        goodsList.add(tea2);

        // 2. Build pricing context
        PriceBO priceBO = new PriceBO();
        priceBO.setGoodsList(goodsList);
        priceBO.setCurrentTime(System.currentTimeMillis());
        priceBO.setChannel(1);
        priceBO.setShippingFee(500L); // 5 yuan shipping

        // 3. Build activities
        List<ActivityBO> activityList = new ArrayList<>();

        // Promotion: full 2500 cents, off 500 cents
        ActivityBO promotion = new ActivityBO();
        promotion.setCode("PROMO_001");
        promotion.setName("满25减5");
        promotion.setActivityType(new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT));
        promotion.setInnerLogicUniqueCode("PROMO_001");
        promotion.setStartTime(System.currentTimeMillis() - 86400000L);
        promotion.setEndTime(System.currentTimeMillis() + 86400000L);
        promotion.setCheckExpression("TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum)");
        promotion.setComputeExpression("TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum) && OffNCentFunction(activity, price, executeEnum)");
        promotion.setShowExpression("ShowLabelFunction(activity, price, executeEnum)");
        Map<String, Object> promoMeta = new HashMap<>();
        promoMeta.put("offNCent", 500L);
        promoMeta.put("thresholdAmount", 2500L);
        promotion.setMetaMap(promoMeta);
        activityList.add(promotion);

        // Coupon: full 2000 cents, off 300 cents
        ActivityBO coupon = new ActivityBO();
        coupon.setCode("COUPON_001");
        coupon.setName("满20减3券");
        coupon.setActivityType(new ActivityTypeBO(ActivityEnum.COUPON));
        coupon.setInnerLogicUniqueCode("COUPON_001");
        coupon.setStartTime(System.currentTimeMillis() - 86400000L);
        coupon.setEndTime(System.currentTimeMillis() + 86400000L);
        coupon.setCreateTime(System.currentTimeMillis() - 172800000L);
        coupon.setFaceAmount(300L);
        coupon.setCheckExpression("TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum)");
        coupon.setComputeExpression("TimeCycleFunction(activity, price, executeEnum) && GoodsMatchAllPriceFunction(activity, price, executeEnum) && OffNCentFunction(activity, price, executeEnum)");
        coupon.setShowExpression("ShowLabelFunction(activity, price, executeEnum)");
        Map<String, Object> couponMeta = new HashMap<>();
        couponMeta.put("offNCent", 300L);
        couponMeta.put("thresholdAmount", 2000L);
        couponMeta.put("label", "满20减3");
        coupon.setMetaMap(couponMeta);
        activityList.add(coupon);

        // 4. Compute
        ComputeService service = new ComputeService();
        ComputeRespBO result = service.compute(priceBO, activityList);

        // 5. Print results
        System.out.println("========== Price Engine Demo ==========");
        System.out.println("Goods:");
        for (GoodsBO goods : result.getGoodsList()) {
            System.out.printf("  %s: sale=%s, compute=%s, num=%d%n",
                    goods.getItemName(),
                    MoneyUtil.centToYuan(goods.getSaleAmount()),
                    MoneyUtil.centToYuan(goods.getComputeAmount()),
                    goods.getNum());
            System.out.println("    Discount details:");
            for (DiscountBO d : goods.getDiscountDetailList()) {
                System.out.printf("      %s (%s): -%s yuan%n",
                        d.getActivityEnum().getDesc(),
                        d.getCode(),
                        MoneyUtil.centToYuan(d.getDiscount()));
            }
        }
        System.out.println();
        System.out.printf("Total original: %s yuan%n", MoneyUtil.centToYuan(result.getTotalAmount()));
        System.out.printf("Total discount: %s yuan%n", MoneyUtil.centToYuan(result.getTotalDiscount()));
        System.out.printf("Shipping fee:   %s yuan%n", MoneyUtil.centToYuan(result.getShippingFee()));
        System.out.printf("Final payable:  %s yuan%n", MoneyUtil.centToYuan(result.getPayAmount()));
        System.out.println();
        System.out.println("Used promotions: " + result.getUsedPromotionList().size());
        for (ActivityBO a : result.getUsedPromotionList()) {
            System.out.printf("  %s: -%s yuan%n", a.getName(), MoneyUtil.centToYuan(a.getDiscountAmount()));
        }
        System.out.println("Used coupons: " + result.getUsedCouponList().size());
        for (ActivityBO a : result.getUsedCouponList()) {
            System.out.printf("  %s: -%s yuan%n", a.getName(), MoneyUtil.centToYuan(a.getDiscountAmount()));
        }
        System.out.println("Available coupons: " + result.getCanUseCouponList().size());
        System.out.println("Unavailable coupons: " + result.getCantUseCouponList().size());
        System.out.println("=======================================");
    }
}
