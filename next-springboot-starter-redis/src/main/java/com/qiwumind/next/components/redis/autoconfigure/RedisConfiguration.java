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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Redis 连接配置项。
 * <p>
 * 前缀与 Spring Boot 3 官方 {@code spring.data.redis} 完全对齐（收口为一份配置）：
 * 本组件与官方自动配置（RedisConnectionFactory 等）读同一份连接信息；
 * 当前实现仅支持单机/代理模式（阿里云标准版、集群版代理地址均为此模式），
 * sentinel/cluster 配置项会被本组件忽略。
 *
 * @author 云redis 2021年3月23日 下午12:38:57
 */
@Setter
@Getter
@ToString(exclude = {"password", "url", "username"})
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfiguration {

    /** 连接方式1: 使用 URI（如 redis://:password@host:6379/0），与官方 spring.data.redis.url 同义 */
    private String url;

    /** 连接方式2: 分离配置 */
    private String host = "localhost";
    private int port = 6379;

    /** Redis 6 ACL 用户名（阿里云"自定义账号"也填这里），默认账号留空 */
    private String username;

    /** 密码；阿里云自定义账号也可用 "账号:密码" 格式（由 Redis 服务端兼容） */
    private String password;

    private Integer database = 0;

    /** SSL/TLS 配置（与官方 spring.data.redis.ssl 对象结构对齐，yaml 写法为 ssl.enabled: true，不要写 ssl: true/false） */
    private Ssl ssl = new Ssl();

    /** 连接/命令超时，Lettuce 与 Redisson 共用（与官方 spring.data.redis.timeout 同为 Duration） */
    private Duration timeout = Duration.ofSeconds(5);

    private String clientName;

    /** Lettuce 连接池配置（与官方 spring.data.redis.lettuce.pool 结构一致） */
    private Lettuce lettuce = new Lettuce();

    /** 是否启用 SSL/TLS */
    public boolean isSslEnabled() {
        return ssl != null && Boolean.TRUE.equals(ssl.getEnabled());
    }

    @Setter
    @Getter
    public static class Ssl {
        /** 是否启用 SSL/TLS */
        private Boolean enabled = false;
    }

    @Setter
    @Getter
    public static class Lettuce {

        private Pool pool = new Pool();

        private Duration shutdownTimeout = Duration.ofMillis(100);

        @Setter
        @Getter
        public static class Pool {
            /** 最大连接数 */
            private int maxActive = 8;
            /** 最大空闲连接数 */
            private int maxIdle = 8;
            /** 最小空闲连接数 */
            private int minIdle = 0;
            /** 获取连接最大等待时间，-1 表示无限等待 */
            private Duration maxWait = Duration.ofMillis(-1);
            /** 空闲连接回收检查周期，null 表示使用默认值 */
            private Duration timeBetweenEvictionRuns;
        }
    }
}
