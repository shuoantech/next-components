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

package com.qiwumind.next.components.freemarker.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 多模块环境下智能路径识别的工具类
 * 用于在MyBatis Generator等代码生成工具中准确定位当前模块的根路径
 *
 * @author qiwumind
 */

public class ModuleUtils {

    /**
     * 最大向上查找层级
     */
    private static final int MAX_PARENT_LEVEL = 5;

    /**
     * 常见模块名称模式（按优先级排序）
     */
    private static final List<String> MODULE_PATTERNS = Arrays.asList(
            "admin", "web", "service", "api", "core", "common", "util", "model"
    );

    /**
     * 模块根路径
     */
    private final String modulePath;

    /**
     * 构造函数，自动检测当前模块路径
     */
    public ModuleUtils() {
        this.modulePath = detectCurrentModulePath();
        System.out.println("✅ 检测到的模块根路径: " + this.modulePath);
    }

    /**
     * 获取模块根路径
     */
    public String getModulePath() {
        return modulePath;
    }

    // ==================== 路径检测核心逻辑 ====================

    /**
     * 检测当前模块的根路径
     * 采用多级策略，确保在各种环境下都能准确定位
     */
    private String detectCurrentModulePath() {
        // 策略1：通过类路径定位（最准确）
        String modulePath = detectByClassLocation();
        if (isValidModulePath(modulePath)) {
            return modulePath;
        }

        // 策略2：通过user.dir并智能查找
        modulePath = detectByUserDir();
        if (isValidModulePath(modulePath)) {
            return modulePath;
        }

        // 策略3：通过系统属性（可在启动时设置 -Dproject.module=...）
        modulePath = detectBySystemProperty();
        if (isValidModulePath(modulePath)) {
            return modulePath;
        }

        // 降级策略：返回user.dir
        String fallbackPath = System.getProperty("user.dir");
        System.out.println("⚠️ 使用降级路径: " + fallbackPath);
        return fallbackPath;
    }

    /**
     * 通过类的位置定位模块根路径
     */
    private String detectByClassLocation() {
        return Optional.ofNullable(getClassLocation())
                .map(this::extractModulePathFromClassPath)
                .orElse(null);
    }

    /**
     * 通过user.dir定位模块根路径
     */
    private String detectByUserDir() {
        String userDir = System.getProperty("user.dir");
        System.out.println("📁 user.dir: " + userDir);
        return findCorrectModule(userDir);
    }

    /**
     * 通过系统属性定位模块根路径
     */
    private String detectBySystemProperty() {
        String projectModule = System.getProperty("project.module");
        if (projectModule != null && !projectModule.trim().isEmpty()) {
            File moduleDir = new File(projectModule);
            if (moduleDir.exists() && moduleDir.isDirectory()) {
                return moduleDir.getAbsolutePath();
            }
        }
        return null;
    }

    // ==================== 路径解析工具方法 ====================

    /**
     * 获取当前类的实际位置（CodeSource方式）
     */
    private String getClassLocation() {
        try {
            CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                return decodeUrlPath(location.getPath());
            }
        } catch (Exception e) {
            System.err.println("⚠️ 获取类路径失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 解码URL路径
     */
    private String decodeUrlPath(String path) {
        if (path == null) return null;
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return path; // 降级返回原路径
        }
    }

    /**
     * 从类路径中提取模块根路径
     */
    private String extractModulePathFromClassPath(String classPath) {
        if (classPath == null) return null;
        // 处理Windows路径分隔符
        classPath = classPath.replace('\\', '/');
        // 常见的构建输出目录
        String[] buildOutputPatterns = {
                "/target/classes/", "/target/test-classes/",
                "/build/classes/", "/bin/", "/out/"
        };
        for (String pattern : buildOutputPatterns) {
            int index = classPath.indexOf(pattern);
            if (index > 0) {
                return classPath.substring(0, index);
            }
        }
        // 如果是目录，检查是否是模块根
        File classFile = new File(classPath);
        if (classFile.isDirectory()) {
            return findParentWithPom(classFile);
        }

        return null;
    }

    /**
     * 向上查找包含pom.xml的目录（模块根路径）
     */
    private String findParentWithPom(File start) {
        File current = start;

        for (int i = 0; i < MAX_PARENT_LEVEL && current != null; i++) {
            if (hasPomXml(current)) {
                return current.getAbsolutePath();
            }
            current = current.getParentFile();
        }
        return null;
    }

    /**
     * 从user.dir中查找正确的模块
     */
    private String findCorrectModule(String startPath) {
        if (startPath == null) return null;

        File startDir = new File(startPath);

        // 如果当前目录就是模块目录
        if (isModuleDirectory(startDir)) {
            return startDir.getAbsolutePath();
        }

        // 查找所有可能的子模块
        List<File> candidateModules = findCandidateModules(startDir);

        // 按优先级排序并返回第一个
        return candidateModules.stream()
                .map(File::getAbsolutePath)
                .findFirst()
                .orElse(null);
    }

    /**
     * 查找候选模块目录
     */
    private List<File> findCandidateModules(File parentDir) {
        List<File> candidates = new ArrayList<>();
        File[] subDirs = parentDir.listFiles(File::isDirectory);

        if (subDirs == null) {
            return candidates;
        }

        // 先按模式匹配
        for (String pattern : MODULE_PATTERNS) {
            for (File subDir : subDirs) {
                if (subDir.getName().contains(pattern) && isModuleDirectory(subDir)) {
                    candidates.add(subDir);
                }
            }
        }

        // 如果没有模式匹配，收集所有有效的模块
        if (candidates.isEmpty()) {
            for (File subDir : subDirs) {
                if (isModuleDirectory(subDir)) {
                    candidates.add(subDir);
                }
            }
        }

        return candidates;
    }

    // ==================== 路径验证工具方法 ====================

    /**
     * 判断目录是否是有效的模块根路径
     */
    private boolean isModuleDirectory(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }

        // 检查是否有pom.xml（Maven项目）
        if (hasPomXml(dir)) {
            return true;
        }

        // 检查是否有src/main/java（典型Java模块结构）
        File srcMainJava = new File(dir, "src/main/java");
        if (srcMainJava.exists() && srcMainJava.isDirectory()) {
            return true;
        }

        // 检查是否有build.gradle（Gradle项目）
        File gradleFile = new File(dir, "build.gradle");
        if (gradleFile.exists()) {
            return true;
        }

        return false;
    }

    /**
     * 检查目录下是否存在pom.xml
     */
    private boolean hasPomXml(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }
        return new File(dir, "pom.xml").exists();
    }

    /**
     * 判断模块路径是否有效
     */
    private boolean isValidModulePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        File moduleDir = new File(path);
        return isModuleDirectory(moduleDir);
    }
}