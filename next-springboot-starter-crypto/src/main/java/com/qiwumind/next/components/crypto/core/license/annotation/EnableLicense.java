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

package com.qiwumind.next.components.crypto.core.license.annotation;

import com.qiwumind.next.components.crypto.autoconfigure.LicenseAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启用 License 验证
 *
 * <p>使用示例：</p>
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableLicense
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LicenseAutoConfiguration.class)
public @interface EnableLicense {
    /**
     * 是否启用 License 验证
     * <p>默认为 true，设置为 false 将禁用所有 License 验证功能</p>
     */
    @AliasFor("value")
    boolean enabled() default true;

    /**
     * 同 enabled
     */
    @AliasFor("enabled")
    boolean value() default true;

    /**
     * 是否在应用启动时验证 License
     * <p>默认为 true，如果验证失败会阻止应用启动</p>
     */
    boolean validateOnStartup() default true;

    /**
     * 是否验证硬件绑定信息
     * <p>默认为 true，会校验 License 绑定的硬件指纹</p>
     */
    boolean hardwareValidation() default true;

    /**
     * 是否验证有效期
     * <p>默认为 true，会校验 License 是否在有效期内</p>
     */
    boolean expiryValidation() default true;

    /**
     * 是否验证签名
     * <p>默认为 true，会校验 License 的数字签名</p>
     */
    boolean signatureValidation() default true;

    /**
     * 需要排除 License 验证的路径（Ant 风格路径匹配）
     * <p>默认为 {"/actuator/**", "/license/**"}</p>
     */
    String[] excludePaths() default {"/actuator/**", "/license/**"};

    /**
     * License 验证失败时的提示信息
     */
    String errorMessage() default "License验证失败，请联系管理员";
}