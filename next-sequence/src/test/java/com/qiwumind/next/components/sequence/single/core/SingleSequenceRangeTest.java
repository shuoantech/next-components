package com.qiwumind.next.components.sequence.single.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 单点序列号号段分配单元测试（纯逻辑，不依赖数据库）。
 */
class SingleSequenceRangeTest {

    @Test
    void getAndIncrement_returnsSequentialValuesWithinRange() {
        SingleSequenceRange range = new SingleSequenceRange(1, 3);
        assertEquals(1, range.getAndIncrement());
        assertEquals(2, range.getAndIncrement());
        assertEquals(3, range.getAndIncrement());
    }

    @Test
    void getAndIncrement_returnsMinusOneWhenExhausted() {
        SingleSequenceRange range = new SingleSequenceRange(1, 3);
        range.getAndIncrement();
        range.getAndIncrement();
        range.getAndIncrement();
        assertEquals(-1L, range.getAndIncrement());
        assertEquals(-1L, range.getAndIncrement());
    }

    @Test
    void isOver_becomesTrueAfterExhausted() {
        SingleSequenceRange range = new SingleSequenceRange(5, 5);
        assertFalse(range.isOver());
        range.getAndIncrement();
        assertTrue(range.isOver());
    }

    @Test
    void getMinAndMax() {
        SingleSequenceRange range = new SingleSequenceRange(10, 20);
        assertEquals(10, range.getMin());
        assertEquals(20, range.getMax());
    }
}
