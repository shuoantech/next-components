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

package com.qiwumind.next.components.compute.core;




import com.qiwumind.next.components.compute.core.dto.DefaultRepaymentDayResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 还款日计算工具类
 * <p>
 * 支持的固定还款日：每月 10号, 15号, 20号, 26号
 */
public final class RepaymentDayCalculator {

    /**
     * 系统支持的固定还款日列表
     */
    public static final List<Integer> SUPPORTED_REPAYMENT_DAYS =
            Collections.unmodifiableList(Arrays.asList(10, 15, 20, 26));

    /**
     * 私有构造函数，防止外部实例化
     */
    private RepaymentDayCalculator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 计算新客户的默认还款日和首期还款日
     *
     * @param currentDate 当前日期
     * @return DefaultRepaymentDayResult 对象，包含默认还款日和首期还款日
     */
    public static DefaultRepaymentDayResult calculateNewCustomerDefault(LocalDate currentDate) {
        int dayOfMonth = currentDate.getDayOfMonth();

        if (dayOfMonth >= 1 && dayOfMonth <= 9) {
            return new DefaultRepaymentDayResult(26, currentDate.withDayOfMonth(26));
        } else if (dayOfMonth >= 10 && dayOfMonth <= 14) {
            return new DefaultRepaymentDayResult(10, currentDate.plusMonths(1).withDayOfMonth(10));
        } else if (dayOfMonth >= 15 && dayOfMonth <= 19) {
            return new DefaultRepaymentDayResult(15, currentDate.plusMonths(1).withDayOfMonth(15));
        } else { // 20号到月底
            return new DefaultRepaymentDayResult(20, currentDate.plusMonths(1).withDayOfMonth(20));
        }
    }

    /**
     * 计算客户自定义还款日时，可选择的目标还款日列表
     * <p>
     * 规则：首期还款日必须落在当前日期 N 的 (N+10, N+35] 天范围内。
     * 即：大于 N+10 天，并且小于或等于 N+35 天。
     *
     * @param currentDate 当前日期
     * @return 符合条件的还款日列表
     */
    public static List<Integer> calculateCustomAvailableDays(LocalDate currentDate) {
        LocalDate rangeStart = currentDate.plusDays(10); // 不包含此日期
        LocalDate rangeEnd = currentDate.plusDays(35);   // 包含此日期

        return SUPPORTED_REPAYMENT_DAYS.stream()
                .filter(day -> isRepaymentDayInRange(day, rangeStart, rangeEnd))
                .collect(Collectors.toList());
    }

    /**
     * 计算老客户的首期还款日
     * <p>
     * 规则：使用客户已有的固定还款日，且首期还款日与当前日期间隔必须大于等于10天。
     *
     * @param currentDate        当前日期
     * @param customerFixedDay   客户的固定还款日
     * @return 计算出的首期还款日
     * @throws IllegalArgumentException 如果客户的固定还款日不被系统支持
     */
    public static LocalDate calculateExistingCustomerFirstDay(LocalDate currentDate, int customerFixedDay) {
        if (!SUPPORTED_REPAYMENT_DAYS.contains(customerFixedDay)) {
            throw new IllegalArgumentException("客户的固定还款日 " + customerFixedDay + " 不被支持。");
        }

        LocalDate firstRepaymentDate = currentDate.withDayOfMonth(customerFixedDay);

        // 如果当月的还款日已过，或者距离今天不足10天，则自动顺延到下个月
        if (firstRepaymentDate.isBefore(currentDate) || ChronoUnit.DAYS.between(currentDate, firstRepaymentDate) < 10) {
            firstRepaymentDate = firstRepaymentDate.plusMonths(1);
        }

        return firstRepaymentDate;
    }

    /**
     * 辅助方法：判断一个给定的还款日（如10号）在指定的半开半闭区间 (rangeStart, rangeEnd] 内是否存在一个有效的日期。
     *
     * @param repaymentDay 还款日（如 10）
     * @param rangeStart   区间起始日期（不包含）
     * @param rangeEnd     区间结束日期（包含）
     * @return 如果存在有效日期则返回 true，否则返回 false
     */
    private static boolean isRepaymentDayInRange(int repaymentDay, LocalDate rangeStart, LocalDate rangeEnd) {
        // 检查当月的还款日
        LocalDate candidateDate = rangeStart.withDayOfMonth(repaymentDay);

        // 如果当月的还款日在范围内，则有效
        if (candidateDate.isAfter(rangeStart) && !candidateDate.isAfter(rangeEnd)) {
            return true;
        }

        // 检查下个月的还款日
        candidateDate = candidateDate.plusMonths(1);
        return candidateDate.isAfter(rangeStart) && !candidateDate.isAfter(rangeEnd);
    }

}
