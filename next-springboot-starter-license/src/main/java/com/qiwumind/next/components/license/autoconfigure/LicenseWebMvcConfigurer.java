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

package com.qiwumind.next.components.license.autoconfigure;

import com.qiwumind.next.components.license.core.LicenseManager;
import com.qiwumind.next.components.license.core.interceptor.LicenseInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * License Web MVC配置
 * 注册全局拦截器
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
@ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "global-block-enabled", havingValue = "true")
public class LicenseWebMvcConfigurer implements WebMvcConfigurer {

    private final LicenseManager licenseManager;
    private final ObjectMapper objectMapper;
    private final LicenseProperties properties;

    public LicenseWebMvcConfigurer(LicenseManager licenseManager, 
                                   ObjectMapper objectMapper,
                                   LicenseProperties properties) {
        this.licenseManager = licenseManager;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LicenseInterceptor interceptor = new LicenseInterceptor(
                licenseManager, 
                objectMapper,
                properties.getValidation().isGlobalBlockEnabled()
        );

        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(getExcludePaths());
        
        log.info("License全局拦截器已注册，排除路径: {}", getExcludePaths());
    }

    private List<String> getExcludePaths() {
        List<String> excludes = properties.getValidation().getExcludePaths();
        if (excludes == null || excludes.isEmpty()) {
            // 默认排除健康检查和静态资源
            return List.of(
                    "/health/**",
                    "/actuator/**",
                    "/error",
                    "/favicon.ico",
                    "/static/**",
                    "/public/**",
                    "/resources/**"
            );
        }
        return excludes;
    }
}