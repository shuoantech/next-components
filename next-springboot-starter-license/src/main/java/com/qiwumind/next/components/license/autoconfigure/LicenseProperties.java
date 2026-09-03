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

package com.qiwumind.next.components.license.autoconfigure;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = SystemConstants.Prefix.LICENSE)
public class LicenseProperties {
    private boolean enabled = true;
    private File file = new File();
    private Validation validation = new Validation();
    private Signing signing = new Signing();
    private Binding binding = new Binding();

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class File {
        private String path = "license/license.dat";
        private String backupPath = "license/license.dat.bak";
        private boolean autoCreate = true;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Validation {
        private boolean strictMode = true;
        private Duration gracePeriod = Duration.ofDays(7);
        private boolean validateOnStartup = true;
        private int maxRetryAttempts = 3;
        private Duration retryInterval = Duration.ofSeconds(5);
        private boolean allowGracePeriod = true;
        /**
         * 是否启用全局拦截（License失效时拦截所有接口）
         */
        private boolean globalBlockEnabled = false;
        
        /**
         * 全局拦截时排除的路径列表
         */
        private List<String> excludePaths;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Signing {
        @NotBlank(message = "私钥路径不能为空")
        private String privateKeyPath = "keys/private.key";
        
        @NotBlank(message = "公钥路径不能为空")
        private String publicKeyPath = "keys/public.key";
        
        private String algorithm = "SHA384withRSA";
        private String provider = "BC";
        private int keySize = 2048;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Binding {
        private boolean requireIpBinding = false;
        private boolean requireMacBinding = false;
        private boolean requireHardwareBinding = false;
        private List<String> allowedIps;
        private List<String> allowedMacs;
        private String hardwareFingerprint;
    }
}