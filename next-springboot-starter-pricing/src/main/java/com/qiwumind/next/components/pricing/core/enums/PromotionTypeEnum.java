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

package com.qiwumind.next.components.pricing.core.enums;

/**
 * 促销类型枚举 - 所有支持的促销/优惠类型的核心分类。
 * <p>
 * 每种促销类型绑定三个 Aviator 规则链：
 * <ul>
 *   <li>firstRuleChainStr  - 可用性检查（时间、渠道、用户、商品匹配）</li>
 *   <li>secondRuleChainStr - 实际计算（优惠计算与金额分摊）</li>
 *   <li>showRuleChainStr   - 展示信息生成（标签、提示、角标）</li>
 * </ul>
 * <p>
 * 编码范围：
 * <pre>
 *  101-110  多品满减
 *  201-204  赠品促销（满赠）
 *  301-306  运费促销（减配送费）
 *  401-405  单品促销（单品/套餐优惠）
 *  411-422  规格/加料促销
 *  501-504  POS 一口价促销
 *  601-604  POS 折扣促销
 *  701-704  POS 直减促销
 *  801-810  POS 满减促销
 *  901+     会员权益促销
 * </pre>
 */
public enum PromotionTypeEnum {

    // ==================== 多品满减 (101-110) ====================

    /** Every full X cents, off N cents (每满X分减N分) */
    PER_FULL_X_CENT_OFF_N_CENT(101, 10, "满减活动", "每满X分减N分"),

    /** Ladder: full X cents, off N cents (阶梯满X分减N分) */
    LADDER_FULL_X_CENT_OFF_N_CENT(102, 10, "满减活动", "阶梯满X分减N分"),

    /** Ladder: full X cents, N% discount (阶梯满X分打N折) */
    LADDER_FULL_X_CENT_DISCOUNT_N(103, 10, "满减活动", "阶梯满X分打N折"),

    /** Ladder: full X items, N% discount (阶梯满X件打N折) */
    LADDER_FULL_X_NUM_DISCOUNT_N(104, 10, "满减活动", "阶梯满X件打N折"),

    /** Ladder: full X items, free N items (阶梯满X件免N件) */
    LADDER_FULL_X_NUM_FREE_N_NUM(105, 10, "满减活动", "阶梯满X件免N件"),

    /** Every full X items, discount 1 item (每满X件折扣1件) */
    PER_FULL_X_NUM_DISCOUNT_ONE_NUM(106, 10, "满减活动", "每满X件折扣1件"),

    /** Full X items, discount 1 item (满X件折扣1件) */
    FULL_X_NUM_DISCOUNT_ONE_NUM(107, 10, "满减活动", "满X件折扣1件"),

    /** Ladder: full X items, group free N items (阶梯满X件分组免N件) */
    LADDER_FULL_X_NUM_GROUP_FREE_N_NUM(108, 10, "满减活动", "阶梯满X件分组免N件"),

    /** All items N% discount (所有商品打N折) */
    ALL_DISCOUNT_N(109, 10, "满减活动", "所有商品打N折"),

    /** Ladder: full X items, set price to N cents (阶梯满X件设置N分) */
    LADDER_FULL_X_NUM_SET_N_CENT(110, 10, "满减活动", "阶梯满X件设置N分"),

    // ==================== 赠品促销 (201-204) ====================

    /** Ladder: full X cents, get gift (阶梯满X分赠品) */
    LADDER_FULL_X_CENT_GIFT(201, 20, "满赠", "阶梯满X分赠品"),

    /** Ladder: full X items, get gift (阶梯满X件赠品) */
    LADDER_FULL_X_NUM_GIFT(202, 20, "满赠", "阶梯满X件赠品"),

    /** Every full X cents, get gift (每满X分赠品) */
    PRE_FULL_X_CENT_GIFT(203, 20, "满赠", "每满X分赠品"),

    /** Every full X items, get gift (每满X件赠品) */
    PRE_FULL_X_NUM_GIFT(204, 20, "满赠", "每满X件赠品"),

    // ==================== 运费促销 (301-306) ====================

    /** No threshold, free shipping (无门槛免配送费) */
    FREE_SHIPPING(301, 10, "减配送费", "无门槛免配送费"),

