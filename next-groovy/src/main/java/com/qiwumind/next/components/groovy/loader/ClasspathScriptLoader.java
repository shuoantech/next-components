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



import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qiwumind.next.components.groovy.properties.GroovyClasspathLoaderProperties;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.google.common.collect.Lists;
import com.qiwumind.next.components.common.exception.BusinessRuntimeException;
import com.qiwumind.next.components.groovy.compiler.DynamicCodeCompiler;
import com.qiwumind.next.components.groovy.entity.ScriptEntry;
import com.qiwumind.next.components.groovy.entity.ScriptQuery;

import lombok.extern.slf4j.Slf4j;

/**
 * 脚本加载器
 * <ol>
 * 默认加载classpath下的groovy下的enhance目录下的*.groovy;此方法后续动态扩展不佳
 * </ol>
 */
@Slf4j
@AllArgsConstructor
public class ClasspathScriptLoader implements ScriptLoader {
    private final DynamicCodeCompiler             dynamicCodeCompiler;
    private final GroovyClasspathLoaderProperties groovyClasspathLoaderProperties;
    private final FilenameFilter                  groovyFileNameFilter;

    @Override
    public ScriptEntry load(@NonNull ScriptQuery query) throws Exception {
        String uniqueKey = query.getUniqueKey();
        // 通过文件名称加载脚本
        ClassPathResource classPathResource = new ClassPathResource(uniqueKey);
        if (!classPathResource.isFile()) {
            throw new BusinessRuntimeException("999999",String.format("[%s] is not a groovy file, please check!", uniqueKey));
        }
        // 获取文件内容
        String fileContext = this.readFileAsString(classPathResource.getFile());
        // 获取脚本指纹
        String fingerprint = DigestUtils.md5DigestAsHex(fileContext.getBytes());
        // 脚本内容编译为Class
        Class<?> aClass = this.dynamicCodeCompiler.compile(fileContext, uniqueKey);
        // 创建脚本对象,以文件名作为唯一ID
        ScriptEntry scriptEntry = new ScriptEntry(classPathResource.getFilename(), fileContext, fingerprint,
                System.currentTimeMillis());
        scriptEntry.setClazz(aClass);
        return scriptEntry;
    }

    @Override
    public List<ScriptEntry> load() throws Exception {
        List<ScriptEntry> resultList = new ArrayList<>();
        // 获取到所有的脚本文件
        List<File> files = this.loadClassPathFile(this.groovyClasspathLoaderProperties.getDirectory(), this.groovyFileNameFilter);
        if (CollectionUtils.isEmpty(files)) {
            log.warn("can not found groovy script from directory [{}]", this.groovyClasspathLoaderProperties.getDirectory());
            return resultList;
        }
        for (File file : files) {
            // 获取文件内容
            String fileContext = this.readFileAsString(file);
            if (StringUtils.isBlank(fileContext)) {
                log.error("note can not found script content by fileName [{}], because file content is empty.",
                        file.getName());
                continue;
            }
            // 获取脚本指纹
            String fingerprint = DigestUtils.md5DigestAsHex(fileContext.getBytes());
            // 创建脚本对象, 以文件名作为唯一ID
            ScriptEntry scriptEntry = new ScriptEntry(file.getName(), fileContext, fingerprint,
                    System.currentTimeMillis());
            resultList.add(scriptEntry);
        }

        return resultList;
    }

    /**
     * 读取文件内容为string
     *
     * @param file 文件
     * @return java.lang.String
     * @author wenpan 2022/9/18 5:26 下午
     */
    private String readFileAsString(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            // size 为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            int read = in.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        }
    }

    private List<File> loadClassPathFile(String directory, FilenameFilter filenameFilter) throws IOException {
        List<File> result = Lists.newArrayList();
        if (StringUtils.isBlank(directory)) {
            return result;
        }
        // 加载目录下的资源
        ClassPathResource classPathResource = new ClassPathResource(directory);
        File file = classPathResource.getFile();
        // 如果是文件则直接添加
        if (file.isFile()) {
            result.add(file);
        }
        // 如果是目录，则将该目录下的所有文件加入
        if (file.isDirectory()) {
            // 通过文件名称过滤器获取当前目录下所有文件
            File[] files = file.listFiles(filenameFilter);
            if (files != null) {
                result.addAll(Arrays.asList(files));
            }
        }

        return result;
    }

    /**
     * 获取directories下多个文件
     *
     * @param directories 目录
     * @param filenameFilter 文件名过滤器
     * @return java.util.List<java.io.File>
     * @author wenpan 2022/9/18 3:01 下午
     */
    @Deprecated
    List<File> getFiles(String[] directories, FilenameFilter filenameFilter) {
        // directories目录下满足条件的所有文件集合
        List<File> allFileList = new ArrayList<>();
        // 遍历每一个目录
        for (String sDirectory : directories) {
            if (sDirectory != null) {
                File directory = this.getDirectory(sDirectory);
                // 获取该目录下所有文件
                File[] aFiles = directory.listFiles(filenameFilter);
                if (aFiles != null) {
                    allFileList.addAll(Arrays.asList(aFiles));
                }
            }
        }
        return allFileList;
    }

    /**
     * 根据sPath获取目录
     *
     * @param sPath sPath
     * @return java.io.File
     * @author wenpan 2022/9/18 3:03 下午
     */
    @Deprecated
    public File getDirectory(String sPath) {
        File directory = new File(sPath);
        if (!directory.isDirectory()) {
            URL resource = ClasspathScriptLoader.class.getClassLoader().getResource(sPath);
            try {
                assert resource != null;
                directory = new File(resource.toURI());
            } catch (Exception e) {
                log.error("Error accessing directory in classloader. path=" + sPath, e);
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException(directory.getAbsolutePath() + " is not a valid directory");
            }
        }
        return directory;
    }
}
