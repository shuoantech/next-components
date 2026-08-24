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

/**
 * License 服务辅助类，供外部调用
 */
public class LicenseServiceHelper {
    
    private final LicenseValidator licenseValidator;
    
    public LicenseServiceHelper(LicenseValidator licenseValidator) {
        this.licenseValidator = licenseValidator;
    }
    
    /**
     * 获取 License 验证结果
     */
    public LicenseValidateResult getValidateResult() {
        return licenseValidator.getCachedResult();
    }
    
    /**
     * 检查 License 是否有效
     */
    public boolean isValid() {
        LicenseValidateResult result = licenseValidator.getCachedResult();
        return result != null && result.isValid();
    }
    
    /**
     * 获取 License 信息
     */
    public LicenseInfo getLicenseInfo() {
        LicenseValidateResult result = licenseValidator.getCachedResult();
        return result != null ? result.getLicenseInfo() : null;
    }
    
    /**
     * 清除缓存，强制重新验证
     */
    public void refresh() {
        licenseValidator.clearCache();
    }
}