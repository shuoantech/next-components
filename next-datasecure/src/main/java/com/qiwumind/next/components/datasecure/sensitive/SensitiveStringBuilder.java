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

package com.qiwumind.next.components.datasecure.sensitive;



import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson2.JSON;
import com.qiwumind.next.components.datasecure.common.DataSecureConstants;
import com.qiwumind.next.components.datasecure.common.annotations.Sensitive;
import com.qiwumind.next.components.datasecure.common.annotations.SensitiveJson;
import com.qiwumind.next.components.datasecure.common.enums.SensitiveFieldTypeEnum;
import com.qiwumind.next.components.datasecure.fieldhandlers.FieldHandler;
import com.qiwumind.next.components.datasecure.fieldhandlers.FieldHandlerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 类SensitiveStringBuilder.java的实现描述：转换敏感字符串构建器
 */
@Slf4j
public class SensitiveStringBuilder {

    /**
     * 获取当前字段的注解类型
     * 
     * @param field
     * @return
     */
    private static SensitiveFieldTypeEnum getFiledType(Field field) {
        if (field.isAnnotationPresent(Sensitive.class)) {
            return SensitiveFieldTypeEnum.GENERAL;
        }
        if (field.isAnnotationPresent(SensitiveJson.class)) {
            return SensitiveFieldTypeEnum.JSON;
        }
        return SensitiveFieldTypeEnum.NONE;
    }

    /**
     * @param object
     * @return
     */
    public static String reflectionToString(Object object) {
        try {
            ToStringBuilder toStringBuilder = (new ReflectionToStringBuilder(object, JToStringStyle.JSON_STYLE) {
                /**
                 * @see org.apache.commons.lang3.builder.ReflectionToStringBuilder#accept(java.lang.reflect.Field)
                 */
                @Override
                protected boolean accept(Field field) {
                    try {
                        Object fieldValue = super.getValue(field);
                        if (fieldValue == null)
                            return false;
                        if ((fieldValue instanceof String) && StringUtils.isBlank(String.valueOf(fieldValue)))
                            return false;
                        SensitiveFieldTypeEnum fieldType = getFiledType(field);
                        FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                        if (fieldHandler == null) {
                            return super.accept(field);
                        }
                        if (fieldHandler.ignore(field)) {
                            return false;
                        } else {
                            return super.accept(field);
                        }
                    } catch (Exception e) {
                        log.info("日志脱敏SensitiveStringBuilder#accept异常", e);
                    }
                    return false;
                }

                /**
                 * @see org.apache.commons.lang3.builder.ReflectionToStringBuilder#getValue(java.lang.reflect.Field)
                 */
                @Override
                protected Object getValue(Field field) {
                    Object fieldValue = null;
                    try {
                        fieldValue = super.getValue(field);
                        SensitiveFieldTypeEnum fieldType = getFiledType(field);
                        FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                        if (fieldHandler == null) {
                            if (fieldValue instanceof String || field.getType().isEnum()) {
                                return fieldValue;
                            } else if (fieldValue instanceof Date) {//日期做特殊格式化处理
                                return DataSecureConstants.DATE_FORMAT.get().format(fieldValue);
                            } else {
                                try {
                                    if (fieldValue.toString().startsWith(DataSecureConstants.LEFT_BRACE) || fieldValue
                                            .toString().startsWith(String.valueOf(DataSecureConstants.LEFT_BRACKET))) {
                                        return fieldValue.toString();
                                    } else {
                                        String format = "millis";
                                        return JSON.toJSONString(fieldValue, format);
                                    }
                                } catch (Exception e) {
                                    log.info("日志脱敏JSON.toJSONString异常", e);
                                    return fieldValue;
                                }
                            }
                        }
                        return fieldHandler.getValue(field, fieldValue);
                    } catch (Exception e) {
                        log.info("日志脱敏SensitiveStringBuilder#getValue异常", e);
                    }
                    return fieldValue;
                }
            });
            return toStringBuilder.toString();
        } catch (Exception e) {
            log.info("日志脱敏异常", e);
            return StringUtils.EMPTY;
        }
    }
}
