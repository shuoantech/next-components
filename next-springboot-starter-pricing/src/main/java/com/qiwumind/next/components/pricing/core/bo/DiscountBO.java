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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * 优惠明细 - 记录某个活动对某个商品优惠了多少。
 * <p>
 * 每个商品维护一个此类列表，每个适用优惠的活动对应一条记录。
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class DiscountBO {

    /** 活动枚举类型 */
    private ActivityEnum activityEnum;

    /** 活动编码 */
    private String code;

    /** 内部逻辑唯一编码 */
    private String innerLogicUniqueCode;

    /** 活动名称（用于展示） */
    private String activityName;

    /** 此活动应用的优惠金额（单位：分） */
    private Long discount = 0L;

    public DiscountBO() {}

    public DiscountBO(ActivityBaseBO activity) {
        this.activityEnum = activity.getActivityEnum();
        this.code = activity.getCode();
        this.innerLogicUniqueCode = activity.getInnerLogicUniqueCode();
    }
}
