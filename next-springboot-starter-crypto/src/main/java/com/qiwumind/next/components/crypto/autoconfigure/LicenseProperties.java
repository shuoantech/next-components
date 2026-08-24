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

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;

/**
 * License 配置属性
 */
@ConfigurationProperties(prefix = SystemConstants.Prefix.LICENSE)
public class LicenseProperties {
    
    /**
     * 是否启用 License 验证
     */
    private boolean enabled = true;
    
    /**
     * 公钥文件路径（支持 classpath: 或 file:）
     */
    private String publicKeyPath = "classpath:license/public_key.der";
    
    /**
     * License 文件路径（支持 classpath: 或 file:）
     */
    private String licenseFilePath = "classpath:license/license.dat";
    
    /**
     * 是否验证硬件信息
     */
    private boolean hardwareValidation = true;
    
    /**
     * 是否验证有效期
     */
    private boolean expiryValidation = true;
    
    /**
     * 是否验证签名
     */
    private boolean signatureValidation = true;
    
    /**
     * 需要排除的路径（不进行 License 验证）
     */
    private String[] excludePaths = {"/actuator/**", "/license/**"};
    
    /**
     * License 验证失败时的响应消息
     */
    private String errorMessage = "License验证失败，请检查授权信息";
    
    /**
     * 自定义 License 加载器 Bean 名称
     */
    private String licenseLoaderBeanName = "";
    
    /**
     * 自定义验证器 Bean 名称
     */
    private String validatorBeanName = "";




    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getPublicKeyPath() {
        return publicKeyPath;
    }
    
    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }
    
    public String getLicenseFilePath() {
        return licenseFilePath;
    }
    
    public void setLicenseFilePath(String licenseFilePath) {
        this.licenseFilePath = licenseFilePath;
    }
    
    public boolean isHardwareValidation() {
        return hardwareValidation;
    }
    
    public void setHardwareValidation(boolean hardwareValidation) {
        this.hardwareValidation = hardwareValidation;
    }
    
    public boolean isExpiryValidation() {
        return expiryValidation;
    }
    
    public void setExpiryValidation(boolean expiryValidation) {
        this.expiryValidation = expiryValidation;
    }
    
    public boolean isSignatureValidation() {
        return signatureValidation;
    }
    
    public void setSignatureValidation(boolean signatureValidation) {
        this.signatureValidation = signatureValidation;
    }
    
    public String[] getExcludePaths() {
        return excludePaths;
    }
    
    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getLicenseLoaderBeanName() {
        return licenseLoaderBeanName;
    }
    
    public void setLicenseLoaderBeanName(String licenseLoaderBeanName) {
        this.licenseLoaderBeanName = licenseLoaderBeanName;
    }
    
    public String getValidatorBeanName() {
        return validatorBeanName;
    }
    
    public void setValidatorBeanName(String validatorBeanName) {
        this.validatorBeanName = validatorBeanName;
    }
}