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

import com.qiwumind.next.components.crypto.core.license.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * License 自动配置类
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@EnableConfigurationProperties(LicenseProperties.class)
public class LicenseAutoConfiguration {
    private LicenseProperties licenseProperties;

    public LicenseAutoConfiguration(LicenseProperties licenseProperties) {
        this.licenseProperties = licenseProperties;

    }

    /**
     * 配置 License 加载器
     */
    @Bean
    @ConditionalOnMissingBean(LicenseLoader.class)
    @ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "enabled", havingValue = "true", matchIfMissing = true)
    public LicenseLoader licenseLoader(ResourceLoader resourceLoader) {
        return new DefaultLicenseLoader(resourceLoader, licenseProperties);
    }

    /**
     * 配置 License 验证器
     */
    @Bean
    @ConditionalOnMissingBean(LicenseValidator.class)
    @ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "enabled", havingValue = "true", matchIfMissing = true)
    public LicenseValidator licenseValidator(ResourceLoader resourceLoader) {
        return new DefaultLicenseValidator(resourceLoader, licenseProperties);
    }

    /**
     * 配置启动验证器
     */
    @Bean
    @ConditionalOnMissingBean(LicenseStartupValidator.class)
    @ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "validate-on-startup", havingValue = "true", matchIfMissing = true)
    public LicenseStartupValidator licenseStartupValidator(LicenseLoader licenseLoader,
                                                           LicenseValidator licenseValidator) {
        return new LicenseStartupValidator(licenseLoader, licenseValidator, licenseProperties);
    }

    /**
     * 配置 Web 拦截器
     */
    @Configuration
    @ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public static class LicenseWebConfig implements WebMvcConfigurer {

        private final LicenseValidator licenseValidator;
        private final LicenseProperties properties;

        public LicenseWebConfig(LicenseValidator licenseValidator, LicenseProperties properties) {
            this.licenseValidator = licenseValidator;
            this.properties = properties;
        }

        @Bean
        public LicenseInterceptor licenseInterceptor() {
            return new LicenseInterceptor(licenseValidator, properties);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(licenseInterceptor())
                    .addPathPatterns("/**");
        }
    }

    /**
     * 导出 License 相关的 Bean，供外部使用
     */
    @Configuration
    public static class LicenseBeanExporter {
        @Bean
        public LicenseServiceHelper licenseServiceHelper(LicenseValidator licenseValidator) {
            return new LicenseServiceHelper(licenseValidator);
        }
    }
}