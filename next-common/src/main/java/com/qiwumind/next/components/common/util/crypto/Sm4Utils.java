package com.qiwumind.next.components.common.util.crypto;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;

import java.nio.charset.StandardCharsets;

/**
 * 国密 SM4 加解密工具类（基于 Hutool 实现）
 * <p>
 * 支持 CBC/ECB 模式，推荐使用 CBC 模式
 * 密钥长度固定为 16 字节（128 位）
 *
 * @author liks
 */
public final class Sm4Utils {
    /**
     * SM4 密钥长度：16 字节
     */
    private static final int KEY_SIZE = 16;

    /**
     * SM4 加密前缀，用于标识已加密数据
     */
    public static final String SM4_PREFIX = "SM4:";

    /**
     * 默认密钥（⚠️ 仅用于开发测试，生产环境必须从配置中心或环境变量读取）
     */
    private static final String DEFAULT_KEY_HEX = "7f3b9a2c4e8d1f5a6b3c7d9e0f1a2b3c";
    private static final String DEFAULT_IV_HEX = "9e4b7c1d5f8a2e6c0f3b7a9d4e8c1f5a";
    /**
     * 默认加密器实例（CBC模式，更安全）
     */
    private static final SM4 DEFAULT_CIPHER_CBC;

    /**
     * ECB 模式加密器（用于兼容旧数据）
     */
    private static final SM4 DEFAULT_CIPHER_ECB;

    static {
        byte[] keyBytes = HexUtil.decodeHex(DEFAULT_KEY_HEX);
        byte[] ivBytes = HexUtil.decodeHex(DEFAULT_IV_HEX);
        // ✅ CBC 模式（推荐）- 使用 SM4 类
        DEFAULT_CIPHER_CBC = new SM4(Mode.CBC, Padding.PKCS5Padding, keyBytes, ivBytes);
        // ✅ ECB 模式（兼容旧数据）
        DEFAULT_CIPHER_ECB = new SM4(Mode.ECB, Padding.PKCS5Padding, keyBytes);
    }

    private Sm4Utils() {
        // 工具类私有构造，禁止实例化
    }

    // ==================== 加密方法 ====================
    /**
     * 使用默认密钥和 CBC 模式加密（推荐）
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        if (isEncrypted(plaintext)) {
            return plaintext;
        }
        String encrypted = DEFAULT_CIPHER_CBC.encryptBase64(plaintext);
        return SM4_PREFIX + encrypted;
    }

    /**
     * 使用指定密钥和 CBC 模式加密
     */
    public static String encryptCbc(String plaintext, String keyHex, String ivHex) {
        validateKeyAndIv(keyHex, ivHex);
        SM4 cipher = createCipherCbc(keyHex, ivHex);
        return cipher.encryptBase64(plaintext);
    }

    /**
     * 使用默认密钥和 ECB 模式加密（仅用于兼容旧数据）
     */
    public static String encryptEcb(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        return DEFAULT_CIPHER_ECB.encryptHex(plaintext);
    }

    /**
     * 使用指定密钥和 ECB 模式加密
     */
    public static String encryptEcb(String plaintext, String keyHex) {
        validateKey(keyHex);
        SM4 cipher = createCipherEcb(keyHex);
        return cipher.encryptHex(plaintext);
    }

    // ==================== 解密方法 ====================

