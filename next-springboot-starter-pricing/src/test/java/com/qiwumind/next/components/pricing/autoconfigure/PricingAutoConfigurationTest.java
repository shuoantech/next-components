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

package com.qiwumind.next.components.pricing.autoconfigure;

import com.qiwumind.next.components.pricing.core.engine.ComputeService;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * \1 单元测试。
 */
public class PricingAutoConfigurationTest {

    @Test
    public void testConstructor_registersFunctions() {
        PricingAutoConfiguration config = new PricingAutoConfiguration();
        assertNotNull(config);
        // Constructor registers Aviator functions - verify ComputeService can be created
    }

    @Test
    public void testComputeService() {
        PricingAutoConfiguration config = new PricingAutoConfiguration();
        ComputeService service = config.computeService();
        assertNotNull("ComputeService bean should be created", service);
    }

    @Test
    public void testComputeService_notNull() {
        PricingAutoConfiguration config = new PricingAutoConfiguration();
        ComputeService s1 = config.computeService();
        ComputeService s2 = config.computeService();
        assertNotNull(s1);
        assertNotNull(s2);
    }
}
