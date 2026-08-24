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

import com.qiwumind.next.components.groovy.entity.EnhanceGroovyScript;


/**
 * EnhanceGroovyEngine资源库
 */
public interface EnhanceGroovyScriptRepository {

    /**
     * 查询全部脚本数据
     */
    List<EnhanceGroovyScript> selectAll();

    /**
     * 按条件查询 EnhanceGroovyScript
     * @param query 查询条件
     */
    List<EnhanceGroovyScript> selectByCondition(@NonNull EnhanceGroovyScript query);

    /**
     * 按条件更新 EnhanceGroovyScript
     *
     * @param enhanceGroovyScript 待更新的数据
     * @return java.lang.Integer 更新条数
     */
    Integer updateByCondition(@NonNull EnhanceGroovyScript enhanceGroovyScript);

    /**
     * 插入
     *
     * @param enhanceGroovyScript 待插入的数据
     * @return java.lang.Integer 影响行数
     */
    Integer insert(@NonNull EnhanceGroovyScript enhanceGroovyScript);

    /**
     * 根据ID删除数据
     *
     * @param id id
     * @return java.lang.Integer
     */
    Integer deleteById(@NonNull Long id);
}
