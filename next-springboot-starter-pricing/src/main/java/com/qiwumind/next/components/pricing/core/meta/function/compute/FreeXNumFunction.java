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
import com.qiwumind.next.components.pricing.core.enums.SelectGoodsRuleEnum;
import com.qiwumind.next.components.pricing.core.meta.AviatorExecuteEnum;
import com.qiwumind.next.components.pricing.core.util.GoodsPriceShareUtil;

import java.util.List;
import java.util.Map;

/**
 * Aviator 函数：FreeXNumFunction（免费送 N 件）
 * <p>
 * 按选择规则对 N 件商品免费（金额设为 0）。
 * <p>
 * 从 activity metaMap 中读取：
 * - "freeNum": 免费的商品数量
 * - "selectRule": 选择规则（HIGHEST, LOWEST 等）
 */
public class FreeXNumFunction extends AbstractVariadicFunction {

    @Override
    public String getName() {
        return "FreeXNumFunction";
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

        Object freeNumObj = meta.get("freeNum");
        if (freeNumObj == null) return AviatorBoolean.valueOf(false);

        Integer freeNum = ((Number) freeNumObj).intValue();
        if (freeNum <= 0) return AviatorBoolean.valueOf(false);

        SelectGoodsRuleEnum rule = SelectGoodsRuleEnum.HIGHEST;
        Object ruleObj = meta.get("selectRule");
        if (ruleObj != null) {
            try {
                rule = SelectGoodsRuleEnum.valueOf(String.valueOf(ruleObj));
            } catch (IllegalArgumentException e) {
                rule = SelectGoodsRuleEnum.HIGHEST;
            }
        }

        List<GoodsBO> goodsList = priceBO.getMiddleGoodsList();
        long discountAmount = GoodsPriceShareUtil.freeNum(goodsList, activity, rule, freeNum);
        activity.setDiscountAmount(activity.getDiscountAmount() + discountAmount);

        return AviatorBoolean.valueOf(discountAmount > 0);
    }
}
