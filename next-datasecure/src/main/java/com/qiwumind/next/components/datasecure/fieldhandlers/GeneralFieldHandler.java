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


import com.qiwumind.next.components.datasecure.common.DataSecureConstants;
import com.qiwumind.next.components.datasecure.common.annotations.Sensitive;
import com.qiwumind.next.components.datasecure.utils.SensitiveProcessUtils;
import com.qiwumind.next.components.common.util.json.JacksonUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用字段脱敏处理器
 */
public class GeneralFieldHandler implements FieldHandler {

    @Override
    public boolean ignore(Field field) {
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        return sensitive != null && sensitive.ignore();
    }

    @Override
    public Object getValue(Field field, Object fieldValue) {
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null || fieldValue == null) {
            return fieldValue;
        }

        if (fieldValue instanceof List) {
            String jsonStr = StringUtils.EMPTY;
            for (Object object : (List<?>) fieldValue) {
                if (object instanceof String) {
                    jsonStr = JacksonUtils.toJsonString(fieldValue);
                    break;
                }
            }
            if (!StringUtils.equals(jsonStr, StringUtils.EMPTY)) {
                Matcher matcher = Pattern.compile(DataSecureConstants.REGEX_LIST).matcher(jsonStr);
                while (matcher.find()) {
                    jsonStr = StringUtils.replace(jsonStr, matcher.group(1),
                            SensitiveProcessUtils.shield(sensitive.format(), matcher.group(1)));
                }
                return jsonStr;
            }
        }

        String value = String.valueOf(fieldValue);
        return SensitiveProcessUtils.shield(sensitive.format(), value);
    }
}
