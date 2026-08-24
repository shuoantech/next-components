package com.qiwumind.next.components.redis.core.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 内存布隆过滤器单元测试（纯本地，不依赖 Redis 服务）。
 */
class GuavaBloomFilterTest {

    @Test
    void put_and_mightContain_returnsTrueForInserted() {
        GuavaBloomFilter<String> filter = new GuavaBloomFilter<>("test", 1000, 0.01);
        assertFalse(filter.mightContain("alice"));
        filter.put("alice");
        assertTrue(filter.mightContain("alice"));
    }

    @Test
    void clear_removesAllElements() {
        GuavaBloomFilter<String> filter = new GuavaBloomFilter<>("test", 1000, 0.001);
        filter.put("x");
        filter.put("y");
        assertTrue(filter.mightContain("x"));
        filter.clear();
        assertFalse(filter.mightContain("x"));
        assertFalse(filter.mightContain("y"));
    }

    @Test
    void falsePositiveRate_withinTolerance() {
        int expected = 10000;
        GuavaBloomFilter<Integer> filter = new GuavaBloomFilter<>("nums", expected, 0.01);
        for (int i = 0; i < expected; i++) {
            filter.put(i);
        }
        int falsePositives = 0;
        for (int i = expected; i < expected * 2; i++) {
            if (filter.mightContain(i)) {
                falsePositives++;
            }
        }
        double rate = (double) falsePositives / expected;
        // 允许少量超出理论值
        assertTrue(rate < 0.05, "误判率应低于 5%，实际：" + rate);
    }

    @Test
    void integerElements_roundTrip() {
        GuavaBloomFilter<Integer> filter = new GuavaBloomFilter<>("ints", 500, 0.01);
        filter.put(12345);
        assertTrue(filter.mightContain(12345));
        assertFalse(filter.mightContain(99999));
    }
}
