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



import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * 异常堆栈敏感信息数据转换器
 *
 * @author chenyao
 * @since 2019年5月14日 上午11:49:42
 */
@Setter
@Getter
public class SensitiveDataThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = super.convert(event);
        //基本脱敏key构造
        SensitiveDataKeysBuilder.checkAndSetSensitiveDataRules(this.getContext());
        // 获取脱敏后的日志
        return SensitiveDataConverter.filterMessage(SensitiveDataKeysBuilder.getSensitiveDataAllowRun(),
                SensitiveDataKeysBuilder.getSensitiveDataRules(), formattedMessage);
    }
}
