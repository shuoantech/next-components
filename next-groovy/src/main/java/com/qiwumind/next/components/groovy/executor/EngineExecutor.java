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



import org.springframework.lang.NonNull;

import com.qiwumind.next.components.groovy.entity.EngineExecutorResult;
import com.qiwumind.next.components.groovy.entity.ExecuteParams;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;

/**
 * 引擎执行器
 */
public interface EngineExecutor {

    /**
     * 执行脚本
     *
     * @param scriptQuery   执行条件
     * @param executeParams 业务参数（传递到groovy脚本里的参数都可以放这里面）
     */
    @NonNull
    EngineExecutorResult execute(@NonNull ScriptQuery scriptQuery, ExecuteParams executeParams);

    /**
     * 执行脚本,优先缓存获取
     *
     * @param scriptEntry   脚本实体
     * @param executeParams 业务参数（传递到groovy脚本里的参数都可以放这里面）
     */
    @NonNull
    EngineExecutorResult execute(@NonNull ScriptEntry scriptEntry, ExecuteParams executeParams);

    /**
     * 根据groovy里的方法名来执行脚本方法
     *
     * @param groovyMethodName 方法名称
     * @param scriptQuery      查询参数
     * @param executeParams    参数
     */
    @NonNull
    EngineExecutorResult execute(@NonNull String groovyMethodName,
                                 @NonNull ScriptQuery scriptQuery,
                                 ExecuteParams executeParams);

    /**
     * 根据groovy里的方法名来执行脚本方法
     *
     * @param groovyMethodName 方法名称
     * @param scriptEntry      脚本实体
     * @param executeParams    参数
     */
    @NonNull
    EngineExecutorResult execute(@NonNull String groovyMethodName,
                                 @NonNull ScriptEntry scriptEntry,
                                 @NonNull ExecuteParams executeParams);

}
