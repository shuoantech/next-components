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



import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.loader.EnhanceGroovyScriptRepository;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;
import com.qiwumind.next.components.groovy.properties.GroovyEngineProperties;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;
import com.qiwumind.next.components.groovy.properties.GroovyMysqlLoaderProperties;
import com.qiwumind.next.components.groovy.helper.RegisterScriptToMysqlHelper;
import com.qiwumind.next.components.groovy.loader.MysqlScriptLoader;

/**
 * 自动配置类
 */
@Configuration
@EnableConfigurationProperties(value = { GroovyMysqlLoaderProperties.class })
@ConditionalOnProperty(prefix = GroovyMysqlLoaderProperties.PREFIX, name = "enable", havingValue = "true")
public class GroovyMysqlLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 注入从MySQL里加载脚本的注册器
     */
    @Bean
    @ConditionalOnMissingBean(MysqlScriptLoader.class)
    public MysqlScriptLoader mysqlScriptLoader(DynamicCodeCompiler dynamicCodeCompiler,
                                               GroovyMysqlLoaderProperties groovyMysqlLoaderProperties,
                                               EnhanceGroovyScriptRepository enhanceGroovyScriptRepository) {
        this.logger.info("***loading ScriptLoader type is [{}]***", MysqlScriptLoader.class);
        return new MysqlScriptLoader(dynamicCodeCompiler, groovyMysqlLoaderProperties, enhanceGroovyScriptRepository);
    }

    @Bean
    @ConditionalOnMissingBean(RegisterScriptToMysqlHelper.class)
    public RegisterScriptToMysqlHelper registerScriptToMysqlHelper(EnhanceGroovyScriptRepository enhanceGroovyScriptRepository,
                                                                   ScriptLoader scriptLoader,
                                                                   ScriptRegistry scriptRegistry) {
        this.logger.info("***loading RegisterScriptToMysqlHelper ***", MysqlScriptLoader.class);

        return new RegisterScriptToMysqlHelper(enhanceGroovyScriptRepository, scriptLoader, scriptRegistry);
    }

}
