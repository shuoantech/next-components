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

package com.qiwumind.next.components.file.core.enums;



import lombok.Getter;

/**
 * 类型建议采用默认值，如果特殊需要则配置即可，注意配置关联性
 * 
 * @author liks 2019年4月17日 上午11:51:26
 */
@Getter
public enum FieldEnum {
    /**
     * 字符串类型-- 定长格式时，长度不够默认右面补充空格
     */
    STRING("string", "字符串类型,含数字0-9字符A-Za-z +汉字 ，不含符号"),
    /**
     * 金额类型-- 定长格式时，长度不够默认填充的时候，左面补0，小数点去除
     */
    DECIMAL("decimal", "金额类型"),
    /**
     * 数字类型 --定长格式时，长度不够 默认填充的时候，左面补0
     */
    NUMBER("number", "数字类型，仅仅包含0-9");
    private String name;
    private String value;

    private FieldEnum(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 尝试转换枚举，如果没有则默认string
     * 
     * @param name
     * @return
     */
    public static FieldEnum tryParse(final String name) {
        final FieldEnum[] enums = values();
        for (final FieldEnum item : enums) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return STRING;
    }

}
