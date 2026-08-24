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

package com.example.licensedemo.controller;

import com.qiwumind.next.components.license.core.LicenseManager;
import com.qiwumind.next.components.license.core.annotations.LicensedFeature;
import com.qiwumind.next.components.license.core.generator.LicenseGenerator;
import com.qiwumind.next.components.license.core.signature.SignatureProvider;
import com.qiwumind.next.components.license.core.util.HostInfoUtils;
import com.qiwumind.next.components.license.core.vo.LicenseBinding;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import com.qiwumind.next.components.license.core.vo.LicenseLimits;
import com.qiwumind.next.components.license.core.vo.LicenseVerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * License管理控制器
 * 提供License状态查询、验证、生成等API
 * 同时演示全局拦截器和@LicensedFeature注解的使用
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class LicenseController {

    private final LicenseManager licenseManager;
    private final LicenseGenerator licenseGenerator;
    private final SignatureProvider signatureProvider;

    public LicenseController(LicenseManager licenseManager,
                            LicenseGenerator licenseGenerator,
                            SignatureProvider signatureProvider) {
        this.licenseManager = licenseManager;
        this.licenseGenerator = licenseGenerator;
        this.signatureProvider = signatureProvider;
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /**
     * 获取License验证状态
     */
    @GetMapping("/license/status")
    public LicenseVerifyResult getLicenseStatus() {
        return licenseManager.getLastVerifyResult();
    }

    /**
     * 获取当前License详细信息
     */
    @GetMapping("/license/info")
    public LicenseInfo getLicenseInfo() {
        return licenseManager.getCurrentLicense();
    }

    /**
     * 获取License剩余天数
     */
    @GetMapping("/license/remaining-days")
    public Long getRemainingDays() {
        return licenseManager.getRemainingDays();
    }

    /**
     * 验证License是否有效
     */
    @GetMapping("/license/valid")
    public boolean isLicenseValid() {
        return licenseManager.isLicenseValid();
    }

    /**
     * 手动验证License内容
     * @param licenseContent License JSON内容
     */
    @PostMapping("/license/validate")
    public LicenseVerifyResult validateLicense(@RequestBody String licenseContent) {
        return licenseManager.loadAndValidateLicense(licenseContent);
    }

    /**
     * 重新验证当前License
     */
    @PostMapping("/license/revalidate")
    public LicenseVerifyResult revalidateLicense() {
        return licenseManager.validateCurrentLicense();
    }

    /**
     * 获取系统信息（用于绑定）
     */
    @GetMapping("/system/info")
    public SystemInfo getSystemInfo() {
        return new SystemInfo(
                HostInfoUtils.getLocalIps(),
                HostInfoUtils.getLocalMacs(),
                HostInfoUtils.getHardwareFingerprint(),
                HostInfoUtils.getHostname()
        );
    }

    /**
     * 生成新的License
     * 需要私钥权限
     */
    @PostMapping("/license/generate")
    @LicensedFeature(value = "license-generate", message = "未授权生成License")
    public LicenseInfo generateLicense(@RequestBody LicenseRequest request) {
        log.info("生成新License: subject={}, days={}", request.subject(), request.days());

        LicenseInfo.LicenseInfoBuilder builder = LicenseInfo.builder()
                .subject(request.subject())
                .issuer(request.issuer() != null ? request.issuer() : "System")
                .expireDate(ZonedDateTime.now().plusDays(request.days()))
                .features(request.features());

        // 添加使用限制
        if (request.maxUsers() != null || request.maxConnections() != null) {
            LicenseLimits limits = LicenseLimits.builder()
                    .maxUsers(request.maxUsers())
                    .maxConnections(request.maxConnections())
                    .build();
            builder.limits(limits);
        }

        // 添加绑定信息
        if (request.bindToHardware() != null && request.bindToHardware()) {
            LicenseBinding binding = LicenseBinding.builder()
                    .hardwareFingerprint(HostInfoUtils.getHardwareFingerprint())
                    .allowedIps(HostInfoUtils.getLocalIps())
                    .allowedMacs(HostInfoUtils.getLocalMacs())
                    .build();
            builder.binding(binding);
        }

        return licenseGenerator.generate(builder);
    }

    /**
     * 生成密钥对
     */
    @PostMapping("/keys/generate")
    @LicensedFeature(value = "key-generate", message = "未授权生成密钥对")
    public String generateKeyPair(@RequestParam String privateKeyPath,
                                  @RequestParam String publicKeyPath) {
        try {
            signatureProvider.generateKeyPair(privateKeyPath, publicKeyPath);
            return "密钥对生成成功: " + privateKeyPath + " / " + publicKeyPath;
        } catch (Exception e) {
            log.error("生成密钥对失败", e);
            return "生成密钥对失败: " + e.getMessage();
        }
    }

    // ==================== 功能授权示例 ====================

    /**
     * 高级报表功能（需要授权）
     */
    @LicensedFeature(value = "advanced-report", message = "未授权使用高级报表功能")
    @GetMapping("/features/advanced-report")
    public String advancedReport() {
        return "{\"status\": \"success\", \"feature\": \"advanced-report\", \"message\": \"高级报表功能 - 已授权访问\"}";
    }

    /**
     * API访问功能（宽松模式）
     */
    @LicensedFeature(value = "api-access", strict = false, message = "API访问受限")
    @GetMapping("/features/api")
    public String apiAccess() {
        return "{\"status\": \"success\", \"feature\": \"api-access\", \"message\": \"API数据访问功能\"}";
    }

    /**
     * 基础功能（无需授权）
     */
    @GetMapping("/features/basic")
    public String basicFeature() {
        return "{\"status\": \"success\", \"feature\": \"basic\", \"message\": \"基础功能 - 无需授权\"}";
    }

    // ==================== 业务接口测试 ====================

    /**
     * 普通业务接口 - 用于测试全局拦截器
     * 当License失效时，此接口会被全局拦截器拦截
     */
    @GetMapping("/business/user")
    public Map<String, Object> getUser() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of("id", "1", "name", "张三"));
        result.put("message", "获取用户信息成功");
        return result;
    }

    /**
     * 普通业务接口 - 用于测试全局拦截器
     * 当License失效时，此接口会被全局拦截器拦截
     */
    @GetMapping("/business/product")
    public Map<String, Object> getProduct() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of("id", "1001", "name", "商品A", "price", 99.99));
        result.put("message", "获取商品信息成功");
        return result;
    }

    /**
     * 普通业务接口 - 用于测试全局拦截器
     * 当License失效时，此接口会被全局拦截器拦截
     */
    @GetMapping("/business/order")
    public Map<String, Object> getOrder() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of("id", "ORD-001", "amount", 199.98));
        result.put("message", "获取订单信息成功");
        return result;
    }

    // ==================== 内部类 ====================

    public record SystemInfo(
            List<String> ips,
            List<String> macs,
            String hardwareFingerprint,
            String hostname
    ) {}

    public record LicenseRequest(
            String subject,
            String issuer,
            Integer days,
            Set<String> features,
            Integer maxUsers,
            Integer maxConnections,
            Boolean bindToHardware
    ) {}
}