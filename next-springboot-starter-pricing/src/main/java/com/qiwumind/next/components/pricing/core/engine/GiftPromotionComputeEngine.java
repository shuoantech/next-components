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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 赠品促销计算引擎 - 处理满赠类促销。
 * <p>
 * 赠品促销不减少现有商品的应付金额；
 * 而是向订单中添加赠品商品。
 * 支持的类型：
 * <ul>
 *   <li>LADDER_FULL_X_CENT_GIFT - 满 X 分送赠品</li>
 *   <li>LADDER_FULL_X_NUM_GIFT - 满 X 件送赠品</li>
 *   <li>PRE_FULL_X_CENT_GIFT - 每满 X 分送赠品</li>
 *   <li>PRE_FULL_X_NUM_GIFT - 每满 X 件送赠品</li>
 * </ul>
 */
@Slf4j
public class GiftPromotionComputeEngine extends AbstractComputeEngine {

    public GiftPromotionComputeEngine(List<ActivityBO> dbAllActivityList, PriceBO priceBO) {
        super(dbAllActivityList, priceBO);
    }

    @Override
    protected List<ActivityBO> filterActivities(List<ActivityBO> allActivityList) {
        List<ActivityBO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(allActivityList)) return result;
        for (ActivityBO activity : allActivityList) {
            if (activity != null && activity.getActivityType() != null
                    && activity.getActivityType().isPromotion()
                    && activity.getActivityType().isGiftPromotion()) {
                result.add(activity);
            }
        }
        return result;
    }
}
