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

package com.qiwumind.next.components.desensitize.core.slider.handler;

import com.qiwumind.next.components.common.util.spring.SpringExpressionUtils;
import com.qiwumind.next.components.desensitize.core.base.handler.DesensitizationHandler;

import java.lang.annotation.Annotation;

/**
 * 滑动脱敏处理器抽象类，已实现通用的方法
 * @author qiwumind gaibu
 */
public abstract class AbstractSliderDesensitizationHandler<T extends Annotation>
        implements DesensitizationHandler<T> {

    @Override
    public String desensitize(String origin, T annotation) {
        // 1. 判断是否禁用脱敏
        Object disable = SpringExpressionUtils.parseExpression(getDisable(annotation));
        if (Boolean.TRUE.equals(disable)) {
            return origin;
        }

        // 2. 执行脱敏
        int prefixKeep = getPrefixKeep(annotation);
        int suffixKeep = getSuffixKeep(annotation);
        String replacer = getReplacer(annotation);
        int length = origin.length();
        int interval = length - prefixKeep - suffixKeep;

        // 情况一：原始字符串长度小于等于前后缀保留字符串长度，则原始字符串全部替换
        if (interval <= 0) {
            return buildReplacerByLength(replacer, length);
        }

        // 情况二：原始字符串长度大于前后缀保留字符串长度，则替换中间字符串
        return origin.substring(0, prefixKeep) +
                buildReplacerByLength(replacer, interval) +
                origin.substring(prefixKeep + interval);
    }

    /**
     * 根据长度循环构建替换符
     * @param replacer 替换符
     * @param length   长度
     * @return 构建后的替换符
     */
    private String buildReplacerByLength(String replacer, int length) {
        return replacer.repeat(length);
    }

    /**
     * 前缀保留长度
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    abstract Integer getPrefixKeep(T annotation);

    /**
     * 后缀保留长度
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    abstract Integer getSuffixKeep(T annotation);

    /**
     * 替换符
     * @param annotation 注解信息
     * @return 替换符
     */
    abstract String getReplacer(T annotation);

}
