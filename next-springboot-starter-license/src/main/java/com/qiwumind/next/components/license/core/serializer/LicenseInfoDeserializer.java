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

package com.qiwumind.next.components.license.core.serializer;// ========== 配套的反序列化器 ==========

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.qiwumind.next.components.license.core.vo.LicenseBinding;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import com.qiwumind.next.components.license.core.vo.LicenseLimits;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * LicenseInfo 反序列化器
 */
public class LicenseInfoDeserializer extends JsonDeserializer<LicenseInfo> {
    
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ISO_ZONED_DATE_TIME;
    
    @Override
    public LicenseInfo deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException {
        
        JsonNode node = parser.getCodec().readTree(parser);
        
        var builder = LicenseInfo.builder();
        
        // 基本字段
        if (node.has("licenseId") && !node.get("licenseId").isNull()) {
            builder.licenseId(node.get("licenseId").asText());
        }
        
        if (node.has("subject")) {
            builder.subject(node.get("subject").asText());
        }
        
        if (node.has("issuer")) {
            builder.issuer(node.get("issuer").asText());
        }
        
        // 日期字段
        if (node.has("issueDate") && !node.get("issueDate").isNull()) {
            builder.issueDate(ZonedDateTime.parse(
                node.get("issueDate").asText(), DATE_FORMATTER));
        }
        
        if (node.has("expireDate") && !node.get("expireDate").isNull()) {
            builder.expireDate(ZonedDateTime.parse(
                node.get("expireDate").asText(), DATE_FORMATTER));
        }
        
        if (node.has("graceEndDate") && !node.get("graceEndDate").isNull()) {
            builder.graceEndDate(ZonedDateTime.parse(
                node.get("graceEndDate").asText(), DATE_FORMATTER));
        }
        
        // 功能模块
        if (node.has("features")) {
            Set<String> features = new HashSet<>();
            node.get("features").forEach(f -> features.add(f.asText()));
            builder.features(features);
        }
        
        // 绑定信息
        if (node.has("binding") && !node.get("binding").isNull()) {
            builder.binding(deserializeBinding(node.get("binding")));
        }
        
        // 限制信息
        if (node.has("limits") && !node.get("limits").isNull()) {
            builder.limits(deserializeLimits(node.get("limits")));
        }
        
        // 签名
        if (node.has("signature") && !node.get("signature").isNull()) {
            builder.signature(node.get("signature").asText());
        }
        
        // 扩展信息
        if (node.has("extensions") && !node.get("extensions").isNull()) {
            Map<String, Object> extensions = new HashMap<>();
            node.get("extensions").fields()
                .forEachRemaining(entry -> extensions.put(
                    entry.getKey(), extractValue(entry.getValue())));
            builder.extensions(extensions);
        }
        
        return builder.build();
    }
    
    private LicenseBinding deserializeBinding(JsonNode node) {
        var bindingBuilder = LicenseBinding.builder();
        
        if (node.has("allowedIps")) {
            List<String> ips = new ArrayList<>();
            node.get("allowedIps").forEach(ip -> ips.add(ip.asText()));
            bindingBuilder.allowedIps(ips);
        }
        
        if (node.has("allowedMacs")) {
            List<String> macs = new ArrayList<>();
            node.get("allowedMacs").forEach(mac -> macs.add(mac.asText()));
            bindingBuilder.allowedMacs(macs);
        }
        
        if (node.has("hardwareFingerprint")) {
            bindingBuilder.hardwareFingerprint(
                node.get("hardwareFingerprint").asText());
        }
        
        if (node.has("instanceId")) {
            bindingBuilder.instanceId(node.get("instanceId").asText());
        }
        
        if (node.has("allowedDomains")) {
            List<String> domains = new ArrayList<>();
            node.get("allowedDomains").forEach(domain -> domains.add(domain.asText()));
            bindingBuilder.allowedDomains(domains);
        }
        
        return bindingBuilder.build();
    }
    
    private LicenseLimits deserializeLimits(JsonNode node) {
        var limitsBuilder = LicenseLimits.builder();
        
        if (node.has("maxUsers")) {
            limitsBuilder.maxUsers(node.get("maxUsers").asInt());
        }
        
        if (node.has("maxConnections")) {
            limitsBuilder.maxConnections(node.get("maxConnections").asInt());
        }
        
        if (node.has("maxDataSize")) {
            limitsBuilder.maxDataSize(node.get("maxDataSize").asLong());
        }
        
        if (node.has("allowedModules")) {
            Set<String> modules = new HashSet<>();
            node.get("allowedModules").forEach(m -> modules.add(m.asText()));
            limitsBuilder.allowedModules(modules);
        }
        
        if (node.has("custom")) {
            Map<String, Object> custom = new HashMap<>();
            node.get("custom").fields()
                .forEachRemaining(entry -> custom.put(
                    entry.getKey(), extractValue(entry.getValue())));
            limitsBuilder.custom(custom);
        }
        
        return limitsBuilder.build();
    }
    
    private Object extractValue(JsonNode node) {
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isDouble()) return node.asDouble();
        if (node.isBoolean()) return node.asBoolean();
        if (node.isNull()) return null;
        return node.toString();
    }
}