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
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 基础活动信息，所有活动类型（促销、优惠券、礼品卡、策略）共享。
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ActivityBaseBO {

    /** 唯一活动编码 */
    private String code;

    /** 活动类型 */
    private ActivityEnum activityEnum;

    /** 活动名称 */
    private String name;

    /** 内部逻辑唯一编码（同活动内去重使用） */
    private String innerLogicUniqueCode;

    /** 开始时间 */
    private Long startTime;

    /** 结束时间 */
    private Long endTime;

    /** 创建时间 */
    private Long createTime;

    /** 此活动计算出的优惠金额 */
    private Long discountAmount = 0L;

    /** 此活动是否被用户选中 */
    private boolean selected;

    /** 此活动不可用的原因 */
    private List<String> disableReason;

    /** 此活动适用的商品索引 */
    private List<Integer> eqIndexList;

    /**
     * 添加此活动影响的商品索引。
     * 用于追踪哪些商品被优惠了。
     */
    public void addEqIndex(int index) {
        if (eqIndexList == null) {
            eqIndexList = new java.util.ArrayList<>();
        }
        if (!eqIndexList.contains(index)) {
            eqIndexList.add(index);
        }
    }
}
