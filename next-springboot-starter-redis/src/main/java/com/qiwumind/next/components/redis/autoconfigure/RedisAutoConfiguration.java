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


import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.redis.core.JedisPoolManager;
import com.qiwumind.next.components.redis.core.bloomfilter.RedisConnectionManager;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import com.qiwumind.next.components.redis.core.cache.RedisTemplateCache;
import com.qiwumind.next.components.redis.core.lock.JedisLockService;
import com.qiwumind.next.components.redis.core.lock.LockManager;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulConnection;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@EnableConfigurationProperties(value = {RedisConfiguration.class})
@Configuration
@ConditionalOnProperty(prefix = SystemConstants.Prefix.REDIS, name = "enabled", havingValue = "true")
public class RedisAutoConfiguration {

    private RedisConfiguration redisConfiguration;

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
        serverConfig.setPassword(redisConfiguration.getPassword());
        serverConfig.setDatabase(redisConfiguration.getDatabase());
        // 2. 配置连接池（Lettuce 也需要 Apache Commons Pool2 依赖）
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        // 基础连接池配置
        poolConfig.setMaxTotal(20);                // 最大连接数
        poolConfig.setMaxIdle(10);                   // 最大空闲连接
        poolConfig.setMinIdle(5);                     // 最小空闲连接
        poolConfig.setMaxWaitMillis(Duration.ofSeconds(5).toMillis()); // 获取连接最大等待时间
        // 连接池优化配置（解决连接泄漏和空闲连接回收）
        poolConfig.setTestOnBorrow(true);            // 获取连接时校验（会发送PING）
        poolConfig.setTestOnReturn(false);            // 归还连接时不校验
        poolConfig.setTestWhileIdle(true);            // 空闲时校验连接有效性
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis()); // 空闲连接检查周期
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofMinutes(5).toMillis());     // 连接最小空闲时间
        poolConfig.setNumTestsPerEvictionRun(3);      // 每次检查的连接数

        // 3. 使用完整构建器配置 Lettuce 客户端
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)                          // 设置连接池
                .commandTimeout(Duration.ofMillis(5000))         // 命令执行超时
                .shutdownTimeout(Duration.ofMillis(2000))        // 关闭超时
                // 解决连接断连问题的关键配置（针对你之前的情况）
                // 客户端选项配置（通过 withClientOptions）
                .clientOptions(createClientOptions())
                // 如果需要SSL
                // .useSsl()
                // 如果需要连接验证（Lettuce 5.2+）
                // .validateConnection()
                .build();

        // 4. 创建工厂
        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
        // 5. 额外设置（重要！用于解决连接校验问题）
        factory.setValidateConnection(true);      // 获取连接时校验连接有效性
        factory.setShareNativeConnection(true);   // 共享本地连接（提高性能，但要注意并发）
        // 如果需要自适应集群拓扑更新（如果是集群模式）
        // factory.setEagerInitialization(true);
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
    public JedisPoolManager jedisPoolManager() {
        JedisPoolManager poolManager = null;
        poolManager = new JedisPoolManager(redisConfiguration.getHost(), redisConfiguration.getPassword(),
                redisConfiguration.getPort());
        log.info("******load JedisPoolManager={} **********", poolManager);
        return poolManager;
    }

    @Bean(value = "jedisLockService")
    @ConditionalOnMissingBean(JedisLockService.class)
    public JedisLockService jedisLockService(JedisPoolManager jedisPoolManager) {
        JedisLockService lockService = new JedisLockService(jedisPoolManager);
        log.info("******load lockService **********");
        return lockService;
    }


    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();

//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
//        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
//        //字段为NULL的时候不会列入
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        //由于bean中缺少json的某个字段属性引起
//        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
//        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);
//        template.setValueSerializer(jsonRedisSerializer);
//        template.setHashValueSerializer(jsonRedisSerializer);
//        template.setKeySerializer(template.getStringSerializer());
//        template.setHashKeySerializer(template.getStringSerializer());
        log.info("******load redisTemplate **********");
        return template;

    }

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    public LockManager lockManager(JedisLockService jedisLockService) {
        LockManager lockManager = new LockManager(jedisLockService);
        log.info("******load lockManager **********");
        return lockManager;
    }

    @Bean
    @ConditionalOnMissingBean(JedisCache.class)
    public JedisCache JedisCache(JedisPoolManager jedisPoolManager) {
        JedisCache JedisCache = new JedisCache(jedisPoolManager);
        log.info("******load JedisCache **********");
        return JedisCache;
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
