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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.google.common.base.Preconditions;
import com.qiwumind.next.components.groovy.properties.GroovyMysqlLoaderProperties;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.entity.EnhanceGroovyScript;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;

/**
 * 从MySQL数据库里加载脚本
 */
@Slf4j
@AllArgsConstructor
public class MysqlScriptLoader implements ScriptLoader {
    private final Logger                  logger = LoggerFactory.getLogger(this.getClass());
    private DynamicCodeCompiler           dynamicCodeCompiler;
    private GroovyMysqlLoaderProperties   groovyMysqlLoaderProperties;
    private EnhanceGroovyScriptRepository enhanceGroovyScriptRepository;

    @Override
    public ScriptEntry load(@NonNull ScriptQuery query) throws Exception {
        // 按条件查询脚本
        EnhanceGroovyScript groovyScriptQuery = new EnhanceGroovyScript();
        groovyScriptQuery = groovyScriptQuery.queryConverter(query);
        groovyScriptQuery.setStatus("ON");
        List<EnhanceGroovyScript> groovyScripts = this.enhanceGroovyScriptRepository
                .selectByCondition(groovyScriptQuery);

        if (CollectionUtils.isEmpty(groovyScripts)) {
            this.logger.warn("can not found groovy script by condition : {}", groovyScriptQuery);
            return null;
        }
        EnhanceGroovyScript groovyScript = groovyScripts.get(0);
        String scriptContent = groovyScript.getScriptContent();
        // 获取脚本指纹
        String fingerprint = DigestUtils.md5DigestAsHex(scriptContent.getBytes());
        // 创建脚本对象
        ScriptEntry scriptEntry = new ScriptEntry(groovyScript.buildOnlyKey(), scriptContent, fingerprint,
                System.currentTimeMillis());
        // 动态加载脚本为Class
        Class<?> aClass = this.dynamicCodeCompiler.compile(scriptEntry);
        scriptEntry.setClazz(aClass);

        return scriptEntry;
    }

    @Override
    public List<ScriptEntry> load() {
        this.logger.info("load all groovy script start.");
        List<ScriptEntry> resultList = new ArrayList<>();
        EnhanceGroovyScript query = new EnhanceGroovyScript();
        query.setStatus("ON");
        query.setNamespace(this.groovyMysqlLoaderProperties.getNamespace());
        Preconditions.checkArgument(StringUtils.isNotBlank(query.getNamespace()));
        // 加载该命名空间下所有的脚本
        List<EnhanceGroovyScript> enhanceGroovyScripts = this.enhanceGroovyScriptRepository.selectByCondition(query);

        // 没有查到脚本，则不处理
        if (CollectionUtils.isEmpty(enhanceGroovyScripts)) {
            this.logger.warn("can not found EnhanceGroovyScripts by condition : [{}].", query);
            return resultList;
        }

        this.logger.info("==load groovy script count is : [{}]==", enhanceGroovyScripts.size());

        for (EnhanceGroovyScript groovyScript : enhanceGroovyScripts) {
            String scriptContent = groovyScript.getScriptContent();
            // 空脚本不处理
            if (StringUtils.isBlank(scriptContent)) {
                this.logger.error("script content is blank , groovyScript is : {}.", groovyScript);
                continue;
            }
            // 获取脚本指纹
            String fingerprint = DigestUtils.md5DigestAsHex(scriptContent.getBytes());
            // 创建脚本对象
            ScriptEntry scriptEntry = new ScriptEntry(groovyScript.buildOnlyKey(), scriptContent, fingerprint,
                    System.currentTimeMillis());
            resultList.add(scriptEntry);
        }

        this.logger.info("load all groovy script success.");

        return resultList;
    }
}
