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

package com.qiwumind.next.components.datapermission.core.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.qiwumind.next.components.datapermission.core.annotation.DataPermission;
import com.qiwumind.next.components.datapermission.core.aop.DataPermissionContextHolder;
import org.dromara.trans.service.impl.SimpleTransService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的 DataPermissionRuleFactoryImpl 实现类
 * 支持通过 {@link DataPermissionContextHolder} 过滤数据权限
 * @author qiwumind
 */
@RequiredArgsConstructor
public class DataPermissionRuleFactoryImpl implements DataPermissionRuleFactory {

    /**
     * 数据权限规则数组
     */
    private final List<DataPermissionRule> rules;

    @Override
    public List<DataPermissionRule> getDataPermissionRules() {
        return rules;
    }

    @Override // mappedStatementId 参数，暂时没有用。以后，可以基于 mappedStatementId + DataPermission 进行缓存
    public List<DataPermissionRule> getDataPermissionRule(String mappedStatementId) {
        // 1.1 无数据权限
        if (CollUtil.isEmpty(rules)) {
            return Collections.emptyList();
        }
        // 1.2 未配置，则默认开启
        DataPermission dataPermission = DataPermissionContextHolder.get();
        if (dataPermission == null) {
            return rules;
        }
        // 1.3 已配置，但禁用
        if (!dataPermission.enable()) {
            return Collections.emptyList();
        }
        // 1.4 特殊：数据翻译时，强制忽略数据权限 https://github.com/YunaiV/ruoyi-vue-pro/issues/1007
        if (isTranslateCall()) {
            return Collections.emptyList();
        }

        // 2.1 情况一：已配置，只选择部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.includeRules())) {
            return rules.stream().filter(rule -> ArrayUtil.contains(dataPermission.includeRules(), rule.getClass()))
                    .collect(Collectors.toList()); // 一般规则不会太多，所以不采用 HashSet 查询
        }
        // 2.2 已配置，只排除部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.excludeRules())) {
            return rules.stream().filter(rule -> !ArrayUtil.contains(dataPermission.excludeRules(), rule.getClass()))
                    .collect(Collectors.toList()); // 一般规则不会太多，所以不采用 HashSet 查询
        }
        // 2.3 已配置，全部规则
        return rules;
    }

    /**
     * 判断是否为数据翻译 {@link org.dromara.core.trans.anno.Trans} 的调用
     * 目前暂时只有这个办法，已经和 easy-trans 做过沟通
     * @return 是否
     */
    private boolean isTranslateCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stack) {
            if (SimpleTransService.class.getName().equals(e.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
