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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 活动业务对象 - 促销、优惠券、礼品卡、策略的统一模型。
 * <p>
 * 这是规则引擎操作的核心领域对象。
 * 每个活动携带三个 Aviator 表达式字符串（规则链）和一个元数据映射，
 * 供 Aviator 函数在执行期间读取。
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ActivityBO extends ActivityBaseBO {

    /** 促销类型（非促销活动为 null） */
    private PromotionTypeEnum promotionType;

    /** 活动类型描述符 */
    private ActivityTypeBO activityType;

    /**
     * 重写 getActivityEnum，当直接字段为 null 时委托给 activityType。
     */
    @Override
    public ActivityEnum getActivityEnum() {
        if (super.getActivityEnum() != null) {
            return super.getActivityEnum();
        }
        return activityType != null ? activityType.getActivityEnum() : null;
    }

    // ==================== Aviator 规则链 ====================

    /** 第一规则链表达式 - 可用性检查 */
    private String checkExpression;

    /** 第二规则链表达式 - 实际计算 */
    private String computeExpression;

    /** 展示规则链表达式 - 展示信息生成 */
    private String showExpression;

    // ==================== 元数据 ====================

    /** Aviator 函数的元数据映射（每个规则的 JSON 解析配置） */
    private Map<String, Object> metaMap;

    // ==================== 计算状态 ====================

    /** 此活动匹配的商品基础集合 */
    private java.util.Set<GoodsBO> goodsSet;

    /** 面额（用于优惠券） */
    private Long faceAmount;

    // ==================== 业务方法 ====================

    /**
     * 基本信息检查与匹配 - 验证时间、渠道、用户等级等。
     * 简化版本：检查活动是否在有效时间范围内。
     */
    public boolean activityBaseInfoCheckAndMatch(PriceBO priceBO) {
        if (getStartTime() != null && priceBO.getCurrentTime() != null
                && priceBO.getCurrentTime() < getStartTime()) {
            return false;
        }
        if (getEndTime() != null && priceBO.getCurrentTime() != null
                && priceBO.getCurrentTime() > getEndTime()) {
            return false;
        }
        return true;
    }

    /**
     * 优惠券基本信息检查。
     */
    public boolean couponBaseInfoCheck(PriceBO priceBO) {
        return activityBaseInfoCheckAndMatch(priceBO);
    }

    /**
     * 初始化优惠券的已购商品信息。
     */
    public void couponInitAllBuyGoods(PriceBO priceBO) {
        // 完整实现中，此处会为优惠券设置商品匹配逻辑
    }
}
