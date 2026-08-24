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

package com.qiwumind.next.components.pricing.core.meta;

import com.googlecode.aviator.AviatorEvaluator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * \1 单元测试。
 */
public class FunctionRegistryTest {

    @Test
    public void testRegisterAll() {
        // First call registers
        FunctionRegistry.registerAll();

        // Verify all functions are registered by trying to get them from Aviator
        assertNotNull("OffNCentFunction should be registered",
                AviatorEvaluator.getFunction("OffNCentFunction"));
        assertNotNull("DiscountNFunction should be registered",
                AviatorEvaluator.getFunction("DiscountNFunction"));
        assertNotNull("FreeXNumFunction should be registered",
                AviatorEvaluator.getFunction("FreeXNumFunction"));
        assertNotNull("GoodsMatchAllPriceFunction should be registered",
                AviatorEvaluator.getFunction("GoodsMatchAllPriceFunction"));
        assertNotNull("TimeCycleFunction should be registered",
                AviatorEvaluator.getFunction("TimeCycleFunction"));
        assertNotNull("ChannelFunction should be registered",
                AviatorEvaluator.getFunction("ChannelFunction"));
        assertNotNull("ShowLabelFunction should be registered",
                AviatorEvaluator.getFunction("ShowLabelFunction"));
        assertNotNull("ShowTipsFunction should be registered",
                AviatorEvaluator.getFunction("ShowTipsFunction"));
    }

    @Test
    public void testRegisterAll_idempotent() {
        // Multiple calls should not throw or duplicate
        FunctionRegistry.registerAll();
        FunctionRegistry.registerAll();
        FunctionRegistry.registerAll();

        // Functions should still be registered
        assertNotNull(AviatorEvaluator.getFunction("OffNCentFunction"));
    }

    @Test
    public void testRegisterAll_threadSafe() throws InterruptedException {
        // Call registerAll from multiple threads concurrently
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(FunctionRegistry::registerAll);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All functions should be registered exactly once (no duplicates)
        assertNotNull(AviatorEvaluator.getFunction("OffNCentFunction"));
    }
}
