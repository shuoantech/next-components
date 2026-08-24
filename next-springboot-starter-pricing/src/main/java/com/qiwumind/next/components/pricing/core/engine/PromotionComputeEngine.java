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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 促销计算引擎 - 处理所有促销类型活动。
 * <p>
 * 促销分为两个执行子阶段：
 * <ol>
 *   <li><b>单品促销</b> - 按商品逐个应用（如单品折扣）
 *     <br>逐个遍历商品，每个商品找到其匹配的促销。
 *     这处理了"订单级数量限制"（只有前 N 件购物车商品享受促销）。</li>
 *   <li><b>多品促销</b> - 按商品组应用（如满减）
 *     <br>遍历促销，每个促销应用于其匹配的商品集合。</li>
 * </ol>
 * <p>
 * 赠品、运费、规格/加料促销由独立的引擎处理。
 */
@Slf4j
public class PromotionComputeEngine extends AbstractComputeEngine {

    public PromotionComputeEngine(List<ActivityBO> dbAllActivityList, PriceBO priceBO) {
        super(dbAllActivityList, priceBO);
    }

    @Override
    protected List<ActivityBO> filterActivities(List<ActivityBO> allActivityList) {
        List<ActivityBO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(allActivityList)) return result;

        for (ActivityBO activity : allActivityList) {
            if (activity == null) continue;
            if (activity.getActivityType() == null || !activity.getActivityType().isPromotion()) continue;
            // 排除赠品、运费、规格/加料促销（由其他引擎处理）
            if (activity.getActivityType().isGiftPromotion()) continue;
            if (activity.getActivityType().isAttrPromotion()) continue;
            if (activity.getActivityType().isShippingPromotion()) continue;
            result.add(activity);
        }
        return result;
    }

    @Override
    public void execute() {
        executeFirst();
        executeDistribute();
        executeSinglePromotion();
        executeMultiPromotion();
        printLog();
    }

    /**
     * 执行单品促销。
     * 每个商品找到其匹配的促销并应用。
     * 这处理了订单级数量限制（只有前 N 件购物车商品享受促销）。
     */
    private void executeSinglePromotion() {
        if (CollectionUtils.isEmpty(distributeList)) return;

        // 找出单品促销
        List<ActivityBO> singlePromotionList = Lists.newArrayList();
        for (ActivityBO promotion : distributeList) {
            if (promotion == null) continue;
            if (promotion.getActivityType() != null && promotion.getActivityType().isSinglePromotion()) {
                singlePromotionList.add(promotion);
            }
        }

        // 应用每个单品促销
        for (ActivityBO promotion : singlePromotionList) {
            if (!promotion.activityBaseInfoCheckAndMatch(priceBO)) continue;

            promotion.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            if (RuleFunction.checkAndAction(promotion, priceBO)) {
                priceBO.resetGoodsForMiddle();
                usedActivityList.add(promotion);
            }
        }
    }

    /**
     * 执行多品促销。
     * 每个促销应用于一组匹配的商品。
     */
    private void executeMultiPromotion() {
        if (CollectionUtils.isEmpty(distributeList)) return;

        for (ActivityBO promotion : distributeList) {
            if (promotion == null) continue;
            if (promotion.getActivityType() != null && promotion.getActivityType().isSinglePromotion()) continue;
            if (!promotion.activityBaseInfoCheckAndMatch(priceBO)) continue;

            promotion.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            if (RuleFunction.checkAndAction(promotion, priceBO)) {
                priceBO.resetGoodsForMiddle();
                usedActivityList.add(promotion);
            }
        }
    }
}
