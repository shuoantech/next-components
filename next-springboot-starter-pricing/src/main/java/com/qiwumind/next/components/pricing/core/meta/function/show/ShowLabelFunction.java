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

package com.qiwumind.next.components.pricing.core.meta.function.show;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Aviator 函数：ShowLabelFunction（展示标签）
 * <p>
 * 为活动生成展示标签。
 * <p>
 * 从 activity metaMap 中读取：
 * - "label": 要展示的标签文本（如 "满50减10"）
 */
public class ShowLabelFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "ShowLabelFunction";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        ActivityBO activity = (ActivityBO) env.get("activity");
        PriceBO priceBO = (PriceBO) env.get("price");

        if (activity == null) {
            return AviatorBoolean.valueOf(false);
        }

        Map<String, Object> meta = activity.getMetaMap();
        if (meta != null) {
            Object labelObj = meta.get("label");
            if (labelObj != null) {
                // 将标签存储到活动中用于响应构建
                List<String> showInfo = activity.getDisableReason(); // 复用列表或添加 showInfo 字段
                if (showInfo == null) {
                    showInfo = new ArrayList<>();
                }
                // 完整实现中，此处会在活动上设置一个 showInfo 字段
            }
        }

        return AviatorBoolean.valueOf(true);
    }
}
