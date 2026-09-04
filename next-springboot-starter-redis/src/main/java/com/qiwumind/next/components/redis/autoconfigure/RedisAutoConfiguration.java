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

package com.qiwumind.next.components.redis.autoconfigure;

import com.qiwumind.next.components.redis.core.JedisPoolManager;
import com.qiwumind.next.components.redis.core.bloomfilter.RedisConnectionManager;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import com.qiwumind.next.components.redis.core.cache.RedisTemplateCache;
import com.qiwumind.next.components.redis.core.lock.LockManager;
import com.qiwumind.next.components.redis.core.lock.RedissonLockService;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulConnection;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redis 自动配置（连接信息收口自 spring.data.redis，与官方共用一份配置）。
 * <p>
 * 开关：配置了 spring.data.redis.host 即启用；不配置则整个组件不生效。
 * before 官方 RedisAutoConfiguration，保证本组件的 RedisConnectionFactory/RedisTemplate
 * （JSON 序列化）优先注册，官方（JDK 序列化）因 @ConditionalOnMissingBean 退出。
 */
@Slf4j
@AutoConfiguration(before = org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@EnableConfigurationProperties(value = {RedisConfiguration.class})
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
@Import(RedissonAutoConfiguration.class)  // 导入 Redisson 配置
public class RedisAutoConfiguration {

    private final RedisConfiguration redisConfiguration;

    public RedisAutoConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory() {
        // 1. 基础连接配置
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisConfiguration.getHost());
        serverConfig.setPort(redisConfiguration.getPort());
        if (StringUtils.hasText(redisConfiguration.getUsername())) {
            serverConfig.setUsername(redisConfiguration.getUsername());
        }
        serverConfig.setPassword(redisConfiguration.getPassword());
        serverConfig.setDatabase(redisConfiguration.getDatabase());

        // 2. 配置连接池（Lettuce 也需要 Apache Commons Pool2 依赖），参数读取 spring.data.redis.lettuce.pool 配置项
        RedisConfiguration.Lettuce lettuce = redisConfiguration.getLettuce();
        RedisConfiguration.Lettuce.Pool pool = lettuce.getPool();
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(pool.getMaxActive());                            // 最大连接数
        poolConfig.setMaxIdle(pool.getMaxIdle());                               // 最大空闲连接
        poolConfig.setMinIdle(pool.getMinIdle());                               // 最小空闲连接
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());              // 获取连接最大等待时间
        // 连接池优化配置（解决连接泄漏和空闲连接回收）
        poolConfig.setTestOnBorrow(true);            // 获取连接时校验（会发送PING）
        poolConfig.setTestOnReturn(false);           // 归还连接时不校验
        poolConfig.setTestWhileIdle(true);           // 空闲时校验连接有效性
        if (pool.getTimeBetweenEvictionRuns() != null) {
            poolConfig.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis()); // 空闲连接检查周期
        }
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofMinutes(5).toMillis()); // 连接最小空闲时间
        poolConfig.setNumTestsPerEvictionRun(3);     // 每次检查的连接数

        // 3. 使用完整构建器配置 Lettuce 客户端
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)                                   // 设置连接池
                .commandTimeout(redisConfiguration.getTimeout())          // 命令执行超时
                .shutdownTimeout(lettuce.getShutdownTimeout())            // 关闭超时
                // 解决连接断连问题的关键配置
                .clientOptions(createClientOptions())
                .build();

        // 4. 创建工厂
        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
        // 5. 额外设置（重要！用于解决连接校验问题）
        factory.setValidateConnection(true);      // 获取连接时校验连接有效性
        factory.setShareNativeConnection(true);   // 共享本地连接（提高性能，但要注意并发）
        return factory;
    }

    private ClientOptions createClientOptions() {
        return ClientOptions.builder()
                // 自动重连配置
                .autoReconnect(true)
                // 断开连接后是否立即重连
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.ACCEPT_COMMANDS)
                // 连接超时配置
                .timeoutOptions(TimeoutOptions.builder()
                        .fixedTimeout(Duration.ofSeconds(10))
                        .build())
                // Ping命令超时配置
                .pingBeforeActivateConnection(true)  // 激活连接前发送PING
                // Socket配置
                .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .keepAlive(true)
                        .tcpNoDelay(true)
                        .build()).build();
    }

    @Bean(value = "jedisPoolManager", initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(JedisPoolManager.class)
    public JedisPoolManager jedisPoolManager() {
        JedisPoolManager poolManager = new JedisPoolManager(redisConfiguration.getHost(), redisConfiguration.getPassword(),
                redisConfiguration.getPort());
        log.info("******load JedisPoolManager={} **********", poolManager);
        return poolManager;
    }

    @Bean(value = "redissonLockService")
    @ConditionalOnMissingBean(RedissonLockService.class)
    public RedissonLockService redissonLockService(RedissonClient redissonClient) {
        RedissonLockService lockService = new RedissonLockService(redissonClient);
        log.info("******load redissonLockService **********");
        return lockService;
    }

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        log.info("******load redisTemplate **********");
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    public LockManager lockManager(RedissonLockService redissonLockService) {
        LockManager lockManager = new LockManager(redissonLockService);
        log.info("******load lockManager **********");
        return lockManager;
    }

    @Bean("JedisCache")
    @ConditionalOnMissingBean(JedisCache.class)
    public JedisCache jedisCache(JedisPoolManager jedisPoolManager) {
        JedisCache cache = new JedisCache(jedisPoolManager);
        log.info("******load JedisCache **********");
        return cache;
    }

    @Bean
    @ConditionalOnMissingBean(RedisTemplateCache.class)
    public RedisTemplateCache redisTemplateCache(RedisTemplate<String, Object> redisTemplate) {
        RedisTemplateCache redisTemplateCache = new RedisTemplateCache(redisTemplate);
        log.info("******load redisTemplateCache **********");
        return redisTemplateCache;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionManager.class)
    public RedisConnectionManager redisConnectionManager() {
        RedisConnectionManager manager = RedisConnectionManager.getInstance();
        manager.setRedisConfiguration(redisConfiguration);
        return manager;
    }

    @PreDestroy
    public void destroy() {
        log.info("******destroy RedisConnectionManager.getInstance().closeAll() **********");
        RedisConnectionManager.getInstance().closeAll();
    }

}