    /** Full X cents, free shipping (满X分免配送费) */
    FULL_X_CENT_FREE_SHIPPING(302, 10, "减配送费", "满X分免配送费"),

    /** Full X items, free shipping (满X件免配送费) */
    FULL_X_NUM_FREE_SHIPPING(303, 10, "减配送费", "满X件免配送费"),

    /** Shipping off N cents (运费减N分) */
    SHIPPING_OFF_N_CENT(304, 10, "减配送费", "运费减N分"),

    /** Full X cents, shipping off N cents (满X分运费减N分) */
    FULL_X_CENT_SHIPPING_OFF_N_CENT(305, 10, "减配送费", "满X分运费减N分"),

    /** Full X items, shipping off N cents (满X件运费减N分) */
    FULL_X_NUM_SHIPPING_OFF_N_CENT(306, 10, "减配送费", "满X件运费减N分"),

    // ==================== 单品促销 (401-422) ====================

    /** Single item set price (单品定价) */
    DAN_PIN_SET_PRICE(401, 40, "商品优惠", "单品定价"),

    /** Single item direct off (单品直减) */
    DAN_PIN_ZHI_JIAN(402, 40, "商品优惠", "单品直减"),

    /** Single item discount (单品折扣) */
    DAN_PIN_ZHE_KOU(403, 40, "商品优惠", "单品折扣"),

    /** Combo set price, child add-on (套餐定价子品加价) */
    TAO_CAN_SET_PRICE(404, 40, "商品优惠", "套餐定价子品加价"),

    /** Combo direct off, child add-on (套餐直减子品加价) */
    TAO_CAN_ZHI_JIAN(405, 40, "商品优惠", "套餐直减子品加价"),

    /** Spec free (规格免费) */
    SPEC_FREE(411, 40, "规格优惠", "规格免费"),

    /** Spec off (规格直减) */
    SPEC_OFF(412, 40, "规格优惠", "规格直减"),

    /** Add-on free (加料免费) */
    ACCESSORIES_FREE(421, 40, "加料优惠", "加料免费"),

    /** Add-on off (加料直减) */
    ACCESSORIES_OFF(422, 40, "加料优惠", "加料直减"),

    // ==================== POS 促销 (501-704) ====================

    /** POS set different price (POS一口价) */
    POS_SET_DIFF_PRICE(503, 50, "POS商品优惠", "POS一口价"),

    /** POS set different price, one item (POS一口价优惠一件) */
    POS_SET_DIFF_PRICE_ONE_NUM(504, 50, "POS商品优惠", "POS一口价优惠一件"),

    /** POS discount all (POS折扣) */
    POS_DISCOUNT(601, 60, "POS商品折扣", "POS折扣"),

    /** POS discount one item (POS折扣优惠一件) */
    POS_DISCOUNT_ONE_NUM(602, 60, "POS商品折扣", "POS折扣优惠一件"),

    /** POS different discount (POS不同折扣) */
    POS_DIFF_DISCOUNT(603, 60, "POS商品折扣", "POS不同折扣"),

    /** POS different discount, one item (POS不同折扣优惠一件) */
    POS_DIFF_DISCOUNT_ONE_NUM(604, 60, "POS商品折扣", "POS不同折扣优惠一件"),

    /** POS off amount (POS直减) */
    POS_OFF(701, 70, "POS商品直减", "POS直减"),

    /** POS off amount, one item (POS直减优惠一件) */
    POS_OFF_ONE_NUM(702, 70, "POS商品直减", "POS直减优惠一件"),

    /** POS different off amount (POS不同直减) */
    POS_DIFF_OFF(703, 70, "POS商品直减", "POS不同直减"),

    /** POS different off amount, one item (POS不同直减优惠一件) */
    POS_DIFF_OFF_ONE_NUM(704, 70, "POS商品直减", "POS不同直减优惠一件"),

    // ==================== POS 满减 (801-810) ====================

