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
import com.qiwumind.next.components.pricing.core.meta.function.compute.DiscountNFunction;
import com.qiwumind.next.components.pricing.core.meta.function.compute.FreeXNumFunction;
import com.qiwumind.next.components.pricing.core.meta.function.compute.OffNCentFunction;
import com.qiwumind.next.components.pricing.core.meta.function.match.ChannelFunction;
import com.qiwumind.next.components.pricing.core.meta.function.match.GoodsMatchAllPriceFunction;
import com.qiwumind.next.components.pricing.core.meta.function.match.TimeCycleFunction;
import com.qiwumind.next.components.pricing.core.meta.function.show.ShowLabelFunction;
import com.qiwumind.next.components.pricing.core.meta.function.show.ShowTipsFunction;

/**
 * Aviator 函数注册中心。
 * <p>
 * 注册所有自定义 Aviator 函数，使其可用于规则链表达式。
 * 在应用启动时调用 {@link #registerAll()} 一次即可。
 * <p>
 * 添加新函数：
 * 1. 创建一个继承 AbstractVariadicFunction 的类
 * 2. 在此处用 AviatorEvaluator.addFunction() 注册它
 * 3. 在规则链表达式中使用其名称
 */
public final class FunctionRegistry {

    private static volatile boolean registered = false;

    private FunctionRegistry() {}

    /**
     * 注册所有内置 Aviator 函数。
     * 线程安全，幂等 —— 多次调用安全。
     */
    public static synchronized void registerAll() {
        if (registered) return;

        // 计算函数
        AviatorEvaluator.addFunction(new OffNCentFunction());
        AviatorEvaluator.addFunction(new DiscountNFunction());
        AviatorEvaluator.addFunction(new FreeXNumFunction());

        // 匹配函数
        AviatorEvaluator.addFunction(new GoodsMatchAllPriceFunction());
        AviatorEvaluator.addFunction(new TimeCycleFunction());
        AviatorEvaluator.addFunction(new ChannelFunction());

        // 展示函数
        AviatorEvaluator.addFunction(new ShowLabelFunction());
        AviatorEvaluator.addFunction(new ShowTipsFunction());

        registered = true;
    }
}
