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

package com.qiwumind.next.components.license.core.aspect;

import com.qiwumind.next.components.license.core.LicenseManager;
import com.qiwumind.next.components.license.core.annotations.LicensedFeature;
import com.qiwumind.next.components.license.core.exception.LicenseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
@Aspect
public class LicenseValidationAspect {
    private final LicenseManager licenseManager;
    public LicenseValidationAspect(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    @Around("@annotation(com.qiwumind.next.components.license.core.annotations.LicensedFeature)")
    public Object validateLicense(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LicensedFeature annotation = signature.getMethod().getAnnotation(LicensedFeature.class);
        String feature = annotation.value();
        boolean strict = annotation.strict();
        String message = annotation.message();
        if (!licenseManager.isLicenseValid()) {
            log.warn("License无效，拒绝访问功能: {}", feature);
            throw new LicenseException(message);
        }
        if (!licenseManager.hasFeature(feature)) {
            log.warn("未授权使用功能: {}", feature);
            if (strict) {
                throw new LicenseException(message);
            }
        }
        try {
            return joinPoint.proceed();
        } catch (LicenseException e) {
            throw e;
        } catch (Throwable e) {
            log.error("执行授权功能时发生异常: {}", feature, e);
            throw e;
        }
    }

    @Around("@within(com.qiwumind.next.components.license.core.annotations.LicensedFeature)")
    public Object validateClassLicense(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        LicensedFeature annotation = targetClass.getAnnotation(LicensedFeature.class);
        
        if (annotation == null) {
            return joinPoint.proceed();
        }

        String feature = annotation.value();
        boolean strict = annotation.strict();
        String message = annotation.message();

        if (!licenseManager.isLicenseValid()) {
            log.warn("License无效，拒绝访问类: {}", targetClass.getName());
            throw new LicenseException(message);
        }

        if (!licenseManager.hasFeature(feature)) {
            log.warn("未授权使用类: {}", targetClass.getName());
            if (strict) {
                throw new LicenseException(message);
            }
        }

        try {
            return joinPoint.proceed();
        } catch (LicenseException e) {
            throw e;
        } catch (Throwable e) {
            log.error("执行授权类方法时发生异常: {}", targetClass.getName(), e);
            throw e;
        }
    }
}