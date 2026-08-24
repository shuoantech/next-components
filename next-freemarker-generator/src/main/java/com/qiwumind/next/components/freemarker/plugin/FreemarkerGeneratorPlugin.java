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

package com.qiwumind.next.components.freemarker.plugin;

import com.qiwumind.next.components.freemarker.util.ModuleUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * 多module环境下智能路径识别的代码生成插件
 */
public class FreemarkerGeneratorPlugin extends PluginAdapter {

    private Configuration freemarkerConfig;
    private String basePackage;
    private String controllerPackage;
    private String servicePackage;
    private String dtoPackage;
    private String converterDtoPackage;
    private String serviceImplPackage;
    // 当前模块的根路径
    private String modulePath;

    // 模板相对路径
    private static final String TEMPLATE_PATH = "src/main/resources/templates";

    public FreemarkerGeneratorPlugin() {
        // 1. 获取当前模块路径
        ModuleUtils moduleUtils = new ModuleUtils();
        modulePath = moduleUtils.getModulePath();
        System.out.println("📁 检测到的基础模块路径: " + modulePath);
        // 2. 初始化Freemarker
        initFreemarker();
        System.out.println("========== 初始化完成 ==========\n");
    }

    /**
     * 初始化Freemarker配置
     */
    private void initFreemarker() {
        freemarkerConfig = new Configuration();
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setNumberFormat("computer");
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(
                FreemarkerGeneratorPlugin.class,  // 使用当前类
                "/templates"                 // 模板文件所在的基础路径
        );
        freemarkerConfig.setTemplateLoader(templateLoader);
        // 3. （可选）设置模板更新延迟，生产环境可以设大一点
        freemarkerConfig.setTemplateUpdateDelayMilliseconds(3600000); // 1小时
        // 4. （可选）设置默认编码
        freemarkerConfig.setDefaultEncoding("UTF-8");

    }

    @Override
    public boolean validate(List<String> warnings) {
        basePackage = properties.getProperty("targetProjectPackage");
        System.out.println("📦 基础包名: " + basePackage);
        String module = properties.getProperty("module");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(module) && !module.equals(modulePath)) {
            modulePath = modulePath + "/" + module;
        }
        System.out.println("📁 模块路径: " + modulePath);

        boolean valid = StringUtility.stringHasValue(basePackage);
        if (valid) {

            dtoPackage = properties.getProperty("dtoPackage",basePackage + ".domain.model");
            converterDtoPackage = properties.getProperty("converterDtoPackage",basePackage + ".domain.converter");
            controllerPackage = properties.getProperty("controllerPackage", basePackage + ".interfaces.controller");
            servicePackage = properties.getProperty("servicePackage",basePackage + ".domain.service");
            serviceImplPackage = servicePackage + ".impl";

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
        generateFile("dto.ftl", dataModel,
                javaPath + dtoPackage.replace('.', '/') + "/" + dataModel.get("modelTypeDtoType") + ".java");
        generateFile("dtoAdd.ftl", dataModel,
                javaPath + dtoPackage.replace('.', '/') + "/" + dataModel.get("modelTypeAddDtoType") + ".java");
        generateFile("dtoConverter.ftl", dataModel,
                javaPath + converterDtoPackage.replace('.', '/') + "/" + dataModel.get("modelTypeConverter") + ".java");

        generateFile("controller.ftl", dataModel,
                javaPath + controllerPackage.replace('.', '/') + "/" + dataModel.get("controllerType") + ".java");

        SqlMapGeneratorConfiguration sqlMapGenerator = introspectedTable.getContext().getSqlMapGeneratorConfiguration();
        // 生成文件
        String modelType = modelClass.getType().getShortName();
        String fileName = sqlMapGenerator.getTargetPackage() + "/" + StringUtils.capitalize(modelType) + "Mapper.xml";
        String resourcePath = modulePath + "/src/main/resources/";
        generateFile("mapper-pagination.ftl", dataModel, resourcePath + fileName);

        System.out.println("========== 生成完成 ==========\n");
    }

    private Map<String, Object> prepareDataModel(TopLevelClass modelClass, IntrospectedTable introspectedTable) {
        Map<String, Object> dataModel = new HashMap<>();
        String entityPackage = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String mapperPackage = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        // 包名
        dataModel.put("basePackage", basePackage);

        dataModel.put("controllerPackage", controllerPackage);
        dataModel.put("servicePackage", servicePackage);
        dataModel.put("serviceImplPackage", serviceImplPackage);
        dataModel.put("entityPackage", entityPackage);

        // 类名
        String modelType = modelClass.getType().getShortName();
        String modeName = modelType;
        if (modelType.endsWith("PO")) {
            modeName = modelType.substring(0, modelType.length() - 2);
        }
        if (modelType.endsWith("Entity")) {
            modeName = modelType.substring(0, modelType.length() - 6);
        }
        dataModel.put("modelType", modelType);
        dataModel.put("modelVarName", StringUtils.uncapitalize(modeName));
        dataModel.put("entityClassName", StringUtils.uncapitalize(modelType));

        dataModel.put("mapperPackage", mapperPackage);
        dataModel.put("mapperType", modelType + "Mapper");
        dataModel.put("mapperClassName", StringUtils.uncapitalize(modelType + "Mapper"));

        dataModel.put("serviceType", modeName + "Service");
        dataModel.put("serviceImplType", modeName + "ServiceImpl");
        dataModel.put("modelTypeDtoType", modeName + "DTO");
        dataModel.put("modelTypeAddDtoType", modeName + "AddDTO");
        dataModel.put("modelTypeConverter", modeName + "Converter");

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
            dataModel.put("pkAutoIncrement", column.isAutoIncrement());
        });
        // 日期
        dataModel.put("currentDate", new Date());
        dataModel.put("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        dataModel.put("offset", new Date());

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
        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> queryColumns = new ArrayList<>(); // 常用于查询的字段
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            Map<String, Object> columnInfo = new HashMap<>();
            String javaProperty = column.getJavaProperty();
            String jdbcType = column.getJdbcTypeName();
            String javaType = column.getFullyQualifiedJavaType().getShortName();
            String columnName = column.getActualColumnName();
            String remarks = column.getRemarks();
            columnInfo.put("javaProperty", javaProperty);
            columnInfo.put("jdbcType", jdbcType);
            columnInfo.put("javaType", javaType);
            columnInfo.put("columnName", columnName);
            columnInfo.put("length",  ""+column.getLength());
            columnInfo.put("remarks", remarks != null ? remarks : "");
            columnInfo.put("nullable", column.isNullable());
            columnInfo.put("autoIncrement", column.isAutoIncrement());

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

            try {
                Writer out = new FileWriter(file);
                template.process(dataModel, out);
                System.out.println("   ✅ 生成成功");
            } catch (Exception e) {
//                try (Writer writer = new FileWriter(file)) {
//                    String content = templateLoader.processTemplate(templateName, dataModel);
//                    writer.write(content);
//                }
            }
        } catch (freemarker.template.TemplateNotFoundException e) {
            System.err.println("   ❌ 模板不存在: " + templateName);
            System.err.println("      请确保模板文件存在于以下位置:");
            System.err.println("      - " + TEMPLATE_PATH + "/" + templateName);
        } catch (Exception e) {
            System.err.println("   ❌ 生成失败: " + e.getMessage());
        }
    }
}