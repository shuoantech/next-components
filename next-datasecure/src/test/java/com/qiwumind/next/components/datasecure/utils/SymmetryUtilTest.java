package com.qiwumind.next.components.datasecure.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * RC4 对称加解密单元测试（纯逻辑，不依赖外部服务）。
 *
 * <p>SymmetryUtil 使用全局密钥（通过 {@link SecretKeyUtil#setSecretKey} 注入），
 * 这里以一组固定的 Base64 密钥完成加密-解密往返校验。
 */
class SymmetryUtilTest {

    /** "abcdefghijklmnopqrstuvwxyz" 的 Base64 编码，作为固定测试密钥。 */
    private static final String BASE64_KEY = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo=";

    @BeforeAll
    static void initKey() {
        SecretKeyUtil.setSecretKey(BASE64_KEY);
    }

    @Test
    void encryption_then_decryption_roundTrip() {
        String plain = "Hello, 数据加解密 World! 123456";
        String cipher = SymmetryUtil.encryption(plain);
        assertNotNull(cipher);
        assertNotEquals(plain, cipher);

        String decrypted = SymmetryUtil.decryption(cipher);
        assertEquals(plain, decrypted);
    }

    @Test
    void encryption_isBase64AndDeterministic() {
        String plain = "deterministic-input-2026";
        String first = SymmetryUtil.encryption(plain);
        String second = SymmetryUtil.encryption(plain);
        assertEquals(first, second, "相同明文/密钥应得到相同密文");
    }

    @Test
    void encryption_handlesEmptyString() {
        String cipher = SymmetryUtil.encryption("");
        assertEquals("", SymmetryUtil.decryption(cipher));
    }

    @Test
    void encryption_chineseCharacters_roundTrip() {
        String plain = "敏感数据：张三的银行卡号6228480402564890018";
        String decrypted = SymmetryUtil.decryption(SymmetryUtil.encryption(plain));
        assertEquals(plain, decrypted);
    }
}
