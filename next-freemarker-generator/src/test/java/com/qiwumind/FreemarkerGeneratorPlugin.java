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

package com.qiwumind;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.*;

/**
 * 多module环境下智能路径识别的代码生成插件
 */
public class FreemarkerGeneratorPlugin extends PluginAdapter {

    private Configuration freemarkerConfig;
    private String basePackage;
    private String controllerPackage;
    private String servicePackage;
    private String serviceImplPackage;
    private String mapperPackage;

    // 当前模块的根路径
    private String modulePath;

    // 模板相对路径
    private static final String TEMPLATE_PATH = "src/main/resources/templates";

    public FreemarkerGeneratorPlugin() {
        System.out.println("\n========== 初始化多module代码生成器 ==========");
        // 1. 获取当前模块路径
        modulePath = detectCurrentModulePath();
        System.out.println("📁 检测到的模块路径: " + modulePath);
        // 2. 初始化Freemarker
        initFreemarker();
        System.out.println("========== 初始化完成 ==========\n");
    }

    /**
     * 检测当前模块的路径
     */
    private String detectCurrentModulePath() {
        // 方法1：通过类路径获取（最准确）
        String classPath = getClassLocation();
        if (classPath != null) {
            System.out.println("📍 类路径: " + classPath);
            String modulePath = extractModulePathFromClassPath(classPath);
            if (modulePath != null && !modulePath.isEmpty()) {
                return modulePath;
            }
        }
        // 方法2：通过user.dir并智能查找
        String userDir = System.getProperty("user.dir");
        System.out.println("📍 user.dir: " + userDir);
        // 尝试查找正确的模块
        String foundModule = findCorrectModule(userDir);
        if (foundModule != null) {
            return foundModule;
        }
        // 方法3：通过系统属性（可以在启动时设置）
//        String projectModule = System.getProperty("project.module");
//        if (projectModule != null && !projectModule.isEmpty()) {
//            File moduleDir = new File(projectModule);
//            if (moduleDir.exists()) {
//                return moduleDir.getAbsolutePath();
//            }
//        }
        // 默认返回user.dir
        return userDir;
    }

