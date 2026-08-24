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



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.compiler.GroovyCompiler;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.executor.DefaultEngineExecutor;
import com.qiwumind.next.components.groovy.executor.EngineExecutor;
import com.qiwumind.next.components.groovy.helper.RefreshScriptHelper;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;
import com.qiwumind.next.components.groovy.properties.GroovyClasspathLoaderProperties;
import com.qiwumind.next.components.groovy.properties.GroovyEngineProperties;
import com.qiwumind.next.components.groovy.registry.DefaultScriptRegistry;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 自动配置类
 * <p>
 * 核心自动配置类 ，配置文件中必须要有 {@code enhance.groovy.engine.enable}配置并且值为true时才开启
 * {@link GroovyEngineProperties#isEnable()}
 * </p>
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = { GroovyEngineProperties.class })
@ConditionalOnProperty(prefix = GroovyEngineProperties.PREFIX, name = "enable", havingValue = "true")
public class GroovyCoreAutoConfiguration {

    /**
     * groovy class编译器
     */
    @Bean
    @ConditionalOnMissingBean(DynamicCodeCompiler.class)
    public GroovyCompiler groovyCompiler() {
        return new GroovyCompiler();
    }

    /**
     * 可由使用方动态替换
     */
    @Bean(name = "enhanceGroovyScriptEngineCache")
    @ConditionalOnMissingBean(name = "enhanceGroovyScriptEngineCache", value = { Cache.class })
    public @NonNull Cache<String, ScriptEntry> enhanceGroovyScriptEngineCache(GroovyEngineProperties groovyEngineProperties) {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期(默认600分钟)
                .expireAfterWrite(groovyEngineProperties.getCacheExpireAfterWrite(), TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(groovyEngineProperties.getCacheInitialCapacity())
                // 缓存的最大条数
                .maximumSize(groovyEngineProperties.getCacheMaximumSize()).build();
    }

    /**
     * 脚本注册中心，依赖于 ScriptLoader ，ScriptLoader实现类由使用方自由选配
     */
    @Bean
    @ConditionalOnMissingBean(ScriptRegistry.class)
    public ScriptRegistry scriptRegistry(ScriptLoader scriptLoader,
                                         @Qualifier("enhanceGroovyScriptEngineCache") Cache<String, ScriptEntry> cache) {
        return new DefaultScriptRegistry(cache, scriptLoader);
    }

    /**
     * 执行引擎
     */
    @Bean
    @ConditionalOnMissingBean(DefaultEngineExecutor.class)
    public EngineExecutor defaultEngineExecutor(ScriptRegistry scriptRegistry) {

        return new DefaultEngineExecutor(scriptRegistry);
    }

    /**
     * 注入刷新groovy脚本助手
     */
    @Bean
    @ConditionalOnMissingBean(RefreshScriptHelper.class)
    public RefreshScriptHelper refreshScriptHelper(ScriptLoader scriptLoader, ScriptRegistry scriptRegistry,
                                                   DynamicCodeCompiler dynamicCodeCompiler) {
        RefreshScriptHelper refreshScriptHelper = new RefreshScriptHelper();
        refreshScriptHelper.setScriptLoader(scriptLoader);
        refreshScriptHelper.setScriptRegistry(scriptRegistry);
        refreshScriptHelper.setDynamicCodeCompiler(dynamicCodeCompiler);

        return refreshScriptHelper;
    }
}
