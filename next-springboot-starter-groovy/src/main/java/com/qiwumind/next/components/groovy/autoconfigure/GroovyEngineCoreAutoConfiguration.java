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

package com.qiwumind.next.components.groovy.autoconfigure;



import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;
import com.qiwumind.next.components.groovy.executor.AutoRefreshScriptExecutor;
import com.qiwumind.next.components.groovy.helper.RefreshScriptHelper;
import com.qiwumind.next.components.groovy.properties.GroovyEngineProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类
 *
 * @author wenpan 2022/09/18 14:25
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {GroovyEngineProperties.class})
@ConditionalOnProperty(prefix = GroovyEngineProperties.PREFIX, name = "enable", havingValue = "true")
public class GroovyEngineCoreAutoConfiguration {
    /**
     * 自动刷新脚本executor
     */
    @Bean
    public AutoRefreshScriptExecutor autoRefreshScriptExecutor(GroovyEngineProperties groovyEngineProperties,
                                                               RefreshScriptHelper refreshScriptHelper) {
        return new AutoRefreshScriptExecutor(groovyEngineProperties, refreshScriptHelper);
    }

//    @Bean
//    @ConditionalOnMissingBean(HotLoadingGroovyScriptAlarm.class)
//    public HotLoadingGroovyScriptAlarm hotLoadingGroovyScriptAlarm() {
//        // 默认打印告警信息到日志里
//        return (scriptEntries, ex) -> log.error("scriptEntry load failed, scriptEntries info is : {}", scriptEntries, ex);
//    }

    /**
     * <p>
     * 导入CoreAutoConfiguration（springboot中默认的加载顺序是：先根据spring.factories文件读取到
     * EnhanceGroovyEngineCoreAutoConfiguration类，然后处理里面的@Import 注解，所以ImportCoreAutoConfiguration里的bean
     * 会优先于EnhanceGroovyEngineCoreAutoConfiguration所有的bean的注入）
     * </p>
     */
    @Import(value = {GroovyCoreAutoConfiguration.class})
    static class ImportCoreAutoConfiguration {

    }

}
