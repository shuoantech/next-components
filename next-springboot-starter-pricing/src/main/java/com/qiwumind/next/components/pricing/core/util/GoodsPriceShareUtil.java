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

package com.qiwumind.next.components.pricing.core.util;

import com.google.common.collect.Lists;
import com.qiwumind.next.components.pricing.core.bo.ActivityBO;
import com.qiwumind.next.components.pricing.core.bo.DiscountBO;
import com.qiwumind.next.components.pricing.core.bo.GoodsBO;
import com.qiwumind.next.components.pricing.core.enums.SelectGoodsRuleEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 商品价格分摊工具 - 优惠分配引擎的核心。
 * <p>
 * 当促销/优惠券对多个商品应用优惠时，优惠金额必须分配（分摊）到各个商品上。
 * 此类实现了核心分摊算法：
 *
 * <h3>分摊算法</h3>
 * <ol>
 *   <li>若优惠金额 >= 商品总金额：所有商品免费（每件设为 0）</li>
 *   <li>否则：
 *     <ol>
 *       <li>每件商品先减 1 分（确保每件商品都受益于此促销）</li>
 *       <li>剩余金额按各商品价格比例分摊</li>
 *       <li>每件商品的分摊额 = sharePrice * selfPrice / totalPrice，上限为 selfPrice</li>
 *     </ol>
 *   </li>
 * </ol>
 *
 * <h3>支持的操作</h3>
 * <ul>
 *   <li>{@link #shareAmount} - 将固定优惠金额分摊到商品</li>
 *   <li>{@link #singleGoodsOffNCent} - 对单个商品减 N 分</li>
 *   <li>{@link #freeNum} - 按选择规则免费 N 件（选最高价/最低价等）</li>
 *   <li>{@link #discountNum} - 对 N 件商品折扣，优惠分摊到全部商品</li>
 *   <li>{@link #assignNum} - 将选中商品价格设为 N 分</li>
 * </ul>
 */
public final class GoodsPriceShareUtil {

    private GoodsPriceShareUtil() {
    }

    // ==================== 核心：分摊金额 ====================

    /**
     * 将优惠金额分摊到所有可优惠商品。
     * <p>
     * 这是所有促销类型使用的核心分摊方法。
     *
     * @param allGoodsList   范围内的所有商品
     * @param canShareAmount 待分配的总优惠金额
     * @param activityBO     应用优惠的活动（用于追踪）
     * @return 实际应用的总优惠金额
     */
    public static long shareAmount(List<GoodsBO> allGoodsList, Long canShareAmount, ActivityBO activityBO) {
        if (canShareAmount == null || canShareAmount <= 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalAmount = totalAmount(canDiscountGoodsList);

        // 情况 1：折扣覆盖全部 -> 所有商品免费
        if (canShareAmount >= totalAmount) {
            return goodsAllFree(canDiscountGoodsList, activityBO);
        }

        // 情况 2：分摊到商品
        // 步骤 1：每件商品先减 1 分（确保每件商品都受益）
        long offOneCentAmount = goodsOffOneCent(canDiscountGoodsList, canShareAmount, activityBO);
        if (offOneCentAmount >= canShareAmount) {
            return offOneCentAmount;
        }
        canShareAmount -= offOneCentAmount;

        // 步骤 2：剩余金额按比例分摊
        long scaleShareAmount = goodsShareByScale(canDiscountGoodsList, canShareAmount, activityBO);
        return scaleShareAmount + offOneCentAmount;
    }

    // ==================== 单个商品操作 ====================

    /**
     * 对单个商品减 N 分。
     * 若 offNCent > 商品金额，仅减免商品金额。
     */
    public static long singleGoodsOffNCent(GoodsBO goods, Long offNCent, ActivityBO activityBO) {
        if (offNCent == null || offNCent <= 0) return 0;
        if (goods == null || goods.getComputeAmount() == null || goods.getComputeAmount() <= 0) return 0;
        return goodsOffNCent(goods, activityBO, offNCent);
    }

    /**
     * 将单个商品免费（金额设为 0）。
     */
    public static long singleGoodsFree(GoodsBO goods, ActivityBO activityBO) {
        if (goods == null || goods.getComputeAmount() == null || goods.getComputeAmount() <= 0) return 0;
        return goodsFree(goods, activityBO);
    }

    // ==================== 基于选择规则的操作 ====================

    /**
     * Free N items by selection rule.
     * Selects N goods by the rule (e.g., highest price first) and sets them to 0.
     *
     * @param allGoodsList all goods
     * @param activityBO   activity info
     * @param rule         selection rule (HIGHEST, LOWEST, etc.)
     * @param canFreeNum   number of items to free
     * @return total discount amount
     */
    public static long freeNum(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                               SelectGoodsRuleEnum rule, Integer canFreeNum) {
        if (canFreeNum == null || canFreeNum <= 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalNum = canDiscountGoodsList.size();
        if (canFreeNum >= totalNum) {
            return goodsAllFree(canDiscountGoodsList, activityBO);
        }

        // Select goods by rule and free them
        List<GoodsBO> freeGoodsList = selectGoodsByRule(canDiscountGoodsList, rule, canFreeNum);
        if (CollectionUtils.isEmpty(freeGoodsList)) return 0;
        return goodsAllFree(freeGoodsList, activityBO);
    }

    /**
     * 免费 N 件商品，并将减免金额均摊到所有商品上。
     * （减免金额按比例分配，不仅应用于被免的商品。）
     */
    public static long freeNumAndAverageShare(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                              SelectGoodsRuleEnum rule, Integer canFreeNum) {
        if (canFreeNum == null || canFreeNum <= 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalNum = canDiscountGoodsList.size();
        if (canFreeNum >= totalNum) {
            return goodsAllFree(canDiscountGoodsList, activityBO);
        }

        // 获取要免费的商品金额
        long canShareAmount = selectGoodsAmountByRule(canDiscountGoodsList, rule, canFreeNum);
        // 将该金额分摊到所有商品
        return shareAmount(canDiscountGoodsList, canShareAmount, activityBO);
    }

    /**
     * 对 N 件商品应用折扣，减少的金额均摊到所有商品。
     *
     * @param discount 折扣值（1000=无折扣, 800=8折）
     */
    public static long discountNum(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                   SelectGoodsRuleEnum rule, Integer canDiscountNum, Integer discount) {
        if (canDiscountNum == null || canDiscountNum <= 0 || discount == null || discount < 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalNum = canDiscountGoodsList.size();
        if (canDiscountNum >= totalNum) {
            long totalAmount = totalAmount(canDiscountGoodsList);
            long canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);
            return shareAmount(canDiscountGoodsList, canShareAmount, activityBO);
        }

        // 选中商品，计算其折扣，然后分摊到所有商品
        List<GoodsBO> discountGoodsList = selectGoodsByRule(canDiscountGoodsList, rule, canDiscountNum);
        if (CollectionUtils.isEmpty(discountGoodsList)) return 0;

        long totalAmount = totalAmount(discountGoodsList);
        long canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);
        return shareAmount(canDiscountGoodsList, canShareAmount, activityBO);
    }

    /**
     * 对 N 件商品应用折扣，仅在选中商品范围内分摊优惠。
     */
    public static long discountNumShareScopeGoods(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                                  SelectGoodsRuleEnum rule, Integer canDiscountNum,
                                                  Integer discount) {
        if (canDiscountNum == null || canDiscountNum <= 0 || discount == null || discount < 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        List<GoodsBO> selectGoods = selectGoodsByRule(canDiscountGoodsList, rule, canDiscountNum);
        if (CollectionUtils.isEmpty(selectGoods)) return 0;

        return discount(selectGoods, activityBO, discount);
    }

    /**
     * 对每件商品应用不同折扣，在选中范围内分摊。
     */
    public static long discountNumShareScopeGoods(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                                  SelectGoodsRuleEnum rule, Integer canDiscountNum,
                                                  Map<String, Integer> goodsDiscountMap) {
        if (canDiscountNum == null || canDiscountNum <= 0 || goodsDiscountMap == null || goodsDiscountMap.isEmpty())
            return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        List<GoodsBO> selectGoods = selectGoodsByRule(canDiscountGoodsList, rule, canDiscountNum);
        if (CollectionUtils.isEmpty(selectGoods)) return 0;

        long totalDiscountAmount = 0;
        for (GoodsBO goods : selectGoods) {
            Integer goodsDiscount = goodsDiscountMap.get(String.valueOf(goods.getItemId()));
            if (goodsDiscount == null) continue;
            totalDiscountAmount += discount(Lists.newArrayList(goods), activityBO, goodsDiscount);
        }
        return totalDiscountAmount;
    }

    /**
     * 对 N 件商品应用折扣，并设置封顶金额。
     */
    public static long discountNumCeiling(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                          SelectGoodsRuleEnum rule, Integer canDiscountNum,
                                          Integer discount, Integer ceiling) {
        if (canDiscountNum == null || canDiscountNum <= 0 || discount == null || discount < 0) return 0;

        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalNum = canDiscountGoodsList.size();
        long totalAmount = totalAmount(canDiscountGoodsList);
        long canShareAmount;

        if (canDiscountNum >= totalNum) {
            canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);
        } else {
            List<GoodsBO> discountGoodsList = selectGoodsByRule(canDiscountGoodsList, rule, canDiscountNum);
            totalAmount = totalAmount(discountGoodsList);
            canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);
        }

        if (ceiling != null && ceiling > 0 && canShareAmount > ceiling) {
            canShareAmount = ceiling;
        }
        return shareAmount(canDiscountGoodsList, canShareAmount, activityBO);
    }

    /**
     * Set price to assignAmount for selected goods.
     */
    public static long assignNum(List<GoodsBO> allGoodsList, ActivityBO activityBO,
                                 SelectGoodsRuleEnum rule, Integer canDiscountNum, Integer assignAmount) {
        if (CollectionUtils.isEmpty(allGoodsList) || assignAmount == null) return 0;

        List<GoodsBO> canAssignGoodsList = Lists.newArrayList();
        for (GoodsBO goods : allGoodsList) {
            long diff = goods.getComputeAmount() - assignAmount;
            if (diff <= 0) continue;
            canAssignGoodsList.add(goods);
        }

        List<GoodsBO> discountGoodsList = selectGoodsByRule(canAssignGoodsList, rule, canDiscountNum);
        if (CollectionUtils.isEmpty(discountGoodsList)) return 0;

        return assignAmount(discountGoodsList, activityBO, assignAmount);
    }

    // ==================== 内部：折扣与定价 ====================

    private static long discount(List<GoodsBO> goodsList, ActivityBO activityBO, Integer discount) {
        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(goodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalAmount = totalAmount(canDiscountGoodsList);
        long canShareAmount = MoneyUtil.getDiscount(totalAmount, (long) discount);
        if (canShareAmount <= 0) return 0;
        return shareAmount(goodsList, canShareAmount, activityBO);
    }

    private static long assignAmount(List<GoodsBO> allGoodsList, ActivityBO activityBO, Integer assignAmount) {
        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(allGoodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        List<DiscountBO> discountList;
        DiscountBO discountBO;
        long totalDiscountAmount = 0;
        for (GoodsBO goods : canDiscountGoodsList) {
            long diff = goods.getComputeAmount() - assignAmount;
            if (diff <= 0) continue;
            goods.setComputeAmount((long) assignAmount);

            discountList = goods.getDiscountDetailList();
            discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
            discountBO.setDiscount(discountBO.getDiscount() + diff);
            activityBO.addEqIndex(goods.getEqIndex());

            totalDiscountAmount += diff;
        }
        return totalDiscountAmount;
    }

    // ==================== 内部：商品操作 ====================

    /**
     * 将所有商品设为 0（免费），返回总减免金额
     */
    private static long goodsAllFree(List<GoodsBO> goodsList, ActivityBO activityBO) {
        long totalDiscount = 0;
        for (GoodsBO goods : goodsList) {
            long discount = goods.getComputeAmount();
            goods.setComputeAmount(0L);
            List<DiscountBO> discountList = goods.getDiscountDetailList();
            DiscountBO discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
            discountBO.setDiscount(discountBO.getDiscount() + discount);
            activityBO.addEqIndex(goods.getEqIndex());
            totalDiscount += discount;
        }
        return totalDiscount;
    }

    /**
     * 将单个商品免费
     */
    private static long goodsFree(GoodsBO goods, ActivityBO activityBO) {
        long discount = goods.getComputeAmount();
        if (discount <= 0) return 0;
        goods.setComputeAmount(0L);
        List<DiscountBO> discountList = goods.getDiscountDetailList();
        DiscountBO discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
        discountBO.setDiscount(discountBO.getDiscount() + discount);
        activityBO.addEqIndex(goods.getEqIndex());
        return discount;
    }

    /**
     * 对单个商品减 N 分
     */
    private static long goodsOffNCent(GoodsBO goods, ActivityBO activityBO, Long offNCent) {
        long discount = Math.min(offNCent, goods.getComputeAmount());
        if (discount <= 0) return 0;
        goods.setComputeAmount(goods.getComputeAmount() - discount);
        List<DiscountBO> discountList = goods.getDiscountDetailList();
        DiscountBO discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
        discountBO.setDiscount(discountBO.getDiscount() + discount);
        activityBO.addEqIndex(goods.getEqIndex());
        return discount;
    }

    /**
     * 每件商品减 1 分，直至 canShareAmount 用尽。
     * 这确保每件商品至少从促销中获益 1 分。
     */
    private static long goodsOffOneCent(List<GoodsBO> goodsList, Long canShareAmount, ActivityBO activityBO) {
        int discount = 0;
        for (GoodsBO goods : goodsList) {
            if (goods.getComputeAmount() <= 0) continue;
            goods.setComputeAmount(goods.getComputeAmount() - 1);
            List<DiscountBO> discountList = goods.getDiscountDetailList();
            DiscountBO discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
            discountBO.setDiscount(discountBO.getDiscount() + 1);
            activityBO.addEqIndex(goods.getEqIndex());
            discount++;
            if (discount >= canShareAmount) break;
        }
        return discount;
    }

    /**
     * 按商品价格比例分摊金额。
     * sharePerGoods = canShareAmount * selfPrice / totalPrice
     */
    private static long goodsShareByScale(List<GoodsBO> goodsList, Long canShareAmount, ActivityBO activityBO) {
        List<GoodsBO> canDiscountGoodsList = canDiscountGoodsList(goodsList);
        if (CollectionUtils.isEmpty(canDiscountGoodsList)) return 0;

        long totalAmount = totalAmount(canDiscountGoodsList);
        if (totalAmount <= 0) return 0;

        long totalShareAmount = 0;
        for (GoodsBO goods : canDiscountGoodsList) {
            long shareAmount = MoneyUtil.getShare(goods.getComputeAmount(), totalAmount, canShareAmount);
            // 不超过剩余可分摊金额
            if (canShareAmount <= totalShareAmount + shareAmount) {
                shareAmount = canShareAmount - totalShareAmount;
            }
            totalShareAmount += shareAmount;

            goods.setComputeAmount(goods.getComputeAmount() - shareAmount);
            List<DiscountBO> discountList = goods.getDiscountDetailList();
            DiscountBO discountBO = getDiscountByActivityDefaultInit(discountList, activityBO);
            discountBO.setDiscount(discountBO.getDiscount() + shareAmount);
            activityBO.addEqIndex(goods.getEqIndex());
        }
        return totalShareAmount;
    }

    // ==================== 内部：商品选择 ====================

    /**
     * 按规则选择商品（HIGHEST, LOWEST, SECOND_HIGHEST, SECOND_LOWEST）。
     * 商品列表已按价格降序排列。
     */
    private static List<GoodsBO> selectGoodsByRule(List<GoodsBO> goodsList, SelectGoodsRuleEnum rule, Integer selectNum) {
        if (CollectionUtils.isEmpty(goodsList)) return Lists.newArrayList();
        if (selectNum >= goodsList.size()) return goodsList;

        List<GoodsBO> result;
        switch (rule) {
            case HIGHEST:
                result = goodsList.subList(0, selectNum);
                break;
            case LOWEST:
                List<GoodsBO> reversed = Lists.newArrayList(goodsList);
                Collections.reverse(reversed);
                result = reversed.subList(0, selectNum);
                break;
            case SECOND_HIGHEST:
                result = goodsList.subList(1, Math.min(selectNum + 1, goodsList.size()));
                break;
            case SECOND_LOWEST:
                List<GoodsBO> reversed2 = Lists.newArrayList(goodsList);
                Collections.reverse(reversed2);
                result = reversed2.subList(1, Math.min(selectNum + 1, reversed2.size()));
                break;
            default:
                result = Lists.newArrayList();
        }
        return Lists.newArrayList(result);
    }

    /**
     * 按规则获取选中商品的总金额
     */
    private static long selectGoodsAmountByRule(List<GoodsBO> goodsList, SelectGoodsRuleEnum rule, Integer selectNum) {
        List<GoodsBO> selected = selectGoodsByRule(goodsList, rule, selectNum);
        if (CollectionUtils.isEmpty(selected)) return 0;
        long amount = 0;
        for (GoodsBO goods : selected) {
            amount += goods.getComputeAmount();
        }
        return amount;
    }

    // ==================== 内部：辅助方法 ====================

    /**
     * 获取可优惠商品（computeAmount > 0），按价格降序排列
     */
    private static List<GoodsBO> canDiscountGoodsList(List<GoodsBO> allGoodsList) {
        if (CollectionUtils.isEmpty(allGoodsList)) return Collections.emptyList();
        List<GoodsBO> result = Lists.newArrayList();
        for (GoodsBO item : allGoodsList) {
            if (item != null && item.getComputeAmount() != null && item.getComputeAmount() > 0) {
                result.add(item);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * 商品列表总金额
     */
    private static long totalAmount(List<GoodsBO> goodsList) {
        if (CollectionUtils.isEmpty(goodsList)) return 0;
        long total = 0;
        for (GoodsBO item : goodsList) {
            if (item != null && item.getComputeAmount() != null && item.getComputeAmount() > 0) {
                total += item.getComputeAmount();
            }
        }
        return total;
    }

    /**
     * 在商品的优惠列表中查找或创建对应活动的 DiscountBO。
     */
    public static DiscountBO getDiscountByActivityDefaultInit(List<DiscountBO> discountList, ActivityBO activityBO) {
        for (DiscountBO discount : discountList) {
            if (discount == null) continue;
            if (activityBO.getActivityType() != null
                    && activityBO.getActivityType().getActivityEnum() == discount.getActivityEnum()
                    && activityBO.getInnerLogicUniqueCode() != null
                    && activityBO.getInnerLogicUniqueCode().equals(discount.getInnerLogicUniqueCode())) {
                return discount;
            }
        }
        DiscountBO discount = new DiscountBO(activityBO);
        discountList.add(discount);
        return discount;
    }
}
