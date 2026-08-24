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
import com.qiwumind.next.components.pricing.core.bo.GoodsBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;

import java.util.List;
import java.util.Map;

/**
 * Aviator 函数：GoodsMatchAllPriceFunction（商品价格门槛匹配）
 * <p>
 * 按价格门槛匹配商品（最低订单金额）。
 * 从 activity metaMap 中读取：
 * - "thresholdAmount": 要求的最低总金额（单位：分）
 * <p>
 * 若匹配商品的总金额 >= 门槛返回 true。
 */
public class GoodsMatchAllPriceFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "GoodsMatchAllPriceFunction";
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

        Object thresholdObj = meta.get("thresholdAmount");
        if (thresholdObj == null) return AviatorBoolean.valueOf(true);

        long threshold = ((Number) thresholdObj).longValue();
        if (threshold <= 0) return AviatorBoolean.valueOf(true);

        // 计算选中商品的总金额（computeAmount * num）
        long totalAmount = 0;
        List<GoodsBO> goodsList = priceBO.getMiddleGoodsList();
        for (GoodsBO goods : goodsList) {
            if (goods.isSelected() && goods.getComputeAmount() != null && goods.getComputeAmount() > 0) {
                int num = goods.getNum() != null ? goods.getNum() : 1;
                totalAmount += goods.getComputeAmount() * num;
            }
        }

        return AviatorBoolean.valueOf(totalAmount >= threshold);
    }
}
