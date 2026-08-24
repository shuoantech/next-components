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



import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import com.qiwumind.next.components.groovy.entity.EnhanceGroovyScript;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;
import com.qiwumind.next.components.groovy.loader.EnhanceGroovyScriptRepository;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * 注册脚本到MySQL中helper
 */
@Slf4j
@AllArgsConstructor
public class RegisterScriptToMysqlHelper implements RegisterScriptHelper {
    private EnhanceGroovyScriptRepository enhanceGroovyScriptRepository;
    private ScriptLoader                  scriptLoader;
    private ScriptRegistry                scriptRegistry;

    @Override
    public boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception {
        log.warn("start manual register script, name is : [{}], script content is : {}", name, content);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("name and content can not be null.");
        }

        // 从数据库加载脚本
        EnhanceGroovyScript enhanceGroovyScript = new EnhanceGroovyScript();
        EnhanceGroovyScript query = enhanceGroovyScript.queryConverter(name);
        List<EnhanceGroovyScript> groovyScripts = this.enhanceGroovyScriptRepository.selectByCondition(query);

        // 如果数据库里不存在 或 允许覆盖，则插入
        if (CollectionUtils.isEmpty(groovyScripts) || allowCover) {
            query.setStatus("ON");
            query.setVersion(1);
            query.setScriptContent(content);
            log.warn("[{}] script store to db start.", name);
            this.enhanceGroovyScriptRepository.insert(query);
            log.warn("[{}] script store to db successfully.", name);
            // 从mysql加载
            ScriptEntry scriptEntry = this.scriptLoader.load(new ScriptQuery(name));
            // 注册到脚本注册中心
            Boolean success = this.scriptRegistry.register(scriptEntry);
            log.warn("[{}] script register to registry result is : [{}].", name, success);
            return success;
        }
        throw new UnsupportedOperationException(
                String.format("can not register script, because [%s] is already exists in datasource.", name));
    }

    @Override
    public boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) {
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
