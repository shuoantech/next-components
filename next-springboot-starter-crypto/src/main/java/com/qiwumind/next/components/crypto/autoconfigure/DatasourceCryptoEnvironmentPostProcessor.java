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

package com.qiwumind.next.components.crypto.autoconfigure;

import com.qiwumind.next.components.crypto.core.datasource.DatasourcePasswordDecryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源密码解密后置处理器。
 * <p>
 * 在 Spring 环境准备完成后、{@code DataSourceAutoConfiguration} 绑定属性之前执行：
 * 扫描环境中所有密文形式的数据源密码，解密后以高优先级 {@link MapPropertySource} 写回环境，
 * 从而 {@code spring.datasource.password}（以及多数据源场景下的 {@code *.password}）最终被解析为明文，
 * 连接池即可正常使用——整个过程对业务代码与数据源配置完全无侵入。
 * </p>
 */
public class DatasourceCryptoEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DatasourceCryptoEnvironmentPostProcessor.class);

    /**
     * 写回环境的高优先级 PropertySource 名称（同时用于去重/替换）
     */
    private static final String PROPERTY_SOURCE_NAME = "cryptoDecryptedDatasourceProperties";
    /**
     * 开关：默认开启
     */
    private static final String ENABLED_KEY = "next.crypto.datasource.enabled";
    /**
     * 额外需要解密的密码属性 key，逗号分隔（默认已覆盖 spring.datasource.password 及所有 *.password）
     */
    private static final String EXTRA_KEYS_KEY = "next.crypto.datasource.password-keys";
    /**
     * 默认主数据源密码 key
     */
    private static final String DEFAULT_KEY = "spring.datasource.password";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        String enabled = env.getProperty(ENABLED_KEY);
        if (enabled != null && !Boolean.parseBoolean(enabled)) {
            return; // 显式关闭，则不做任何处理
        }
        Map<String, Object> decrypted = new HashMap<>();
        // 1) 默认主数据源密码
        decryptAndCollect(env, DEFAULT_KEY, decrypted);
        // 2) 用户显式指定的额外密码 key
        String extraKeys = env.getProperty(EXTRA_KEYS_KEY);
        if (StringUtils.hasText(extraKeys)) {
            for (String key : extraKeys.split(",")) {
                decryptAndCollect(env, key.trim(), decrypted);
            }
        }

        // 3) 扫描所有以 ".password" 结尾的属性（兼容 dynamic-datasource 等多数据源）
        scanAndDecryptPasswords(env, decrypted);

        if (!decrypted.isEmpty()) {
            // 仅打印被解密的配置项 key，避免泄露明文或密文
            log.info("数据源密码密文已自动解密，共 {} 项：{}", decrypted.size(), decrypted);
            MapPropertySource source = new MapPropertySource(PROPERTY_SOURCE_NAME, decrypted);
            if (env.getPropertySources().contains(PROPERTY_SOURCE_NAME)) {
                env.getPropertySources().replace(PROPERTY_SOURCE_NAME, source);
            } else {
                // addFirst：保证解密后的明文优先级最高，覆盖原始密文配置
                env.getPropertySources().addFirst(source);
            }
        }
    }

    private void decryptAndCollect(ConfigurableEnvironment env, String key, Map<String, Object> target) {
        if (!StringUtils.hasText(key) || target.containsKey(key)) {
            return;
        }
        String value = env.getProperty(key);
        if (DatasourcePasswordDecryptor.isCipher(value)) {
            target.put(key, DatasourcePasswordDecryptor.decrypt(value));
        }
    }

    @SuppressWarnings("rawtypes")
    private void scanAndDecryptPasswords(ConfigurableEnvironment env, Map<String, Object> target) {
        for (PropertySource<?> ps : env.getPropertySources()) {
            if (!(ps instanceof EnumerablePropertySource) || PROPERTY_SOURCE_NAME.equals(ps.getName())) {
                continue;
            }
            EnumerablePropertySource eps = (EnumerablePropertySource) ps;
            for (String name : eps.getPropertyNames()) {
                if (name.endsWith(".password") && !target.containsKey(name)) {
                    Object val = eps.getProperty(name);
                    if (val instanceof String && DatasourcePasswordDecryptor.isCipher((String) val)) {
                        target.put(name, DatasourcePasswordDecryptor.decrypt((String) val));
                    }
                }
            }
        }
    }

    @Override
    public int getOrder() {
        // 在所有默认 EnvironmentPostProcessor（含 ConfigData 加载）之后执行，
        // 但仍远早于 DataSource 属性绑定，确保能读到最终解析后的 spring.datasource.password。
        return Ordered.LOWEST_PRECEDENCE;
    }
}
