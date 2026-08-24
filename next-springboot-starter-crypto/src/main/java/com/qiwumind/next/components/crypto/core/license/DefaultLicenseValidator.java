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
import com.qiwumind.next.components.crypto.core.license.model.LicenseInfo;
import com.qiwumind.next.components.crypto.core.license.model.LicenseValidateResult;
import com.qiwumind.next.components.common.util.crypto.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.security.PublicKey;
import java.util.Date;

/**
 * 默认 License 验证器实现
 */
public class DefaultLicenseValidator implements LicenseValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultLicenseValidator.class);
    
    private final ResourceLoader resourceLoader;
    private final LicenseProperties properties;
    private final ObjectMapper objectMapper;
    private LicenseValidateResult cachedResult;
    
    public DefaultLicenseValidator(ResourceLoader resourceLoader, LicenseProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public LicenseValidateResult validate(LicenseInfo licenseInfo) {
        if (cachedResult != null) {
            return cachedResult;
        }
        try {
            // 1. 验证签名
            if (properties.isSignatureValidation() && !verifySignature(licenseInfo)) {
                cachedResult = LicenseValidateResult.fail("LICENSE_INVALID_SIGNATURE", "License 签名无效");
                return cachedResult;
            }
            // 2. 验证有效期
            if (properties.isExpiryValidation()) {
                Date now = new Date();
                if (now.before(licenseInfo.getStartTime())) {
                    cachedResult = LicenseValidateResult.fail("LICENSE_NOT_STARTED", "License 尚未生效");
                    return cachedResult;
                }
                if (now.after(licenseInfo.getEndTime())) {
                    cachedResult = LicenseValidateResult.fail("LICENSE_EXPIRED", "License 已过期");
                    return cachedResult;
                }
            }
            
            // 3. 验证硬件绑定
            if (properties.isHardwareValidation() && licenseInfo.getHardwareFingerprint() != null 
                    && !licenseInfo.getHardwareFingerprint().isEmpty()) {
                if (!HardwareUtils.validateHardware(licenseInfo.getHardwareFingerprint())) {
                    cachedResult = LicenseValidateResult.fail("LICENSE_HARDWARE_MISMATCH", 
                        "当前硬件与 License 绑定的硬件不匹配");
                    return cachedResult;
                }
            }
            cachedResult = LicenseValidateResult.success(licenseInfo);
            logger.info("License 验证通过: {}", licenseInfo.getLicenseId());
        } catch (Exception e) {
            logger.error("License 验证异常", e);
            cachedResult = LicenseValidateResult.fail("LICENSE_VALIDATE_ERROR", 
                "License 验证异常: " + e.getMessage());
        }
        return cachedResult;
    }
    
    /**
     * 验证签名
     */
    private boolean verifySignature(LicenseInfo licenseInfo) {
        try {
            // 加载公钥
            Resource resource = resourceLoader.getResource(properties.getPublicKeyPath());
            byte[] publicKeyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            PublicKey publicKey = RSAUtils.loadPublicKey(publicKeyBytes);
            // 获取签名
            String signatureStr = licenseInfo.getSignature();
            if (signatureStr == null || signatureStr.isEmpty()) {
                return false;
            }
            
            // 创建不含签名的 License 数据
            LicenseInfo infoWithoutSign = new LicenseInfo();
            infoWithoutSign.setLicenseId(licenseInfo.getLicenseId());
            infoWithoutSign.setProductName(licenseInfo.getProductName());
            infoWithoutSign.setProductVersion(licenseInfo.getProductVersion());
            infoWithoutSign.setLicenseType(licenseInfo.getLicenseType());
            infoWithoutSign.setStartTime(licenseInfo.getStartTime());
            infoWithoutSign.setEndTime(licenseInfo.getEndTime());
            infoWithoutSign.setHardwareFingerprint(licenseInfo.getHardwareFingerprint());
            infoWithoutSign.setMaxUsers(licenseInfo.getMaxUsers());
            infoWithoutSign.setExtra(licenseInfo.getExtra());
            
            // 序列化数据
            byte[] data = objectMapper.writeValueAsBytes(infoWithoutSign);
            
            // 验证签名
            byte[] signature = Base64.decodeBase64(signatureStr);
            return RSAUtils.verify(data, signature, publicKey);
            
        } catch (Exception e) {
            logger.error("验证签名失败", e);
            return false;
        }
    }
    
    @Override
    public LicenseValidateResult getCachedResult() {
        return cachedResult;
    }
    
    @Override
    public void clearCache() {
        this.cachedResult = null;
    }
}