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

package com.qiwumind.next.components.pricing.core.meta.function.compute;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.GoodsBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;
import com.qiwumind.next.components.pricing.core.meta.AviatorExecuteEnum;
import com.qiwumind.next.components.pricing.core.util.GoodsPriceShareUtil;

import java.util.List;
import java.util.Map;

/**
 * Aviator 函数：OffNCentFunction（减 N 分）
 * <p>
 * 对匹配的商品减 N 分。
 * <p>
 * 从 activity metaMap 中读取：
 * - "offNCent": 减免金额（单位：分）
 * <p>
 * CHECK_AND_ACTION 模式：对商品应用优惠。
 * AVAILABLE_CHECK 模式：返回 true（不执行计算）。
 */
public class OffNCentFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "OffNCentFunction";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        ActivityBO activity = (ActivityBO) env.get("activity");
        PriceBO priceBO = (PriceBO) env.get("price");
        AviatorExecuteEnum executeEnum = (AviatorExecuteEnum) env.get("executeEnum");

        if (activity == null || priceBO == null) {
            return AviatorBoolean.valueOf(false);
        }

        // 检查阶段直接返回 true
        if (executeEnum == AviatorExecuteEnum.AVAILABLE_CHECK) {
            return AviatorBoolean.valueOf(true);
        }

        // 从 meta 中获取减免金额
        Map<String, Object> meta = activity.getMetaMap();
        if (meta == null) return AviatorBoolean.valueOf(false);

        Object offNCentObj = meta.get("offNCent");
        if (offNCentObj == null) return AviatorBoolean.valueOf(false);

        Long offNCent = ((Number) offNCentObj).longValue();
        if (offNCent <= 0) return AviatorBoolean.valueOf(false);

        // 对匹配商品应用优惠
        List<GoodsBO> goodsList = priceBO.getMiddleGoodsList();
        long discountAmount = GoodsPriceShareUtil.shareAmount(goodsList, offNCent, activity);

        activity.setDiscountAmount(activity.getDiscountAmount() + discountAmount);

        return AviatorBoolean.valueOf(discountAmount > 0);
    }
}
