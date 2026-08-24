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

import java.util.List;

/**
 * 抽象计算引擎 - 所有活动类型计算引擎的基类。
 * <p>
 * 所有计算引擎遵循统一的三阶段模式：
 *
 * <h3>三阶段执行模式</h3>
 * <ol>
 *   <li><b>executeFirst()</b> - 可用性检查（通过 Aviator 执行第一规则链）
 *     <ul>
 *       <li>对每个活动：重置中间商品，执行 availableCheck</li>
 *       <li>若通过：加入 firstFilterList</li>
 *     </ul>
 *   </li>
 *   <li><b>executeDistribute()</b> - 冲突解决与优先级排序
 *     <ul>
 *       <li>解决活动间的互斥冲突</li>
 *       <li>按优先级排序（单品优先，多品次之等）</li>
 *       <li>应用订单级使用次数限制</li>
 *     </ul>
 *   </li>
 *   <li><b>executeSecond()</b> - 实际计算（通过 Aviator 执行第二规则链）
 *     <ul>
 *       <li>对每个分配后的活动：重置中间商品，执行 checkAndAction</li>
 *       <li>若成功：将中间商品变更提交到正式商品</li>
 *       <li>加入 usedActivityList</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>中间商品模式</h3>
 * 在每次活动的规则执行前，当前商品状态被复制到"中间商品"（工作副本）。
 * Aviator 函数操作的是中间商品。若规则成功，中间商品的变更被提交回去。
 * 若规则失败，正式商品状态不变——允许下一个活动以干净的状态重试。
 */
@Getter
@Slf4j
public abstract class AbstractComputeEngine {

    /** 通过第一轮过滤（可用性检查）的活动 */
    protected List<ActivityBO> firstFilterList = Lists.newArrayList();

    /** 分配后的活动（冲突解决、优先级排序后） */
    protected List<ActivityBO> distributeList = Lists.newArrayList();

    /** 成功应用的活动 */
    protected List<ActivityBO> usedActivityList = Lists.newArrayList();

    /** 数据源中的所有活动 */
    protected List<ActivityBO> dbAllActivityList;

    /** 定价上下文 */
    protected PriceBO priceBO;

    /**
     * 使用所有活动和定价上下文构造引擎。
     * 将活动列表过滤为仅本引擎类型处理的活动。
     */
    protected AbstractComputeEngine(List<ActivityBO> dbAllActivityList, PriceBO priceBO) {
        this.dbAllActivityList = filterActivities(dbAllActivityList);
        this.priceBO = priceBO;
    }

    /**
     * 执行完整的计算管线。
     * 可重写以自定义执行顺序。
     */
    public void execute() {
        executeFirst();
        executeDistribute();
        executeSecond();
        printLog();
    }

    /**
     * 阶段 1：第一轮过滤 - 可用性检查。
     * 对每个活动，检查其是否适用于当前订单。
     */
    protected void executeFirst() {
        if (CollectionUtils.isEmpty(dbAllActivityList)) return;

        for (ActivityBO activity : dbAllActivityList) {
            if (activity == null) continue;
            if (!activity.activityBaseInfoCheckAndMatch(priceBO)) continue;

            priceBO.resetMiddleGoods();
            if (RuleFunction.availableCheck(activity, priceBO)) {
                firstFilterList.add(activity);
            }
        }
    }

    /**
     * 阶段 2：分配 - 冲突解决与优先级排序。
     * 默认实现：直接透传（无冲突解决）。
     * 子类可重写以实现互斥/优先级逻辑。
     */
    protected void executeDistribute() {
        this.distributeList = Lists.newArrayList(firstFilterList);
    }

    /**
     * 阶段 3：第二轮计算 - 实际优惠计算。
     * 对每个活动执行计算规则链并应用变更。
     */
    protected void executeSecond() {
        if (CollectionUtils.isEmpty(distributeList)) return;

        for (ActivityBO activity : distributeList) {
            if (activity == null) continue;
            if (!activity.activityBaseInfoCheckAndMatch(priceBO)) continue;

            activity.setDiscountAmount(0L);
            priceBO.resetMiddleGoods();
            if (RuleFunction.checkAndAction(activity, priceBO)) {
                priceBO.resetGoodsForMiddle();
                usedActivityList.add(activity);
            }
        }
    }

    /**
     * 打印不可用活动的日志用于调试。
     */
    protected void printLog() {
        if (CollectionUtils.isEmpty(this.dbAllActivityList)) return;
        for (ActivityBO activity : dbAllActivityList) {
            if (activity == null) continue;
            if (activity.getDisableReason() != null && !activity.getDisableReason().isEmpty()) {
                log.info("Activity unavailable - code: {}, reasons: {}",
                        activity.getCode(), activity.getDisableReason());
            }
        }
    }

    /**
     * 过滤活动列表，仅保留本引擎处理的活动。
     * 子类必须重写。
     */
    protected abstract List<ActivityBO> filterActivities(List<ActivityBO> allActivityList);
}