    /**
     * 获取当前类的实际位置
     */
    private String getClassLocation() {
        try {
            CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                String path = URLDecoder.decode(location.getPath(), "UTF-8");
                return path;
            }
        } catch (Exception e) {
            System.err.println("获取类路径失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 从类路径中提取模块路径
     */
    private String extractModulePathFromClassPath(String classPath) {
        // 处理不同的路径格式
        File classFile = new File(classPath);
        // 如果在target/classes中
        if (classPath.contains("/target/classes")) {
            return classPath.substring(0, classPath.indexOf("/target/classes"));
        }
        // 如果在target/test-classes中
        if (classPath.contains("/target/test-classes")) {
            return classPath.substring(0, classPath.indexOf("/target/test-classes"));
        }
        // 如果是目录且包含pom.xml
        if (classFile.isDirectory()) {
            File pomFile = new File(classFile, "pom.xml");
            if (pomFile.exists()) {
                return classFile.getAbsolutePath();
            }
            // 向上查找
            return findParentWithPom(classFile);
        }
        return null;
    }

    /**
     * 向上查找包含pom.xml的目录
     */
    private String findParentWithPom(File start) {
        File current = start;
        int maxDepth = 5; // 最多向上查找5级

        for (int i = 0; i < maxDepth && current != null; i++) {
            File pomFile = new File(current, "pom.xml");
            if (pomFile.exists()) {
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
        File startDir = new File(startPath);

        // 如果当前目录就是模块目录
        if (isModuleDirectory(startDir)) {
            return startDir.getAbsolutePath();
        }

        // 查找所有子模块
        File[] subDirs = startDir.listFiles(File::isDirectory);
        if (subDirs != null) {
            // 按优先级查找
            String[] modulePatterns = {"admin", "web", "service", "api", "core", "common"};

            for (String pattern : modulePatterns) {
                for (File subDir : subDirs) {
                    if (subDir.getName().contains(pattern) && isModuleDirectory(subDir)) {
                        System.out.println("✅ 找到匹配的模块: " + subDir.getName());
                        return subDir.getAbsolutePath();
                    }
                }
            }

            // 如果没有匹配模式，返回第一个有效的模块
            for (File subDir : subDirs) {
                if (isModuleDirectory(subDir)) {
                    return subDir.getAbsolutePath();
                }
            }
        }

        return null;
    }

    /**
     * 判断是否是模块目录
     */
    private boolean isModuleDirectory(File dir) {
        if (!dir.isDirectory()) return false;

        // 检查是否有pom.xml
        File pomFile = new File(dir, "pom.xml");
        if (pomFile.exists()) return true;

        // 检查是否有src/main/java
        File srcMainJava = new File(dir, "src/main/java");
        if (srcMainJava.exists()) return true;

        return false;
    }

    /**
     * 初始化Freemarker配置
     */
    private void initFreemarker() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_34);
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setNumberFormat("computer");

        // 查找并设置模板目录
        setupTemplateDirectory();
    }

    /**
     * 设置模板目录
     */
    private void setupTemplateDirectory() {
        System.out.println("\n🔍 查找模板目录...");
        // 可能的模板路径列表
        List<File> possiblePaths = new ArrayList<>();
        // 1. 当前模块下的模板目录
        if (modulePath != null) {
            possiblePaths.add(new File(modulePath, TEMPLATE_PATH));
            possiblePaths.add(new File(modulePath, "src/main/resources/templates"));
            possiblePaths.add(new File(modulePath, "templates"));
        }
        // 2. 父项目中的模板目录
        if (modulePath != null) {
            File parent = new File(modulePath).getParentFile();
            if (parent != null) {
                possiblePaths.add(new File(parent, "qiwumind-common/" + TEMPLATE_PATH));
                possiblePaths.add(new File(parent, "common/" + TEMPLATE_PATH));
                possiblePaths.add(new File(parent, TEMPLATE_PATH));
            }
        }
        // 3. 类路径资源
        try {
            URL resource = getClass().getResource("/templates");
            if (resource != null) {
                possiblePaths.add(new File(resource.getPath()));
            }
        } catch (Exception e) {
            // ignore
        }
        // 尝试每个路径
        for (File path : possiblePaths) {
            System.out.println("  检查: " + path.getAbsolutePath());
            if (path.exists() && path.isDirectory()) {
                try {
                    freemarkerConfig.setDirectoryForTemplateLoading(path);
                    System.out.println("  ✅ 找到模板目录: " + path.getAbsolutePath());
                    // 列出模板文件
                    File[] templates = path.listFiles((dir, name) -> name.endsWith(".ftl"));
                    if (templates != null && templates.length > 0) {
                        System.out.println("  📄 模板文件:");
                        for (File template : templates) {
                            System.out.println("     - " + template.getName());
                        }
                    }
                    return;
                } catch (IOException e) {
                    System.err.println("  ❌ 设置模板目录失败: " + e.getMessage());
                }
            }
        }
        // 如果都没找到，使用类路径加载器
        System.out.println("  ⚠️ 使用类路径加载器作为后备");
        freemarkerConfig.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(),
                "templates/"
        );
    }

    @Override
    public boolean validate(List<String> warnings) {
        System.out.println("\n========== 插件验证 ==========");
        basePackage = properties.getProperty("targetProjectPackage");
        System.out.println("📦 基础包名: " + basePackage);
        System.out.println("📁 模块路径: " + modulePath);
        System.out.println("===============================\n");

        basePackage = properties.getProperty("targetProjectPackage");
        boolean valid = StringUtility.stringHasValue(basePackage);
        if (valid) {
            controllerPackage = basePackage + ".interfaces.controller";
            servicePackage = basePackage + ".domain.service";
            serviceImplPackage = servicePackage + ".impl";
            mapperPackage = basePackage + ".infra.repository.mapper";
        } else {
            if (!StringUtility.stringHasValue(basePackage)) {
                warnings.add(Messages.getString("ValidationError.18", "FreemarkerGeneratorPlugin", "basePackage"));
            }

        }
        return valid;
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {

            generateBusinessLayer(topLevelClass, introspectedTable);
        } catch (Exception e) {
            System.err.println("❌ 生成业务层代码失败: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private void generateBusinessLayer(TopLevelClass modelClass, IntrospectedTable introspectedTable) throws Exception {
        System.out.println("\n========== 开始生成业务层代码 ==========");
        Map<String, Object> dataModel = prepareDataModel(modelClass, introspectedTable);
        // 生成文件
        String javaPath = modulePath + "/src/main/java/";
        generateFile("service.ftl", dataModel,
                javaPath + servicePackage.replace('.', '/') + "/" + dataModel.get("serviceType") + ".java");
        generateFile("serviceImpl.ftl", dataModel,
                javaPath + serviceImplPackage.replace('.', '/') + "/" + dataModel.get("serviceImplType") + ".java");
        generateFile("controller.ftl", dataModel,
                javaPath + controllerPackage.replace('.', '/') + "/" + dataModel.get("controllerType") + ".java");

        SqlMapGeneratorConfiguration sqlMapGenerator= introspectedTable.getContext().getSqlMapGeneratorConfiguration();
        // 生成文件
        String modelType = modelClass.getType().getShortName();
        String fileName =  sqlMapGenerator.getTargetPackage()+ "/" + StringUtils.capitalize(modelType) + "Mapper.xml";
        String resourcePath = modulePath + "/src/main/resources/";
        generateFile("mapper-pagination.ftl", dataModel,
                resourcePath +fileName);
        System.out.println("========== 生成完成 ==========\n");
    }

    private Map<String, Object> prepareDataModel(TopLevelClass modelClass, IntrospectedTable introspectedTable) {
        Map<String, Object> dataModel = new HashMap<>();
        String entityPackage = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String mapperPackage = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
//        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        // 包名
        dataModel.put("basePackage", basePackage);

        dataModel.put("controllerPackage", controllerPackage);
        dataModel.put("servicePackage", servicePackage);
        dataModel.put("serviceImplPackage", serviceImplPackage);
        dataModel.put("entityPackage", entityPackage);

        // 类名
        String modelType = modelClass.getType().getShortName();
        String modeName=modelType;
        if(modelType.endsWith("PO")){
            modeName=modelType.substring(0,modelType.length()-2);
        }
        if(modelType.endsWith("Entity") ){
            modeName=modelType.substring(0,modelType.length()-6);
        }
        dataModel.put("modelType", modelType);
        dataModel.put("modelVarName", StringUtils.uncapitalize(modeName));
        dataModel.put("entityClassName", StringUtils.uncapitalize(modelType));

        dataModel.put("mapperPackage", mapperPackage);
        dataModel.put("mapperType", modelType + "Mapper");
        dataModel.put("mapperClassName", StringUtils.uncapitalize(modelType + "Mapper"));

        dataModel.put("serviceType", modeName + "Service");
        dataModel.put("serviceImplType", modeName + "ServiceImpl");
        dataModel.put("controllerType", modeName + "Controller");
        dataModel.put("serviceVarName", StringUtils.uncapitalize(modeName + "Service"));
        // 表信息
        dataModel.put("tableName", introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        dataModel.put("tableRemark", introspectedTable.getRemarks() != null ? introspectedTable.getRemarks() : "");
        // 主键
        introspectedTable.getPrimaryKeyColumns().stream().findFirst().ifPresent(column -> {
            dataModel.put("pkJavaProperty", column.getJavaProperty());
            dataModel.put("pkJdbcType", column.getJdbcTypeName());
            dataModel.put("pkJavaType", column.getFullyQualifiedJavaType().getShortName());
        });
        // 日期
        dataModel.put("currentDate", new Date());
        dataModel.put("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        dataModel.put("offset", new Date());
        dataModel.put("currentDate", new Date());

        // ========== 新增：处理所有字段信息，用于分页查询 ==========
        processAllColumns(dataModel, introspectedTable);
        // ========== 新增：添加分页查询所需的默认排序字段 ==========
        dataModel.put("defaultOrderBy", determineDefaultOrderBy(dataModel));

        System.out.println("生成表: " + dataModel.get("tableName") + " -> " + modelType);

        return dataModel;
    }



    /**
     * 处理所有字段信息，用于生成查询条件
     */
    private void processAllColumns(Map<String, Object> dataModel, IntrospectedTable introspectedTable) {
        List<Map<String, String>> columns = new ArrayList<>();
        List<Map<String, String>> queryColumns = new ArrayList<>(); // 常用于查询的字段
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            Map<String, String> columnInfo = new HashMap<>();
            String javaProperty = column.getJavaProperty();
            String jdbcType = column.getJdbcTypeName();
            String javaType = column.getFullyQualifiedJavaType().getShortName();
            String columnName = column.getActualColumnName();
            String remarks = column.getRemarks();
            columnInfo.put("javaProperty", javaProperty);
            columnInfo.put("jdbcType", jdbcType);
            columnInfo.put("javaType", javaType);
            columnInfo.put("columnName", columnName);
            columnInfo.put("remarks", remarks != null ? remarks : "");
            columns.add(columnInfo);
            // 判断是否常用查询字段（可以根据字段名或类型判断）
            if (isCommonQueryField(javaProperty, javaType, columnName)) {
                queryColumns.add(columnInfo);
            }
        }
        dataModel.put("columns", columns);
        dataModel.put("queryColumns", queryColumns);
//        dataModel.put("keywordColumns", queryColumns);

        dataModel.put("hasQueryColumns", !queryColumns.isEmpty());
    }

    /**
     * 判断是否为常用查询字段
     */
    private boolean isCommonQueryField(String javaProperty, String javaType, String columnName) {
        String lowerProperty = javaProperty.toLowerCase();
        String lowerColumn = columnName.toLowerCase();

        // 常见的查询字段名
        String[] commonQueryFields = {
                "name", "title", "code", "type", "status",
                "is_deleted", "creator", "gmt_created", "modifier", "gmt_modified"
        };
        for (String field : commonQueryFields) {
            if (lowerProperty.contains(field) || lowerColumn.contains(field)) {
                return true;
            }
        }
        // 字符串类型通常可用于模糊查询
        if ("String".equals(javaType) &&
                !"id".equalsIgnoreCase(lowerProperty) &&
                !lowerProperty.endsWith("id")) {
            return true;
        }

        return false;
    }

    /**
     * 确定默认排序字段
     */
    private String determineDefaultOrderBy(Map<String, Object> dataModel) {
        List<Map<String, String>> columns = (List<Map<String, String>>) dataModel.get("columns");
        if (columns != null) {
            // 优先使用创建时间
            for (Map<String, String> column : columns) {
                String prop = column.get("javaProperty");
                if ("createTime".equals(prop) || "create_time".equals(prop) ||
                        "gmtCreated".equals(prop) || "gmt_created".equals(prop)) {
                    return prop + " DESC";
                }
            }
            // 其次使用更新时间
            for (Map<String, String> column : columns) {
                String prop = column.get("javaProperty");
                if ("updateTime".equals(prop) || "update_time".equals(prop) ||
                        "gmtModified".equals(prop) || "gmt_modified".equals(prop)) {
                    return prop + " DESC";
                }
            }
            // 然后使用主键
            String pk = (String) dataModel.get("pkJavaProperty");
            if (pk != null) {
                return pk + " DESC";
            }
            // 最后使用第一个字段
            if (!columns.isEmpty()) {
                return columns.get(0).get("javaProperty") + " ASC";
            }
        }

        return "id DESC";
    }

    private void generateFile(String templateName, Map<String, Object> dataModel, String filePath) {
        System.out.println(" 📝 生成文件: " + templateName);
        System.out.println("   目标路径: " + filePath);
        try {
            Template template = freemarkerConfig.getTemplate(templateName);

            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (Writer out = new FileWriter(file)) {
                template.process(dataModel, out);
                System.out.println("   ✅ 生成成功");
            }
        } catch (freemarker.template.TemplateNotFoundException e) {
            System.err.println("   ❌ 模板不存在: " + templateName);
            System.err.println("      请确保模板文件存在于以下位置:");
            System.err.println("      - " + modulePath + "/" + TEMPLATE_PATH + "/" + templateName);
        } catch (Exception e) {
            System.err.println("   ❌ 生成失败: " + e.getMessage());
        }
    }
}