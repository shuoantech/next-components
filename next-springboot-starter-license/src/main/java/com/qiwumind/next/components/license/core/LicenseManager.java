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

package com.qiwumind.next.components.license.core;

import com.qiwumind.next.components.license.autoconfigure.LicenseProperties;
import com.qiwumind.next.components.license.core.generator.LicenseGenerator;
import com.qiwumind.next.components.license.core.validator.LicenseValidator;
import com.qiwumind.next.components.license.core.util.JsonUtils;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import com.qiwumind.next.components.license.core.vo.LicenseVerifyResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class LicenseManager {

    private final LicenseProperties properties;
    private final LicenseValidator validator;
    private final LicenseGenerator generator;
    private final AtomicReference<LicenseInfo> currentLicense = new AtomicReference<>();
    private final AtomicReference<LicenseVerifyResult> lastVerifyResult = new AtomicReference<>();

    @Getter
    private volatile boolean initialized = false;

    public LicenseManager(LicenseProperties properties, LicenseValidator validator, LicenseGenerator generator) {
        this.properties = properties;
        this.validator = validator;
        this.generator = generator;
    }

    public void init() {
        if (!properties.isEnabled()) {
            log.info("License模块已禁用");
            initialized = true;
            return;
        }

        try {
            loadLicenseFromFile();
            
            if (properties.getValidation().isValidateOnStartup()) {
                validateCurrentLicense();
            }
            
            initialized = true;
            log.info("LicenseManager初始化完成");
        } catch (Exception e) {
            log.error("LicenseManager初始化失败", e);
            initialized = false;
        }
    }

    public LicenseVerifyResult loadAndValidateLicense(String licenseContent) {
        try {
            LicenseInfo license = JsonUtils.fromJson(licenseContent, LicenseInfo.class);
            return validateAndSetLicense(license);
        } catch (Exception e) {
            log.error("加载License失败", e);
            return LicenseVerifyResult.failure("加载License失败: " + e.getMessage());
        }
    }

    public LicenseVerifyResult loadLicenseFromFile() {
        try {
            Path licensePath = Paths.get(properties.getFile().getPath());
            
            if (!Files.exists(licensePath)) {
                log.warn("License文件不存在: {}", licensePath);
                if (properties.getFile().isAutoCreate()) {
                    createTrialLicense();
                }
                return LicenseVerifyResult.failure("License文件不存在");
            }

            String content = Files.readString(licensePath);
            return loadAndValidateLicense(content);
            
        } catch (IOException e) {
            log.error("读取License文件失败", e);
            return LicenseVerifyResult.failure("读取License文件失败: " + e.getMessage());
        }
    }

    public LicenseVerifyResult validateAndSetLicense(LicenseInfo license) {
        LicenseVerifyResult result = validator.validate(license);
        
        if (result.success()) {
            currentLicense.set(license);
            lastVerifyResult.set(result);
            log.info("License验证成功: {}", license.getLicenseId());
        } else {
            lastVerifyResult.set(result);
            log.warn("License验证失败: {}", result.message());
        }
        
        return result;
    }

    public LicenseVerifyResult validateCurrentLicense() {
        LicenseInfo license = currentLicense.get();
        if (license == null) {
            return LicenseVerifyResult.failure("当前无License");
        }
        
        LicenseVerifyResult result = validator.validate(license);
        lastVerifyResult.set(result);
        return result;
    }

    public void saveLicenseToFile(LicenseInfo license) throws IOException {
        String content = generator.serializeToFile(license);
        Path licensePath = Paths.get(properties.getFile().getPath());
        
        Files.createDirectories(licensePath.getParent());
        Files.writeString(licensePath, content);
        
        Path backupPath = Paths.get(properties.getFile().getBackupPath());
        Files.writeString(backupPath, content);
        
        log.info("License已保存: {}", licensePath);
    }

    public LicenseInfo createTrialLicense() {
        LicenseInfo trial = generator.generateTrialLicense("Trial License", 30);
        
        try {
            saveLicenseToFile(trial);
            currentLicense.set(trial);
            log.info("创建试用License成功");
        } catch (IOException e) {
            log.error("保存试用License失败", e);
        }
        
        return trial;
    }

    public LicenseInfo getCurrentLicense() {
        return currentLicense.get();
    }

    public LicenseVerifyResult getLastVerifyResult() {
        return lastVerifyResult.get();
    }

    public boolean hasFeature(String feature) {
        LicenseInfo license = currentLicense.get();
        return validator.hasFeature(license, feature);
    }

    public boolean isLicenseValid() {
        LicenseVerifyResult result = getLastVerifyResult();
        return result != null && result.success();
    }

    public long getRemainingDays() {
        LicenseInfo license = currentLicense.get();
        if (license == null || license.getExpireDate() == null) {
            return -1;
        }
        
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(license.getExpireDate())) {
            return 0;
        }
        
        return java.time.Duration.between(now, license.getExpireDate()).toDays();
    }

    public void invalidate() {
        currentLicense.set(null);
        lastVerifyResult.set(LicenseVerifyResult.failure("License已失效"));
        log.info("License已失效");
    }
}