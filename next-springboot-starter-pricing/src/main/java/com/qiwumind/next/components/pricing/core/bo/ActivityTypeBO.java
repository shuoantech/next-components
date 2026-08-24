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
import lombok.Data;

/**
 * 活动类型描述符 - 封装 ActivityEnum 和 PromotionTypeEnum。
 * 提供类型检查的便捷方法。
 */
@Data
public class ActivityTypeBO {

    private ActivityEnum activityEnum;
    private PromotionTypeEnum promotionTypeEnum;

    public ActivityTypeBO(ActivityEnum activityEnum) {
        this.activityEnum = activityEnum;
    }

    public ActivityTypeBO(ActivityEnum activityEnum, PromotionTypeEnum promotionTypeEnum) {
        this.activityEnum = activityEnum;
        this.promotionTypeEnum = promotionTypeEnum;
    }

    public ActivityEnum getActivityEnum() {
        return activityEnum;
    }

    public boolean isPromotion() {
        return activityEnum == ActivityEnum.PROMOTION;
    }

    public boolean isCoupon() {
        return activityEnum == ActivityEnum.COUPON;
    }

    public boolean isCard() {
        return activityEnum == ActivityEnum.CARD;
    }

    public boolean isStrategy() {
        return activityEnum == ActivityEnum.STRATEGY;
    }

    public boolean isGiftPromotion() {
        return promotionTypeEnum != null && promotionTypeEnum.isGift();
    }

    public boolean isAttrPromotion() {
        return promotionTypeEnum != null && promotionTypeEnum.isAttrPromotion();
    }

    public boolean isShippingPromotion() {
        return promotionTypeEnum != null && promotionTypeEnum.isShipping();
    }

    public boolean isSinglePromotion() {
        return promotionTypeEnum != null && promotionTypeEnum.isSinglePromotion();
    }

    public boolean isMemberRightsPromotion() {
        return promotionTypeEnum != null && promotionTypeEnum.isMemberRightsPromotion();
    }
}
