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
 * 礼品卡计算引擎 - 处理礼品卡类型活动。
 * <p>
 * 礼品卡与优惠券类似但匹配逻辑不同。
 * 它们可能覆盖特定商品分类或整笔订单。
 */
@Slf4j
public class CardComputeEngine extends AbstractComputeEngine {

    public CardComputeEngine(List<ActivityBO> dbAllActivityList, PriceBO priceBO) {
        super(dbAllActivityList, priceBO);
    }

    @Override
    protected List<ActivityBO> filterActivities(List<ActivityBO> allActivityList) {
        List<ActivityBO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(allActivityList)) return result;
        for (ActivityBO activity : allActivityList) {
            if (activity != null && activity.getActivityType() != null && activity.getActivityType().isCard()) {
                result.add(activity);
            }
        }
        return result;
    }
}
