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

import com.qiwumind.next.components.license.core.serializer.LicenseInfoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 2. 针对不同场景创建不同的序列化器实例
@Configuration
public class LicenseSerializerConfig {

    // 用于存储的完整序列化器
    @Bean("fullLicenseSerializer")
    public LicenseInfoSerializer fullSerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.FULL,
                true,  // 加密签名
                false  // 不美化输出
        );
    }

    // 用于API响应的公开序列化器
    @Bean("publicLicenseSerializer")
    public LicenseInfoSerializer publicSerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.PUBLIC,
                false,
                true   // 美化输出
        );
    }

    // 用于签名验证的序列化器
    @Bean("verifyLicenseSerializer")
    public LicenseInfoSerializer verifySerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.VERIFY_ONLY,
                false,
                false
        );
    }
}