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
import com.qiwumind.next.components.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import com.qiwumind.next.components.datapermission.core.db.DataPermissionRuleHandler;
import com.qiwumind.next.components.datapermission.core.rule.DataPermissionRule;
import com.qiwumind.next.components.datapermission.core.rule.DataPermissionRuleFactory;
import com.qiwumind.next.components.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import com.qiwumind.next.components.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 数据权限的自动配置类
 * <p>
 * 数据权限是「可选」能力，业务项目不一定都要配置。这里提供两级按需开关：
 * 1. 总开关：next.data-permission.enabled=false，则整个数据权限不生效（不注册任何相关 Bean）
 * 2. 规则可选：一个 DataPermissionRule 都没有时，也能正常启动，只是不做任何 SQL 改写
 *
 * @author qiwumind
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = SystemConstants.Prefix.DATA_PERMISSION, name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DataPermissionProperties.class)
public class QiwumindDataPermissionAutoConfiguration {

    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(ObjectProvider<DataPermissionRule> rules) {
        // 用 ObjectProvider 而非 List<DataPermissionRule> 直接注入：
        // 业务项目可能一个数据权限规则都没有，此时 List 注入会退化成「找不到 java.util.List 类型的 Bean」
        // 直接启动失败；ObjectProvider 在零规则时返回空流，保证「不配置也能启动」。
        // orderedStream() 会按 @Order / Ordered 排序，与原 List 注入的行为一致。
        return new DataPermissionRuleFactoryImpl(rules.orderedStream().toList());
    }

    @Bean
    public DataPermissionRuleHandler dataPermissionRuleHandler(MybatisPlusInterceptor interceptor,
                                                               DataPermissionRuleFactory ruleFactory) {
        // 创建 DataPermissionInterceptor 拦截器
        DataPermissionRuleHandler handler = new DataPermissionRuleHandler(ruleFactory);
        DataPermissionInterceptor inner = new DataPermissionInterceptor(handler);
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return handler;
    }

    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}
