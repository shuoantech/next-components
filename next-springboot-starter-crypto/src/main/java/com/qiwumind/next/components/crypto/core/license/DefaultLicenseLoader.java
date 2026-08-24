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

package com.qiwumind.next.components.crypto.core.license;

import com.qiwumind.next.components.common.exception.BusinessException;
import com.qiwumind.next.components.crypto.autoconfigure.LicenseProperties;
import com.qiwumind.next.components.crypto.core.license.model.LicenseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ObjectInputStream;

/**
 * 默认 License 加载器实现
 */
public class DefaultLicenseLoader implements LicenseLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultLicenseLoader.class);
    
    private final ResourceLoader resourceLoader;
    private final LicenseProperties properties;
    
    public DefaultLicenseLoader(ResourceLoader resourceLoader, LicenseProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }
    
    @Override
    public LicenseInfo loadLicense() throws Exception {
        Resource resource = resourceLoader.getResource(properties.getLicenseFilePath());
        
        try (ObjectInputStream ois = new ObjectInputStream(resource.getInputStream())) {
            LicenseInfo licenseInfo = (LicenseInfo) ois.readObject();
            logger.info("License 文件加载成功: {}", licenseInfo.getLicenseId());
            return licenseInfo;
        } catch (Exception e) {
            logger.error("加载 License 文件失败", e);
            throw new BusinessException("LICENSE_LOAD_ERROR", "加载 License 文件失败: " + e.getMessage(), e);
        }
    }
}