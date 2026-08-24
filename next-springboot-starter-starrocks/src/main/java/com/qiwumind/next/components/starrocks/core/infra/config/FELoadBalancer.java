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

package com.qiwumind.next.components.starrocks.core.infra.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * StarRocks FE节点健康检测与负载均衡器
 */
public class FELoadBalancer {
    private static final Logger logger = LoggerFactory.getLogger(FELoadBalancer.class);

    private final List<FENode> feNodes;
    private final ScheduledExecutorService healthCheckScheduler;
    private final Map<String, FENodeStatus> nodeStatus;
    private final Random random;

    // 健康检查配置
    private static final int HEALTH_CHECK_INTERVAL = 30; // 秒
    private static final int CONNECT_TIMEOUT = 5000; // 5秒
    private static final int READ_TIMEOUT = 10000; // 10秒

    public FELoadBalancer(List<FENode> feNodes) {
        this.feNodes = new CopyOnWriteArrayList<>(feNodes);
        this.healthCheckScheduler = Executors.newScheduledThreadPool(1);
        this.nodeStatus = new ConcurrentHashMap<>();
        this.random = new Random();

    }

    public void init() {
        // 初始化节点状态
        initializeNodeStatus();
        // 启动健康检查
        startHealthCheck();
    }

    /**
     * FE节点信息
     */
    public static class FENode {
        private final String host;
        private final int httpPort;
        private final String name;
        private final int priority; // 优先级，数字越小优先级越高

        public FENode(String host, int httpPort, String name, int priority) {
            this.host = host;
            this.httpPort = httpPort;
            this.name = name;
            this.priority = priority;
        }

        public FENode(String host, int httpPort) {
            this(host, httpPort, host + ":" + httpPort, 1);
        }

        // Getters
        public String getHost() {
            return host;
        }

        public int getHttpPort() {
            return httpPort;
        }

        public String getName() {
            return name;
        }

        public int getPriority() {
            return priority;
        }

        public String getHttpUrl() {
            return "http://" + host + ":" + httpPort;
        }

        @Override
        public String toString() {
            return String.format("FENode{name='%s', host='%s', port=%d, priority=%d}",
                    name, host, httpPort, priority);
        }
    }

    /**
     * FE节点状态
     */
    public static class FENodeStatus {
        private volatile boolean healthy = false;
        private volatile long lastCheckTime = 0;
        private volatile long responseTime = Long.MAX_VALUE;
        private volatile int failureCount = 0;
        private volatile int successCount = 0;
        private volatile String lastError;

        // Getters and Setters
        public boolean isHealthy() {
            return healthy;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }

        public long getLastCheckTime() {
            return lastCheckTime;
        }

        public void setLastCheckTime(long lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public String getLastError() {
            return lastError;
        }

        public void setLastError(String lastError) {
            this.lastError = lastError;
        }

        public double getSuccessRate() {
            int total = successCount + failureCount;
            return total == 0 ? 0 : (double) successCount / total * 100;
        }
    }

    private void initializeNodeStatus() {
        for (FENode node : feNodes) {
            nodeStatus.put(node.getName(), new FENodeStatus());
        }
    }

