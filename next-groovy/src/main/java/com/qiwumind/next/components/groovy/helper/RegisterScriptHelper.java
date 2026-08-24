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

import org.springframework.lang.NonNull;

/**
 * 手动注册脚本助手
 *
 */
public interface RegisterScriptHelper {

    /**
     * <p>
     * 注册groovy脚本
     * </p>
     *
     * @param name       脚本名称
     * @param content    脚本内容
     * @param allowCover 是否允许覆盖
     * @return true / false
     * @throws Exception 异常
     */
    boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception;

    /**
     * <p>
     * 批量注册groovy脚本，key为脚本名称，value 为脚本内容
     * </p>
     *
     * @param scriptMap  脚本信息map
     * @param allowCover 是否允许覆盖
     * @return true / false
     * @throws Exception 异常
     */
    boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) throws Exception;
}
