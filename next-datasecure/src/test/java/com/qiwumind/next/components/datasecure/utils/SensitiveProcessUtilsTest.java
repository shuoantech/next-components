package com.qiwumind.next.components.datasecure.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;

/**
 * 敏感信息脱敏工具单元测试（纯逻辑，不依赖外部服务）。
 */
class SensitiveProcessUtilsTest {

    @Test
    void shield_phone_masksMiddle() {
        String phone = "13800138000";
        String result = SensitiveProcessUtils.shield(SensitiveRulesEnum.PHONE_NO, phone);
        assertEquals("138******00", result);
    }

    @Test
    void shield_email_masksLocalPart() {
        String email = "zhangsan@example.com";
        String result = SensitiveProcessUtils.shield(SensitiveRulesEnum.EMAIL, email);
        assertTrue(result.endsWith("@example.com"));
        assertFalse(result.startsWith("zhangsan"));
    }

    @Test
    void shield_blankReturnsOriginal() {
        assertEquals("", SensitiveProcessUtils.shield(SensitiveRulesEnum.PHONE_NO, ""));
    }

    @Test
    void shield_hash_rule_returnsMd5() {
        String id = "1234567890";
        String result = SensitiveProcessUtils.shield(SensitiveRulesEnum.HASH, id);
        // MD5 hex 长度为 32
        assertEquals(32, result.length());
    }

    @Test
    void jsonShield_masksConfiguredFields() {
        String json = "{\"name\":\"张三\",\"mobile\":\"13912345678\",\"age\":30}";
        Map<String, SensitiveRulesEnum> fields = new HashMap<>();
        fields.put("mobile", SensitiveRulesEnum.PHONE_NO);
        String result = SensitiveProcessUtils.jsonShield(json, fields);
        assertFalse(result.contains("13912345678"));
        assertTrue(result.contains("139******78"));
        assertTrue(result.contains("\"name\":\"张三\""));
    }

    @Test
    void dataShield_masksKeyValuePair() {
        String src = "username=admin&token=abcdef123456&code=ok";
        Map<String, SensitiveRulesEnum> fields = new HashMap<>();
        fields.put("token", SensitiveRulesEnum.CARD_NO);
        String result = SensitiveProcessUtils.dataShield(src, fields);
        assertFalse(result.contains("abcdef123456"));
        assertTrue(result.contains("token="));
    }
}
