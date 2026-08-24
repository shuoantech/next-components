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



import com.qiwumind.next.components.datasecure.common.enums.SensitiveFieldTypeEnum;

/**
 * 脱敏字段处理器工厂类
 */
public class FieldHandlerFactory {

    /** 通用处理器 */
    private static final FieldHandler GENERAL_FIELD_HANDLER = new GeneralFieldHandler();

    /** json处理器 */
    private static final FieldHandler JSON_FIELD_HANDLER    = new JsonFieldHandler();

    /**
     * 获取字段处理器工厂方法
     * 
     * @param fieldTypeEnum
     * @return
     */
    public static FieldHandler getFieldHandler(SensitiveFieldTypeEnum fieldTypeEnum) {

        switch (fieldTypeEnum) {
            case GENERAL:
                return GENERAL_FIELD_HANDLER;
            case JSON:
                return JSON_FIELD_HANDLER;
            case NONE:
                return null;
            default:
                return null;
        }

    }
}
