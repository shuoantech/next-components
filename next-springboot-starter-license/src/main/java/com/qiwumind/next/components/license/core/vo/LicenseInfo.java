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

package com.qiwumind.next.components.license.core.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qiwumind.next.components.license.core.serializer.LicenseInfoDeserializer;
import com.qiwumind.next.components.license.core.serializer.LicenseInfoSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

// 使用Jakarta Validation

/**
 * License数据模型
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = LicenseInfoSerializer.class)
@JsonDeserialize(using = LicenseInfoDeserializer.class)
public class LicenseInfo {

    @NotNull
    private String licenseId;              // License唯一标识
    @NotBlank
    private String subject;               // 授权主题
    @NotBlank
    private String issuer;                // 签发者
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime issueDate;      // 签发时间（支持时区）
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime expireDate;     // 过期时间
    private ZonedDateTime graceEndDate;   // 宽限期结束时间
    @Singular
    private Set<String> features;         // 授权功能模块
    private LicenseBinding binding;       // 绑定信息
    private LicenseLimits limits;         // 使用限制
    private String signature;             // 数字签名
    private Map<String, Object> extensions; // 扩展信息
}

