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

import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.crypto.core.db.CryptoFieldService;
import com.qiwumind.next.components.crypto.core.db.CryptoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库字段加解密自动配置。
 * <ul>
 *     <li>仅在 classpath 存在 MyBatis（{@code org.apache.ibatis.plugin.Interceptor}）时装配；</li>
 *     <li>可通过 {@code next.crypto.db.enabled} 开关控制，默认开启；</li>
 *     <li>MyBatis-Plus 会自动收集本拦截器 bean 并注册到 SqlSessionFactory，无需额外配置。</li>
 * </ul>
 */
@Configuration
@ConditionalOnClass(name = "org.apache.ibatis.plugin.Interceptor")
@EnableConfigurationProperties(CryptoDbProperties.class)
@ConditionalOnProperty(prefix = SystemConstants.Prefix.Crypto.CRYTO, name = "enabled", havingValue = "true", matchIfMissing = true)
public class CryptoDbAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CryptoFieldService cryptoFieldService() {
        return new CryptoFieldService();
    }

    @Bean
    @ConditionalOnMissingBean
    public CryptoInterceptor cryptoInterceptor(CryptoFieldService cryptoFieldService) {
        return new CryptoInterceptor(cryptoFieldService);
    }
}
