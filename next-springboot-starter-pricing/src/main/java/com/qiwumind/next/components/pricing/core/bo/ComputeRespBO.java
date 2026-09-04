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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 计算结果 - 定价计算的最终输出。
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ComputeRespBO {

    /** 原始总金额（单位：分） */
    private Long totalAmount = 0L;

    /** 总优惠金额（单位：分） */
    private Long totalDiscount = 0L;

    /** 最终应付金额（单位：分） */
    private Long payAmount = 0L;

    /** 运费（单位：分） */
    private Long shippingFee = 0L;

    /** 已使用的促销 */
    private List<ActivityBO> usedPromotionList = new ArrayList<>();

    /** 已使用的优惠券 */
    private List<ActivityBO> usedCouponList = new ArrayList<>();

    /** 可用（但未使用）的优惠券 */
    private List<ActivityBO> canUseCouponList = new ArrayList<>();

    /** 不可用的优惠券 */
    private List<ActivityBO> cantUseCouponList = new ArrayList<>();

    /** 含优惠明细的商品列表 */
    private List<GoodsBO> goodsList = new ArrayList<>();

    /**
     * 根据商品和活动计算最终金额。
     */
    public void calculate() {
        totalDiscount = 0L;
        totalAmount = 0L;
        payAmount = 0L;

        for (GoodsBO goods : goodsList) {
            if (!goods.isSelected()) continue;
            long original = goods.getSaleAmount() * goods.getNum();
            long payable = goods.getComputeAmount() * goods.getNum();
            totalAmount += original;
            payAmount += payable;
            totalDiscount += (original - payable);
        }

        // 运费加到最终应付金额中
        payAmount += shippingFee;
    }
}
