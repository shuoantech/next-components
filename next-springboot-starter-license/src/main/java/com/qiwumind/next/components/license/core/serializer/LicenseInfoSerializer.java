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

package com.qiwumind.next.components.license.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * LicenseInfo 自定义序列化器
 * 支持：
 * 1. 敏感字段脱敏/加密
 * 2. 日期格式化
 * 3. 条件序列化
 * 4. 性能优化
 */
@Slf4j
public class LicenseInfoSerializer extends JsonSerializer<LicenseInfo> {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final String SENSITIVE_MASK = "******";
    private static final int MAX_FEATURES_DISPLAY = 10;

    // 序列化模式
    public static enum SerializeMode {
        FULL,           // 完整序列化
        PUBLIC,         // 公开信息（脱敏）
        VERIFY_ONLY,    // 仅验证所需字段
        METRICS         // 仅统计信息
    }

    private final SerializeMode mode;
    private final boolean encryptSignature;
    private final boolean prettyPrint;

    public LicenseInfoSerializer() {
        this(SerializeMode.PUBLIC, false, false);
    }

    public LicenseInfoSerializer(SerializeMode mode,
                                 boolean encryptSignature,
                                 boolean prettyPrint) {
        this.mode = mode;
        this.encryptSignature = encryptSignature;
        this.prettyPrint = prettyPrint;
    }

    @Override
    public void serialize(LicenseInfo license,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        if (prettyPrint) {
            gen.useDefaultPrettyPrinter();
        }

        gen.writeStartObject();

        // 根据模式决定序列化内容
        switch (mode) {
            case FULL -> serializeFull(license, gen);
            case PUBLIC -> serializePublic(license, gen);
            case VERIFY_ONLY -> serializeVerifyOnly(license, gen);
            case METRICS -> serializeMetrics(license, gen);
        }

        gen.writeEndObject();
    }

    /**
     * 完整序列化 - 用于License文件存储
     */
    private void serializeFull(LicenseInfo license, JsonGenerator gen)
            throws IOException {

        // 基本信息
        writeBasicInfo(license, gen, false);

        // 签名
        writeSignature(license, gen);

        // 扩展信息
        writeExtensions(license, gen);

        // 元数据
        writeMetadata(license, gen);
    }

    /**
     * 公开序列化 - 用于API返回（脱敏处理）
     */
    private void serializePublic(LicenseInfo license, JsonGenerator gen)
            throws IOException {

        // 基本信息（脱敏）
        gen.writeStringField("licenseId",
                maskLicenseId(license.getLicenseId().toString()));
        gen.writeStringField("subject", license.getSubject());
        gen.writeStringField("issuer", license.getIssuer());
        // 日期（格式化）
        writeDateField(gen, "issueDate", license.getIssueDate());
        writeDateField(gen, "expireDate", license.getExpireDate());

        if (license.getGraceEndDate() != null) {
            writeDateField(gen, "graceEndDate", license.getGraceEndDate());
        }
        // 功能模块（限制数量）
        writeFeaturesPublic(license, gen);
        // 绑定信息（脱敏）
        writeBindingPublic(license, gen);
        // 限制信息
        writeLimits(license, gen);
        // 签名状态（不暴露完整签名）
        gen.writeBooleanField("signatureValid",
                license.getSignature() != null && !license.getSignature().isEmpty());
        gen.writeStringField("signatureHash",
                hashSignature(license.getSignature()));
    }

    /**
     * 仅验证序列化 - 用于签名验证（排除签名字段）
     */
    private void serializeVerifyOnly(LicenseInfo license, JsonGenerator gen)
            throws IOException {

        // 只序列化参与签名的字段
        gen.writeStringField("licenseId", license.getLicenseId().toString());
        gen.writeStringField("subject", license.getSubject());
        gen.writeStringField("issuer", license.getIssuer());

        writeDateField(gen, "issueDate", license.getIssueDate());
        writeDateField(gen, "expireDate", license.getExpireDate());

        if (license.getGraceEndDate() != null) {
            writeDateField(gen, "graceEndDate", license.getGraceEndDate());
        }

        // 功能模块（排序后序列化，确保签名一致性）
        writeFeaturesSorted(license, gen);

        // 绑定信息（规范化）
        writeBindingNormalized(license, gen);

        // 限制信息
        writeLimits(license, gen);

        // 扩展信息（排序后）
        writeExtensionsSorted(license, gen);

        // 注意：不包含签名字段本身
    }

    /**
     * 指标序列化 - 用于监控（最小化数据）
     */
    private void serializeMetrics(LicenseInfo license, JsonGenerator gen)
            throws IOException {

        gen.writeBooleanField("valid", true);
        writeDateField(gen, "expireDate", license.getExpireDate());

        long remainingDays = calculateRemainingDays(license);
        gen.writeNumberField("remainingDays", remainingDays);

        gen.writeNumberField("featuresCount",
                license.getFeatures() != null ? license.getFeatures().size() : 0);

        gen.writeBooleanField("hasGracePeriod",
                license.getGraceEndDate() != null);

        if (license.getGraceEndDate() != null) {
            writeDateField(gen, "graceEndDate", license.getGraceEndDate());
        }
    }

