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

package com.qiwumind.next.components.common.util.number;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 高性能权重分发器 - 支持多种权重算法和策略
 * 线程安全，支持高并发场景
 */
public class WeightedDistributor {

    /**
     * 权重项接口，允许自定义权重计算逻辑
     */
    public interface WeightItem {
        double getWeight();
        default boolean isEnabled() {
            return true;
        }
    }

    /**
     * 权重分发器配置
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Config {
        private Algorithm algorithm = Algorithm.BINARY_SEARCH;
        private boolean cacheEnabled = true;
        private boolean validationEnabled = true;
        private double epsilon = 1e-10; // 浮点数比较精度
        private int aliasPrecision = 10000; // Alias算法精度
    }

    /**
     * 权重算法枚举
     *
     * 混合策略建议
     * 在实际系统中，常常使用混合策略：
     * 按权重分组：将高频项用Alias Method，低频项用二分查找
     * 分层抽样：先按大类抽样（二分查找），再在类内抽样（线性搜索）
     * 自适应切换：根据实际运行时的N和调用频率动态选择算法
     */
    public  enum Algorithm {
        /**
         * 二分查找法 - 适合权重项较少的情况
         *
         * 适用场景
         *
         * 中等规模权重项（10-1000）：在O(log n)和实现复杂度间取得平衡
         * 权重固定或很少变化：每次变化需要重建前缀和数组
         * 通用场景：不知道用什么时，二分查找是安全选择
         * 需要较好性能但实现不太复杂
         * 实际例子
         *
         * 游戏中的掉落表（几十到几百种物品）
         * 推荐系统的候选集抽样
         * 一般的随机事件系统
         *
         */
        BINARY_SEARCH,
        /**
         * 线性搜索法 - 适合权重项非常少的情况
         *
         * 适用场景
         *
         * 权重项非常少（通常 ≤ 10）：此时O(n)的开销可以忽略不计
         * 代码简单性优先：快速原型开发，不需要复杂实现
         * 一次性或极低频调用：总共只采样几次，优化无意义
         * 内存极度受限：不需要额外数据结构
         * 实际例子
         *
         * 只有3个选项的简单游戏抽奖
         * 配置文件中读取的少量选项
         * 测试代码或临时实现
         *
         */
        LINEAR_SEARCH,
        /**
         * Alias Method - 适合权重项多且频繁调用的场景
         * 时间复杂度O(1)，空间复杂度O(n)
         *
         * 适用场景
         * 大规模权重项（几千到几百万）：预处理成本可被分摊
         * 极高频调用（每秒数千到数百万次）：O(1)采样速度至关重要
         * 权重固定或很少变化：每次变化需要重新预处理
         * 对性能要求极高：蒙特卡洛模拟、实时系统
         *
         * 实际例子
         *
         * 大规模蒙特卡洛模拟
         * 高频交易中的随机决策
         * 实时渲染中的重要性采样
         * 大型游戏服务器的随机事件系统
         */
        ALIAS,
        /**
         * 树状数组法 - 适合权重动态变化的场景
         *
         * 适用场景
         *
         * 权重动态变化：需要频繁添加、删除或修改权重
         * 流式数据或在线学习：权重随时间不断调整
         * 中等规模的动态系统：O(log n)的更新和采样都可接受
         * 需要支持增删改查的完整操作
         * 实际例子
         *
         * 在线学习中的采样（权重随学习变化）
         * 动态负载均衡（服务器权重实时调整）
         * 自适应推荐系统
         * 游戏中的动态难度调整
         *
         */
        FENWICK_TREE
    }

    /**
     * 权重分发器结果
     */
    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Result<T extends WeightItem> {
        private T item;
        private double selectedValue;
        private double normalizedWeight;
        private int index;
        private Algorithm algorithm;


        public Result(T item, double selectedValue, double totalWeight, int index, Algorithm algorithm) {
            this.item = item;
            this.selectedValue = selectedValue;
            this.normalizedWeight = selectedValue / totalWeight;
            this.index = index;
            this.algorithm = algorithm;
        }
    }

    /**
     * 权重表抽象
     */
    private abstract static class WeightTable<T extends WeightItem> {
        protected final List<T> items;
        protected final double totalWeight;

        WeightTable(List<T> items) {
            this.items = new ArrayList<>(items);
            this.totalWeight = items.stream().mapToDouble(T::getWeight).sum();
        }

        abstract Result<T> select(double randomValue);

        abstract Result<T> select();

        public double getTotalWeight() {
            return totalWeight;
        }

        public int size() {
            return items.size();
        }
    }

