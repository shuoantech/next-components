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

import com.qiwumind.next.components.license.core.LicenseManager;
import com.qiwumind.next.components.license.core.aspect.LicenseValidationAspect;
import com.qiwumind.next.components.license.core.generator.LicenseGenerator;
import com.qiwumind.next.components.license.core.serializer.LicenseInfoDeserializer;
import com.qiwumind.next.components.license.core.serializer.LicenseInfoSerializer;
import com.qiwumind.next.components.license.core.signature.SignatureProvider;
import com.qiwumind.next.components.license.core.validator.LicenseValidator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(LicenseProperties.class)
@ConditionalOnProperty(prefix = SystemConstants.Prefix.LICENSE, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({LicenseSerializerConfig.class, ObjectMapperConfig.class, LicenseWebMvcConfigurer.class})
public class LicenseAutoConfiguration {

    private final LicenseProperties properties;

    public LicenseAutoConfiguration(LicenseProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void logConfiguration() {
        log.info("License模块配置: enabled={}, file.path={}",
                properties.isEnabled(), properties.getFile().getPath());
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public SignatureProvider signatureProvider() {
        return new SignatureProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LicenseValidator licenseValidator(SignatureProvider signatureProvider) {
        return new LicenseValidator(signatureProvider, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LicenseGenerator licenseGenerator(SignatureProvider signatureProvider) {
        return new LicenseGenerator(signatureProvider);
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public LicenseManager licenseManager(LicenseValidator licenseValidator,
                                         LicenseGenerator licenseGenerator) {
        return new LicenseManager(properties, licenseValidator, licenseGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    public LicenseValidationAspect licenseValidationAspect(LicenseManager licenseManager) {
        return new LicenseValidationAspect(licenseManager);
    }

    @Bean("fullLicenseSerializer")
    @ConditionalOnMissingBean(name = "fullLicenseSerializer")
    public LicenseInfoSerializer fullLicenseSerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.FULL,
                true,
                false
        );
    }

    @Bean("publicLicenseSerializer")
    @ConditionalOnMissingBean(name = "publicLicenseSerializer")
    public LicenseInfoSerializer publicLicenseSerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.PUBLIC,
                false,
                true
        );
    }

    @Bean("verifyLicenseSerializer")
    @ConditionalOnMissingBean(name = "verifyLicenseSerializer")
    public LicenseInfoSerializer verifyLicenseSerializer() {
        return new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.VERIFY_ONLY,
                false,
                false
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public LicenseInfoDeserializer licenseInfoDeserializer() {
        return new LicenseInfoDeserializer();
    }
}