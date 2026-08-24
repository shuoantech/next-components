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

package com.qiwumind.next.components.file.core.xml;



import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.qiwumind.next.components.file.core.enums.*;
import com.qiwumind.next.components.file.core.valueobject.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 类StringFormat.java的实现描述
 *
 * @author liks 2019年4月19日 下午2:04:26
 */
public class StringFormat {
    /**
     * @param formatEnum
     * @return
     */
    public static Map<String, String> read(final String lineValue, final FormatEnum formatEnum, final String spacer,
                                           final List<Field> fields) {
        final Map<String, String> map = Maps.newHashMap();
        if (formatEnum == FormatEnum.UN_FIXED) {
            Preconditions.checkArgument(StringUtils.isNotBlank(spacer), "分隔符不能为空");
            final List<String> list = Splitter.on(spacer).splitToList(lineValue);
            int i = 0;
            for (final Field fi : fields) {
                map.put(fi.getProperty(), list.get(i));
                i++;
            }
            return map;
        }
        int index = 0;
        for (final Field fi : fields) {
            Preconditions.checkArgument(fi.getLength() != 0, "定长格式 长度不能为空");
            final String value = lineValue.substring(index, index + fi.getLength());
            final String result = read(value, fi.getFieldEnum(), fi.getLength(), fi.getFill(), fi.getFilltext(),
                    fi.getPoint());
            map.put(fi.getProperty(), result);
            index = index + fi.getLength();
        }

        return map;

    }

    /**
     * @param value
     * @param length
     * @return
     */
    private static String read(final String value, final FieldEnum fieldEnum, final int length,
                               final FillEnum fillEnum, final FillTextEnum fillTextEnum, final PointEnum point) {
        String result = "";
        switch (fillTextEnum) {
            case NO:
            case SPACE:
                result = value.trim();
                break;
            case NUMBER_0:
                result = read(value.trim(), fieldEnum, length, fillEnum, point, fillTextEnum.getName());
                break;
            default:
                break;
        }
        return result;

    }

    private static String read(String value, final FieldEnum fieldEnum, final int length, final FillEnum fillEnum,
                               final PointEnum point, final String fillText) {
        switch (fieldEnum) {
            case DECIMAL:
                if (point == PointEnum.POINT_Y) {
                    value = new BigDecimal(value).toPlainString();
                } else if (point == PointEnum.POINT_N) {
                    if (fillEnum == FillEnum.LEFT) {
                        //默认取位小数
                        value = new BigDecimal(value).movePointLeft(2).toPlainString();
                    } else {
                        //数字金额右面补充0  待定
                    }
                }
                break;
            case NUMBER:
                if (fillEnum == FillEnum.LEFT) {
                    value = new BigDecimal(value).toPlainString();
                } else {
                    //数字金额右面补充0  待定
                }
                break;
            case STRING:
                //字符串补充0  待定
                break;
            default:
                break;
        }

        return value;

    }

    /**
     * @param value
     * @param formatEnum
     * @return
     */
    public static String format(String value, final FormatEnum formatEnum, final Field field) {
        final PointEnum point = field.getPoint();
        if (point == PointEnum.POINT_N) {
            value = value.replace(".", "");
        }
        if (formatEnum == FormatEnum.FIXED) {
            final int length = field.getLength();
            final FillEnum fillEnum = field.getFill();
            final FillTextEnum fillTextEnum = field.getFilltext();
            final FieldEnum fieldEnum = field.getFieldEnum();
            Preconditions.checkArgument(length != 0, "定长格式 长度不能为空");
            return format(fieldEnum, value, fillEnum, length, fillTextEnum);
        }
        return value;

    }

    /**
     * @param value
     * @param fillEnum
     * @param length
     * @param fillTextEnum
     * @return
     */
    private static String format(final FieldEnum fieldEnum, final String value, final FillEnum fillEnum,
                                 final int length, final FillTextEnum fillTextEnum) {
        String result = "";
        Preconditions.checkArgument(value.length() <= length, "取值长度不能大于约定长度");
        if (length == value.length()) {
            return value;
        }
        final int fillLength = length - value.length();
        if (fieldEnum == FieldEnum.NUMBER) {

        } else if (fieldEnum == FieldEnum.STRING) {

        } else if (fieldEnum == FieldEnum.DECIMAL) {

        }
        switch (fillEnum) {
            case LEFT:
                result = fillText(fillTextEnum, fillLength) + value;
                break;
            case RIGHT:
                result = value + fillText(fillTextEnum, fillLength);
                break;
            case NO:
                result = value;
                break;
            default:
                result = value;
                break;
        }
        return result;

    }

    /**
     * @param fillTextEnum
     * @param fillLength
     * @return
     */
    public static String fillText(final FillTextEnum fillTextEnum, final int fillLength) {
        String fi = "";
        switch (fillTextEnum) {
            case NO:
                fi = "";
                break;
            case NUMBER_0:
                fi = "0";
                break;
            case SPACE:
                fi = " ";
                break;
            default:
                fi = "";
                break;
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fillLength; i++) {
            builder.append(fi);
        }
        return builder.toString();

    }

}
