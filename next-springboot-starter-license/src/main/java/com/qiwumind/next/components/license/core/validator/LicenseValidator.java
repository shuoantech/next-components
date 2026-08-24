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

package com.qiwumind.next.components.license.core.validator;

import com.qiwumind.next.components.license.autoconfigure.LicenseProperties;
import com.qiwumind.next.components.license.core.serializer.LicenseInfoSerializer;
import com.qiwumind.next.components.license.core.signature.SignatureProvider;
import com.qiwumind.next.components.license.core.util.HostInfoUtils;
import com.qiwumind.next.components.license.core.vo.LicenseBinding;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import com.qiwumind.next.components.license.core.vo.LicenseVerifyResult;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class LicenseValidator {
    private final SignatureProvider signatureProvider;
    private final LicenseProperties properties;

    public LicenseValidator(SignatureProvider signatureProvider, LicenseProperties properties) {
        this.signatureProvider = signatureProvider;
        this.properties = properties;
    }

    public LicenseVerifyResult validate(LicenseInfo license) {
        if (license == null) {
            return LicenseVerifyResult.failure("License信息为空");
        }

        var signatureResult = validateSignature(license);
        if (!signatureResult.success()) {
            return signatureResult;
        }

        var dateResult = validateDate(license);
        if (!dateResult.success()) {
            return dateResult;
        }

        var bindingResult = validateBinding(license);
        if (!bindingResult.success()) {
            return bindingResult;
        }

        return LicenseVerifyResult.success(license);
    }

    public LicenseVerifyResult validateSignature(LicenseInfo license) {
        if (license.getSignature() == null || license.getSignature().isEmpty()) {
            return LicenseVerifyResult.failure("签名为空");
        }

        try {
            LicenseInfoSerializer verifySerializer = new LicenseInfoSerializer(
                    LicenseInfoSerializer.SerializeMode.VERIFY_ONLY,
                    false,
                    false
            );
            String verifyJson = verifySerializer.toJsonForSigning(license);
            
            if (signatureProvider.verifyBase64(verifyJson.getBytes(), license.getSignature())) {
                return LicenseVerifyResult.success(license);
            } else {
                return LicenseVerifyResult.failure("签名验证失败");
            }
        } catch (SignatureException | InvalidKeyException e) {
            log.error("签名验证异常", e);
            return LicenseVerifyResult.failure("签名验证异常: " + e.getMessage());
        }
    }

    public LicenseVerifyResult validateDate(LicenseInfo license) {
        ZonedDateTime now = ZonedDateTime.now();

        if (license.getIssueDate() != null && now.isBefore(license.getIssueDate())) {
            return LicenseVerifyResult.failure("License尚未生效");
        }

        if (license.getExpireDate() == null) {
            return LicenseVerifyResult.success(license);
        }

        if (now.isAfter(license.getExpireDate())) {
            if (properties.getValidation().isAllowGracePeriod() && license.getGraceEndDate() != null) {
                if (now.isBefore(license.getGraceEndDate())) {
                    log.warn("License已过期，当前处于宽限期");
                    return LicenseVerifyResult.success(license);
                }
            }
            return LicenseVerifyResult.failure("License已过期");
        }

        return LicenseVerifyResult.success(license);
    }

    public LicenseVerifyResult validateBinding(LicenseInfo license) {
        LicenseBinding binding = license.getBinding();
        if (binding == null) {
            return LicenseVerifyResult.success(license);
        }

        List<String> currentIps = HostInfoUtils.getLocalIps();
        List<String> currentMacs = HostInfoUtils.getLocalMacs();
        String currentHardwareFingerprint = HostInfoUtils.getHardwareFingerprint();

        if (binding.getAllowedIps() != null && !binding.getAllowedIps().isEmpty()) {
            boolean ipMatch = currentIps.stream()
                    .anyMatch(ip -> binding.getAllowedIps().stream()
                            .anyMatch(allowedIp -> isIpAllowed(ip, allowedIp)));
            if (!ipMatch) {
                return LicenseVerifyResult.failure("IP地址不在许可范围内");
            }
        }

        if (binding.getAllowedMacs() != null && !binding.getAllowedMacs().isEmpty()) {
            boolean macMatch = currentMacs.stream()
                    .anyMatch(mac -> binding.getAllowedMacs().contains(mac));
            if (!macMatch) {
                return LicenseVerifyResult.failure("MAC地址不在许可范围内");
            }
        }

        if (binding.getHardwareFingerprint() != null && !binding.getHardwareFingerprint().isEmpty()) {
            if (!binding.getHardwareFingerprint().equals(currentHardwareFingerprint)) {
                return LicenseVerifyResult.failure("硬件指纹不匹配");
            }
        }

        return LicenseVerifyResult.success(license);
    }

    public boolean hasFeature(LicenseInfo license, String feature) {
        if (license == null || license.getFeatures() == null) {
            return false;
        }
        return license.getFeatures().contains(feature);
    }

    private boolean isIpAllowed(String clientIp, String allowedIp) {
        if (clientIp.equals(allowedIp)) {
            return true;
        }

        if (allowedIp.contains("/")) {
            return isIpInCidr(clientIp, allowedIp);
        }

        return false;
    }

    private boolean isIpInCidr(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            int[] ipParts = parseIp(ip);
            int[] networkParts = parseIp(networkAddress);

            int fullBytes = prefixLength / 8;
            int partialBits = prefixLength % 8;

            for (int i = 0; i < fullBytes; i++) {
                if (ipParts[i] != networkParts[i]) {
                    return false;
                }
            }

            if (partialBits > 0 && fullBytes < 4) {
                int mask = 0xFF << (8 - partialBits);
                if ((ipParts[fullBytes] & mask) != (networkParts[fullBytes] & mask)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.warn("CIDR解析失败: {}", cidr, e);
            return false;
        }
    }

    private int[] parseIp(String ip) {
        String[] parts = ip.split("\\.");
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }
}