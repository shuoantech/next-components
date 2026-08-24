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

import com.google.common.collect.Lists;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;
import com.qiwumind.next.components.pricing.core.meta.RuleFunction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 优惠券计算引擎 - 处理优惠券类型的活动。
 * <p>
 * 优惠券计算相比促销有独特的要求：
 * <ul>
 *   <li>必须将优惠券分类为：可用、不可用、已使用</li>
 *   <li>支持用户自选优惠券和自动推荐</li>
 *   <li>自动推荐：找出优惠力度最大的优惠券</li>
 *   <li>多张券优惠相同时，优先选择即将过期的券</li>
 * </ul>
 *
 * <h3>自动推荐逻辑</h3>
 * <ol>
 *   <li>遍历所有可用优惠券，计算每张券的优惠金额</li>
 *   <li>追踪优惠力度最大的券</li>
 *   <li>平局规则：过期时间越早的优先（即将过期的先使用）</li>
 *   <li>二次平局规则：创建时间越早的优先</li>
 * </ol>
 */
@Slf4j
@Getter
public class CouponComputeEngine {

    /**
     * 可使用（但尚未应用）的优惠券
     */
    private List<ActivityBO> canUseCouponList = Lists.newArrayList();

    /**
     * 不可使用的优惠券（含原因）
     */
    private List<ActivityBO> cantUseCouponList = Lists.newArrayList();

    /**
     * 已应用的优惠券
     */
    private List<ActivityBO> usedCouponList = Lists.newArrayList();

    private final List<ActivityBO> dbAllCouponList;
    private final PriceBO priceBO;
    private final List<String> choiceCodeList;

    /**
     * 优惠券前的订单应付金额（用于面额校验）
     */
    private final Long beforeUsedCouponOrderPayAmount;

    /**
     * 是否存在优惠最大的券因面额过大而无法自动推荐
     */
    private boolean unAutoUseMaxCoupon = false;

    public CouponComputeEngine(List<ActivityBO> dbAllActivityList, PriceBO priceBO) {
        this.priceBO = priceBO;
        this.beforeUsedCouponOrderPayAmount = priceBO.orderCanUseCouponGoodsCurrentPayAmount();
        this.dbAllCouponList = findCouponFromActivity(dbAllActivityList);
        this.choiceCodeList = priceBO.choiceAllCouponCodeList();
    }

    /**
     * 使用用户选中的优惠券执行计算。
     */
    public void executeCanUseAndCantUseCoupon() {
        if (CollectionUtils.isEmpty(dbAllCouponList)) return;

        List<ActivityBO> selectedCouponList = Lists.newArrayList();

        for (ActivityBO coupon : dbAllCouponList) {
            if (!coupon.couponBaseInfoCheck(priceBO)) {
                cantUseCouponList.add(coupon);
                continue;
            }
            // 若用户选了此券，单独处理
            if (CollectionUtils.isNotEmpty(choiceCodeList) && choiceCodeList.contains(coupon.getCode())) {
                selectedCouponList.add(coupon);
                continue;
            }

            // 检查优惠券是否可用
            coupon.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            coupon.couponInitAllBuyGoods(priceBO);
            if (RuleFunction.checkAndAction(coupon, priceBO)) {
                canUseCouponList.add(coupon);
            } else {
                cantUseCouponList.add(coupon);
            }
        }

        // 处理用户选中的优惠券
        List<ActivityBO> selectedCanUsedCouponList = canUseAndCantUseBySelect(selectedCouponList);
        usedCouponListBySelect(selectedCanUsedCouponList);
        printLog();
    }