    /**
     * 二分查找权重表
     */
    private static class BinarySearchTable<T extends WeightItem> extends WeightTable<T> {
        private final double[] cumulativeWeights;

        BinarySearchTable(List<T> items) {
            super(items);
            this.cumulativeWeights = new double[items.size()];
            double sum = 0;
            for (int i = 0; i < items.size(); i++) {
                sum += items.get(i).getWeight();
                cumulativeWeights[i] = sum;
            }
        }

        @Override
        public Result<T> select(double randomValue) {
            int index = binarySearch(randomValue);
            return new Result<>(items.get(index), randomValue, totalWeight, index, Algorithm.BINARY_SEARCH);
        }

        @Override
        public Result<T> select() {
            double randomValue = ThreadLocalRandom.current().nextDouble() * totalWeight;
            return select(randomValue);
        }

        private int binarySearch(double value) {
            int left = 0, right = cumulativeWeights.length - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (value <= cumulativeWeights[mid]) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            return left;
        }
    }

    /**
     * Alias Method 权重表
     * 参考: https://en.wikipedia.org/wiki/Alias_method
     */
    private static class AliasTable<T extends WeightItem> extends WeightTable<T> {
        private final int[] alias;
        private final double[] probability;
        private final int precision;

        AliasTable(List<T> items, int precision) {
            super(items);
            this.precision = precision;
            int n = items.size();
            this.alias = new int[n];
            this.probability = new double[n];
            initializeAliasTable();
        }

        private void initializeAliasTable() {
            int n = items.size();
            double[] weights = items.stream().mapToDouble(T::getWeight).toArray();
            // 归一化权重
            double average = totalWeight / n;
            double[] scaled = new double[n];
            for (int i = 0; i < n; i++) {
                scaled[i] = weights[i] / average;
            }
            // 使用两个队列：小的和大的
            Deque<Integer> small = new ArrayDeque<>();
            Deque<Integer> large = new ArrayDeque<>();
            for (int i = 0; i < n; i++) {
                if (scaled[i] < 1.0) {
                    small.addLast(i);
                } else {
                    large.addLast(i);
                }
            }

            // 构建Alias表
            while (!small.isEmpty() && !large.isEmpty()) {
                int smallIndex = small.removeFirst();
                int largeIndex = large.removeFirst();

                probability[smallIndex] = scaled[smallIndex];
                alias[smallIndex] = largeIndex;

                scaled[largeIndex] = (scaled[largeIndex] + scaled[smallIndex]) - 1.0;

                if (scaled[largeIndex] < 1.0) {
                    small.addLast(largeIndex);
                } else {
                    large.addLast(largeIndex);
                }
            }

            while (!small.isEmpty()) {
                probability[small.removeFirst()] = 1.0;
            }
            while (!large.isEmpty()) {
                probability[large.removeFirst()] = 1.0;
            }
        }

        @Override
        public Result<T> select(double randomValue) {
            double scaledValue = randomValue / totalWeight * items.size();
            int index = (int) Math.floor(scaledValue);
            double prob = scaledValue - index;

            if (prob < probability[index]) {
                return new Result<>(items.get(index), randomValue, totalWeight, index, Algorithm.ALIAS);
            } else {
                return new Result<>(items.get(alias[index]), randomValue, totalWeight, alias[index], Algorithm.ALIAS);
            }
        }

        @Override
        public Result<T> select() {
            double randomValue = ThreadLocalRandom.current().nextDouble() * totalWeight;
            return select(randomValue);
        }
    }

    /**
     * 线性搜索权重表
     */
    private static class LinearSearchTable<T extends WeightItem> extends WeightTable<T> {
        LinearSearchTable(List<T> items) {
            super(items);
        }

        @Override
        public Result<T> select(double randomValue) {
            double sum = 0;
            for (int i = 0; i < items.size(); i++) {
                sum += items.get(i).getWeight();
                if (randomValue <= sum) {
                    return new Result<>(items.get(i), randomValue, totalWeight, i, Algorithm.LINEAR_SEARCH);
                }
            }
            // 应该不会执行到这里
            return new Result<>(items.get(items.size() - 1), randomValue, totalWeight, items.size() - 1, Algorithm.LINEAR_SEARCH);
        }

        @Override
        public Result<T> select() {
            double randomValue = ThreadLocalRandom.current().nextDouble() * totalWeight;
            return select(randomValue);
        }
    }

