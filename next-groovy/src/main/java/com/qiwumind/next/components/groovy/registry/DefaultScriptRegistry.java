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

package com.qiwumind.next.components.groovy.registry;



import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.qiwumind.next.components.common.exception.BusinessRuntimeException;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认注册中心
 *
 * @author 2022/09/18 11:49
 */
@Slf4j
@AllArgsConstructor
public class DefaultScriptRegistry implements ScriptRegistry {
    /**
     * 咖啡因缓存
     */
    private Cache<String, ScriptEntry> cache;
    private ScriptLoader               scriptLoader;

    @Override
    public Boolean register(ScriptEntry scriptEntry) {
        if (Objects.isNull(scriptEntry)) {
            log.warn("register scriptEntry failed, because it is null.");
            return true;
        }
        try {
            // 强制覆盖
            this.cache.put(scriptEntry.getName(), scriptEntry);
        } catch (Exception ex) {
            log.error("DefaultScriptRegistry#register occur exception.", ex);
            return false;
        }
        return true;
    }

    @Override
    public Boolean batchRegister(List<ScriptEntry> scriptEntries) {
        log.debug("batch register start, scriptEntries is : {}", scriptEntries);
        Boolean success = this.batchRegister(scriptEntries, true);
        log.debug("batch register result is : [{}], scriptEntries is : {}", success, scriptEntries);
        return success;
    }

    @Override
    public Boolean register(ScriptEntry scriptEntry, boolean allowToCover) {
        // 旧 entry
        ScriptEntry oldEntry = this.cache.getIfPresent(scriptEntry.getName());
        if (Objects.isNull(oldEntry) || allowToCover) {
            return this.register(scriptEntry);
        }
        log.error("can not register [{}], because [{}] already exists, please check.", scriptEntry.getName(),
                scriptEntry.getLastModifiedTime());
        // 不覆盖，返回false
        return false;
    }

    @Override
    public Boolean batchRegister(List<ScriptEntry> scriptEntries, boolean allowToCover) {
        log.debug("batch register start, scriptEntries is : {}, allowToCover is : {}", scriptEntries, allowToCover);
        if (CollectionUtils.isEmpty(scriptEntries)) {
            log.warn("scriptEntries is empty, not register.");
            return true;
        }
        boolean executeResult = true;
        for (ScriptEntry scriptEntry : scriptEntries) {
            executeResult &= this.register(scriptEntry, allowToCover);
        }
        log.debug("batch register success, scriptEntries is : {}, allowToCover is : {}", scriptEntries, allowToCover);
        return executeResult;
    }

    @Override
    public ScriptEntry findOnCache(ScriptQuery scriptQuery) {
        // 直接从缓存中通过条件查询
        return this.cache.getIfPresent(scriptQuery.getUniqueKey());
    }

    @Override
    public ScriptEntry find(ScriptQuery scriptQuery) throws Exception {
        // 先从缓存中查找
        ScriptEntry entry = this.cache.getIfPresent(scriptQuery.getUniqueKey());

        if (Objects.nonNull(entry)) {
            return entry;
        }

        // 缓存中没有则通过脚本加载器进行加载
        synchronized (scriptQuery.getUniqueKey().intern()) {
            entry = this.cache.getIfPresent(scriptQuery.getUniqueKey());
            // DCL
            if (Objects.isNull(entry)) {
                log.info("DefaultScriptRegistry can not found ScriptEntry by scriptQuery [{}], load it now.",
                        scriptQuery);
                // 加载脚本
                entry = this.scriptLoader.load(scriptQuery);
                // 没有加载到脚本
                if (Objects.isNull(entry)) {
                    log.error("can not found ScriptEntry by scriptQuery : {}", scriptQuery);
                    return null;
                }
                log.info("DefaultScriptRegistry ScriptEntry by scriptQuery [{}] success, ScriptEntry is {}.",
                        scriptQuery, entry);
                // 设置脚本名称
                entry.setName(scriptQuery.getUniqueKey());
                // 放入缓存
                if (!this.register(entry)) {
                    log.error(
                            "put ScriptEntry to cache failed, name is [{}], lastModifiedTime is [{}], "
                                    + "scriptContext is [{}]",
                            entry.getName(), entry.getLastModifiedTime(), entry.getScriptContext());
                }
            }
        }

        return entry;
    }

    @Override
    public Map<String, ScriptEntry> findAllOnCache(boolean needLatestData) {
        // 需要数据，则先从数据源拉取，然后再返回
        if (needLatestData) {
            List<ScriptEntry> scriptEntries;
            try {
                log.info("findAllOnCache load script start.");
                scriptEntries = this.scriptLoader.load();
                log.info("findAllOnCache load script success, scriptEntries size is : [{}]",
                        Objects.isNull(scriptEntries) ? 0 : scriptEntries.size());
            } catch (Exception ex) {
                throw new BusinessRuntimeException("999999", "load script by scriptLoader occur exception.", ex);
            }
            // 注册到本地注册中心
            if (!this.batchRegister(scriptEntries)) {
                // 注册失败，抛出异常，保证获取到的数据一定是最新的
                throw new BusinessRuntimeException("999999", "batch register failed.");
            }
        }
        return this.cache.asMap();
    }

    @Override
    public void clear() {
        log.warn("clear script registry start.");
        this.cache.invalidateAll();
        log.warn("clear script registry success.");
    }

    @Override
    public Boolean clear(ScriptQuery scriptQuery) {
        log.warn("start clear script registry by key: [{}].", scriptQuery.getUniqueKey());
        this.cache.invalidate(scriptQuery.getUniqueKey());
        log.warn("success clear script registry by key: [{}].", scriptQuery.getUniqueKey());
        return true;
    }

}
