package com.qiwumind.next.components.common.util.crypto;

import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * RSA 加解密工具类（统一使用 Hutool 实现）
 */
public final class RSAUtils {
    private static final Logger log = LoggerFactory.getLogger(RSAUtils.class);

    private static final String CHARSET = StandardCharsets.UTF_8.name();
    private static final int KEY_SIZE = 2048;
    private static final SignAlgorithm SIGN_ALGORITHM = SignAlgorithm.SHA256withRSA;

    private RSAUtils() {
        // 工具类私有构造
    }

    /**
     * 生成 RSA 公私钥对（2048位）
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = SecureUtil.generateKeyPair(SIGN_ALGORITHM.getValue(), KEY_SIZE);
        log.debug("RSA 2048 密钥对生成成功");
        logKeyPairInfo(keyPair);
        return keyPair;
    }

    /**
     * 打印密钥信息
     */
    private static void logKeyPairInfo(KeyPair keyPair) {
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        log.info("========== RSA 密钥信息（请妥善保管）==========");
        log.info("公钥（可放入项目配置）：{}", publicKey);
        log.info("私钥（必须妥善保管，用于签名）：{}", privateKey);
        log.info("=============================================");
    }

    /**
     * 私钥签名（用于 License 授权）
     */
    public static String sign(String data, String privateKeyBase64) {
        try {
            Sign sign = SecureUtil.sign(SIGN_ALGORITHM,
                    Base64.getDecoder().decode(privateKeyBase64), null);
            byte[] signBytes = sign.sign(data.getBytes(StandardCharsets.UTF_8));
            log.debug("签名完成，数据长度：{}，签名长度：{}", data.length(), signBytes.length);
            return Base64.getEncoder().encodeToString(signBytes);
        } catch (Exception e) {
            log.error("签名失败，请检查私钥是否正确", e);
            throw new CryptoException("RSA签名失败: " + e.getMessage(), e);
        }
    }

    /**
     * 公钥验签（用于 License 校验）
     */
    public static boolean verify(String data, String signBase64, String publicKeyBase64) {
        try {
            Sign sign = SecureUtil.sign(SIGN_ALGORITHM, null,
                    Base64.getDecoder().decode(publicKeyBase64));
            boolean result = sign.verify(
                    data.getBytes(StandardCharsets.UTF_8),
                    Base64.getDecoder().decode(signBase64)
            );
            log.debug("验签完成，结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("验签失败，请检查公钥或签名是否正确", e);
            return false;
        }
    }

    /**
     * 公钥加密（适合加密小数据，如 AES 密钥）
     * RSA 2048 最大加密长度：245 字节
     */
    public static String encryptByPublicKey(String data, String publicKeyBase64) {
        try {
            RSA rsa = new RSA(null, publicKeyBase64);
            String encrypted = rsa.encryptBase64(data, cn.hutool.crypto.asymmetric.KeyType.PublicKey);
            log.debug("公钥加密完成，原文长度：{}", data.length());
            return encrypted;
        } catch (Exception e) {
            log.error("公钥加密失败", e);
            throw new CryptoException("RSA公钥加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 私钥解密（配合公钥加密使用）
     */
    public static String decryptByPrivateKey(String encryptedBase64, String privateKeyBase64) {
        try {
            RSA rsa = new RSA(privateKeyBase64, null);
            String decrypted = rsa.decryptStr(encryptedBase64, cn.hutool.crypto.asymmetric.KeyType.PrivateKey);
            log.debug("私钥解密完成");
            return decrypted;
        } catch (Exception e) {
            log.error("私钥解密失败，请检查私钥是否正确", e);
            throw new CryptoException("RSA私钥解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 加载公钥对象
     */
    public static PublicKey loadPublicKey(String publicKeyBase64) {
        RSA rsa = new RSA(null, publicKeyBase64);
        return rsa.getPublicKey();
    }

    /**
     * 加载私钥对象
     */
    public static PrivateKey loadPrivateKey(String privateKeyBase64) {
        RSA rsa = new RSA(privateKeyBase64, null);
        return rsa.getPrivateKey();
    }

    /**
     * 公钥转 Base64 字符串
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 私钥转 Base64 字符串
     */
    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}