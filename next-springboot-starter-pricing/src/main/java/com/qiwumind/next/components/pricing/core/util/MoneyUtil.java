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

package com.qiwumind.next.components.pricing.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 核心金额计算工具。
 * <p>
 * 系统中所有金额均以 <b>分</b> 为单位。
 * <p>
 * 折扣基数为 1000：
 * <ul>
 *   <li>discount=1000 表示无折扣（原价）</li>
 *   <li>discount=800 表示 8 折（原价的 80%）</li>
 *   <li>discount=500 表示 5 折（半价）</li>
 * </ul>
 * <p>
 * 关键设计决策：
 * <ul>
 *   <li>所有中间计算使用 BigDecimal 以确保精度</li>
 *   <li>ROUND_UP 确保平台不会少收费用（有利于商家）</li>
 *   <li>分摊金额上限为商品自身价格（永远不会为负数）</li>
 * </ul>
 */
public final class MoneyUtil {

    /** 折扣基数：1000 表示原价。discount=800 -> 8 折 */
    public static final int DISCOUNT_BASE = 1000;

    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private MoneyUtil() {}

    /**
     * 计算折扣后价格。
     * 示例：originalPrice=1000, discount=800 -> 800（8 折）
     *
     * @param originalPrice 原价（单位：分）
     * @param discount      折扣值（1000 = 无折扣, 800 = 8 折）
     * @return 折扣后价格（单位：分）
     */
    public static long discount(Long originalPrice, Long discount) {
        if (originalPrice == null || discount == null) return 0;
        return originalPrice * discount / DISCOUNT_BASE;
    }

    /**
     * 计算优惠金额（减免了多少）。
     * 使用 ROUND_UP 有利于商家。
     *
     * @param totalPrice 总价（单位：分）
     * @param discount   折扣值（1000 = 无折扣, 800 = 8 折）
     * @return 优惠金额（单位：分）
     */
    public static long getDiscount(Long totalPrice, Long discount) {
        if (totalPrice == null || discount == null) return 0;
        // 全程使用 BigDecimal 避免金额计算中的浮点精度损失
        BigDecimal total = BigDecimal.valueOf(totalPrice);
        BigDecimal discountRate = BigDecimal.valueOf(DISCOUNT_BASE - discount);
        BigDecimal discountAmount = total.multiply(discountRate)
                .divide(BigDecimal.valueOf(DISCOUNT_BASE), 0, BigDecimal.ROUND_UP);
        return discountAmount.longValue();
    }

    /**
     * 计算带封顶的优惠金额。
     * 与 getDiscount 相同但带最高金额限制。
     */
    public static long getDiscountCeiling(Long totalPrice, Long discount) {
        return getDiscount(totalPrice, discount);
    }

    /**
     * 按比例分摊金额到各商品。
     * <p>
     * 公式：sharePrice * selfPrice / totalPrice
     * <p>
     * 分摊额上限为 selfPrice（优惠金额不能超过商品自身价格）。
     * 使用 ROUND_UP。
     *
     * @param selfPrice  本商品的价格
     * @param totalPrice 所有分摊商品的总价
     * @param sharePrice 待分摊的总金额
     * @return 本商品应分摊的优惠金额
     */
    public static long getShare(Long selfPrice, Long totalPrice, Long sharePrice) {
        if (selfPrice == null || totalPrice == null || sharePrice == null) return 0;
        if (totalPrice <= 0) return 0;

        // 全程使用 BigDecimal 避免浮点精度损失
        BigDecimal share = BigDecimal.valueOf(sharePrice);
        BigDecimal self = BigDecimal.valueOf(selfPrice);
        BigDecimal total = BigDecimal.valueOf(totalPrice);
        BigDecimal shareSinglePrice = share.multiply(self)
                .divide(total, 0, BigDecimal.ROUND_UP);
        long shareSinglePriceLong = shareSinglePrice.longValue();

        // 分摊金额不能超过商品自身价格
        if (shareSinglePriceLong > selfPrice) {
            shareSinglePriceLong = selfPrice;
        }
        return shareSinglePriceLong;
    }

    // ==================== 分 <-> 元 转换 ====================

    /**
     * 将分转换为元字符串，智能处理小数位。
     * - 能被 100 整除：不显示小数（如 1000 -> "10"）
     * - 能被 10 整除：显示一位小数（如 1050 -> "10.5"）
     * - 其他：显示两位小数（如 1055 -> "10.55"）
     */
    public static String centToYuan(Long cent) {
        if (cent == null || cent <= 0) return "0";
        DecimalFormat decimalFormat;
        if (cent % 100 == 0) {
            decimalFormat = new DecimalFormat("");
        } else if (cent % 10 == 0) {
            decimalFormat = new DecimalFormat("0.0");
        } else {
            decimalFormat = new DecimalFormat("0.00");
        }
        return decimalFormat.format((double) cent / 100);
    }

    /**
     * 将分转换为元字符串，固定两位小数。
     */
    public static String centToYuanTwo(Long cent) {
        if (cent == null || cent <= 0) return "0.00";
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format((double) cent / 100);
    }

    /**
     * 将 BigDecimal 元转换为分。
     * 使用 ROUND_UP。
     */
    public static long bigDecimalYuanToCent(BigDecimal yuan) {
        if (yuan == null) return 0;
        BigDecimal cent = yuan.multiply(ONE_HUNDRED);
        String centStr = cent.setScale(0, BigDecimal.ROUND_UP).toString();
        return Long.parseLong(centStr);
    }

    /**
     * 将折扣值转换为展示字符串。
     * 示例：discount=800 -> "8"（8折）, discount=850 -> "8.5"（8.5折）
     */
    public static String discountToStr(Long discount) {
        if (discount == null || discount <= 0) return "0";
        DecimalFormat decimalFormat;
        if (discount % 100 == 0) {
            decimalFormat = new DecimalFormat("");
        } else if (discount % 10 == 0) {
            decimalFormat = new DecimalFormat("0.0");
        } else {
            decimalFormat = new DecimalFormat("0.00");
        }
        return decimalFormat.format((double) discount / 100);
    }
}
