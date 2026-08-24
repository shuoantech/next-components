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

package com.qiwumind.next.components.pricing.core.meta.function.match;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;

import java.util.Map;

/**
 * Aviator 函数：ChannelFunction（渠道匹配）
 * <p>
 * 验证当前订单渠道是否与活动允许的渠道匹配。
 * <p>
 * 从 activity metaMap 中读取：
 * - "channels": 允许的渠道列表（逗号分隔）
 *   例如 "1,2,3" 表示渠道 1、2、3 被允许
 */
public class ChannelFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "ChannelFunction";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        ActivityBO activity = (ActivityBO) env.get("activity");
        PriceBO priceBO = (PriceBO) env.get("price");

        if (activity == null || priceBO == null) {
            return AviatorBoolean.valueOf(false);
        }

        Map<String, Object> meta = activity.getMetaMap();
        if (meta == null) return AviatorBoolean.valueOf(true);

        Object channelsObj = meta.get("channels");
        if (channelsObj == null) return AviatorBoolean.valueOf(true);

        String channels = String.valueOf(channelsObj);
        if (channels.isEmpty()) return AviatorBoolean.valueOf(true);

        Integer channel = priceBO.getChannel();
        if (channel == null) return AviatorBoolean.valueOf(false);

        return AviatorBoolean.valueOf(channels.contains(String.valueOf(channel)));
    }
}
