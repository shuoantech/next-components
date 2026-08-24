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


import java.io.FilenameFilter;

import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.groovy.properties.GroovyClasspathLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.filter.GroovyFileNameFilter;
import com.qiwumind.next.components.groovy.loader.ClasspathScriptLoader;


/**
 * <p>
 * 从classpath下加载groovy脚本的loader自动配置类 配置里必须要显示开启该加载器时才注入
 * {@code enhance.groovy.engine.classpath-loader.enable}
 * </p>
 *
 * @author wenpan 2022/09/25 15:23
 */
@Configuration
@EnableConfigurationProperties(value = {GroovyClasspathLoaderProperties.class})
@ConditionalOnProperty(prefix = GroovyClasspathLoaderProperties.PREFIX, name = "enable", havingValue = "true")
public class GroovyClasspathLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>
     * 注册基于 classpath 的ScriptLoader
     * </p>
     */
    @Bean(value = "classpathScriptLoader")
    @ConditionalOnMissingBean(ClasspathScriptLoader.class)
    public ClasspathScriptLoader classpathScriptLoader(DynamicCodeCompiler dynamicCodeCompiler,
                                                       GroovyClasspathLoaderProperties classpathLoaderProperties) {
        this.logger.info("loading ScriptLoader type is [{}]", ClasspathScriptLoader.class);
        return new ClasspathScriptLoader(dynamicCodeCompiler, classpathLoaderProperties, this.groovyFileNameFilter());
    }

    /**
     * 文件名称过滤器
     */
    @Bean
    public FilenameFilter groovyFileNameFilter() {

        return new GroovyFileNameFilter();
    }
}
