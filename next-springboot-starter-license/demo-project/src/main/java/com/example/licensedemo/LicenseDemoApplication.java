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

package com.example.licensedemo;

import com.qiwumind.next.components.license.core.LicenseManager;
import com.qiwumind.next.components.license.core.vo.LicenseVerifyResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * License Starter Demo 主应用类
 * 
 * 展示如何在外部Spring Boot项目中使用 next-springboot-starter-license 二方库
 */
@Slf4j
@SpringBootApplication
public class LicenseDemoApplication {

    private final LicenseManager licenseManager;

    public LicenseDemoApplication(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(LicenseDemoApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("=== License Demo Application Started ===");
        
        // 获取License验证状态
        LicenseVerifyResult result = licenseManager.getLastVerifyResult();
        if (result != null) {
            log.info("License验证状态: {}", result.success() ? "成功" : "失败");
            log.info("验证消息: {}", result.message());
            log.info("验证时间: {}", result.verifyTime());
        }
        
        // 获取当前License信息
        if (licenseManager.getCurrentLicense() != null) {
            var license = licenseManager.getCurrentLicense();
            log.info("当前License ID: {}", license.getLicenseId());
            log.info("授权主题: {}", license.getSubject());
            log.info("签发者: {}", license.getIssuer());
            log.info("签发时间: {}", license.getIssueDate());
            log.info("过期时间: {}", license.getExpireDate());
            log.info("剩余天数: {}", licenseManager.getRemainingDays());
        }
        
        log.info("=== License Demo Application Ready ===");
    }
}