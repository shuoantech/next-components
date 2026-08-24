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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * \1 单元测试。
 */
public class GoodsBOTest {

    @Test
    public void testTotalAmount() {
        GoodsBO goods = new GoodsBO();
        goods.setComputeAmount(1500L);
        goods.setNum(2);
        assertEquals(3000L, goods.totalAmount());
    }

    @Test
    public void testTotalAmount_nullComputeAmount() {
        GoodsBO goods = new GoodsBO();
        goods.setNum(2);
        assertEquals(0L, goods.totalAmount());
    }

    @Test
    public void testTotalAmount_nullNum() {
        GoodsBO goods = new GoodsBO();
        goods.setComputeAmount(1000L);
        assertEquals(1000L, goods.totalAmount());
    }

    @Test
    public void testCanDiscount_positive() {
        GoodsBO goods = new GoodsBO();
        goods.setComputeAmount(1000L);
        assertTrue(goods.canDiscount());
    }

    @Test
    public void testCanDiscount_zero() {
        GoodsBO goods = new GoodsBO();
        goods.setComputeAmount(0L);
        assertFalse(goods.canDiscount());
    }

    @Test
    public void testCanDiscount_null() {
        GoodsBO goods = new GoodsBO();
        assertFalse(goods.canDiscount());
    }

    @Test
    public void testEqIndex() {
        GoodsBO goods = new GoodsBO();
        goods.setEqIndex(5);
        assertEquals(5, goods.eqIndex());
    }

    @Test
    public void testSelected_defaultTrue() {
        GoodsBO goods = new GoodsBO();
        assertTrue(goods.isSelected());
    }

    @Test
    public void testSelected_setFalse() {
        GoodsBO goods = new GoodsBO();
        goods.setSelected(false);
        assertFalse(goods.isSelected());
    }

    @Test
    public void testDiscountDetailList_defaultEmpty() {
        GoodsBO goods = new GoodsBO();
        assertNotNull(goods.getDiscountDetailList());
        assertTrue(goods.getDiscountDetailList().isEmpty());
    }

    @Test
    public void testCompareTo_descendingOrder() {
        GoodsBO high = new GoodsBO();
        high.setComputeAmount(3000L);
        high.setItemName("Expensive");

        GoodsBO mid = new GoodsBO();
        mid.setComputeAmount(2000L);
        mid.setItemName("Medium");

        GoodsBO low = new GoodsBO();
        low.setComputeAmount(1000L);
        low.setItemName("Cheap");

        List<GoodsBO> list = new ArrayList<>();
        list.add(low);
        list.add(high);
        list.add(mid);

        Collections.sort(list);

        assertEquals("Expensive", list.get(0).getItemName());
        assertEquals("Medium", list.get(1).getItemName());
        assertEquals("Cheap", list.get(2).getItemName());
    }

    @Test
    public void testCompareTo_nullComputeAmount() {
        GoodsBO a = new GoodsBO();
        a.setComputeAmount(null);

        GoodsBO b = new GoodsBO();
        b.setComputeAmount(1000L);

        // null treated as 0, so a(0) vs b(1000) -> b first (descending)
        assertTrue(b.compareTo(a) < 0);
        assertTrue(a.compareTo(b) > 0);
    }

    @Test
    public void testCompareTo_equal() {
        GoodsBO a = new GoodsBO();
        a.setComputeAmount(1000L);

        GoodsBO b = new GoodsBO();
        b.setComputeAmount(1000L);

        assertEquals(0, a.compareTo(b));
    }
}
