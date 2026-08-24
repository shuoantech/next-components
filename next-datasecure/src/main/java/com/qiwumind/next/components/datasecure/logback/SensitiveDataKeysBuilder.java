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



import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiwumind.next.components.common.dto.BaseDTO;

import ch.qos.logback.core.Context;

/**
 * 敏感信息关键字构造
 */
public class SensitiveDataKeysBuilder {

    /**
     * 脱敏keys集合定义
     */
    protected static String[]               keys = { "sensitiveDataKeys", "sensitiveDataKeys1", "sensitiveDataKeys2",
            "sensitiveDataKeys3" };

    /**
     * 日志脱敏开关 默认为打开
     */
    protected static String                 sensitiveDataAllowRun;

    /**
     * 日志脱敏关键字集合set
     */
    protected static Set<SensitiveDataRule> sensitiveDataRules;

    /**
     * 检查开关、构造脱敏关键字集合
     * 
     * @param context
     */
    public static void checkAndSetSensitiveDataRules(Context context) {
        //不用每次进来都操作
        if (StringUtils.isEmpty(sensitiveDataAllowRun)) {
            String allowRun = context.getProperty("sensitiveDataAllowRun");
            setSensitiveDataAllowRun(StringUtils.defaultIfBlank(allowRun, "true"));
            sensitiveDataRules = getSensitiveDataRules(context);
        }
    }

    /**
     * 构造敏感信息脱敏字段集合
     * 
     * @param context
     * @return
     */
    private static Set<SensitiveDataRule> getSensitiveDataRules(Context context) {
        Set<SensitiveDataRule> sensitiveDataRulesInit = new LinkedHashSet<SensitiveDataRule>();
        for (String key : keys) {
            String dataKeys = context.getProperty(key);

            List<SensitiveDataRule> sensitiveDataRules = BaseDTO.fromJson(dataKeys,
                    new TypeReference<List<SensitiveDataRule>>() {
                    });
            if (null != sensitiveDataRules && sensitiveDataRules.size() > 0) {
                sensitiveDataRulesInit.addAll(sensitiveDataRules);
            }
        }
        return sensitiveDataRulesInit;
    }

    public static String getSensitiveDataAllowRun() {
        return sensitiveDataAllowRun;
    }

    public static Set<SensitiveDataRule> getSensitiveDataRules() {
        return sensitiveDataRules;
    }

    private static void setSensitiveDataAllowRun(String sensitiveDataAllowRun) {
        SensitiveDataKeysBuilder.sensitiveDataAllowRun = sensitiveDataAllowRun;
    }
}
