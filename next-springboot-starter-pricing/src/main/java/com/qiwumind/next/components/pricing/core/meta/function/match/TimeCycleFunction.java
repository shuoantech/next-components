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
 * Aviator 函数：TimeCycleFunction（时间周期匹配）
 * <p>
 * 验证当前时间是否在活动的有效时间周期内。
 * 支持时段周期和工作日限制。
 * <p>
 * 从 activity metaMap 中读取：
 * - "timeStart": 开始时间（时间戳 ms）
 * - "timeEnd": 结束时间（时间戳 ms）
 * - "weekDays": 允许的工作日（逗号分隔，1=周一 ... 7=周日）
 */
public class TimeCycleFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "TimeCycleFunction";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        ActivityBO activity = (ActivityBO) env.get("activity");
        PriceBO priceBO = (PriceBO) env.get("price");

        if (activity == null || priceBO == null) {
            return AviatorBoolean.valueOf(false);
        }

        Long currentTime = priceBO.getCurrentTime();
        if (currentTime == null) {
            currentTime = System.currentTimeMillis();
        }

        // 检查开始/结束时间
        if (activity.getStartTime() != null && currentTime < activity.getStartTime()) {
            return AviatorBoolean.valueOf(false);
        }
        if (activity.getEndTime() != null && currentTime > activity.getEndTime()) {
            return AviatorBoolean.valueOf(false);
        }

        // 检查工作日限制
        Map<String, Object> meta = activity.getMetaMap();
        if (meta != null) {
            Object weekDaysObj = meta.get("weekDays");
            if (weekDaysObj != null) {
                String weekDays = String.valueOf(weekDaysObj);
                if (!weekDays.isEmpty()) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTimeInMillis(currentTime);
                    int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                    // Calendar: 1=周日, 2=周一, ... 7=周六
                    // 我们的约定: 1=周一 ... 7=周日
                    int ourDay = dayOfWeek == 1 ? 7 : dayOfWeek - 1;
                    if (!weekDays.contains(String.valueOf(ourDay))) {
                        return AviatorBoolean.valueOf(false);
                    }
                }
            }
        }

        return AviatorBoolean.valueOf(true);
    }
}
