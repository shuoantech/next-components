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

package com.qiwumind.next.components.license.core.interceptor;

import com.qiwumind.next.components.license.core.LicenseManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * License全局拦截器
 * 当License失效时，拦截所有接口请求
 */
@Slf4j
public class LicenseInterceptor implements HandlerInterceptor {

    private final LicenseManager licenseManager;
    private final ObjectMapper objectMapper;
    private final boolean enableGlobalBlock;

    public LicenseInterceptor(LicenseManager licenseManager, 
                             ObjectMapper objectMapper,
                             boolean enableGlobalBlock) {
        this.licenseManager = licenseManager;
        this.objectMapper = objectMapper;
        this.enableGlobalBlock = enableGlobalBlock;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果未启用全局拦截，直接放行
        if (!enableGlobalBlock) {
            return true;
        }

        // 检查License是否有效
        if (!licenseManager.isLicenseValid()) {
            log.warn("License失效，拦截请求: {} {}", request.getMethod(), request.getRequestURI());
            
            // 返回错误响应
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "LICENSE_INVALID");
            errorResponse.put("message", "License已失效，请联系管理员获取有效License");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("path", request.getRequestURI());
            
            var lastResult = licenseManager.getLastVerifyResult();
            if (lastResult != null) {
                errorResponse.put("detail", lastResult.message());
            }
            
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        return true;
    }
}