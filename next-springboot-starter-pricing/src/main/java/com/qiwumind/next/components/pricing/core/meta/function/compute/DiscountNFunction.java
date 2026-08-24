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
import com.qiwumind.next.components.pricing.core.util.MoneyUtil;

import java.util.List;
import java.util.Map;

/**
 * Aviator 函数：DiscountNFunction（打折）
 * <p>
 * 对匹配的商品应用 N 折优惠。
 * <p>
 * 从 activity metaMap 中读取：
 * - "discount": 折扣值（1000=无折扣, 800=8折, 500=5折）
 * - "ceiling": 可选，最高优惠金额（单位：分）
 * <p>
 * 优惠金额 = totalPrice * (1000 - discount) / 1000，结果向上取整。
 */
public class DiscountNFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "DiscountNFunction";
    }

    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        ActivityBO activity = (ActivityBO) env.get("activity");
        PriceBO priceBO = (PriceBO) env.get("price");
        AviatorExecuteEnum executeEnum = (AviatorExecuteEnum) env.get("executeEnum");

        if (activity == null || priceBO == null) {
            return AviatorBoolean.valueOf(false);
        }

        if (executeEnum == AviatorExecuteEnum.AVAILABLE_CHECK) {
            return AviatorBoolean.valueOf(true);
        }

        Map<String, Object> meta = activity.getMetaMap();
        if (meta == null) return AviatorBoolean.valueOf(false);

        Object discountObj = meta.get("discount");
        if (discountObj == null) return AviatorBoolean.valueOf(false);

        Integer discount = ((Number) discountObj).intValue();
        if (discount >= 1000 || discount < 0) return AviatorBoolean.valueOf(false);

        // 计算匹配商品的总金额
        List<GoodsBO> goodsList = priceBO.getMiddleGoodsList();
        long totalAmount = 0;
        for (GoodsBO goods : goodsList) {
            if (goods.isSelected() && goods.getComputeAmount() != null && goods.getComputeAmount() > 0) {
                totalAmount += goods.getComputeAmount();
            }
        }

        if (totalAmount <= 0) return AviatorBoolean.valueOf(false);

        long canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);

        // 若设置了封顶金额，则应用
        Object ceilingObj = meta.get("ceiling");
        if (ceilingObj != null) {
            long ceiling = ((Number) ceilingObj).longValue();
            if (ceiling > 0 && canShareAmount > ceiling) {
                canShareAmount = ceiling;
            }
        }

        long discountAmount = GoodsPriceShareUtil.shareAmount(goodsList, canShareAmount, activity);
        activity.setDiscountAmount(activity.getDiscountAmount() + discountAmount);

        return AviatorBoolean.valueOf(discountAmount > 0);
    }
}
