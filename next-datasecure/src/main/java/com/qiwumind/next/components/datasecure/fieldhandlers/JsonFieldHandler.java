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

package com.qiwumind.next.components.datasecure.fieldhandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.qiwumind.next.components.datasecure.common.annotations.SensitiveJson;
import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;
import com.qiwumind.next.components.datasecure.utils.SensitiveProcessUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON字段脱敏处理器
 * 处理被 {@link SensitiveJson} 注解标记的JSON类型字段，根据注解中定义的规则对JSON内指定字段进行脱敏
 *
 * @author liks (modified)
 */
public class JsonFieldHandler implements FieldHandler {

    /**
     * 缓存已解析的格式配置，避免重复解析JSON
     */
    private final Map<String, Map<String, SensitiveRulesEnum>> formatPatternCache = new ConcurrentHashMap<>();
    /**
     * 初始化Fastjson2全局日期格式配置（只执行一次）
     */
    static {
        JSON.configWriterDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public boolean ignore(Field field) {
        SensitiveJson sensitiveJson = field.getAnnotation(SensitiveJson.class);
        return sensitiveJson != null && sensitiveJson.ignore();
    }

    @Override
    public Object getValue(Field field, Object fieldValue) {
        SensitiveJson sensitiveJson = field.getAnnotation(SensitiveJson.class);
        if (sensitiveJson == null || Objects.isNull(fieldValue)) {
            return fieldValue;
        }

        // 1. 解析并校验格式配置
        String formatPattern = sensitiveJson.format();
        if (StringUtils.isBlank(formatPattern)) {
            return fieldValue;
        }

        Map<String, SensitiveRulesEnum> fieldsRuleMap = getOrParseFormatConfig(formatPattern);
        if (MapUtils.isEmpty(fieldsRuleMap)) {
            return fieldValue;
        }

        // 2. 将字段值转换为JSON字符串
        String jsonVal = convertToJsonString(fieldValue);

        // 3. 执行脱敏
        return SensitiveProcessUtils.jsonShield(jsonVal, fieldsRuleMap);
    }

    /**
     * 从缓存获取或解析格式配置
     */
    private Map<String, SensitiveRulesEnum> getOrParseFormatConfig(String formatPattern) {
        return formatPatternCache.computeIfAbsent(formatPattern, pattern -> {
            JSONObject formatJson = JSONObject.parseObject(pattern);
            if (MapUtils.isEmpty(formatJson)) {
                return new HashMap<>();
            }
            Map<String, SensitiveRulesEnum> ruleMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : formatJson.entrySet()) {
                String key = entry.getKey();
                String ruleName = String.valueOf(entry.getValue());
                SensitiveRulesEnum rule = SensitiveRulesEnum.getSensitiveRule(ruleName);
                if (rule != null) {
                    ruleMap.put(key, rule);
                }
            }
            return ruleMap;
        });
    }

    /**
     * 将字段值转换为JSON字符串
     * 注：全局日期格式已在 {@link  } 中配置
     */
    private String convertToJsonString(Object fieldValue) {
        if (fieldValue instanceof String) {
            return (String) fieldValue;
        }
        return JSON.toJSONString(fieldValue);
    }
}