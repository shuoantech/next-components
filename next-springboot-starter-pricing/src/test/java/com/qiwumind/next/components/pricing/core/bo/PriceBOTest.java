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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class PriceBOTest {

    private PriceBO priceBO;

    @Before
    public void setUp() {
        priceBO = new PriceBO();
        priceBO.setShippingFee(500L);
        priceBO.setCurrentTime(System.currentTimeMillis());

        GoodsBO coffee = new GoodsBO();
        coffee.setItemId(1L);
        coffee.setItemName("Coffee");
        coffee.setSaleAmount(1500L);
        coffee.setComputeAmount(1500L);
        coffee.setNum(1);
        coffee.setEqIndex(0);

        GoodsBO tea = new GoodsBO();
        tea.setItemId(2L);
        tea.setItemName("Tea");
        tea.setSaleAmount(1000L);
        tea.setComputeAmount(1000L);
        tea.setNum(2);
        tea.setEqIndex(1);

        List<GoodsBO> goodsList = new ArrayList<>();
        goodsList.add(coffee);
        goodsList.add(tea);
        priceBO.setGoodsList(goodsList);
    }

    @Test
    public void testTotalOriginalAmount() {
        // coffee 1500*1 + tea 1000*2 = 3500
        assertEquals(3500L, priceBO.totalOriginalAmount());
    }

    @Test
    public void testTotalOriginalAmount_unselectedExcluded() {
        priceBO.getGoodsList().get(0).setSelected(false);
        // only tea: 1000*2 = 2000
        assertEquals(2000L, priceBO.totalOriginalAmount());
    }

    @Test
    public void testCurrentPayAmount() {
        assertEquals(3500L, priceBO.currentPayAmount());
    }

    @Test
    public void testCurrentPayAmount_afterDiscount() {
        priceBO.getGoodsList().get(0).setComputeAmount(1200L);
        // coffee 1200*1 + tea 1000*2 = 3200
        assertEquals(3200L, priceBO.currentPayAmount());
    }

    @Test
    public void testOrderCanUseCouponGoodsCurrentPayAmount() {
        assertEquals(3500L, priceBO.orderCanUseCouponGoodsCurrentPayAmount());
    }

    @Test
    public void testSelectedGoodsList() {
        List<GoodsBO> selected = priceBO.selectedGoodsList();
        assertEquals(2, selected.size());

        priceBO.getGoodsList().get(0).setSelected(false);
        selected = priceBO.selectedGoodsList();
        assertEquals(1, selected.size());
        assertEquals("Tea", selected.get(0).getItemName());
    }

    @Test
    public void testSelectedGoodsList_empty() {
        priceBO.setGoodsList(new ArrayList<>());
        List<GoodsBO> selected = priceBO.selectedGoodsList();
        assertTrue(selected.isEmpty());
    }

    @Test
    public void testChoiceAllCouponCodeList_null() {
        assertNotNull(priceBO.choiceAllCouponCodeList());
        assertTrue(priceBO.choiceAllCouponCodeList().isEmpty());
    }

    @Test
    public void testChoiceAllCouponCodeList_withCodes() {
        List<String> codes = new ArrayList<>();
        codes.add("COUPON_A");
        codes.add("COUPON_B");
        priceBO.setChoiceCouponList(codes);

        assertEquals(2, priceBO.choiceAllCouponCodeList().size());
        assertTrue(priceBO.choiceAllCouponCodeList().contains("COUPON_A"));
    }

    @Test
    public void testAddUsedCouponCode() {
        ActivityBO coupon = new ActivityBO();
        coupon.setCode("COUPON_USED");

        priceBO.addUsedCouponCode(coupon);
        assertEquals(1, priceBO.getUsedCouponCodeList().size());
        assertEquals("COUPON_USED", priceBO.getUsedCouponCodeList().get(0));
    }

    @Test
    public void testAddUsedCouponCode_nullCode() {
        ActivityBO coupon = new ActivityBO();
        priceBO.addUsedCouponCode(coupon);
        assertTrue(priceBO.getUsedCouponCodeList().isEmpty());
    }

    // ==================== Middle Goods Pattern ====================

    @Test
    public void testResetMiddleGoods() {
        priceBO.resetMiddleGoods();
        assertEquals(2, priceBO.getMiddleGoodsList().size());

        GoodsBO middleCoffee = priceBO.getMiddleGoodsList().get(0);
        assertEquals("Coffee", middleCoffee.getItemName());
        assertEquals(Long.valueOf(1500L), middleCoffee.getComputeAmount());
        assertNotSame(priceBO.getGoodsList().get(0), middleCoffee);
    }

    @Test
    public void testResetMiddleGoods_discountDetailCopied() {
        GoodsBO goods = priceBO.getGoodsList().get(0);
        DiscountBO discount = new DiscountBO();
        discount.setDiscount(100L);
        goods.getDiscountDetailList().add(discount);

        priceBO.resetMiddleGoods();
        GoodsBO middle = priceBO.getMiddleGoodsList().get(0);
        assertEquals(1, middle.getDiscountDetailList().size());
        assertEquals(Long.valueOf(100L), middle.getDiscountDetailList().get(0).getDiscount());

        // Verify deep copy - modifying middle doesn't affect original
        middle.getDiscountDetailList().add(new DiscountBO());
        assertEquals(1, goods.getDiscountDetailList().size());
    }

    @Test
    public void testResetGoodsForMiddle() {
        priceBO.resetMiddleGoods();
        // Modify middle goods
        priceBO.getMiddleGoodsList().get(0).setComputeAmount(1200L);
        priceBO.getMiddleGoodsList().get(1).setComputeAmount(800L);

        priceBO.resetGoodsForMiddle();

        assertEquals(Long.valueOf(1200L), priceBO.getGoodsList().get(0).getComputeAmount());
        assertEquals(Long.valueOf(800L), priceBO.getGoodsList().get(1).getComputeAmount());
    }

    @Test
    public void testResetMiddleGoods_secondCallClears() {
        priceBO.resetMiddleGoods();
        assertEquals(2, priceBO.getMiddleGoodsList().size());

        priceBO.resetMiddleGoods();
        assertEquals(2, priceBO.getMiddleGoodsList().size());
        // Old middle goods are cleared and recreated
    }
}