    private void startHealthCheck() {
        healthCheckScheduler.scheduleAtFixedRate(() -> {
            try {
                performHealthCheck();
            } catch (Exception e) {
                logger.error("健康检查执行异常", e);
            }
        }, 0, HEALTH_CHECK_INTERVAL, TimeUnit.SECONDS);

        logger.info("FE节点健康检查已启动，检查间隔: {}秒", HEALTH_CHECK_INTERVAL);
    }

    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (FENode node : feNodes) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                checkNodeHealth(node);
            });
            futures.add(future);
        }
        // 等待所有检查完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        logHealthStatus();
    }

    /**
     * 检查单个节点健康状态
     */
    private void checkNodeHealth(FENode node) {
        FENodeStatus status = nodeStatus.get(node.getName());
        long startTime = System.currentTimeMillis();
        try {
            URL url = new URL(node.getHttpUrl() + "/api/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            int responseCode = connection.getResponseCode();
            long responseTime = System.currentTimeMillis() - startTime;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 健康检查成功
                status.setHealthy(true);
                status.setResponseTime(responseTime);
                status.setSuccessCount(status.getSuccessCount() + 1);
                status.setLastError(null);

                logger.debug("FE节点健康检查成功: {}, 响应时间: {}ms", node.getName(), responseTime);
            } else {
                // 健康检查失败
                handleNodeFailure(node, status, "HTTP状态码: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            // 连接异常
            handleNodeFailure(node, status, "连接异常: " + e.getMessage());
        } finally {
            status.setLastCheckTime(System.currentTimeMillis());
        }
    }

    private void handleNodeFailure(FENode node, FENodeStatus status, String error) {
        status.setHealthy(false);
        status.setFailureCount(status.getFailureCount() + 1);
        status.setLastError(error);

        logger.warn("FE节点健康检查失败: {}, 错误: {}", node.getName(), error);
    }

    /**
     * 选择最优FE节点
     */
    public FENode selectOptimalNode(SelectionStrategy strategy) {
        List<FENode> availableNodes = getAvailableNodes();

        if (availableNodes.isEmpty()) {
            throw new IllegalStateException("没有可用的FE节点");
        }

        switch (strategy) {
            case RANDOM:
                return selectRandomNode(availableNodes);
            case ROUND_ROBIN:
                return selectRoundRobinNode(availableNodes);
            case RESPONSE_TIME:
                return selectByResponseTime(availableNodes);
            case PRIORITY:
                return selectByPriority(availableNodes);
            default:
                return selectRandomNode(availableNodes);
        }
    }

    /**
     * 随机选择
     */
    private FENode selectRandomNode(List<FENode> availableNodes) {
        return availableNodes.get(random.nextInt(availableNodes.size()));
    }

    /**
     * 轮询选择
     */
    private int roundRobinIndex = 0;

    private FENode selectRoundRobinNode(List<FENode> availableNodes) {
        synchronized (this) {
            FENode selected = availableNodes.get(roundRobinIndex % availableNodes.size());
            roundRobinIndex = (roundRobinIndex + 1) % availableNodes.size();
            return selected;
        }
    }

    /**
     * 根据响应时间选择
     */
    private FENode selectByResponseTime(List<FENode> availableNodes) {
        return availableNodes.stream()
                .min(Comparator.comparingLong(node ->
                        nodeStatus.get(node.getName()).getResponseTime()))
                .orElse(availableNodes.get(0));
    }

    /**
     * 根据优先级选择
     */
    private FENode selectByPriority(List<FENode> availableNodes) {
        return availableNodes.stream()
                .min(Comparator.comparingInt(FENode::getPriority)
                        .thenComparingLong(node ->
                                nodeStatus.get(node.getName()).getResponseTime()))
                .orElse(availableNodes.get(0));
    }

    /**
     * 获取可用节点列表
     */
    public List<FENode> getAvailableNodes() {
        return feNodes.stream()
                .filter(node -> {
                    FENodeStatus status = nodeStatus.get(node.getName());
                    return status != null && status.isHealthy();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取所有节点状态
     */
    public Map<String, FENodeStatus> getAllNodeStatus() {
        return new HashMap<>(nodeStatus);
    }

    /**
     * 添加新的FE节点
     */
    public void addFENode(FENode node) {
        feNodes.add(node);
        nodeStatus.put(node.getName(), new FENodeStatus());
        logger.info("添加新的FE节点: {}", node);
    }

    /**
     * 移除FE节点
     */
    public void removeFENode(String nodeName) {
        feNodes.removeIf(node -> node.getName().equals(nodeName));
        nodeStatus.remove(nodeName);
        logger.info("移除FE节点: {}", nodeName);
    }

    /**
     * 记录健康状态
     */
    private void logHealthStatus() {
        List<FENode> availableNodes = getAvailableNodes();
        logger.info("FE节点健康状态: 总数={}, 可用={}, 不可用={}",
                feNodes.size(), availableNodes.size(), feNodes.size() - availableNodes.size());

        for (FENode node : feNodes) {
            FENodeStatus status = nodeStatus.get(node.getName());
            if (status != null) {
                logger.debug("节点 {}: 健康={}, 响应时间={}ms, 成功率={:.1f}%",
                        node.getName(), status.isHealthy(), status.getResponseTime(),
                        status.getSuccessRate());
            }
        }
    }

    /**
     * 关闭负载均衡器
     */
    public void shutdown() {
        healthCheckScheduler.shutdown();
        try {
            if (!healthCheckScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                healthCheckScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            healthCheckScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("FE负载均衡器已关闭");
    }

    /**
     * 选择策略枚举
     */
    public static enum SelectionStrategy {
        RANDOM,          // 随机选择
        ROUND_ROBIN,     // 轮询选择
        RESPONSE_TIME,   // 响应时间最优
        PRIORITY         // 优先级 + 响应时间
    }
}