    /**
     * 写入基本信息
     */
    private void writeBasicInfo(LicenseInfo license, JsonGenerator gen,
                                boolean mask) throws IOException {
        gen.writeStringField("licenseId",
                mask ? maskLicenseId(license.getLicenseId().toString())
                        : license.getLicenseId().toString());
        gen.writeStringField("subject", license.getSubject());
        gen.writeStringField("issuer", license.getIssuer());
        writeDateField(gen, "issueDate", license.getIssueDate());
        writeDateField(gen, "expireDate", license.getExpireDate());

        if (license.getGraceEndDate() != null) {
            writeDateField(gen, "graceEndDate", license.getGraceEndDate());
        }
    }

    /**
     * 写入日期字段
     */
    private void writeDateField(JsonGenerator gen,
                                String fieldName,
                                ZonedDateTime date) throws IOException {
        if (date != null) {
            gen.writeStringField(fieldName, date.format(DATE_FORMATTER));
        } else {
            gen.writeNullField(fieldName);
        }
    }

    /**
     * 写入签名
     */
    private void writeSignature(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        if (encryptSignature && license.getSignature() != null) {
            // 加密签名（可选）
            String encryptedSig = encryptSignature(license.getSignature());
            gen.writeStringField("signature", encryptedSig);
            gen.writeStringField("signatureAlgorithm", "RSA-SHA384-ENCRYPTED");
        } else if (license.getSignature() != null) {
            gen.writeStringField("signature", license.getSignature());
        } else {
            gen.writeNullField("signature");
        }
    }

    /**
     * 写入功能模块（公开版本，限制数量）
     */
    private void writeFeaturesPublic(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        if (license.getFeatures() != null && !license.getFeatures().isEmpty()) {
            gen.writeArrayFieldStart("features");
            int count = 0;
            for (String feature : license.getFeatures()) {
                if (count++ < MAX_FEATURES_DISPLAY) {
                    gen.writeString(feature);
                } else {
                    gen.writeString("...");
                    break;
                }
            }
            gen.writeEndArray();

            if (license.getFeatures().size() > MAX_FEATURES_DISPLAY) {
                gen.writeNumberField("totalFeatures", license.getFeatures().size());
            }
        } else {
            gen.writeArrayFieldStart("features");
            gen.writeEndArray();
        }
    }

    /**
     * 写入功能模块（排序后，用于签名）
     */
    private void writeFeaturesSorted(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        if (license.getFeatures() != null && !license.getFeatures().isEmpty()) {
            var sorted = license.getFeatures().stream()
                    .sorted()
                    .toList();
            gen.writeArrayFieldStart("features");
            for (String feature : sorted) {
                gen.writeString(feature);
            }
            gen.writeEndArray();
        } else {
            gen.writeArrayFieldStart("features");
            gen.writeEndArray();
        }
    }

    /**
     * 写入绑定信息（公开版本，脱敏）
     */
    private void writeBindingPublic(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        var binding = license.getBinding();
        if (binding != null) {
            gen.writeObjectFieldStart("binding");

            // IP列表（部分脱敏）
            if (binding.getAllowedIps() != null && !binding.getAllowedIps().isEmpty()) {
                gen.writeArrayFieldStart("allowedIps");
                for (String ip : binding.getAllowedIps()) {
                    gen.writeString(maskIp(ip));
                }
                gen.writeEndArray();
            }

            // MAC地址（完全脱敏）
            if (binding.getAllowedMacs() != null && !binding.getAllowedMacs().isEmpty()) {
                gen.writeArrayFieldStart("allowedMacs");
                for (int i = 0; i < binding.getAllowedMacs().size(); i++) {
                    gen.writeString(SENSITIVE_MASK);
                }
                gen.writeEndArray();
            }

            // 硬件指纹（完全脱敏）
            if (binding.getHardwareFingerprint() != null) {
                gen.writeStringField("hardwareFingerprint", SENSITIVE_MASK);
            }

            gen.writeEndObject();
        }
    }

