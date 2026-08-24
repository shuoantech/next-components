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

package com.qiwumind.next.components.groovy.annotation;



import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;

/**
 * 属性值必须和指定注解里的值相同
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class OnExistingPropertyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnExistingProperty.class.getName());
        if (annotationAttributes == null) {
            return ConditionOutcome.match("no @ConditionalOnExistingProperty, return match.");
        }
        // 获取注解属性值
        String property = (String) annotationAttributes.get("property");
        String value = (String) annotationAttributes.get("value");
        String configValue = context.getEnvironment().getProperty(property);
        // 注解里的value和配置文件里的value相同，则认为匹配
        if (value.equals(configValue)) {
            return ConditionOutcome.match(
                    ConditionMessage.forCondition(ConditionalOnExistingProperty.class)
                            .because("property [" + property + "] value " + configValue + "matched."));
        }
        return ConditionOutcome.noMatch(
                ConditionMessage.forCondition(ConditionalOnExistingProperty.class)
                        .because("value and configValue is not equals, not matched.")
        );
    }
}
