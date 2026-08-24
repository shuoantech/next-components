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

package com.qiwumind.next.components.common.util.crypto;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RSA 加解密工具类
 */
public class RSAUtils {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtils.class);

    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    
    /**
     * 从字节数组加载公钥
     */
    public static PublicKey loadPublicKey(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
    
    /**
     * 从字节数组加载私钥
     */
    public static PrivateKey loadPrivateKey(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
    
    /**
     * 签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
    
    /**
     * 验证签名
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }
    
    /**
     * 公钥转字符串
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    /**
     * 私钥转字符串
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 用私钥对 License 信息进行签名（Hutool 简化实现）
     */
    public static String sign(String data, String privateKey) {
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, cn.hutool.core.codec.Base64.decode(privateKey), null);
        byte[] signBytes = sign.sign(data.getBytes());
        logger.info("License 信息签名完成，签名长度：{}", signBytes.length);
        return cn.hutool.core.codec.Base64.encode(signBytes);
    }

    /**
     * 用公钥校验签名合法性（Hutool 简化实现）
     */
    public static boolean verify(String data, String signBase64, String publicKey) {
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, cn.hutool.core.codec.Base64.decode(publicKey));
        boolean verifyResult = sign.verify(data.getBytes(), cn.hutool.core.codec.Base64.decode(signBase64));
        logger.info("License 签名校验完成，校验结果：{}", verifyResult);
        return verifyResult;
    }

    /**
     * 从公钥字节数组中获取 PublicKey 对象（Hutool 简化实现）
     */
    public static PublicKey getPublicKey(byte[] publicKeyBytes) {
        logger.info("从字节数组中获取公钥对象，字节数组长度：{}", publicKeyBytes.length);
        RSA rsa = SecureUtil.rsa(publicKeyBytes, null);
        return rsa.getPublicKey();
    }

    /**
     * 生成 RSA 公私钥对
     * @return 公私钥对，私钥自己保管，公钥放入项目中
     * @throws Exception 密钥生成异常
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPair keyPair = SecureUtil.generateKeyPair(SignAlgorithm.SHA256withRSA.getValue(), 2048);
        logger.info("公钥（放入项目中）：{}", cn.hutool.core.codec.Base64.encode(keyPair.getPublic().getEncoded()));
        logger.info("私钥（妥善保管，用于生成 License）：{}", cn.hutool.core.codec.Base64.encode(keyPair.getPrivate().getEncoded()));
        return keyPair;
    }

    /**
     * 测试
     *
     * 关键提示：公私钥对生成后，私钥一定要妥善保管（比如存放在本地加密文件中，不要随项目打包），
     * 公钥可以直接内置到项目配置文件中，用于校验License；工具类自带测试方法，可直接运行测试签名与验签功能。
     */
    public static void main(String[] args) {
        try {
            KeyPair keyPair = RSAUtils.generateKeyPair();
            String data = "123456";
            String privateKeyBase64 = cn.hutool.core.codec.Base64.encode(keyPair.getPrivate().getEncoded());
            String publicKeyBase64 = cn.hutool.core.codec.Base64.encode(keyPair.getPublic().getEncoded());

            String sign = RSAUtils.sign(data, privateKeyBase64);
            boolean verify = RSAUtils.verify(data, sign, publicKeyBase64);
            System.out.println("签名校验结果：" + verify);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}