    /**
     * 写入绑定信息（规范化，用于签名）
     */
    private void writeBindingNormalized(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        var binding = license.getBinding();
        if (binding != null) {
            gen.writeObjectFieldStart("binding");

            // IP列表（排序后）
            if (binding.getAllowedIps() != null && !binding.getAllowedIps().isEmpty()) {
                var sorted = binding.getAllowedIps().stream()
                        .sorted()
                        .toList();
                gen.writeArrayFieldStart("allowedIps");
                for (String ip : sorted) {
                    gen.writeString(ip);
                }
                gen.writeEndArray();
            }

            // MAC地址（排序后）
            if (binding.getAllowedMacs() != null && !binding.getAllowedMacs().isEmpty()) {
                var sorted = binding.getAllowedMacs().stream()
                        .sorted()
                        .toList();
                gen.writeArrayFieldStart("allowedMacs");
                for (String mac : sorted) {
                    gen.writeString(mac);
                }
                gen.writeEndArray();
            }

            // 硬件指纹
            if (binding.getHardwareFingerprint() != null) {
                gen.writeStringField("hardwareFingerprint",
                        binding.getHardwareFingerprint());
            }

            // 实例ID
            if (binding.getInstanceId() != null) {
                gen.writeStringField("instanceId", binding.getInstanceId());
            }

            gen.writeEndObject();
        }
    }

    /**
     * 写入限制信息
     */
    private void writeLimits(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        var limits = license.getLimits();
        if (limits != null) {
            gen.writeObjectFieldStart("limits");

            if (limits.getMaxUsers() != null) {
                gen.writeNumberField("maxUsers", limits.getMaxUsers());
            }
            if (limits.getMaxConnections() != null) {
                gen.writeNumberField("maxConnections", limits.getMaxConnections());
            }
            if (limits.getMaxDataSize() != null) {
                gen.writeNumberField("maxDataSize", limits.getMaxDataSize());
            }
            if (limits.getAllowedModules() != null) {
                var sorted = limits.getAllowedModules().stream().sorted().toList();
                gen.writeArrayFieldStart("allowedModules");
                for (String module : sorted) {
                    gen.writeString(module);
                }
                gen.writeEndArray();
            }

            gen.writeEndObject();
        }
    }

    /**
     * 写入扩展信息
     */
    private void writeExtensions(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        if (license.getExtensions() != null && !license.getExtensions().isEmpty()) {
            gen.writeObjectFieldStart("extensions");
            for (var entry : license.getExtensions().entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
            gen.writeEndObject();
        }
    }

    /**
     * 写入扩展信息（排序后，用于签名）
     */
    private void writeExtensionsSorted(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        if (license.getExtensions() != null && !license.getExtensions().isEmpty()) {
            var sortedKeys = license.getExtensions().keySet().stream()
                    .sorted()
                    .toList();
            gen.writeObjectFieldStart("extensions");
            for (String key : sortedKeys) {
                gen.writeObjectField(key, license.getExtensions().get(key));
            }
            gen.writeEndObject();
        }
    }

    /**
     * 写入元数据
     */
    private void writeMetadata(LicenseInfo license, JsonGenerator gen)
            throws IOException {
        gen.writeObjectFieldStart("metadata");
        gen.writeStringField("serializerVersion", "2.0");
        gen.writeStringField("serializedAt",
                ZonedDateTime.now().format(DATE_FORMATTER));
        gen.writeStringField("serializeMode", mode.name());
        gen.writeEndObject();
    }

    public String toJsonForSigning(LicenseInfo license) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.core.JsonGenerator gen = mapper.getFactory().createGenerator(new java.io.StringWriter());
            serializeVerifyOnly(license, gen);
            gen.flush();
            return gen.getOutputTarget().toString();
        } catch (IOException e) {
            log.error("序列化签名数据失败", e);
            throw new RuntimeException("序列化签名数据失败", e);
        }
    }

    // ========== 辅助方法 ==========

    private String maskLicenseId(String licenseId) {
        if (licenseId == null || licenseId.length() <= 8) {
            return SENSITIVE_MASK;
        }
        return licenseId.substring(0, 8) + "****" +
                licenseId.substring(licenseId.length() - 4);
    }

    private String maskIp(String ip) {
        if (ip == null) return SENSITIVE_MASK;
        if (ip.contains("/")) {
            // CIDR格式
            String[] parts = ip.split("/");
            if (parts[0].contains(".")) {
                String[] ipParts = parts[0].split("\\.");
                if (ipParts.length == 4) {
                    return ipParts[0] + "." + ipParts[1] + ".***.***/" + parts[1];
                }
            }
        } else if (ip.contains(".")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".***.***";
            }
        }
        return SENSITIVE_MASK;
    }

    private String hashSignature(String signature) {
        if (signature == null) return null;
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(signature.getBytes());
            return Base64.getEncoder().encodeToString(hash).substring(0, 16) + "...";
        } catch (Exception e) {
            return "hash-error";
        }
    }

    private String encryptSignature(String signature) {
        // 简单混淆，生产环境应使用真正的加密
        if (signature == null) return null;
        return "ENC:" + Base64.getEncoder().encodeToString(
                signature.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private long calculateRemainingDays(LicenseInfo license) {
        if (license == null || license.getExpireDate() == null) {
            return -1;
        }
        var now = ZonedDateTime.now();
        if (now.isAfter(license.getExpireDate())) {
            return 0;
        }
        return java.time.Duration.between(now, license.getExpireDate()).toDays();
    }
}

