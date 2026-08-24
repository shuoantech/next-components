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

//package com.qiwumind.next.components.license.demo;
//
//import com.qiwumind.next.components.license.core.LicenseManager;
//import com.qiwumind.next.components.license.core.annotations.LicensedFeature;
//import com.qiwumind.next.components.license.core.generator.LicenseGenerator;
//import com.qiwumind.next.components.license.core.signature.SignatureProvider;
//import com.qiwumind.next.components.license.core.util.HostInfoUtils;
//import com.qiwumind.next.components.license.core.vo.LicenseBinding;
//import com.qiwumind.next.components.license.core.vo.LicenseInfo;
//import com.qiwumind.next.components.license.core.vo.LicenseLimits;
//import com.qiwumind.next.components.license.core.vo.LicenseVerifyResult;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Set;
//
//@Slf4j
//@SpringBootApplication
//@RestController
//@RequestMapping("/api")
//public class DemoApplication {
//
//    private final LicenseManager licenseManager;
//    private final LicenseGenerator licenseGenerator;
//    private final SignatureProvider signatureProvider;
//
//    public DemoApplication(LicenseManager licenseManager,
//                          LicenseGenerator licenseGenerator,
//                          SignatureProvider signatureProvider) {
//        this.licenseManager = licenseManager;
//        this.licenseGenerator = licenseGenerator;
//        this.signatureProvider = signatureProvider;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("=== License Demo Application Started ===");
//
//        LicenseVerifyResult result = licenseManager.getLastVerifyResult();
//        if (result != null) {
//            log.info("License验证状态: {}", result.success() ? "成功" : "失败");
//            log.info("验证消息: {}", result.message());
//        }
//
//        if (licenseManager.getCurrentLicense() != null) {
//            LicenseInfo license = licenseManager.getCurrentLicense();
//            log.info("当前License ID: {}", license.getLicenseId());
//            log.info("授权主题: {}", license.getSubject());
//            log.info("过期时间: {}", license.getExpireDate());
//        }
//    }
//
//    @GetMapping("/health")
//    public String health() {
//        return "OK";
//    }
//
//    @GetMapping("/license/status")
//    public LicenseVerifyResult getLicenseStatus() {
//        return licenseManager.getLastVerifyResult();
//    }
//
//    @GetMapping("/license/info")
//    public LicenseInfo getLicenseInfo() {
//        return licenseManager.getCurrentLicense();
//    }
//
//    @GetMapping("/license/remaining-days")
//    public Long getRemainingDays() {
//        return licenseManager.getRemainingDays();
//    }
//
//    @PostMapping("/license/validate")
//    public LicenseVerifyResult validateLicense(@RequestBody String licenseContent) {
//        return licenseManager.loadAndValidateLicense(licenseContent);
//    }
//
//    @LicensedFeature(value = "advanced-report", message = "未授权使用高级报表功能")
//    @GetMapping("/features/advanced-report")
//    public String advancedReport() {
//        return "高级报表功能 - 已授权访问";
//    }
//
//    @LicensedFeature(value = "api-access", strict = false, message = "API访问受限")
//    @GetMapping("/features/api")
//    public String apiAccess() {
//        return "API访问功能 - 已授权访问";
//    }
//
//    @GetMapping("/system/info")
//    public SystemInfo getSystemInfo() {
//        return new SystemInfo(
//                HostInfoUtils.getLocalIps(),
//                HostInfoUtils.getLocalMacs(),
//                HostInfoUtils.getHardwareFingerprint(),
//                HostInfoUtils.getHostname()
//        );
//    }
//
//    @PostMapping("/license/generate")
//    public LicenseInfo generateLicense(@RequestBody LicenseRequest request) {
//        LicenseInfo.LicenseInfoBuilder builder = LicenseInfo.builder()
//                .subject(request.getSubject())
//                .issuer(request.getIssuer())
//                .expireDate(ZonedDateTime.now().plusDays(request.getDays()))
//                .features(request.getFeatures());
//
//        if (request.getMaxUsers() != null || request.getMaxConnections() != null) {
//            LicenseLimits limits = LicenseLimits.builder()
//                    .maxUsers(request.getMaxUsers())
//                    .maxConnections(request.getMaxConnections())
//                    .build();
//            builder.limits(limits);
//        }
//
//        if (request.getBindToHardware()) {
//            LicenseBinding binding = LicenseBinding.builder()
//                    .hardwareFingerprint(HostInfoUtils.getHardwareFingerprint())
//                    .allowedIps(HostInfoUtils.getLocalIps())
//                    .allowedMacs(HostInfoUtils.getLocalMacs())
//                    .build();
//            builder.binding(binding);
//        }
//
//        return licenseGenerator.generate(builder);
//    }
//
//    @PostMapping("/keys/generate")
//    public String generateKeyPair(@RequestParam String privateKeyPath,
//                                  @RequestParam String publicKeyPath) throws Exception {
//        signatureProvider.generateKeyPair(privateKeyPath, publicKeyPath);
//        return "密钥对生成成功: " + privateKeyPath + " / " + publicKeyPath;
//    }
//
//    public record SystemInfo(
//            List<String> ips,
//            List<String> macs,
//            String hardwareFingerprint,
//            String hostname
//    ) {}
//
//    public record LicenseRequest(
//            String subject,
//            String issuer,
//            Integer days,
//            Set<String> features,
//            Integer maxUsers,
//            Integer maxConnections,
//            Boolean bindToHardware
//    ) {}
//}