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

package com.qiwumind.next.components.pricing.core.engine;

import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.ComputeRespBO;
import com.qiwumind.next.components.pricing.core.bo.GoodsBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;
import com.qiwumind.next.components.pricing.core.meta.FunctionRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 计算编排服务 - 定价计算的主入口。
 * <p>
 * 编排完整的定价管线：
 * <pre>
 * 1. 注册 Aviator 函数（一次）
 * 2. 策略计算
 * 3. 优惠券计算（可用/不可用/已使用）
 * 4. 规格/加料促销计算
 * 5. 单品促销计算
 * 6. 多品促销计算
 * 7. 赠品促销计算
 * 8. 运费促销计算
 * 9. 礼品卡计算
 * 10. 构建响应
 * </pre>
 *
 * <h3>执行顺序的重要性</h3>
 * 顺序至关重要，因为：
 * <ul>
 *   <li>策略必须最先执行 - 它们可能修改哪些促销可用</li>
 *   <li>优惠券在促销之前执行 - 优惠券优惠会影响促销门槛</li>
 *   <li>单品促销在多品促销之前 - 单品修改单个商品价格</li>
 *   <li>赠品促销在影响价格的促销之后 - 赠品不改变应付金额</li>
 *   <li>运费促销最后 - 它们作用于运费而非商品</li>
 * </ul>
 */
@Slf4j
public class ComputeService {

    static {
        // 类加载时一次性注册 Aviator 函数
        FunctionRegistry.registerAll();
    }

    /**
     * 执行完整的定价计算。
     *
     * @param priceBO         定价上下文（商品、用户、门店信息）
     * @param allActivityList 所有可用活动（促销 + 优惠券 + 礼品卡）
     * @return 应用所有优惠后的计算结果
     */
    public ComputeRespBO compute(PriceBO priceBO, List<ActivityBO> allActivityList) {
        FunctionRegistry.registerAll();

        // 用销售价初始化商品的 computeAmount
        initGoodsComputeAmount(priceBO);

        ComputeRespBO resp = new ComputeRespBO();

        // 1. 优惠券计算
        CouponComputeEngine couponEngine = new CouponComputeEngine(allActivityList, priceBO);
        couponEngine.executeCanUseAndCantUseCouponAutoUseCoupon();
        resp.setCanUseCouponList(couponEngine.getCanUseCouponList());
        resp.setCantUseCouponList(couponEngine.getCantUseCouponList());
        resp.setUsedCouponList(couponEngine.getUsedCouponList());

        // 2. 促销计算（单品 + 多品）
        PromotionComputeEngine promotionEngine = new PromotionComputeEngine(allActivityList, priceBO);
        promotionEngine.execute();
        resp.getUsedPromotionList().addAll(promotionEngine.getUsedActivityList());

        // 3. 赠品促销计算
        GiftPromotionComputeEngine giftEngine = new GiftPromotionComputeEngine(allActivityList, priceBO);
        giftEngine.execute();
        resp.getUsedPromotionList().addAll(giftEngine.getUsedActivityList());

        // 4. 运费促销计算
        ShippingPromotionComputeEngine shippingEngine = new ShippingPromotionComputeEngine(allActivityList, priceBO);
        shippingEngine.execute();
        resp.getUsedPromotionList().addAll(shippingEngine.getUsedActivityList());

        // 5. 礼品卡计算
        CardComputeEngine cardEngine = new CardComputeEngine(allActivityList, priceBO);
        cardEngine.execute();

        // 构建响应
        resp.setGoodsList(priceBO.getGoodsList());
        resp.setShippingFee(priceBO.getShippingFee());
        resp.calculate();

        return resp;
    }

    /**
     * 若 computeAmount 未设置，则用 saleAmount 初始化。
     */
    private void initGoodsComputeAmount(PriceBO priceBO) {
        for (GoodsBO goods : priceBO.getGoodsList()) {
            if (goods.getComputeAmount() == null) {
                goods.setComputeAmount(goods.getSaleAmount());
            }
            // 初始化 eqIndex
            goods.setEqIndex(priceBO.getGoodsList().indexOf(goods));
        }
    }
}
