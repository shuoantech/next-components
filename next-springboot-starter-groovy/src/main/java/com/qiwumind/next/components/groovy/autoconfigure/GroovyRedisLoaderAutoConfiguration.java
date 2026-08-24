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



import com.qiwumind.next.components.groovy.properties.GroovyMysqlLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.qiwumind.next.components.groovy.annotation.condition.ConditionalOnExistingProperty;
import com.qiwumind.next.components.groovy.properties.GroovyRedisLoaderProperties;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.helper.ManualRegisterScriptHelper;
import com.qiwumind.next.components.groovy.loader.RedisScriptLoader;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;


/**
 * 自动配置类
 */
@Configuration
@EnableConfigurationProperties(value = { GroovyRedisLoaderProperties.class })
@ConditionalOnProperty(prefix = GroovyRedisLoaderProperties.PREFIX, name = "enable", havingValue = "true")
public class GroovyRedisLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>
     * 注册基于Redis的ScriptLoader，配置里必须要显示开启该加载器时才注入
     * {@code enhance.groovy.engine.redis-loader.enable}
     * 需要依赖于RedisTemplate，所以项目里必须要配置redis
     * </p>
     */
    @Bean("redisScriptLoader")
    public RedisScriptLoader redisScriptLoader(RedisTemplate<String, String> redisTemplate,
                                               DynamicCodeCompiler dynamicCodeCompiler,
                                               GroovyRedisLoaderProperties groovyRedisLoaderProperties) {
        this.logger.info("loading ScriptLoader type is [{}]", RedisScriptLoader.class);
        return new RedisScriptLoader(redisTemplate, dynamicCodeCompiler, groovyRedisLoaderProperties);
    }

    /**
     * 注入手动注册脚本助手
     */
    @Bean
    public ManualRegisterScriptHelper registerScriptHelper(ScriptRegistry scriptRegistry,
                                                           RedisScriptLoader redisScriptLoader,
                                                           RedisTemplate<String, String> redisTemplate,
                                                           GroovyRedisLoaderProperties groovyRedisLoaderProperties) {
        return new ManualRegisterScriptHelper(scriptRegistry, redisScriptLoader, redisTemplate,
                groovyRedisLoaderProperties);
    }

}
