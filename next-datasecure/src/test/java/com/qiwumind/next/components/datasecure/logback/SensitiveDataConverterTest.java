package com.qiwumind.next.components.datasecure.logback;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;

/**
 * SensitiveDataConverter 回归测试：
 * URL 路径中的敏感关键字（如 /admin-api/system/tenant/get-id-by-name 中的 name）
 * 被误判为 key 时，其后面可能没有任何 key/value 分隔符，此前会导致
 * StringIndexOutOfBoundsException: Index N out of bounds for length N。
 */
class SensitiveDataConverterTest {

    private static Set<SensitiveDataRule> rules() {
        Set<SensitiveDataRule> set = new LinkedHashSet<>();
        SensitiveDataRule name = new SensitiveDataRule();
        name.setFieldName("name");
        name.setFormat(SensitiveRulesEnum.NAME);
        set.add(name);
        return set;
    }

    @Test
    void urlKeyWithoutSeparator_doesNotThrow() {
        // 复现线上越界场景：消息长度 78，name 出现在 URL 末尾，其后无 '=' / ':'
        String message = "[afterCompletion][完成请求 URL(/admin-api/system/tenant/get-id-by-name) 耗时(136 ms)]";
        String result = assertDoesNotThrow(
                () -> SensitiveDataConverter.filterMessage("true", rules(), message));
        assertEquals(message, result);
    }

    @Test
    void urlKeyWithSeparator_masksValue() {
        // preHandle 场景：URL 中的 name 扫描会命中后面参数里的 '='，value 应被脱敏
        String message = "[preHandle][开始请求 URL(/admin-api/system/tenant/get-id-by-name) 参数({name=qiwumind})]";
        String result = assertDoesNotThrow(
                () -> SensitiveDataConverter.filterMessage("true", rules(), message));
        assertEquals(
                "[preHandle][开始请求 URL(/admin-api/system/tenant/get-id-by-name) 参数({name=q**})]",
                result);
    }

    @Test
    void keyAtMessageEnd_doesNotThrow() {
        // key 恰好位于消息末尾（valueStart 起点 = msg.length()）
        String message = "tenant name";
        String result = assertDoesNotThrow(
                () -> SensitiveDataConverter.filterMessage("true", rules(), message));
        assertEquals(message, result);
    }

    @Test
    void separatorAtMessageEnd_doesNotThrow() {
        // 分隔符恰好位于消息末尾（valueStart 在跳过分隔符后 = msg.length()）
        String message = "name=";
        String result = assertDoesNotThrow(
                () -> SensitiveDataConverter.filterMessage("true", rules(), message));
        assertEquals("name=", result);
    }
}
