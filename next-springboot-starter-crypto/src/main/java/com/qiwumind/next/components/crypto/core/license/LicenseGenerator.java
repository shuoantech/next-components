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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiwumind.next.components.crypto.core.license.model.LicenseInfo;
import com.qiwumind.next.components.common.util.crypto.RSAUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * License 生成工具（独立使用）
 */
public class LicenseGenerator {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 生成 RSA 密钥对
     */
    public static KeyPair generateKeyPair() throws Exception {
        return RSAUtils.generateKeyPair();
    }
    
    /**
     * 保存公钥
     */
    public static void savePublicKey(PublicKey publicKey, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(publicKey.getEncoded());
        }
    }
    
    /**
     * 保存私钥
     */
    public static void savePrivateKey(PrivateKey privateKey, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(privateKey.getEncoded());
        }
    }
    
    /**
     * 生成 License 文件
     */
    public static void generateLicense(LicenseInfo licenseInfo, PrivateKey privateKey,
                                       String outputPath) throws Exception {
        // 1. 将 License 信息转换为 JSON 字节数组
        byte[] data = objectMapper.writeValueAsBytes(licenseInfo);
        
        // 2. 使用私钥签名
        byte[] signature = RSAUtils.sign(data, privateKey);
        licenseInfo.setSignature(Base64.encodeBase64String(signature));
        
        // 3. 写入文件
        try (FileOutputStream fos = new FileOutputStream(outputPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(licenseInfo);
        }
        
        System.out.println("License 文件已生成: " + outputPath);
    }
    
    /**
     * 生成示例 License
     */
    public static void main(String[] args) {
        try {
            // 1. 生成密钥对
            KeyPair keyPair = generateKeyPair();
            
            // 2. 保存公钥和私钥
            savePublicKey(keyPair.getPublic(), "public_key.der");
            savePrivateKey(keyPair.getPrivate(), "private_key.der");
            System.out.println("密钥对生成成功");
            
            // 3. 创建 License 信息
            LicenseInfo licenseInfo = new LicenseInfo();
            licenseInfo.setLicenseId("DEMO-2024-001");
            licenseInfo.setProductName("Demo Product");
            licenseInfo.setProductVersion("1.0.0");
            licenseInfo.setLicenseType("OFFICIAL");
            licenseInfo.setStartTime(new Date());
            
            // 设置有效期（1年）
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.YEAR, 1);
            licenseInfo.setEndTime(calendar.getTime());
            
            // 设置最大用户数
            licenseInfo.setMaxUsers(100);
            
            // 4. 生成 License
            generateLicense(licenseInfo, keyPair.getPrivate(), "license.dat");
            
            System.out.println("License 生成成功");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}