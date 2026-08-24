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

package com.qiwumind.next.components.groovy.loader;



import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.qiwumind.next.components.groovy.properties.GroovyRedisLoaderProperties;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;

/**
 * 从Redis里加载脚本loader
 */
@Slf4j
@AllArgsConstructor
public class RedisScriptLoader implements ScriptLoader {
    private final RedisTemplate<String, String> redisTemplate;
    private final DynamicCodeCompiler           dynamicCodeCompiler;
    private final GroovyRedisLoaderProperties   groovyRedisLoaderProperties;

    @Override
    public ScriptEntry load(@NonNull ScriptQuery query) throws Exception {
        // 从Redis中根据key查找脚本
        String script = (String) this.redisTemplate.opsForHash().get(this.groovyRedisLoaderProperties.getNamespace(),
                query.getUniqueKey());
        if (!StringUtils.hasText(script)) {
            return null;
        }
        // 获取脚本指纹
        String fingerprint = DigestUtils.md5DigestAsHex(script.getBytes());
        // 创建脚本对象
        ScriptEntry scriptEntry = new ScriptEntry(query.getUniqueKey(), script, fingerprint,
                System.currentTimeMillis());
        // 动态加载脚本为Class
        Class<?> aClass = this.dynamicCodeCompiler.compile(scriptEntry);
        scriptEntry.setClazz(aClass);
        return scriptEntry;
    }

    @Override
    public List<ScriptEntry> load() {
        List<ScriptEntry> resultList = new ArrayList<>();
        String key = this.groovyRedisLoaderProperties.getNamespace();
        // 获取到所有脚本的key
        Set<Object> hashKeys = this.redisTemplate.opsForHash().keys(key);
        // 没有脚本
        if (CollectionUtils.isEmpty(hashKeys)) {
            log.error("can not found hashKeys by key [{}].", key);
            return resultList;
        }
        // 获取所有脚本
        for (Object hashKey : hashKeys) {
            // groovy脚本内容
            String script = (String) this.redisTemplate.opsForHash().get(key, hashKey);
            if (!StringUtils.hasText(script)) {
                log.error("note can not found script content by key [{}] and hashKey [{}]", key, hashKey);
                continue;
            }
            // 获取脚本指纹
            String fingerprint = DigestUtils.md5DigestAsHex(script.getBytes());
            // 创建脚本对象
            ScriptEntry scriptEntry = new ScriptEntry(hashKey.toString(), script, fingerprint,
                    System.currentTimeMillis());
            resultList.add(scriptEntry);
        }

        return resultList;
    }
}
