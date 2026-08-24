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

package com.qiwumind.next.components.common.util.vintageloss;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.stream.Stream;

/**
 * 针对金融风控vintageloss 的预测终损进行
 * f(n) + g(n) = x * y
 * g(n) = f(n-1)
 * t(n) = x - x * y
 */
public class VintageUtils {

    // 计算 f(n)
    public static int f(int n, int x) {
        double y = VintageMobEnum.tryParse(n).getMob();
        if (n <= 0) {
            // 需要定义初始条件，假设 f(0) = 0 或需要您指定
            return 0;
        }
        if (n == 1) {
            // 对于 n=1，根据公式: f(1) + g(1) = x*y，且 g(1) = f(0)
            // 所以 f(1) = x*y - f(0)
            return (int) Math.ceil(x * y);
        } else {
            // 对于 n>1: f(n) + f(n-1) = x*y
            return (int) Math.ceil(x * y) - f(n - 1, x);
        }
    }

    // 计算 g(n)
    public static int g(int n, int x) {
        // g(n) = f(n-1)
        if (n <= 1) {
            return 0; // 需要定义初始条件
        }
        return f(n - 1, x);
    }

    // 计算 t(n)
    public static int t(int n, int x) {
        // t(n) = x - x*y
        // 注意：这个公式与 n 无关，只与 x 和 y 有关
        return x - (f(n, x) + g(n, x));
    }

    public static void vintage(int n, int x, VintageRepay vintage) {
        int f = f(n, x);
        int g = g(n, x);
        int t = t(n, x);
        vintage.setSodr(f);
        vintage.setFld(g);
        vintage.setTor(t);
        vintage.setCount(x);

        double y = VintageMobEnum.tryParse(n).getMob();
        vintage.setMob(y);
        vintage.setInstallment(n);
    }


    @Getter
    @ToString
    public static enum VintageMobEnum {
        // 可配置化
        MOB2(1, 0.035D), MOB3(2, 0.051D),
        MOB4(3, 0.067D), MOB5(4, 0.079D),
        MOB6(5, 0.083D), MOB7(6, 0.086D);
        private int installment;
        private double mob;

        private VintageMobEnum(int installment, double mob) {
            this.installment = installment;
            this.mob = mob;
        }

        public static VintageMobEnum tryParse(int installment) {
            return Stream.of(VintageMobEnum.values()).filter(mob -> mob.getInstallment() == installment)
                    .findFirst().orElse(null);
        }
    }

    @Setter
    @Getter
    @ToString
    public static class VintageRepay {
        // x
        private int count;
        // y
        private double mob;
        // 期次
        private int installment;
        /**
         * 单期代偿  指为借款人偿还某一期特定的逾期款项。  f(n)
         */
        private int sodr;//SingleOverdueRepayment
        /**
         * 整笔代偿：指为借款人一次性结清该笔贷款的全部剩余未还本金、利息等。 g(n)
         */
        private int fld;//Full Loan Settlement
        /**
         * 转线下还款：指将还款方式从线上自动扣款等模式，转变为通过银行柜台、现金等方式进行线下手动还款。  t(n)
         */
        private int tor;//Transfer to Offline Repayment 转线下还款

    }


    public static void main(String[] args) {
        VintageRepay vr = new VintageRepay();
        int x = 78;
        vintage(1, 78, vr);
        System.out.println("vintage 1=" + vr);
        vintage(2, 78, vr);
        System.out.println("vintage 2=" + vr);
        vintage(3, 78, vr);
        System.out.println("vintage 3=" + vr);
        vintage(4, 78, vr);
        System.out.println("vintage 4=" + vr);
        vintage(5, 78, vr);
        System.out.println("vintage 5=" + vr);
        vintage(6, 78, vr);
        System.out.println("vintage 6=" + vr);

    }

}
