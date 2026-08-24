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

package com.qiwumind.next.components.license.core.generator;

import com.qiwumind.next.components.license.core.serializer.LicenseInfoSerializer;
import com.qiwumind.next.components.license.core.signature.SignatureProvider;
import com.qiwumind.next.components.license.core.util.JsonUtils;
import com.qiwumind.next.components.license.core.vo.LicenseInfo;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
public class LicenseGenerator {

    private final SignatureProvider signatureProvider;

    public LicenseGenerator(SignatureProvider signatureProvider) {
        this.signatureProvider = signatureProvider;
    }

    public LicenseInfo generate(LicenseInfo.LicenseInfoBuilder builder) {
        LicenseInfo license = builder
                .licenseId(UUID.randomUUID().toString())
                .issueDate(ZonedDateTime.now())
                .build();

        return signLicense(license);
    }

    public LicenseInfo signLicense(LicenseInfo license) {
        try {
            LicenseInfoSerializer verifySerializer = new LicenseInfoSerializer(
                    LicenseInfoSerializer.SerializeMode.VERIFY_ONLY,
                    false,
                    false
            );

            String verifyJson = verifySerializer.toJsonForSigning(license);
            String signature = signatureProvider.signBase64(verifyJson.getBytes());

            return LicenseInfo.builder()
                    .licenseId(license.getLicenseId())
                    .subject(license.getSubject())
                    .issuer(license.getIssuer())
                    .issueDate(license.getIssueDate())
                    .expireDate(license.getExpireDate())
                    .graceEndDate(license.getGraceEndDate())
                    .features(license.getFeatures())
                    .binding(license.getBinding())
                    .limits(license.getLimits())
                    .signature(signature)
                    .extensions(license.getExtensions())
                    .build();

        } catch (SignatureException | InvalidKeyException e) {
            log.error("签名License失败", e);
            throw new RuntimeException("签名License失败", e);
        }
    }

    public String serializeToFile(LicenseInfo license) {
        LicenseInfoSerializer fullSerializer = new LicenseInfoSerializer(
                LicenseInfoSerializer.SerializeMode.FULL,
                true,
                false
        );
        return fullSerializer.toJsonForSigning(license);
    }

    public LicenseInfo generateTrialLicense(String subject, int days) {
        return generate(LicenseInfo.builder()
                .subject(subject)
                .issuer("System")
                .expireDate(ZonedDateTime.now().plusDays(days)));
    }
}