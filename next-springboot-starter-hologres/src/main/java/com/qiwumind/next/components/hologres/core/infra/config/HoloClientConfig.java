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

package com.qiwumind.next.components.hologres.core.infra.config;

import java.util.stream.Stream;

import com.qiwumind.next.components.hologres.autoconfigure.DataSourceConfiguration;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Hologres 客户端配置管理器。
 * <p>
 * 根据部署环境自动选择对应的数据库名称和连接池参数。
 *
 * @author KS.Li
 */
@Data
public class HoloClientConfig {

    private String deployEnv;
    private DataSourceConfig config;

    public HoloClientConfig(String deployenv) {
        this.deployEnv = deployenv;
    }

    /**
     * 使用 DataSourceConfiguration 初始化配置。
     */
    public void init(DataSourceConfiguration dataSourceConfiguration) {
        Env env = Env.getEnv(this.deployEnv);
        String url = "jdbc:postgresql://%s:%d/%s".formatted(dataSourceConfiguration.getHoloConfig().getUrl(),
                dataSourceConfiguration.getHoloConfig().getPort(),
                dataSourceConfiguration.getDatabase());

        DataSourceConfig.DataSourceConfigBuilder builder = DataSourceConfig.builder()
                .url(url)
                .username(dataSourceConfiguration.getUsername())
                .password(dataSourceConfiguration.getPwd())
                .pool(DataSourceConfig.DataSourceType.DruidCP);

        // 测试及预发环境，限制最大连接数为 5
        if (env == null || env == Env.TEST || env == Env.PRE) {
            Integer maxPoolSize = dataSourceConfiguration.getMaxPoolSize();
            builder.maxPoolSize((maxPoolSize != null && maxPoolSize > 5) ? 5 : maxPoolSize);
        } else if (dataSourceConfiguration.getMaxPoolSize() != null) {
            builder.maxPoolSize(dataSourceConfiguration.getMaxPoolSize());
        }

        if (dataSourceConfiguration.getIdleTimeoutMilliseconds() != null) {
            builder.idleTimeoutMilliseconds(dataSourceConfiguration.getIdleTimeoutMilliseconds());
        }

        config = builder.build();
    }

    /**
     * 部署环境枚举。
     */

    public enum Env {
        TEST("dev", "test", "sit", "test1", "test2", "test3", "test4", "test5"),
        PRE("uat", "pre"),
        PRD("prd", "prod");

        private final String[] env;

        Env(String... env) {
            this.env = env;
        }

        public String[] getEnv() {
            return env;
        }

        /**
         * 使用 Switch 模式匹配获取环境
         * 优先精确匹配，再包含匹配
         */
        public static Env getEnv(String env) {
            if (env == null || env.isBlank()) {
                return null;
            }
            // 1. 精确匹配
            Env exactMatch = getEnvByExactMatch(env);
            if (exactMatch != null) {
                return exactMatch;
            }
            // 2. 包含匹配（兜底）
            return getEnvByContains(env);
        }

        private static Env getEnvByExactMatch(String env) {
            // 使用 Switch 表达式 + 模式匹配
            return switch (env) {
                case String e when isInGroup(e, TEST) -> TEST;
                case String e when isInGroup(e, PRE) -> PRE;
                case String e when isInGroup(e, PRD) -> PRD;
                default -> null;
            };
        }

        private static Env getEnvByContains(String env) {
            return switch (env) {
                case String e when containsInGroup(e, TEST) -> TEST;
                case String e when containsInGroup(e, PRE) -> PRE;
                case String e when containsInGroup(e, PRD) -> PRD;
                default -> null;
            };
        }

        private static boolean isInGroup(String env, Env group) {
            for (String e : group.getEnv()) {
                if (e.equals(env)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean containsInGroup(String env, Env group) {
            for (String e : group.getEnv()) {
                if (env.contains(e)) {
                    return true;
                }
            }
            return false;
        }
    }

}
