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

package com.qiwumind.next.components.datasecure.common.annotations;



import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类Sensitive.java的实现描述：格式为jsond的字段，含敏感信息.
 * <p>
 * 对于json特定的敏感信息格式化，如果json中某个或者多个字段的值需要脱敏，使用该注解
 * </p>
 *
 * @author wanghy 2017年3月28日 下午3:50:21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveJson {

    /**
     * json脱敏格式化方法.
     * <p>
     * web:
     * 
     * <pre>
     * 对于形如{"bankCardNo":"60059029302981203","mobileNo":"18982938240","amount":10000}这样的字符串，如果需要对其中的bankCardNo和mobileNo进行脱敏，格式化字符串应如下
     * {"bankCardNo":"CARD_NO","mobileNo":"PHONE_NO"}
     * rule值请参照：{@link SensitiveRulesEnum}
     * </pre>
     * </p>
     *
     * @return 格式化后字符串
     */
    String format() default "";

    /**
     * 是否忽略
     * <li>默认不忽略
     * 
     * @return
     */
    boolean ignore() default false;
}
