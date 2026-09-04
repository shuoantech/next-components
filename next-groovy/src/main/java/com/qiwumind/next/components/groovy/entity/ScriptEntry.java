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

package com.qiwumind.next.components.groovy.entity;



import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 脚本项，一条记录对应着一个脚本
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ScriptEntry {
    /**
     * 脚本名称（需要保证唯一）
     */
    private String       name;
    /**
     * 脚本内容
     */
    private final String scriptContext;
    /**
     * 脚本指纹
     */
    private final String fingerprint;
    /**
     * 最近修改时间
     */
    private Long         lastModifiedTime;
    /**
     * 脚本code对应的Class
     */
    private Class<?>     clazz;

    public ScriptEntry(String name, String scriptContext, String fingerprint, Long lastModifiedTime) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(scriptContext), "scriptContext can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(fingerprint), "fingerprint can not be null.");
        Preconditions.checkArgument(Objects.nonNull(lastModifiedTime), "lastModifiedTime can not be null.");
        this.name = name;
        this.scriptContext = scriptContext;
        this.fingerprint = fingerprint;
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * 指纹是否相同
     */
    public boolean fingerprintIsEquals(String otherFingerprint) {
        return this.fingerprint.equals(otherFingerprint);
    }

}
