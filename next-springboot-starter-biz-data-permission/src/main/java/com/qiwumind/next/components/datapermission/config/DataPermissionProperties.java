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

package com.qiwumind.next.components.datapermission.config;

import com.qiwumind.next.components.common.constant.SystemConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据权限的配置项
 * <p>
 * 数据权限是「可选」能力，业务项目可以按需配置：
 * 1. 完全不需要：设置 next.data-permission.enabled=false，或者干脆不注册任何 DataPermissionRule
 * 2. 只需要部分表：注册 {@link com.qiwumind.next.components.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer}
 * 只声明需要过滤的表
 *
 * @author qiwumind
 */
@ConfigurationProperties(prefix = SystemConstants.Prefix.DATA_PERMISSION)
@Data
public class DataPermissionProperties {

    /**
     * 数据权限总开关
     * <p>
     * 关闭后，不会向 MyBatis Plus 注册数据权限拦截器，SQL 不做任何改写，
     * 也不会创建 DataPermissionRuleFactory / DataPermissionAnnotationAdvisor 等 Bean。
     */
    private Boolean enabled = true;

}
