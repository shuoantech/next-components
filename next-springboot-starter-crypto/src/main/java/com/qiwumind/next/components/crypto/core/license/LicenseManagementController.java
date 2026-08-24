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

import com.qiwumind.next.components.crypto.core.license.model.LicenseInfo;
import com.qiwumind.next.components.crypto.core.license.model.LicenseValidateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * License 管理接口（可选）
 */
@RestController
@RequestMapping("/license")
@ConditionalOnBean(LicenseServiceHelper.class)
@ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "management-enabled", havingValue = "true", matchIfMissing = false)
public class LicenseManagementController {
    
    @Autowired(required = false)
    private LicenseServiceHelper licenseServiceHelper;
    
    /**
     * 获取 License 信息
     */
    @GetMapping("/info")
    public Map<String, Object> getLicenseInfo() {
        Map<String, Object> result = new HashMap<>();
        
        if (licenseServiceHelper == null) {
            result.put("success", false);
            result.put("message", "License 服务未启用");
            return result;
        }
        
        LicenseInfo info = licenseServiceHelper.getLicenseInfo();
        if (info != null) {
            result.put("success", true);
            result.put("data", info);
        } else {
            result.put("success", false);
            result.put("message", "未找到 License 信息");
        }
        
        return result;
    }
    
    /**
     * 验证 License
     */
    @GetMapping("/validate")
    public Map<String, Object> validate() {
        Map<String, Object> result = new HashMap<>();
        
        if (licenseServiceHelper == null) {
            result.put("valid", false);
            result.put("message", "License 服务未启用");
            return result;
        }
        
        LicenseValidateResult validateResult = licenseServiceHelper.getValidateResult();
        result.put("valid", validateResult != null && validateResult.isValid());
        
        if (validateResult != null) {
            result.put("message", validateResult.getErrorMessage());
            if (validateResult.getLicenseInfo() != null) {
                result.put("licenseId", validateResult.getLicenseInfo().getLicenseId());
                result.put("expireDate", validateResult.getLicenseInfo().getEndTime());
                result.put("productName", validateResult.getLicenseInfo().getProductName());
                result.put("productVersion", validateResult.getLicenseInfo().getProductVersion());
            }
        }
        
        return result;
    }
    
    /**
     * 刷新 License（重新加载和验证）
     */
    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        Map<String, Object> result = new HashMap<>();
        if (licenseServiceHelper == null) {
            result.put("success", false);
            result.put("message", "License 服务未启用");
            return result;
        }
        try {
            licenseServiceHelper.refresh();
            result.put("success", true);
            result.put("message", "License 已刷新");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "刷新失败: " + e.getMessage());
        }
        
        return result;
    }
}