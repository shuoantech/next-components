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

import com.qiwumind.next.components.crypto.autoconfigure.LicenseProperties;
import com.qiwumind.next.components.crypto.core.license.model.LicenseInfo;
import com.qiwumind.next.components.crypto.core.license.model.LicenseValidateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 启动时 License 验证器
 */
public class LicenseStartupValidator implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(LicenseStartupValidator.class);
    
    private final LicenseLoader licenseLoader;
    private final LicenseValidator licenseValidator;
    private final LicenseProperties properties;
    
    public LicenseStartupValidator(LicenseLoader licenseLoader, 
                                   LicenseValidator licenseValidator,
                                   LicenseProperties properties) {
        this.licenseLoader = licenseLoader;
        this.licenseValidator = licenseValidator;
        this.properties = properties;
    }
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!properties.isEnabled()) {
            logger.info("License 验证未启用");
            return;
        }
        logger.info("开始验证 License...");
        try {
            // 加载 License
            LicenseInfo licenseInfo = licenseLoader.loadLicense();
            // 验证 License
            LicenseValidateResult result = licenseValidator.validate(licenseInfo);
            if (!result.isValid()) {
                logger.error("License 验证失败: {}", result.getErrorMessage());
                // 可以选择抛出异常阻止启动
                throw new RuntimeException("License 验证失败: " + result.getErrorMessage());
            } else {
                logger.info("License 验证成功");
                logger.info("License 信息: ID={}, 产品={}, 版本={}, 有效期={} - {}", 
                    licenseInfo.getLicenseId(),
                    licenseInfo.getProductName(),
                    licenseInfo.getProductVersion(),
                    licenseInfo.getStartTime(),
                    licenseInfo.getEndTime());
            }
            
        } catch (Exception e) {
            logger.error("License 验证异常", e);
            throw new RuntimeException("License 验证失败", e);
        }
    }
}