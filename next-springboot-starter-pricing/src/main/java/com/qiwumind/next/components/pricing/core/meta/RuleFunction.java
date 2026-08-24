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
import com.googlecode.aviator.Expression;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.PriceBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则函数执行器 - Aviator 表达式引擎与定价领域的桥梁。
 * <p>
 * 每个活动（促销/优惠券/礼品卡）携带三个 Aviator 表达式字符串：
 * <ul>
 *   <li>checkExpression  - 第一规则链（可用性检查）</li>
 *   <li>computeExpression - 第二规则链（实际计算）</li>
 *   <li>showExpression   - 展示规则链（展示信息）</li>
 * </ul>
 * <p>
 * 规则链由函数名通过 &&（逻辑与）连接组成。
 * 每个函数通过 AviatorEvaluator 注册，接收三个环境变量：
 * <ul>
 *   <li>activity - 被评估的 ActivityBO</li>
 *   <li>price    - PriceBO 定价上下文</li>
 *   <li>executeEnum - 当前处于哪个执行阶段</li>
 * </ul>
 * <p>
 * 规则链示例：
 * <pre>
 * TimeCycleFunction && ChannelFunction && GoodsMatchAllPriceFunction && OffNCentFunction
 * </pre>
 * 含义：检查时间有效性 且 渠道匹配 且 商品匹配，然后减 N 分。
 */
@Slf4j
public final class RuleFunction {

    private RuleFunction() {
    }

    private static final String ACTIVITY_ARG = "activity";
    private static final String PRICE_ARG = "price";
    private static final String EXECUTE_ARG = "executeEnum";

    /**
     * 阶段 1：可用性检查。
     * 评估活动的 checkExpression（第一规则链）。
     * 不修改商品状态 —— 仅验证活动是否可用。
     *
     * @param activityBO 要检查的活动
     * @param priceBO    定价上下文
     * @return 活动通过所有可用性检查返回 true
     */
    public static boolean availableCheck(ActivityBO activityBO, PriceBO priceBO) {
        if (StringUtils.isEmpty(activityBO.getCheckExpression())) {
            return false;
        }
        try {
            Expression compiledExp = AviatorEvaluator.compile(activityBO.getCheckExpression(), true);
            Map<String, Object> env = new HashMap<>();
            env.put(ACTIVITY_ARG, activityBO);
            env.put(PRICE_ARG, priceBO);
            env.put(EXECUTE_ARG, AviatorExecuteEnum.AVAILABLE_CHECK);
            Boolean result = (Boolean) compiledExp.execute(env);
            return BooleanUtils.isTrue(result);
        } catch (Exception e) {
            log.error("availableCheck error, activity: {}, code: {}",
                    activityBO.getActivityType(), activityBO.getCode(), e);
        } catch (Error error) {
            log.error("availableCheck error, activity: {}, code: {}",
                    activityBO.getActivityType(), activityBO.getCode(), error);
        }
        return false;
    }

    /**
     * 阶段 2：检查并执行。
     * 评估活动的 computeExpression（第二规则链）。
     * 若所有检查通过，计算函数会修改商品状态（优惠金额）。
     *
     * @param activityBO 要计算的活动
     * @param priceBO    定价上下文
     * @return 活动成功应用返回 true
     */
    public static boolean checkAndAction(ActivityBO activityBO, PriceBO priceBO) {
        if (StringUtils.isEmpty(activityBO.getComputeExpression())) {
            return false;
        }
        try {
            Expression compiledExp = AviatorEvaluator.compile(activityBO.getComputeExpression(), true);
            Map<String, Object> env = new HashMap<>();
            env.put(ACTIVITY_ARG, activityBO);
            env.put(PRICE_ARG, priceBO);
            env.put(EXECUTE_ARG, AviatorExecuteEnum.CHECK_AND_ACTION);
            Boolean result = (Boolean) compiledExp.execute(env);
            return BooleanUtils.isTrue(result);
        } catch (Exception e) {
            log.error("checkAndAction error, activity: {}, code: {}",
                    activityBO.getActivityType(), activityBO.getCode(), e);
        } catch (Error error) {
            log.error("checkAndAction error, activity: {}, code: {}",
                    activityBO.getActivityType(), activityBO.getCode(), error);
        }
        return false;
    }

    /**
     * 展示表达式评估。
     * 为活动生成展示信息（标签、提示、角标）。
     */
    public static boolean actionShow(ActivityBO activityBO, PriceBO priceBO) {
        if (StringUtils.isEmpty(activityBO.getShowExpression())) {
            return false;
        }
        try {
            Expression compiledExp = AviatorEvaluator.compile(activityBO.getShowExpression(), true);
            Map<String, Object> env = new HashMap<>();
            env.put(ACTIVITY_ARG, activityBO);
            env.put(PRICE_ARG, priceBO);
            env.put(EXECUTE_ARG, AviatorExecuteEnum.CHECK_AND_ACTION);
            Boolean result = (Boolean) compiledExp.execute(env);
            return BooleanUtils.isTrue(result);
        } catch (Exception e) {
            log.error("actionShow error, activity: {}, code: {}",
                    activityBO.getActivityType(), activityBO.getCode(), e);
        }
        return false;
    }
}
