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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品业务对象。
 * <p>
 * 所有金额均以 <b>分</b> 为单位。
 * {@code computeAmount} 是动态计算的应付金额，随着促销/优惠券的叠加应用而逐步减少。
 * 初始值等于 {@code saleAmount}，之后递减。
 */
@Data
public class GoodsBO implements Comparable<GoodsBO> {

    /** 商品 ID */
    private Long itemId;

    /** 商品名称 */
    private String itemName;

    /** 销售价（原价，单位：分） */
    private Long saleAmount;

    /**
     * 当前计算的应付金额（单位：分）。
     * 这是每次促销逐次减免后的动态剩余金额。
     */
    private Long computeAmount;

    /** 数量 */
    private Integer num = 1;

    /** 此商品是否在购物车中被选中 */
    private boolean selected = true;

    /** 商品类型（普通、赠品等） */
    private Integer goodsType;

    /** 分类 ID */
    private Long categoryId;

    /** 品牌 ID */
    private Long brandId;

    /** 门店 ID */
    private Long storeId;

    /** 购物车排序索引 */
    private Integer sortIndex;

    /** 优惠明细列表 - 记录哪些活动优惠了此商品以及优惠了多少 */
    private List<DiscountBO> discountDetailList = new ArrayList<>();

    /** 对等索引，用于在分摊时追踪位置 */
    private int eqIndex;

    // ==================== 计算属性 ====================

    /** 总金额 = computeAmount * num */
    public long totalAmount() {
        return (computeAmount == null ? 0 : computeAmount) * (num == null ? 1 : num);
    }

    /** 此商品是否可被优惠（computeAmount > 0） */
    public boolean canDiscount() {
        return computeAmount != null && computeAmount > 0;
    }

    /** 追踪用的唯一对等索引 */
    public int eqIndex() {
        return eqIndex;
    }

    /**
     * 按 computeAmount 降序排列（价格最高的在前）。
     * 这是商品选择规则使用的默认排序方式。
     */
    @Override
    public int compareTo(GoodsBO other) {
        long thisAmount = this.computeAmount == null ? 0 : this.computeAmount;
        long otherAmount = other.computeAmount == null ? 0 : other.computeAmount;
        return Long.compare(otherAmount, thisAmount); // 降序
    }
}
