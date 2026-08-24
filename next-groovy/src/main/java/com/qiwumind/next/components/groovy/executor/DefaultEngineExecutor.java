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

package com.qiwumind.next.components.groovy.executor;



import java.util.Objects;

import com.qiwumind.next.components.context.helper.SpringContextHelper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.google.common.base.Preconditions;
import com.qiwumind.next.components.common.exception.BusinessRuntimeException;
import com.qiwumind.next.components.groovy.constants.GroovyEngineConstants;
import com.qiwumind.next.components.groovy.entity.EngineExecutorResult;
import com.qiwumind.next.components.groovy.entity.ExecuteParams;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;
import com.qiwumind.next.components.groovy.registry.ScriptRegistry;

import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * 默认引擎执行器
 *
 * @author 2022/09/18 12:39
 */
public class DefaultEngineExecutor implements EngineExecutor {

    private final Logger   logger = LoggerFactory.getLogger(this.getClass());

    private ScriptRegistry scriptRegistry;

    public DefaultEngineExecutor(ScriptRegistry scriptRegistry) {
        this.scriptRegistry = scriptRegistry;
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull ScriptQuery scriptQuery, ExecuteParams executeParams) {
        // 先根据scriptEntryQuery查询到要执行的脚本
        ScriptEntry scriptEntry;
        try {
            scriptEntry = this.scriptRegistry.find(scriptQuery);
            // 没有找到脚本，抛出异常
            if (Objects.isNull(scriptEntry)) {
                throw new BusinessRuntimeException("999999",
                        String.format("can not found script by [%s]", scriptQuery.getUniqueKey()));
            }
        } catch (Exception ex) {
            this.logger.error("execute groovy script error, scriptQuery is : {}, " + "executeParams is : {}",
                    scriptQuery, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }
        return this.execute(scriptEntry, executeParams);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull ScriptEntry scriptEntry, ExecuteParams executeParams) {

        this.logger.debug("DefaultEngineExecutor start execute script, scriptEntry is : {}, " + "executeParams is : {}",
                scriptEntry, executeParams);

        Object result;
        try {
            // 构建binding入参
            Binding binding = this.buildBinding(executeParams);
            // 创建脚本（可以看到这里就是基于Class去new一个script对象）
            Preconditions.checkNotNull(scriptEntry.getClazz(), "execute script failed, clazz can not be null.");
            Script script = InvokerHelper.createScript(scriptEntry.getClazz(), binding);
            script.setBinding(binding);
            // 执行脚本
            result = script.run();
        } catch (Exception ex) {
            this.logger.error("execute groovy script error, scriptEntry is : {}," + " executeParams is : {}",
                    scriptEntry, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }

        this.logger.debug("DefaultEngineExecutor execute script success, result is : {}", result);

        // 返回执行结果
        return EngineExecutorResult.success(result);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull String groovyMethodName, @NonNull ScriptQuery scriptQuery,
                                        ExecuteParams executeParams) {
        // 先根据scriptEntryQuery查询到要执行的脚本
        ScriptEntry scriptEntry;
        try {
            scriptEntry = this.scriptRegistry.find(scriptQuery);
            // 没有找到脚本，抛出异常
            if (Objects.isNull(scriptEntry)) {
                throw new BusinessRuntimeException("999999",
                        String.format("can not found script by [%s]", scriptQuery.getUniqueKey()));
            }
        } catch (Exception ex) {
            this.logger.error(
                    "execute groovy script by groovyMethodName error, scriptQuery is : {}, " + "executeParams is : {}",
                    scriptQuery, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }
        return this.execute(groovyMethodName, scriptEntry, executeParams);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull String groovyMethodName, @NonNull ScriptEntry scriptEntry,
                                        @NonNull ExecuteParams executeParams) {

        this.logger.debug("DefaultEngineExecutor start execute script by groovyMethodName, scriptEntry is : {}, "
                + "executeParams is : {}", scriptEntry, executeParams);

        if (StringUtils.isBlank(groovyMethodName)) {
            return EngineExecutorResult.failed("groovyMethodName can not be null.");
        }

        Object result;
        try {
            // 构建binding入参
            Binding binding = this.buildBinding(executeParams);
            // 创建脚本（可以看到这里就是基于Class去new一个script对象）
            Preconditions.checkNotNull(scriptEntry.getClazz(), "execute script failed, clazz can not be null.");
            Script script = InvokerHelper.createScript(scriptEntry.getClazz(), binding);
            // 按照groovy里的方法名来执行脚本
            result = script.invokeMethod(groovyMethodName, executeParams);
        } catch (Exception ex) {
            this.logger.error(
                    "execute groovy script  by groovyMethodName error, scriptEntry is : {}," + " executeParams is : {}",
                    scriptEntry, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }

        this.logger.debug("DefaultEngineExecutor execute script by groovyMethodName success, result is : {}", result);

        // 返回执行结果
        return EngineExecutorResult.success(result);
    }

    /**
     * 构建binding信息
     */
    private Binding buildBinding(ExecuteParams params) {
        Binding binding = new Binding();
        // 将spring容器上下文放入脚本
        binding.setProperty(GroovyEngineConstants.ContextConstants.APPLICATION_CONTEXT,
                SpringContextHelper.getContext());
        // 没有需要传递的参数
        if (Objects.isNull(params)) {
            return binding;
        }
        params.forEach(binding::setProperty);
        return binding;
    }

}
