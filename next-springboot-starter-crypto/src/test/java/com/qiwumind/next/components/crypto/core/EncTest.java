package com.qiwumind.next.components.crypto.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * 配置项加密/解密单元测试（纯逻辑，不依赖外部服务/动态库）。
 *
 * <p>Enc 在静态初始化块中将前缀设为 {@code enc_test_}（DEPLOY_ENV 默认 test），
 * 因此加密结果以该前缀开头，解密时也需匹配该前缀。底层使用 next-common 的
 * AESUtil 完成 AES 对称加解密。
 */
class EncTest {

    private static final String PREFIX = "enc_test_";

    @Test
    void encryptData_returnsPrefixedCipher() throws Exception {
        String cipher = Enc.encryptData("my-secret-password");
        assertNotNull(cipher);
        assertTrue(cipher.startsWith(PREFIX), "密文应以 enc_test_ 前缀开头");
    }

    @Test
    void encryptData_then_decryptData_roundTrip() throws Exception {
        String plain = "数据库密码: Db@2026!abc";
        String cipher = Enc.encryptData(plain);
        String decrypted = Enc.decryptData(cipher);
        assertEquals(plain, decrypted);
    }

    @Test
    void decryptData_withoutPrefix_throws() {
        assertThrows(IllegalArgumentException.class, () -> Enc.decryptData("not-a-cipher"));
    }

    @Test
    void encryptData_null_throwsNpe() {
        assertThrows(NullPointerException.class, () -> Enc.encryptData(null));
    }

    @Test
    void decryptData_null_throwsNpe() {
        assertThrows(NullPointerException.class, () -> Enc.decryptData(null));
    }
}