    /**
     * 使用默认密钥和 CBC 模式解密（推荐）
     * 自动识别是否带有 SM4: 前缀
     */
    public static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }

        if (!isEncrypted(ciphertext)) {
            return ciphertext;
        }

        String realCipher = removePrefix(ciphertext);
        String decrypted = DEFAULT_CIPHER_CBC.decryptStr(realCipher);
        return decrypted;
    }

    /**
     * 使用指定密钥和 CBC 模式解密
     */
    public static String decryptCbc(String ciphertext, String keyHex, String ivHex) {
        validateKeyAndIv(keyHex, ivHex);
        SM4 cipher = createCipherCbc(keyHex, ivHex);
        return cipher.decryptStr(ciphertext);
    }

    /**
     * 使用默认密钥和 ECB 模式解密（仅用于兼容旧数据）
     */
    public static String decryptEcb(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }
        return DEFAULT_CIPHER_ECB.decryptStr(ciphertext);
    }

    /**
     * 使用指定密钥和 ECB 模式解密
     */
    public static String decryptEcb(String ciphertext, String keyHex) {
        validateKey(keyHex);
        SM4 cipher = createCipherEcb(keyHex);
        return cipher.decryptStr(ciphertext);
    }

    // ==================== 便捷方法 ====================

    /**
     * 加密并返回 Hex 格式（便于数据库存储）
     */
    public static String encryptHex(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        if (isEncrypted(plaintext)) {
            return plaintext;
        }
        return SM4_PREFIX + DEFAULT_CIPHER_CBC.encryptHex(plaintext);
    }

    /**
     * 从 Hex 格式解密
     */
    public static String decryptHex(String ciphertextHex) {
        if (ciphertextHex == null || ciphertextHex.isEmpty()) {
            return ciphertextHex;
        }
        if (!isEncrypted(ciphertextHex)) {
            return ciphertextHex;
        }
        String realCipher = removePrefix(ciphertextHex);
        return DEFAULT_CIPHER_CBC.decryptStr(realCipher, StandardCharsets.UTF_8);
    }

    /**
     * 判断字符串是否已加密
     */
    public static boolean isEncrypted(String data) {
        return data != null && data.startsWith(SM4_PREFIX);
    }

    /**
     * 移除加密前缀
     */
    public static String removePrefix(String data) {
        if (data == null) {
            return null;
        }
        return data.startsWith(SM4_PREFIX) ? data.substring(SM4_PREFIX.length()) : data;
    }

    /**
     * 生成随机密钥（十六进制字符串）
     */
    public static String generateRandomKey() {
        byte[] key = new byte[KEY_SIZE];
        new java.security.SecureRandom().nextBytes(key);
        return HexUtil.encodeHexStr(key);
    }

    /**
     * 生成随机 IV
     */
    public static String generateRandomIv() {
        return generateRandomKey();
    }

    /**
     * 校验密钥合法性
     */
    public static boolean isValidKey(String keyHex) {
        if (keyHex == null) {
            return false;
        }
        try {
            byte[] bytes = HexUtil.decodeHex(keyHex);
            return bytes.length == KEY_SIZE;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * ✅ 使用 SM4 类创建 CBC 模式加密器
     */
    private static SM4 createCipherCbc(String keyHex, String ivHex) {
        byte[] keyBytes = HexUtil.decodeHex(keyHex);
        byte[] ivBytes = HexUtil.decodeHex(ivHex);
        return new SM4(Mode.CBC, Padding.PKCS5Padding, keyBytes, ivBytes);
    }

    /**
     * ✅ 使用 SM4 类创建 ECB 模式加密器
     */
    private static SM4 createCipherEcb(String keyHex) {
        byte[] keyBytes = HexUtil.decodeHex(keyHex);
        return new SM4(Mode.ECB, Padding.PKCS5Padding, keyBytes);
    }

    private static void validateKey(String keyHex) {
        if (keyHex == null || keyHex.isEmpty()) {
            throw new IllegalArgumentException("SM4 密钥不能为空");
        }
        byte[] bytes = HexUtil.decodeHex(keyHex);
        if (bytes.length != KEY_SIZE) {
            throw new IllegalArgumentException(
                    String.format("SM4 密钥长度必须为 %d 字节，当前: %d 字节", KEY_SIZE, bytes.length)
            );
        }
    }

    private static void validateKeyAndIv(String keyHex, String ivHex) {
        validateKey(keyHex);
        validateKey(ivHex);
    }

    // ==================== 测试入口 ====================

    public static void main(String[] args) {
        // 生成生产环境密钥
        String key1 = Sm4Utils.generateRandomKey();
        String iv1 = Sm4Utils.generateRandomIv();


        System.out.println("========== SM4 工具类测试 ==========\n");

        String plaintext = "Hello, SM4 国密加密测试! 123456";
        System.out.println("原始数据: " + plaintext);

        // 测试 CBC 模式
        System.out.println("\n--- CBC 模式（推荐） ---");
        String encryptedCbc = encrypt(plaintext);
        System.out.println("加密结果: " + encryptedCbc);
        String decryptedCbc = decrypt(encryptedCbc);
        System.out.println("解密结果: " + decryptedCbc);
        System.out.println("验证通过: " + plaintext.equals(decryptedCbc));

        // 测试 ECB 模式
        System.out.println("\n--- ECB 模式（兼容） ---");
        String encryptedEcb = encryptEcb(plaintext);
        System.out.println("加密结果: " + encryptedEcb);
        String decryptedEcb = decryptEcb(encryptedEcb);
        System.out.println("解密结果: " + decryptedEcb);
        System.out.println("验证通过: " + plaintext.equals(decryptedEcb));

        // 测试自定义密钥
        System.out.println("\n--- 自定义密钥 ---");
        String keyHex = "0123456789abcdef0123456789abcdef";
        String ivHex = "fedcba9876543210fedcba9876543210";
        String encCustom = encryptCbc(plaintext, keyHex, ivHex);
        System.out.println("加密结果: " + encCustom);
        String decCustom = decryptCbc(encCustom, keyHex, ivHex);
        System.out.println("解密结果: " + decCustom);
        System.out.println("验证通过: " + plaintext.equals(decCustom));

        System.out.println("\n========== 测试完成 ==========");
    }
}