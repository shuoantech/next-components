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

package com.qiwumind.next.components.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具类（基于 Jackson ObjectMapper）
 * 核心原则：全局单例 + 实例重用 + 性能优化
 *
 * @author liks 2018年8月8日 下午2:12:19
 */
public class BaseDTO implements Serializable, Cloneable {

    /**
     * 全局单例 ObjectMapper（线程安全）
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 常用 TypeReference 缓存（避免重复创建）
     */
    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF =
            new TypeReference<Map<String, Object>>() {
            };
    public static final TypeReference<Map<String, String>> MAP_STRING_TYPE_REF =
            new TypeReference<Map<String, String>>() {
            };
    public static final TypeReference<List<String>> STRING_LIST_TYPE_REF =
            new TypeReference<List<String>>() {
            };
    public static final TypeReference<List<Map<String, Object>>> MAP_LIST_TYPE_REF =
            new TypeReference<List<Map<String, Object>>>() {
            };

    /**
     * 静态初始化块：配置 ObjectMapper
     */
    static {
        // 1. 基础配置
        // 忽略未知属性（防止 JSON 中有 Java 对象不存在的字段时报错）
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许字段名不用引号（非标准 JSON，但某些场景有用）
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号（非标准 JSON）
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 2. 序列化配置
        // 禁用美化输出（节省空间，提高性能）
        MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
        // 日期类型不输出为时间戳，输出为字符串
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 空对象不序列化（减少输出体积）
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 4. 注册 Java 8 时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 配置 LocalDateTime 的序列化格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        MAPPER.registerModule(javaTimeModule);

        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        MAPPER.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 5. 启用默认类型缓存（提高反射性能）
//        MAPPER.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
    }

    /**
     * 获取 ObjectMapper 实例（用于特殊场景）
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    // ==================== 序列化方法 ====================
    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败: " + obj.getClass().getName(), e);
        }
    }

    /**
     * 对象转字节数组（用于网络传输）
     */
    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转字节数组失败: " + obj.getClass().getName(), e);
        }
    }

    // ==================== 反序列化方法 ====================

    /**
     * JSON 字符串转对象（Class 方式）
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败: " + json + " -> " + clazz.getName(), e);
        }
    }

    /**
     * JSON 字符串转对象（TypeReference 方式，用于泛型）
     * 示例：JsonUtils.fromJson(json, new TypeReference<List<User>>() {});
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败: " + json, e);
        }
    }

    /**
     * 字节数组转对象
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException("字节数组转对象失败: " + clazz.getName(), e);
        }
    }

    /**
     * 输入流转对象（用于大文件流式读取）
     */
    public static <T> T fromStream(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            return null;
        }
        try {
            return MAPPER.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new RuntimeException("输入流转对象失败: " + clazz.getName(), e);
        }
    }

    // ==================== Map 转换专用方法 ====================

    /**
     * JSON 转 Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, MAP_TYPE_REF);
    }

    /**
     * JSON 转 Map<String, String>
     */
    public static Map<String, String> toStringMap(String json) {
        return fromJson(json, MAP_STRING_TYPE_REF);
    }

    /**
     * 对象转 Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.convertValue(obj, MAP_TYPE_REF);
    }

    /**
     * Map 转对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return MAPPER.convertValue(map, clazz);
    }

    // ==================== List 转换专用方法 ====================

    /**
     * JSON 转 List<String>
     */
    public static List<String> toStringList(String json) {
        return fromJson(json, STRING_LIST_TYPE_REF);
    }

    /**
     * JSON 转 List<Map<String, Object>>
     */
    public static List<Map<String, Object>> toMapList(String json) {
        return fromJson(json, MAP_LIST_TYPE_REF);
    }

    /**
     * JSON 转 List<具体类型>
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);
            return MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转List失败: " + json, e);
        }
    }

    // ==================== JSON 节点操作 ====================

    /**
     * 创建空的 ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * 创建空的 ArrayNode
     */
    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }

    /**
     * 解析 JSON 为 JsonNode（用于动态操作）
     */
    public static JsonNode parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }

    /**
     * 从 JsonNode 中提取值
     */
    public static <T> T nodeToValue(JsonNode node, Class<T> clazz) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonNode转对象失败", e);
        }
    }

    /**
     * 对象转 JsonNode
     */
    public static JsonNode toJsonNode(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.valueToTree(obj);
    }

    // ==================== 深拷贝 ====================

    /**
     * 深拷贝对象
     */
    public static <T> T deepCopy(T obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        String json = toJson(obj);
        return fromJson(json, clazz);
    }

    /**
     * 深拷贝对象（泛型版本）
     */
    public static <T> T deepCopy(T obj, TypeReference<T> typeRef) {
        if (obj == null) {
            return null;
        }
        String json = toJson(obj);
        return fromJson(json, typeRef);
    }

    // ==================== 验证方法 ====================

    /**
     * 验证 JSON 字符串是否合法
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 验证 JSON 是否可以转换为指定类型
     */
    public static <T> boolean isConvertible(String json, Class<T> clazz) {
        try {
            fromJson(json, clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void main(String[] args) {
        String json="[{\"fieldName\":\"customerName\",\"format\":\"NAME\"},\n" +
                "\t\t{\"fieldName\":\"withdrawCardNo\",\"format\":\"CARD_NO\"},\n" +
                "\t\t{\"fieldName\":\"certNo\",\"format\":\"CERTI_NO\"},\n" +
                "\t\t{\"fieldName\":\"phoneNo\",\"format\":\"PHONE_NO\"},\n" +
                "\t\t{\"fieldName\":\"payerName\",\"format\":\"NAME\"},\n" +
                "\t\t{\"fieldName\":\"payerCardNo\",\"format\":\"CARD_NO\"},\n" +
                "\t\t{\"fieldName\":\"payerPhoneNo\",\"format\":\"PHONE_NO\"},\n" +
                "\t\t{\"fieldName\":\"contact1_phone\",\"format\":\"PHONE_NO\"},\n" +
                "\t\t{\"fieldName\":\"bank_card_no\",\"format\":\"CARD_NO\"},\n" +
                "\t\t{\"fieldName\":\"card_phone\",\"format\":\"PHONE_NO\"},\n" +
                "\t\t{\"fieldName\":\"cardName\",\"format\":\"NAME\"}\n" +
                "\t\t]";
        List sensitiveDataRules = BaseDTO.fromJson(json,
                new TypeReference<List>() {
                });

        System.out.println(sensitiveDataRules);
    }
}