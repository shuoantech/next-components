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

import java.util.List;

/**
 * {@link DataPermissionRule} 工厂接口
 * 作为 {@link DataPermissionRule} 的容器，提供管理能力
 * @author qiwumind
 */
public interface DataPermissionRuleFactory {

    /**
     * 获得所有数据权限规则数组
     * @return 数据权限规则数组
     */
    List<DataPermissionRule> getDataPermissionRules();

    /**
     * 获得指定 Mapper 的数据权限规则数组
     * @param mappedStatementId 指定 Mapper 的编号
     * @return 数据权限规则数组
     */
    List<DataPermissionRule> getDataPermissionRule(String mappedStatementId);

}
