package com.qiwumind.next.components.redis.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 自动配置类（连接信息收口自 spring.data.redis）
 *
 * @author chenyao
 * @since 2026年9月
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
@EnableConfigurationProperties(RedisConfiguration.class)
public class RedissonAutoConfiguration {

    private final RedisConfiguration redisConfiguration;

    public RedissonAutoConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    /**
     * 创建 RedissonClient Bean
     */
    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 构建 Redis 地址
        String address = buildRedisAddress();
        // 配置单机模式
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisConfiguration.getDatabase())
                .setConnectionPoolSize(20)
                .setConnectionMinimumIdleSize(5)
                .setIdleConnectionTimeout(30000)
                .setConnectTimeout((int) redisConfiguration.getTimeout().toMillis())
                .setTimeout((int) redisConfiguration.getTimeout().toMillis())
                .setRetryAttempts(3)
                .setRetryInterval(1000)
                .setKeepAlive(true)
                .setTcpNoDelay(true);

        // Redis 6 ACL 用户名（阿里云自定义账号）
        if (hasText(redisConfiguration.getUsername())) {
            serverConfig.setUsername(redisConfiguration.getUsername());
        }

        // 如果有密码，设置密码
        if (hasText(redisConfiguration.getPassword())) {
            serverConfig.setPassword(redisConfiguration.getPassword());
        }

        // 如果启用 SSL
        if (redisConfiguration.isSslEnabled()) {
            serverConfig.setSslProvider(org.redisson.config.SslProvider.JDK);
            serverConfig.setSslEnableEndpointIdentification(true);
        }

        // 如果配置了 clientName
        if (hasText(redisConfiguration.getClientName())) {
            serverConfig.setClientName(redisConfiguration.getClientName());
        }

        log.info("****** RedissonClient initialized, address: {} ******", address);
        return Redisson.create(config);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    /**
     * 构建 Redis 地址
     */
    private String buildRedisAddress() {
        // 优先使用 URL 配置
        if (hasText(redisConfiguration.getUrl())) {
            return redisConfiguration.getUrl();
        }

        String protocol = redisConfiguration.isSslEnabled() ? "rediss://" : "redis://";
        String host = redisConfiguration.getHost();
        int port = redisConfiguration.getPort();
        return protocol + host + ":" + port;
    }
}