    /**
     * 权重表工厂
     */
    private static class WeightTableFactory {
        private static final Map<String, WeightTable<?>> CACHE = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        static <T extends WeightItem> WeightTable<T> createTable(
                List<T> items, Config config, String cacheKey) {

            if (config.isCacheEnabled() && CACHE.containsKey(cacheKey)) {
                return (WeightTable<T>) CACHE.get(cacheKey);
            }

            WeightTable<T> table;
            switch (config.getAlgorithm()) {
                case ALIAS:
                    table = new AliasTable<>(items, config.getAliasPrecision());
                    break;
                case LINEAR_SEARCH:
                    table = new LinearSearchTable<>(items);
                    break;
                case BINARY_SEARCH:
                default:
                    table = new BinarySearchTable<>(items);
                    break;
            }

            if (config.isCacheEnabled()) {
                CACHE.put(cacheKey, table);
            }

            return table;
        }

        static void clearCache() {
            CACHE.clear();
        }

        static int getCacheSize() {
            return CACHE.size();
        }

        static String generateCacheKey(List<? extends WeightItem> items, Config config) {
            StringBuilder sb = new StringBuilder();
            sb.append(config.getAlgorithm()).append(":");
            sb.append(config.getAliasPrecision()).append(":");

            // 生成基于内容的哈希
            items.forEach(item -> {
                sb.append(item.hashCode()).append(":");
                sb.append(Double.hashCode(item.getWeight())).append(":");
            });

            return sb.toString();
        }
    }

    /**
     * 分发器核心类
     */
    public static class Distributor<T extends WeightItem> {
        private final WeightTable<T> weightTable;
        private final Config config;
        private final String cacheKey;

        private Distributor(List<T> items, Config config) {
            validateItems(items, config);
            this.config = config;
            this.cacheKey = WeightTableFactory.generateCacheKey(items, config);
            this.weightTable = WeightTableFactory.createTable(items, config, cacheKey);
        }

        /**
         * 随机选择一个项
         */
        public Result<T> distribute() {
            return weightTable.select();
        }

        /**
         * 根据指定值选择一个项
         */
        public Result<T> distribute(double value) {
            // 将value归一化到[0, totalWeight)区间
            double normalized = (value % weightTable.getTotalWeight() + weightTable.getTotalWeight())
                    % weightTable.getTotalWeight();
            return weightTable.select(normalized);
        }

        /**
         * 批量分发
         */
        public List<Result<T>> batchDistribute(int count) {
            List<Result<T>> results = new ArrayList<>(count);
            ThreadLocalRandom random = ThreadLocalRandom.current();

            for (int i = 0; i < count; i++) {
                double randomValue = random.nextDouble() * weightTable.getTotalWeight();
                results.add(weightTable.select(randomValue));
            }

            return results;
        }

        /**
         * 按权重分配指定数量的项（允许重复）
         */
        public List<T> allocate(int count) {
            List<T> allocated = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                allocated.add(distribute().getItem());
            }
            return allocated;
        }

        /**
         * 获取总权重
         */
        public double getTotalWeight() {
            return weightTable.getTotalWeight();
        }

        /**
         * 获取权重项数量
         */
        public int getItemCount() {
            return weightTable.size();
        }

        /**
         * 获取归一化权重
         */
        public Map<T, Double> getNormalizedWeights() {
            return weightTable.items.stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            item -> item.getWeight() / weightTable.getTotalWeight()
                    ));
        }

        private void validateItems(List<T> items, Config config) {
            if (!config.isValidationEnabled()) {
                return;
            }
            if (CollectionUtils.isEmpty(items)) {
                throw new IllegalArgumentException("权重项不能为空");
            }
            // 检查权重有效性
            int enabledCount = 0;
            for (T item : items) {
                if (item.isEnabled()) {
                    if (item.getWeight() < 0) {
                        throw new IllegalArgumentException("权重不能为负数: " + item);
                    }
                    enabledCount++;
                }
            }
            if (enabledCount == 0) {
                throw new IllegalArgumentException("没有启用的权重项");
            }
            // 检查权重和是否为0
            double totalWeight = items.stream()
                    .filter(T::isEnabled)
                    .mapToDouble(T::getWeight)
                    .sum();
            if (totalWeight <= config.getEpsilon()) {
                throw new IllegalArgumentException("总权重必须大于0");
            }
        }
    }

    /**
     * 创建分发器
     */
    public static <T extends WeightItem> Distributor<T> create(List<T> items) {
        return create(items, new Config());
    }

    public static <T extends WeightItem> Distributor<T> create(List<T> items, Config config) {
        return new Distributor<>(items, config);
    }

    /**
     * 快速分发（简单场景）
     */
    public static <T extends WeightItem> T quickDistribute(List<T> items) {
        return create(items).distribute().getItem();
    }

    /**
     * 清除所有缓存
     */
    public static void clearAllCache() {
        WeightTableFactory.clearCache();
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return WeightTableFactory.getCacheSize();
    }
}
