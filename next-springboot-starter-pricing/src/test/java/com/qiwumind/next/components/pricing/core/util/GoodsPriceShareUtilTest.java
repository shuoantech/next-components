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

import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.ActivityTypeBO;
import com.qiwumind.next.components.pricing.core.bo.DiscountBO;
import com.qiwumind.next.components.pricing.core.bo.GoodsBO;
import com.qiwumind.next.components.pricing.core.enums.ActivityEnum;
import com.qiwumind.next.components.pricing.core.enums.PromotionTypeEnum;
import com.qiwumind.next.components.pricing.core.enums.SelectGoodsRuleEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class GoodsPriceShareUtilTest {

    private ActivityBO activity;
    private List<GoodsBO> goodsList;

    @Before
    public void setUp() {
        activity = new ActivityBO();
        activity.setActivityType(new ActivityTypeBO(ActivityEnum.PROMOTION, PromotionTypeEnum.PER_FULL_X_CENT_OFF_N_CENT));
        activity.setCode("PROMO_SHARE");
        activity.setInnerLogicUniqueCode("PROMO_SHARE");

        // 3 goods: Coffee1500, Tea1000, Juice2000 (sorted desc by ComputeAmount: 2000, 1500, 1000)
        goodsList = new ArrayList<>();

        GoodsBO juice = new GoodsBO();
        juice.setItemId(3L);
        juice.setItemName("Juice");
        juice.setComputeAmount(2000L);
        juice.setEqIndex(0);

        GoodsBO coffee = new GoodsBO();
        coffee.setItemId(1L);
        coffee.setItemName("Coffee");
        coffee.setComputeAmount(1500L);
        coffee.setEqIndex(1);

        GoodsBO tea = new GoodsBO();
        tea.setItemId(2L);
        tea.setItemName("Tea");
        tea.setComputeAmount(1000L);
        tea.setEqIndex(2);

        goodsList.add(juice);
        goodsList.add(coffee);
        goodsList.add(tea);
    }

    // ==================== shareAmount() ====================

    @Test
    public void testShareAmount_zeroAmount() {
        long result = GoodsPriceShareUtil.shareAmount(goodsList, 0L, activity);
        assertEquals(0L, result);
    }

    @Test
    public void testShareAmount_nullAmount() {
        long result = GoodsPriceShareUtil.shareAmount(goodsList, null, activity);
        assertEquals(0L, result);
    }

    @Test
    public void testShareAmount_negativeAmount() {
        long result = GoodsPriceShareUtil.shareAmount(goodsList, -100L, activity);
        assertEquals(0L, result);
    }

    @Test
    public void testShareAmount_emptyList() {
        long result = GoodsPriceShareUtil.shareAmount(new ArrayList<>(), 500L, activity);
        assertEquals(0L, result);
    }

    @Test
    public void testShareAmount_coversAll() {
        // Total = 4500, discount = 4500 -> all free
        long discount = GoodsPriceShareUtil.shareAmount(goodsList, 4500L, activity);

        for (GoodsBO goods : goodsList) {
            assertEquals(Long.valueOf(0L), goods.getComputeAmount());
        }
        assertEquals(4500L, discount);
    }

    @Test
    public void testShareAmount_coversMoreThanAll() {
        // Total = 4500, discount = 5000 (exceeds total) -> all free
        long discount = GoodsPriceShareUtil.shareAmount(goodsList, 5000L, activity);

        for (GoodsBO goods : goodsList) {
            assertEquals(Long.valueOf(0L), goods.getComputeAmount());
        }
        assertEquals(4500L, discount);
    }

    @Test
    public void testShareAmount_partialShare() {
        // Total = 4500, share 900 (20% off)
        long discount = GoodsPriceShareUtil.shareAmount(goodsList, 900L, activity);

        // Each goods first gets 1 cent (3 cents spent), then remaining 897 shared proportionally
        assertTrue("Discount should be positive", discount > 0);
        assertTrue("Discount should not exceed 900", discount <= 900);

        for (GoodsBO goods : goodsList) {
            assertTrue("Each goods should have discount detail",
                    !goods.getDiscountDetailList().isEmpty());
        }
    }

    @Test
    public void testShareAmount_tinyAmount() {
        // Total = 4500, share 1 cent
        long discount = GoodsPriceShareUtil.shareAmount(goodsList, 1L, activity);
        assertEquals(1L, discount);
    }

    @Test
    public void testShareAmount_onlyOneGoodsCanDiscount() {
        // Make only first goods discountable
        goodsList.get(1).setComputeAmount(0L);
        goodsList.get(2).setComputeAmount(0L);

        // Share 500 across juice(2000) only
        long discount = GoodsPriceShareUtil.shareAmount(goodsList, 500L, activity);
        assertEquals(500L, discount);
        assertEquals(Long.valueOf(1500L), goodsList.get(0).getComputeAmount());
    }

    // ==================== singleGoodsOffNCent() ====================

    @Test
    public void testSingleGoodsOffNCent_normal() {
        GoodsBO goods = goodsList.get(0);
        long discount = GoodsPriceShareUtil.singleGoodsOffNCent(goods, 500L, activity);

        assertEquals(500L, discount);
        assertEquals(Long.valueOf(1500L), goods.getComputeAmount());
        assertFalse(goods.getDiscountDetailList().isEmpty());
    }

    @Test
    public void testSingleGoodsOffNCent_exceedsAmount() {
        GoodsBO goods = goodsList.get(0);
        // Try to off 3000 from a 2000 goods
        long discount = GoodsPriceShareUtil.singleGoodsOffNCent(goods, 3000L, activity);

        assertEquals(2000L, discount);
        assertEquals(Long.valueOf(0L), goods.getComputeAmount());
    }

    @Test
    public void testSingleGoodsOffNCent_nullGoods() {
        long discount = GoodsPriceShareUtil.singleGoodsOffNCent(null, 500L, activity);
        assertEquals(0L, discount);
    }

    @Test
    public void testSingleGoodsOffNCent_nullAmount() {
        long discount = GoodsPriceShareUtil.singleGoodsOffNCent(goodsList.get(0), null, activity);
        assertEquals(0L, discount);
    }

    // ==================== singleGoodsFree() ====================

    @Test
    public void testSingleGoodsFree() {
        GoodsBO goods = goodsList.get(0);
        long discount = GoodsPriceShareUtil.singleGoodsFree(goods, activity);

        assertEquals(2000L, discount);
        assertEquals(Long.valueOf(0L), goods.getComputeAmount());
    }

    @Test
    public void testSingleGoodsFree_nullGoods() {
        long discount = GoodsPriceShareUtil.singleGoodsFree(null, activity);
        assertEquals(0L, discount);
    }

    @Test
    public void testSingleGoodsFree_alreadyZero() {
        GoodsBO goods = goodsList.get(0);
        goods.setComputeAmount(0L);
        long discount = GoodsPriceShareUtil.singleGoodsFree(goods, activity);
        assertEquals(0L, discount);
    }

    // ==================== freeNum() ====================

    @Test
    public void testFreeNum_HIGHEST() {
        // Free 1 item by highest price -> Juice(2000) goes free
        long discount = GoodsPriceShareUtil.freeNum(goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 1);

        assertEquals(2000L, discount);
        assertEquals(Long.valueOf(0L), goodsList.get(0).getComputeAmount()); // Juice freed
        assertEquals(Long.valueOf(1500L), goodsList.get(1).getComputeAmount()); // Coffee unchanged
    }

    @Test
    public void testFreeNum_LOWEST() {
        // Free 1 item by lowest price -> Tea(1000) goes free
        long discount = GoodsPriceShareUtil.freeNum(goodsList, activity, SelectGoodsRuleEnum.LOWEST, 1);

        assertEquals(1000L, discount);
        assertEquals(Long.valueOf(0L), goodsList.get(2).getComputeAmount()); // Tea freed
    }

    @Test
    public void testFreeNum_freesAll() {
        // Free 5 items (more than available)
        long discount = GoodsPriceShareUtil.freeNum(goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 5);

        assertEquals(4500L, discount);
        for (GoodsBO goods : goodsList) {
            assertEquals(Long.valueOf(0L), goods.getComputeAmount());
        }
    }

    @Test
    public void testFreeNum_nullNum() {
        long discount = GoodsPriceShareUtil.freeNum(goodsList, activity, SelectGoodsRuleEnum.HIGHEST, null);
        assertEquals(0L, discount);
    }

    @Test
    public void testFreeNum_zeroNum() {
        long discount = GoodsPriceShareUtil.freeNum(goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 0);
        assertEquals(0L, discount);
    }

    // ==================== freeNumAndAverageShare() ====================

    @Test
    public void testFreeNumAndAverageShare() {
        // Free 1 item by highest, share the amount across ALL goods
        long discount = GoodsPriceShareUtil.freeNumAndAverageShare(goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 1);

        // The 2000 (Juice) is shared across all 3 goods proportionally
        assertTrue("Discount should be positive", discount > 0);
        // Each goods should benefit
        for (GoodsBO goods : goodsList) {
            assertTrue("Each goods should be discounted", goods.getComputeAmount() < goods.getComputeAmount() + 1);
        }
    }

    // ==================== discount() 折扣后价格 ====================

    @Test
    public void testDiscount_normal() {
        // Apply 800 discount (20% off) to all goods, share across all
        long discount = GoodsPriceShareUtil.discountNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 3, 800);

        // Total=4500, 20% off = 900
        assertEquals(900L, discount);
    }

    @Test
    public void testDiscount_onlyOne() {
        // Apply 800 discount to 1 item (highest = Juice 2000), share across all
        long discount = GoodsPriceShareUtil.discountNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 1, 800);

        // 2000 * (1000-800)/1000 = 2000 * 0.2 = 400, share across all
        assertEquals(400L, discount);
    }

    @Test
    public void testDiscount_invalidDiscount() {
        long discount = GoodsPriceShareUtil.discountNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 3, -1);
        assertEquals(0L, discount);
    }

    @Test
    public void testDiscount_nullParams() {
        assertEquals(0L, GoodsPriceShareUtil.discountNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, null, 800));
        assertEquals(0L, GoodsPriceShareUtil.discountNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 0, 800));
    }

    @Test
    public void testDiscountNumShareScopeGoods() {
        // Select 2 highest (Juice+ Coffee=3500), 20% off = 700, but only share within selected
        long discount = GoodsPriceShareUtil.discountNumShareScopeGoods(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 2, 800);

        assertEquals(700L, discount);
        // Only Juice and Coffee were discounted
        assertEquals(Long.valueOf(1000L), goodsList.get(2).getComputeAmount()); // Tea unchanged
    }

    @Test
    public void testDiscountNumCeiling() {
        // Normal 20% off on all (4500) = 900, but ceiling at 500
        long discount = GoodsPriceShareUtil.discountNumCeiling(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 3, 800, 500);

        assertEquals(500L, discount);
    }

    @Test
    public void testDiscountNumCeiling_noCeiling() {
        // Ceiling higher than discount, no effect
        long discount = GoodsPriceShareUtil.discountNumCeiling(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 3, 800, 2000);

        assertEquals(900L, discount);
    }

    // ==================== assignNum() ====================

    @Test
    public void testAssignNum_setPrice() {
        // Select 2 highest goods, assign price 1000 each
        // Juice: 2000->1000 (diff 1000), Coffee: 1500->1000 (diff 500)
        long discount = GoodsPriceShareUtil.assignNum(
                goodsList, activity, SelectGoodsRuleEnum.HIGHEST, 2, 1000);

        assertEquals(1500L, discount);
        assertEquals(Long.valueOf(1000L), goodsList.get(0).getComputeAmount()); // Juice
        assertEquals(Long.valueOf(1000L), goodsList.get(1).getComputeAmount()); // Coffee
        assertEquals(Long.valueOf(1000L), goodsList.get(2).getComputeAmount()); // Tea unchanged
    }

    // ==================== getDiscountByActivityDefaultInit() ====================

    @Test
    public void testGetDiscountByActivityDefaultInit_new() {
        List<DiscountBO> discountList = new ArrayList<>();
        DiscountBO result = GoodsPriceShareUtil.getDiscountByActivityDefaultInit(discountList, activity);

        assertNotNull(result);
        assertEquals(1, discountList.size());
        assertEquals(ActivityEnum.PROMOTION, result.getActivityEnum());
        assertEquals("PROMO_SHARE", result.getInnerLogicUniqueCode());
    }

    @Test
    public void testGetDiscountByActivityDefaultInit_existing() {
        List<DiscountBO> discountList = new ArrayList<>();
        DiscountBO existing = new DiscountBO();
        existing.setActivityEnum(ActivityEnum.PROMOTION);
        existing.setInnerLogicUniqueCode("PROMO_SHARE");
        existing.setDiscount(100L);
        discountList.add(existing);

        DiscountBO result = GoodsPriceShareUtil.getDiscountByActivityDefaultInit(discountList, activity);

        assertSame(existing, result);
        assertEquals(1, discountList.size());
    }

    @Test
    public void testGetDiscountByActivityDefaultInit_mixedExisting() {
        List<DiscountBO> discountList = new ArrayList<>();
        // Add a different activity's discount first
        DiscountBO other = new DiscountBO();
        other.setActivityEnum(ActivityEnum.COUPON);
        other.setInnerLogicUniqueCode("COUPON_001");
        discountList.add(other);

        DiscountBO result = GoodsPriceShareUtil.getDiscountByActivityDefaultInit(discountList, activity);

        assertNotSame(other, result);
        assertEquals(2, discountList.size());
    }
}
