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



import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

/**
 * 用于支持输出异常堆栈的时候，也能正常过滤敏感信息
 *
 * @author chenyao
 * @since 2026年8月
 * @see ch.qos.logback.classic.encoder.PatternLayoutEncoder
 */
public class SensitiveDataPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    @Override
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(this.getPattern());
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.setPostCompileProcessor(new EnsureExceptionHandling() {
            @Override
            public void process(Context context, Converter<ILoggingEvent> head) {
                if (head == null) {
                    // this should never happen
                    throw new IllegalArgumentException("cannot process empty chain");
                }
                if (!this.chainHandlesThrowable(head)) {
                    Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
                    Converter<ILoggingEvent> exConverter = null;
                    LoggerContext loggerContext = (LoggerContext) context;
                    if (loggerContext.isPackagingDataEnabled()) {
                        exConverter = new ExtendedThrowableProxyConverter();
                    } else {
                        exConverter = new SensitiveDataThrowableProxyConverter();
                    }
                    tail.setNext(exConverter);
                }
            }
        });
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
}
