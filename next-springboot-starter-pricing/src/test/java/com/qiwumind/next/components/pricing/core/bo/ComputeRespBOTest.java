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

import static org.junit.Assert.assertEquals;

/**
 * \1 单元测试。
 */
public class ComputeRespBOTest {

    private ComputeRespBO resp;

    @Before
    public void setUp() {
        resp = new ComputeRespBO();
    }

    @Test
    public void testCalculate_basicScenario() {
        // Coffee: sale=1500, compute=1200, num=1 -> original=1500, payable=1200, discount=300
        GoodsBO coffee = new GoodsBO();
        coffee.setSaleAmount(1500L);
        coffee.setComputeAmount(1200L);
        coffee.setNum(1);
        coffee.setSelected(true);

        // Tea: sale=1000, compute=800, num=2 -> original=2000, payable=1600, discount=400
        GoodsBO tea = new GoodsBO();
        tea.setSaleAmount(1000L);
        tea.setComputeAmount(800L);
        tea.setNum(2);
        tea.setSelected(true);

        resp.getGoodsList().add(coffee);
        resp.getGoodsList().add(tea);
        resp.setShippingFee(500L);

        resp.calculate();

        assertEquals(Long.valueOf(3500L), resp.getTotalAmount()); // 1500 + 2000
        assertEquals(Long.valueOf(3300L), resp.getPayAmount());   // 1200 + 1600 + 500
        assertEquals(Long.valueOf(700L), resp.getTotalDiscount()); // 300 + 400
    }

    @Test
    public void testCalculate_emptyGoods() {
        resp.setShippingFee(500L);
        resp.calculate();

        assertEquals(Long.valueOf(0L), resp.getTotalAmount());
        assertEquals(Long.valueOf(500L), resp.getPayAmount()); // only shipping
        assertEquals(Long.valueOf(0L), resp.getTotalDiscount());
    }

    @Test
    public void testCalculate_unselectedGoodsExcluded() {
        GoodsBO coffee = new GoodsBO();
        coffee.setSaleAmount(1500L);
        coffee.setComputeAmount(1200L);
        coffee.setNum(1);
        coffee.setSelected(false); // unselected

        GoodsBO tea = new GoodsBO();
        tea.setSaleAmount(1000L);
        tea.setComputeAmount(1000L);
        tea.setNum(2);
        tea.setSelected(true);

        resp.getGoodsList().add(coffee);
        resp.getGoodsList().add(tea);
        resp.setShippingFee(0L);

        resp.calculate();

        assertEquals(Long.valueOf(2000L), resp.getTotalAmount()); // only tea
        assertEquals(Long.valueOf(2000L), resp.getPayAmount());
        assertEquals(Long.valueOf(0L), resp.getTotalDiscount());
    }

    @Test
    public void testCalculate_noShipping() {
        GoodsBO goods = new GoodsBO();
        goods.setSaleAmount(1000L);
        goods.setComputeAmount(900L);
        goods.setNum(3);
        goods.setSelected(true);

        resp.getGoodsList().add(goods);
        resp.setShippingFee(0L);

        resp.calculate();

        assertEquals(Long.valueOf(3000L), resp.getTotalAmount());
        assertEquals(Long.valueOf(2700L), resp.getPayAmount());
        assertEquals(Long.valueOf(300L), resp.getTotalDiscount());
    }

    @Test
    public void testCalculate_multipleCallsReset() {
        GoodsBO goods = new GoodsBO();
        goods.setSaleAmount(1000L);
        goods.setComputeAmount(500L);
        goods.setNum(1);
        goods.setSelected(true);
        resp.getGoodsList().add(goods);
        resp.setShippingFee(100L);

        resp.calculate();
        assertEquals(Long.valueOf(600L), resp.getPayAmount());

        // Change goods compute amount and recalculate
        goods.setComputeAmount(800L);
        resp.calculate();
        assertEquals(Long.valueOf(900L), resp.getPayAmount());
    }
}