    /**
     * 使用自动推荐执行优惠券计算。
     * 找出并应用优惠力度最大的优惠券。
     */
    public void executeCanUseAndCantUseCouponAutoUseCoupon() {
        if (CollectionUtils.isEmpty(dbAllCouponList)) return;

        ActivityBO maxCoupon = null;
        Long maxDiscount = 0L;

        for (ActivityBO coupon : dbAllCouponList) {
            if (!coupon.couponBaseInfoCheck(priceBO)) {
                cantUseCouponList.add(coupon);
                continue;
            }

            coupon.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            coupon.couponInitAllBuyGoods(priceBO);
            if (!RuleFunction.checkAndAction(coupon, priceBO)) {
                cantUseCouponList.add(coupon);
                continue;
            }

            // 自动推荐：找优惠最大的券
            if (coupon.getFaceAmount() != null && coupon.getFaceAmount() > beforeUsedCouponOrderPayAmount) {
                // 面额过大，不自动推荐
                unAutoUseMaxCoupon = true;
            } else if (recommendCoupon(maxCoupon, maxDiscount, coupon)) {
                maxDiscount = coupon.getDiscountAmount();
                maxCoupon = coupon;
            }

            canUseCouponList.add(coupon);
        }

        // 应用推荐的优惠券
        if (maxCoupon != null) {
            usedCouponListBySelect(Lists.newArrayList(maxCoupon));
        }
        printLog();
    }

    /**
     * 判断 compareCoupon 是否应替代当前 maxCoupon。
     * 平局规则：过期时间早的优先，其次创建时间早的优先。
     */
    private boolean recommendCoupon(ActivityBO maxCoupon, Long maxDiscount, ActivityBO compareCoupon) {
        if (compareCoupon.getDiscountAmount() == null || compareCoupon.getDiscountAmount() <= 0) {
            return false;
        }
        if (maxDiscount > compareCoupon.getDiscountAmount()) {
            return false;
        }
        if (maxDiscount < compareCoupon.getDiscountAmount()) {
            return true;
        }

        // 优惠金额相同 - 选过期更早的
        if (maxCoupon == null) return true;
        if (compareCoupon.getEndTime() == null) return false;
        if (maxCoupon.getEndTime() == null) return true;

        int cmp = compareCoupon.getEndTime().compareTo(maxCoupon.getEndTime());
        if (cmp < 0) return true;
        if (cmp > 0) return false;

        // 过期时间相同 - 选创建更早的
        if (compareCoupon.getCreateTime() == null) return false;
        if (maxCoupon.getCreateTime() == null) return true;
        return compareCoupon.getCreateTime() < maxCoupon.getCreateTime();
    }

    /**
     * 检查用户选中的优惠券是否可用。
     */
    private List<ActivityBO> canUseAndCantUseBySelect(List<ActivityBO> selectedCouponList) {
        if (CollectionUtils.isEmpty(selectedCouponList)) {
            return Collections.emptyList();
        }
        List<ActivityBO> selectedCanUsedCouponList = Lists.newArrayList();
        for (ActivityBO coupon : selectedCouponList) {
            coupon.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            coupon.couponInitAllBuyGoods(priceBO);
            if (RuleFunction.checkAndAction(coupon, priceBO)) {
                canUseCouponList.add(coupon);
                selectedCanUsedCouponList.add(coupon);
            } else {
                cantUseCouponList.add(coupon);
            }
        }
        return selectedCanUsedCouponList;
    }

    /**
     * 应用选中的优惠券。
     */
    private void usedCouponListBySelect(List<ActivityBO> selectedCouponList) {
        if (CollectionUtils.isEmpty(selectedCouponList)) return;

        for (ActivityBO coupon : selectedCouponList) {
            coupon.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            coupon.couponInitAllBuyGoods(priceBO);
            if (RuleFunction.checkAndAction(coupon, priceBO)) {
                coupon.setSelected(true);
                priceBO.resetGoodsForMiddle();
                priceBO.addUsedCouponCode(coupon);
                usedCouponList.add(coupon);
            }
        }
    }

    private void printLog() {
        if (CollectionUtils.isEmpty(dbAllCouponList)) return;
        for (ActivityBO coupon : dbAllCouponList) {
            if (coupon != null && coupon.getDisableReason() != null && !coupon.getDisableReason().isEmpty()) {
                log.info("Coupon unavailable - code: {}, reasons: {}",
                        coupon.getCode(), coupon.getDisableReason());
            }
        }
    }

    private List<ActivityBO> findCouponFromActivity(List<ActivityBO> allActivityList) {
        List<ActivityBO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(allActivityList)) return result;
        for (ActivityBO activity : allActivityList) {
            if (activity != null && activity.getActivityType() != null && activity.getActivityType().isCoupon()) {
                result.add(activity);
            }
        }
        return result;
    }
}
