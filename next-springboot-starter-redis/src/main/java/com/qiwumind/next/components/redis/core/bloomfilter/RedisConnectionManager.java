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

package com.qiwumind.next.components.redis.core.bloomfilter;



import com.qiwumind.next.components.redis.autoconfigure.RedisConfiguration;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis连接池管理器
 * 负责管理Redis连接的创建、复用和关闭
 * 使用单例模式确保全局只有一个连接池管理器
 */
public class RedisConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(RedisConnectionManager.class);

    /**
     * 单例实例
     */
    private static volatile RedisConnectionManager instance;

    // Redis配置
    private RedisConfiguration redisConfiguration;

    // 存储Redis客户端实例，key为Redis连接标识
    private final Map<String, RedisClient> redisClients = new ConcurrentHashMap<>();

    // 存储Redis连接实例
    private final Map<String, StatefulRedisConnection<String, String>> connections = new ConcurrentHashMap<>();

    // 客户端资源，用于管理连接池
    private ClientResources clientResources;

    // 连接标识常量
    private static final String DEFAULT_CONNECTION_KEY = "default";

    /**
     * 私有构造函数
     */
    private RedisConnectionManager() {
        initializeClientResources();
    }

    /**
     * 获取单例实例
     */
    public static RedisConnectionManager getInstance() {
        if (instance == null) {
            synchronized (RedisConnectionManager.class) {
                if (instance == null) {
                    instance = new RedisConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化客户端资源
     */
    private void initializeClientResources() {
        try {
            this.clientResources = DefaultClientResources.builder()
                    .ioThreadPoolSize(4)
                    .computationThreadPoolSize(4)
                    .build();
            log.info("Redis客户端资源初始化完成");
        } catch (Exception e) {
            log.error("初始化Redis客户端资源失败", e);
            throw new RuntimeException("初始化Redis客户端资源失败", e);
        }
    }

    /**
     * 设置Redis配置
     */
    public void setRedisConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
        log.info("Redis配置已加载: {}", redisConfiguration);
    }


    /**
     * 获取指定配置的Redis连接
     */
    public synchronized RedisCommands<String, String> getConnection(String connectionKey) {
        validateRedisConfiguration();
        //获取默认Redis连接
        if (StringUtils.isBlank(connectionKey)) {
            connectionKey = DEFAULT_CONNECTION_KEY;
        }
        StatefulRedisConnection<String, String> connection = connections.get(connectionKey);

        if (connection == null || !connection.isOpen()) {
            RedisClient redisClient = getOrCreateRedisClient(connectionKey);
            connection = redisClient.connect();
            connections.put(connectionKey, connection);
            log.debug("创建新的Redis连接: {}", connectionKey);
        }

        return connection.sync();
    }

    /**
     * 获取StatefulRedisConnection连接
     */
    public synchronized StatefulRedisConnection<String, String> getStatefulConnection(String connectionKey) {
        validateRedisConfiguration();

        StatefulRedisConnection<String, String> connection = connections.get(connectionKey);

        if (connection == null || !connection.isOpen()) {
            RedisClient redisClient = getOrCreateRedisClient(connectionKey);
            connection = redisClient.connect();
            connections.put(connectionKey, connection);
            log.debug("创建新的StatefulRedis连接: {}", connectionKey);
        }

        return connection;
    }

    /**
     * 获取或创建Redis客户端实例
     */
    private RedisClient getOrCreateRedisClient(String connectionKey) {
        return redisClients.computeIfAbsent(connectionKey, key -> {
            try {
                RedisURI redisURI = buildRedisURI();
                RedisClient client = RedisClient.create(clientResources, redisURI);

                // 配置客户端选项
                client.setOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .pingBeforeActivateConnection(true)
                        .cancelCommandsOnReconnectFailure(true)
                        .requestQueueSize(1000)
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .build());

                log.info("创建Redis客户端: {}, host: {}", key, redisConfiguration.getHost());
                return client;
            } catch (Exception e) {
                log.error("创建Redis客户端失败: {}", key, e);
                throw new RuntimeException("创建Redis客户端失败: " + key, e);
            }
        });
    }

    /**
     * 构建RedisURI
     */
    private RedisURI buildRedisURI() {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(redisConfiguration.getHost())
                .withPort(redisConfiguration.getPort())
                .withTimeout(redisConfiguration.getTimeout());

        // 设置密码
        // 注：当前 Lettuce 版本的 RedisURI.Builder 不支持单独设置 username，
        // 使用 Redis 6 ACL 自定义账号时请走主连接工厂（RedisAutoConfiguration）或 Redisson
        if (redisConfiguration.getPassword() != null && !redisConfiguration.getPassword().isEmpty()) {
            builder.withPassword(redisConfiguration.getPassword().toCharArray());
        }

        // 设置数据库
        if (redisConfiguration.getDatabase() != null) {
            builder.withDatabase(redisConfiguration.getDatabase());
        }

        // 设置SSL
        if (redisConfiguration.isSslEnabled()) {
            builder.withSsl(true);
        }

        // 设置客户端名称
        if (redisConfiguration.getClientName() != null) {
            builder.withClientName(redisConfiguration.getClientName());
        }

        return builder.build();
    }

    /**
     * 验证Redis配置
     */
    private void validateRedisConfiguration() {
        if (redisConfiguration == null) {
            throw new IllegalStateException("Redis配置未初始化，请先调用setredisConfiguration方法");
        }

        if (redisConfiguration.getHost() == null || redisConfiguration.getHost().isEmpty()) {
            throw new IllegalArgumentException("Redis主机地址不能为空");
        }

        if (redisConfiguration.getPort() <= 0 || redisConfiguration.getPort() > 65535) {
            throw new IllegalArgumentException("Redis端口号无效: " + redisConfiguration.getPort());
        }
    }

    /**
     * 关闭指定连接
     */
    public synchronized void closeConnection(String connectionKey) {
        StatefulRedisConnection<String, String> connection = connections.remove(connectionKey);
        if (connection != null && connection.isOpen()) {
            connection.close();
            log.debug("关闭Redis连接: {}", connectionKey);
        }
    }

    /**
     * 关闭指定客户端
     */
    public synchronized void closeClient(String connectionKey) {
        closeConnection(connectionKey);
        RedisClient redisClient = redisClients.remove(connectionKey);
        if (redisClient != null) {
            redisClient.shutdown();
            log.debug("关闭Redis客户端: {}", connectionKey);
        }
    }

    /**
     * 关闭所有连接和客户端
     */
    public synchronized void closeAll() {
        // 关闭所有连接
        connections.forEach((key, connection) -> {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        });
        connections.clear();
        log.info("已关闭所有Redis连接");

        // 关闭所有客户端
        redisClients.forEach((key, client) -> {
            if (client != null) {
                client.shutdown();
            }
        });
        redisClients.clear();
        log.info("已关闭所有Redis客户端");

        // 关闭客户端资源
        if (clientResources != null) {
            clientResources.shutdown();
            log.info("已关闭Redis客户端资源");
        }
    }

    /**
     * 获取连接统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("connectionCount", connections.size());
        stats.put("clientCount", redisClients.size());
        stats.put("redisConfiguration", redisConfiguration);
        stats.put("initialized", redisConfiguration != null);
        return stats;
    }

    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            RedisCommands<String, String> commands = getConnection(null);
            String result = commands.ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            log.error("Redis连接测试失败", e);
            return false;
        }
    }
}
