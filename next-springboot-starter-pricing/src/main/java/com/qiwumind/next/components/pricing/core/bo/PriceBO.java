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
 * 价格上下文对象 - 贯穿整个计算管线的核心定价上下文。
 * <p>
 * 包含：
 * <ul>
 *   <li>订单/购物车中所有商品</li>
 *   <li>用户信息（ID、等级、VIP 状态）</li>
 *   <li>门店/品牌/渠道上下文</li>
 *   <li>用户选中的优惠券（显式选择的优惠券）</li>
 *   <li>运费</li>
 *   <li>当前时间戳</li>
 *   <li>中间商品（规则引擎执行用的工作副本）</li>
 * </ul>
 * <p>
 * "中间商品"模式：在每次活动的规则执行前，商品状态被复制到 middleGoods。
 * 若规则执行成功，middleGoods 的变更通过 {@link #resetGoodsForMiddle()} 提交回正式商品列表。
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class PriceBO {

    // ==================== 商品 ====================

    /** 购物车/订单中的所有商品 */
    private List<GoodsBO> goodsList = new ArrayList<>();

    /** 中间商品 - 规则引擎试运行的工作副本 */
    private List<GoodsBO> middleGoodsList = new ArrayList<>();

    // ==================== 用户信息 ====================

    private Long userId;
    private Integer userVipLevel;
    private boolean vipUser;

    // ==================== 门店 / 品牌 / 渠道 ====================

    private Long storeId;
    private Long brandId;
    private String brand;
    private Integer channel;
    private Integer clientPlatform;
    private Long cityId;
    private Integer storeType;
    private Integer diningType; // 1: 外卖, 2: 自取, 3: 堂食

    // ==================== 优惠券选择 ====================

    /** 用户选中的优惠券编码 */
    private List<String> choiceCouponList;

    /** 已使用的优惠券编码（计算过程中填充） */
    private List<String> usedCouponCodeList = new ArrayList<>();

    // ==================== 订单信息 ====================

    private Long shippingFee = 0L;
    private Long currentTime = System.currentTimeMillis();
    private String orderType;

    // ==================== 中间商品管理 ====================

    /**
     * 重置中间商品 - 将当前商品状态复制到中间态以进行试运行。
     * 在每次活动的规则执行前调用。
     */
    public void resetMiddleGoods() {
        middleGoodsList.clear();
        for (GoodsBO goods : goodsList) {
            GoodsBO copy = new GoodsBO();
            copy.setItemId(goods.getItemId());
            copy.setItemName(goods.getItemName());
            copy.setSaleAmount(goods.getSaleAmount());
            copy.setComputeAmount(goods.getComputeAmount());
            copy.setNum(goods.getNum());
            copy.setSelected(goods.isSelected());
            copy.setEqIndex(goods.getEqIndex());
            copy.setDiscountDetailList(new ArrayList<>(goods.getDiscountDetailList()));
            middleGoodsList.add(copy);
        }
    }

    /**
     * 将中间商品变更提交回正式商品。
     * 在规则执行成功后调用。
     */
    public void resetGoodsForMiddle() {
        for (int i = 0; i < goodsList.size() && i < middleGoodsList.size(); i++) {
            GoodsBO real = goodsList.get(i);
            GoodsBO middle = middleGoodsList.get(i);
            real.setComputeAmount(middle.getComputeAmount());
            real.setDiscountDetailList(middle.getDiscountDetailList());
        }
    }

    // ==================== 计算金额 ====================

    /**
     * 所有选中商品的原始总金额（优惠前）。
     */
    public long totalOriginalAmount() {
        long total = 0;
        for (GoodsBO goods : goodsList) {
            if (goods.isSelected() && goods.getSaleAmount() != null) {
                total += goods.getSaleAmount() * goods.getNum();
            }
        }
        return total;
    }

    /**
     * 当前总应付金额（已应用促销优惠后、优惠券前）。
     */
    public long currentPayAmount() {
        long total = 0;
        for (GoodsBO goods : goodsList) {
            if (goods.isSelected() && goods.getComputeAmount() != null) {
                total += goods.getComputeAmount() * goods.getNum();
            }
        }
        return total;
    }

    /**
     * 可使用优惠券的商品当前应付金额。
     */
    public long orderCanUseCouponGoodsCurrentPayAmount() {
        return currentPayAmount();
    }

    // ==================== 优惠券辅助方法 ====================

    public List<String> choiceAllCouponCodeList() {
        return choiceCouponList != null ? choiceCouponList : new ArrayList<>();
    }

    public void addUsedCouponCode(ActivityBO coupon) {
        if (coupon.getCode() != null) {
            usedCouponCodeList.add(coupon.getCode());
        }
    }

    // ==================== 商品访问器 ====================

    /**
     * 获取所有选中的商品（可被优惠的）。
     */
    public List<GoodsBO> selectedGoodsList() {
        List<GoodsBO> result = new ArrayList<>();
        for (GoodsBO goods : goodsList) {
            if (goods.isSelected()) {
                result.add(goods);
            }
        }
        return result;
    }
}
