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

package com.qiwumind.next.components.groovy.helper;



import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import com.qiwumind.next.components.groovy.properties.GroovyRedisLoaderProperties;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * 手动注册脚本助手
 */
@Slf4j
public class ManualRegisterScriptHelper implements RegisterScriptHelper {

    private ScriptRegistry                scriptRegistry;

    private ScriptLoader                  redisScriptLoader;

    private RedisTemplate<String, String> redisTemplate;

    private GroovyRedisLoaderProperties   groovyRedisLoaderProperties;

    public ManualRegisterScriptHelper(ScriptRegistry scriptRegistry, ScriptLoader redisScriptLoader,
                                      RedisTemplate<String, String> redisTemplate,
                                      GroovyRedisLoaderProperties groovyRedisLoaderProperties) {
        this.scriptRegistry = scriptRegistry;
        this.redisScriptLoader = redisScriptLoader;
        this.redisTemplate = redisTemplate;
        this.groovyRedisLoaderProperties = groovyRedisLoaderProperties;
    }

    @Override
    public boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception {
        log.warn("start manual register script, name is : [{}], script content is : {}", name, content);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("name and content can not be null.");
        }
        // 查找脚本是否存在
        Object oldScript = this.redisTemplate.opsForHash().get(this.groovyRedisLoaderProperties.getNamespace(), name);
        // 如果脚本不存在或允许覆盖，则写入数据源然后注册到registry
        if (Objects.isNull(oldScript) || allowCover) {
            // 脚本放入Redis缓存
            this.redisTemplate.opsForHash().put(this.groovyRedisLoaderProperties.getNamespace(), name, content);
            log.warn("[{}] script store to redis successfully.", name);
            // 从Redis加载
            ScriptEntry scriptEntry = this.redisScriptLoader.load(new ScriptQuery(name));
            // 注册到脚本注册中心
            Boolean success = this.scriptRegistry.register(scriptEntry);
            log.warn("[{}] script register to registry result is : [{}].", name, success);
            return success;
        }
        throw new UnsupportedOperationException(
                String.format("can not register script, because [%s] is already exists in datasource.", name));
    }

    @Override
    public boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) throws Exception {
        log.warn("batch register script start.");
        scriptMap.forEach((name, content) -> {
            try {
                this.registerScript(name, content, allowCover);
            } catch (Exception e) {
                throw new RuntimeException("register failed，please retry.", e);
            }
        });
        log.warn("batch register script success.");
        return true;
    }
}