    POS_PER_FULL_X_CENT_OFF_N_CENT(801, 10, "POS满减活动", "每满X分减N分"),
    POS_LADDER_FULL_X_CENT_OFF_N_CENT(802, 10, "POS满减活动", "阶梯满X分减N分"),
    POS_LADDER_FULL_X_CENT_DISCOUNT_N(803, 10, "POS满减活动", "阶梯满X分打N折"),
    POS_LADDER_FULL_X_NUM_DISCOUNT_N(804, 10, "POS满减活动", "阶梯满X件打N折"),
    POS_LADDER_FULL_X_NUM_FREE_N_NUM(805, 10, "POS满减活动", "阶梯满X件免N件"),
    POS_PER_FULL_X_NUM_DISCOUNT_ONE_NUM(806, 10, "POS满减活动", "每满X件折扣1件"),
    POS_FULL_X_NUM_DISCOUNT_ONE_NUM(807, 10, "POS满减活动", "满X件折扣1件"),
    POS_LADDER_FULL_X_NUM_GROUP_FREE_N_NUM(808, 10, "POS满减活动", "阶梯满X件分组免N件"),
    POS_LADDER_FULL_X_NUM_SET_N_CENT(810, 10, "POS满减活动", "阶梯满X件设置N分"),

    // ==================== 会员权益 (901+) ====================

    /** Member rights breakfast card (会员权益早餐卡) */
    MEMBER_RIGHTS_BREAKFIRST(901, 90, "会员权益卡", "会员权益早餐卡");

    private final int code;
    private final int bigCode;
    private final String name;
    private final String desc;

    PromotionTypeEnum(int code, int bigCode, String name, String desc) {
        this.code = code;
        this.bigCode = bigCode;
        this.name = name;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public int getBigCode() { return bigCode; }
    public String getName() { return name; }
    public String getDesc() { return desc; }

    public static PromotionTypeEnum getEnumByCode(Integer value) {
        if (value == null || value < 0) return null;
        for (PromotionTypeEnum item : values()) {
            if (value == item.getCode()) return item;
        }
        return null;
    }

    /** 是否为赠品促销？ */
    public boolean isGift() {
        return this == LADDER_FULL_X_CENT_GIFT || this == LADDER_FULL_X_NUM_GIFT
                || this == PRE_FULL_X_CENT_GIFT || this == PRE_FULL_X_NUM_GIFT;
    }

    /** 是否为运费促销？ */
    public boolean isShipping() {
        return this == FREE_SHIPPING || this == FULL_X_CENT_FREE_SHIPPING
                || this == FULL_X_NUM_FREE_SHIPPING || this == SHIPPING_OFF_N_CENT
                || this == FULL_X_CENT_SHIPPING_OFF_N_CENT || this == FULL_X_NUM_SHIPPING_OFF_N_CENT;
    }

    /** 是否为单品促销？ */
    public boolean isSinglePromotion() {
        return this == DAN_PIN_SET_PRICE || this == DAN_PIN_ZHI_JIAN
                || this == TAO_CAN_SET_PRICE || this == TAO_CAN_ZHI_JIAN
                || this == DAN_PIN_ZHE_KOU;
    }

    /** 是否为规格/加料促销？ */
    public boolean isAttrPromotion() {
        return this == SPEC_FREE || this == SPEC_OFF
                || this == ACCESSORIES_FREE || this == ACCESSORIES_OFF;
    }

    /** 是否为 POS 促销？ */
    public boolean isPosPromotion() {
        return code >= 500 && code < 900 && !isMemberRightsPromotion();
    }

    /** 是否为会员权益促销？ */
    public boolean isMemberRightsPromotion() {
        return this == MEMBER_RIGHTS_BREAKFIRST;
    }

    /**
     * 促销优先级，用于标准品牌排序。
     * 数值越小 = 优先级越高（越先应用）。
     */
    public int priority() {
        if (isSinglePromotion() || isAttrPromotion()) return 1;
        if (this == LADDER_FULL_X_NUM_FREE_N_NUM || this == LADDER_FULL_X_NUM_GROUP_FREE_N_NUM) return 2;
        if (code >= 101 && code <= 110) return 3;
        if (this == PER_FULL_X_NUM_DISCOUNT_ONE_NUM || this == FULL_X_NUM_DISCOUNT_ONE_NUM) return 4;
        if (isGift()) return 5;
        if (isShipping()) return 6;
        return 100;
    }
}
