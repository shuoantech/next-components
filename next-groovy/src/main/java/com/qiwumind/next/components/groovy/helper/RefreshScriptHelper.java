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
import java.util.Objects;

import org.springframework.lang.NonNull;

import com.qiwumind.next.components.common.exception.BusinessRuntimeException;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;
import com.qiwumind.next.components.groovy.loader.ScriptLoader;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 刷新 groovy 脚本helper
 */
@Slf4j
@Setter
public class RefreshScriptHelper {

    private ScriptLoader        scriptLoader;
    private ScriptRegistry      scriptRegistry;
    private DynamicCodeCompiler dynamicCodeCompiler;
    //    private HotLoadingGroovyScriptAlarm hotLoadingGroovyScriptAlarm;

    /**
     * <p>
     * 按条件进行强制刷新脚本
     * <ol>
     * <li>首先从数据源加载脚本，加载到脚本后编译为Class，然后将脚本注册到本地registry</li>
     * </ol>
     * </p>
     *
     * @param scriptQuery 脚本刷新条件
     * @param allowCover 是否需要覆盖
     */
    public boolean refresh(@NonNull ScriptQuery scriptQuery, boolean allowCover) {
        log.warn("===RefreshScriptHelper start refresh groovy script, scriptQuery is : {} ==", scriptQuery);
        ScriptEntry scriptEntry = null;
        try {
            // 从数据源按条件加载脚本
            scriptEntry = this.scriptLoader.load(scriptQuery);
            // 没有脚本，则注册失败
            if (Objects.isNull(scriptEntry)) {
                log.error("can not found scriptEntry by key : [{}]", scriptQuery.getUniqueKey());
                return false;
            }
            // 从缓存中查找
            ScriptEntry oldScriptEntry = this.scriptRegistry.findOnCache(scriptQuery);
            // 不存在脚本 或 允许覆盖 或 新旧脚本指纹不同，则更新本地registry
            if (Objects.isNull(oldScriptEntry) || allowCover
                    || !oldScriptEntry.getFingerprint().equals(scriptEntry.getFingerprint())) {
                log.warn("register scriptEntry to registry, oldScriptEntry is : {}," + " newScriptEntry is : {}",
                        oldScriptEntry, scriptEntry);
                // 注册到注册中心统一进行管理
                Boolean success = this.scriptRegistry.register(scriptEntry);
                log.warn("RefreshScriptHelper refresh groovy script result is : [{}], scriptQuery is : {}", success,
                        scriptQuery);
                return success;
            } else {
                log.warn("can not refresh, oldScriptEntry is : {}, newScriptEntry is : {}", oldScriptEntry,
                        scriptEntry);
                return false;
            }
        } catch (Exception ex) {
            log.error("RefreshScriptHelper refresh occur error, scriptEntry is : {}", scriptEntry, ex);
            // 调用用户自定义告警接口
            //            hotLoadingGroovyScriptAlarm.alarm(Collections.singletonList(scriptEntry), ex);
            return false;
        }
    }

    /**
     * <p>
     * 刷新所有script
     * <ol>
     * <li>扫描指定位置的所有脚本，如果脚本信息有变化，则更新本地脚本缓存scriptRegistry</li>
     * <li>如果脚本没有变化，则不更新缓存，什么也不做</li>
     * </ol>
     * </p>
     */
    public boolean refreshAll() {
        log.info("===RefreshScriptHelper start refresh all groovy script.===");
        boolean success = true;
        // 记录新增的脚本数量
        int addScriptCount = 0;
        int updateScriptCount = 0;
        List<ScriptEntry> scriptEntries = null;
        try {
            // 加载所有脚本
            scriptEntries = this.scriptLoader.load();
            log.debug("===RefreshScriptHelper load script is : {}===", scriptEntries);
            log.info("===RefreshScriptHelper load script count is : {}===", scriptEntries.size());
            for (ScriptEntry scriptEntry : scriptEntries) {
                ScriptEntry oldScriptEntry = this.scriptRegistry.findOnCache(new ScriptQuery(scriptEntry.getName()));

                // 缓存中没有，则注册进缓存
                if (Objects.isNull(oldScriptEntry)) {
                    log.info("can not found script by [{}], register to registry directly.", scriptEntry.getName());
                    scriptEntry.setClazz(this.dynamicCodeCompiler.compile(scriptEntry));
                    if (!this.scriptRegistry.register(scriptEntry)) {
                        throw new BusinessRuntimeException("999999","register script return false.");
                    }
                    addScriptCount++;
                    continue;
                }

                boolean isEquals = oldScriptEntry.fingerprintIsEquals(scriptEntry.getFingerprint());
                // 指纹相同，则说明脚本没有变化，指纹不同则说明脚本有变化
                if (!isEquals) {
                    log.info("found script by [{}], but their fingerprints are different, modify it.",
                            scriptEntry.getName());
                    // 编译脚本为Class
                    Class<?> aClass = this.dynamicCodeCompiler.compile(scriptEntry);
                    scriptEntry.setClazz(aClass);
                    scriptEntry.setLastModifiedTime(System.currentTimeMillis());
                    // 注册
                    if (!this.scriptRegistry.register(scriptEntry)) {
                        throw new BusinessRuntimeException("999999","register script return false.");
                    }
                    updateScriptCount++;
                }

            }
        } catch (Exception ex) {
            success = false;
            log.error("RefreshScriptHelper refresh occur error, scriptEntries is : {}", scriptEntries, ex);
            // 调用用户自定义告警接口
            //            hotLoadingGroovyScriptAlarm.alarm(scriptEntries, ex);
        }
        // 打印刷新情况
        log.info(
                "RefreshScriptHelper refresh groovy script end,scriptCount is [{}], addScriptCount is [{}],"
                        + " updateScriptCount is [{}]",
                Objects.isNull(scriptEntries) ? null : scriptEntries.size(), addScriptCount, updateScriptCount);
        return success;
    }

}
