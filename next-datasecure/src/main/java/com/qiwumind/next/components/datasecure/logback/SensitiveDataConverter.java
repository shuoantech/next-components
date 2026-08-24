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

package com.qiwumind.next.components.datasecure.logback;



import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.qiwumind.next.components.datasecure.utils.SensitiveProcessUtils;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 敏感信息数据转换器
 *
 * @author chenyao
 * @since 2019年1月21日 下午6:03:20
 */
public class SensitiveDataConverter extends MessageConverter {
    
    @Override
    public String convert(ILoggingEvent event) {
        //基本脱敏key构造
        SensitiveDataKeysBuilder.checkAndSetSensitiveDataRules(this.getContext());

        // 获取原始正常日志
        String formattedMessage = event.getFormattedMessage();

        // 获取脱敏后的日志
        return filterMessage(SensitiveDataKeysBuilder.getSensitiveDataAllowRun(),
                SensitiveDataKeysBuilder.getSensitiveDataRules(), formattedMessage);
    }

    /**
     * 处理日志字符串，返回脱敏后的字符串
     *
     * @param allowRun 日志脱敏开关
     * @param sensitiveDataRules 日志脱敏关键字
     * @param message 原始日志字符串
     * @return 脱敏后的字符串
     */
    public static String filterMessage(String allowRun, Set<SensitiveDataRule> sensitiveDataRules, String message) {
        if (!"true".equals(allowRun) || sensitiveDataRules == null || sensitiveDataRules.isEmpty()
                || StringUtils.isEmpty(message)) {
            return message;
        }

        StringBuilder temp = new StringBuilder(message);
        for (SensitiveDataRule sensitiveDataRule : sensitiveDataRules) {
            String key = sensitiveDataRule.getFieldName();
            int index = -1;
            do {
                index = temp.indexOf(key, index + 1);
                if (index != -1) {
                    // 判断key是否为单词字符
                    if (isWordChar(temp, key, index)) {
                        continue;
                    }
                    // 寻找值的开始位置
                    int valueStart = getValueStartIndex(temp, index + key.length());
                    // 查找值的结束位置（逗号，分号）
                    int valueEnd = getValueEndIndex(temp, valueStart);

                    // 对获取的值进行脱敏
                    String value = temp.substring(valueStart, valueEnd);
                    String replace = SensitiveProcessUtils.shield(sensitiveDataRule.getFormat(), value);
                    temp.replace(valueStart, valueEnd, replace);
                }
            } while (index != -1);
        }
        return temp.toString();
    }

    /**
     * 判断从 {@code msg} 中获取的key值是否为单词，
     *
     * @param msg 完整字符串内容
     * @param keyword 检查的关键词
     * @param index 为key在msg中的索引值
     * @return true/false
     */
    private static boolean isWordChar(CharSequence msg, String keyword, int index) {
        // 必须确定key是一个单词
        if (index != 0) {
            // 判断key前面一个字符
            char pre = msg.charAt(index - 1);
            boolean flag = (pre > 47 && pre < 58) || (pre > 64 && pre < 91) || (pre > 96 && pre < 123);
            if (flag) {
                // 0-9 , A-Z, a-z
                return true;
            }
        }
        // 判断key后面一个字符，先检查边界
        int nextIndex = index + keyword.length();
        if (nextIndex >= msg.length()) {
            return false;
        }
        char next = msg.charAt(nextIndex);
        if ((next > 47 && next < 58) || (next > 64 && next < 91) || (next > 96 && next < 123)) {
            // 0-9 , A-Z, a-z
            return true;
        }
        return false;
    }

    /**
     * 获取value值的开始位置
     *
     * @param msg 要查找的字符串
     * @param valueStart 查找的开始位置
     * @return value值的开始位置
     */
    private static int getValueStartIndex(CharSequence msg, int valueStart) {
        // 寻找值的开始位置
        do {
            char c = msg.charAt(valueStart);
            if (c == ':' || c == '=' || c == '：') {
                // key与 value的分隔符
                valueStart++;
                c = msg.charAt(valueStart);
                if (c == '"' || c == ' ') {
                    valueStart++;
                    c = msg.charAt(valueStart);
                    if (c == '"' || c == ' ') {
                        valueStart++;
                    }
                }
                // 找到值的开始位置
                break;
            } else {
                valueStart++;
            }
        } while (true);
        return valueStart;
    }

    /**
     * 获取value值的结束位置
     *
     * @param msg 要查找的字符串
     * @param valueEnd 查找的开始位置
     * @return value值的结束位置
     */
    private static int getValueEndIndex(CharSequence msg, int valueEnd) {
        do {
            if (valueEnd == msg.length()) {
                break;
            }
            char c = msg.charAt(valueEnd);

            if (c == '"') {
                // 引号时，判断下一个值是结束，分号还是逗号决定是否为值的结束
                if (valueEnd + 1 == msg.length()) {
                    break;
                }
                char next = msg.charAt(valueEnd + 1);
                if (next == ';' || next == ',' || next == '}' || next == ' ') {
                    break;
                } else {
                    valueEnd++;
                }
            } else if (c == ';' || c == ',' || c == '}' || c == '\n' || c == '\t') {
                break;
            } else {
                valueEnd++;
            }

        } while (true);
        return valueEnd;
    }
}
