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



import java.util.List;

import org.springframework.lang.NonNull;

import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;

/**
 * 脚本加载器
 *
 * @author
 */
public interface ScriptLoader {

    /**
     * <p>
     * 加载脚本，如果缓存中不存在，则从数据源查找，找到后将脚本编译为Class
     * </p>
     *
     * @param query 查询对象
     * @return org.basis.enhance.groovy.entity.ScriptEntry 脚本实体
     * @throws Exception 异常
     * @author 2022/9/18 12:13 下午
     */
    ScriptEntry load(@NonNull ScriptQuery query) throws Exception;

    /**
     * <p>
     * 从数据源预加载所有的脚本（不会将脚本编译为Class）
     * </p>
     *
     * @return java.util.List<org.basis.enhance.groovy.entity.ScriptEntry>
     * @throws Exception 异常
     * @author 2022/9/18 3:57 下午
     */
    List<ScriptEntry> load() throws Exception;

}
