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

package com.qiwumind.next.components.crypto.core.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiwumind.next.components.crypto.autoconfigure.LicenseProperties;
import com.qiwumind.next.components.crypto.core.license.model.LicenseValidateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * License 拦截器
 */
public class LicenseInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LicenseInterceptor.class);
    
    private final LicenseValidator licenseValidator;
    private final LicenseProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public LicenseInterceptor(LicenseValidator licenseValidator, LicenseProperties properties) {
        this.licenseValidator = licenseValidator;
        this.properties = properties;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        
        // 如果未启用 License 验证，直接放行
        if (!properties.isEnabled()) {
            return true;
        }
        
        String uri = request.getRequestURI();
        
        // 检查排除路径
        for (String excludePath : properties.getExcludePaths()) {
            if (pathMatcher.match(excludePath, uri)) {
                return true;
            }
        }
        
        // 验证 License
        LicenseValidateResult result = licenseValidator.getCachedResult();
        if (result == null) {
            logger.warn("License 尚未验证，请确保启动时已验证");
            // 这里可以尝试重新验证，但为了避免性能问题，建议在启动时验证
            result = LicenseValidateResult.fail("LICENSE_NOT_VALIDATED", "License 尚未验证");
        }
        
        if (!result.isValid()) {
            logger.warn("License 验证失败: {} - {}", result.getErrorCode(), result.getErrorMessage());
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", result.getErrorCode());
            errorResponse.put("message", properties.getErrorMessage());
            errorResponse.put("success", false);
            
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }
        
        return true;
    }
}