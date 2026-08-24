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

package com.qiwumind.next.components.freemarker.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

public class CustomCommentGenerator extends DefaultCommentGenerator {

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {
        // 获取数据库表的注释
        String tableRemarks = introspectedTable.getRemarks();
        if (StringUtility.stringHasValue(tableRemarks)) {
            // 在实体类上添加 Javadoc 注释
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" * " + tableRemarks);
            // 可以添加其他自定义信息，如作者、日期等
            topLevelClass.addJavaDocLine(" * @author MyBatis Generator");
            topLevelClass.addJavaDocLine(" */");
        }
    }

    // 也可以重写 addFieldComment 方法，为字段添加注释
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String columnRemarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(columnRemarks)) {
            field.addJavaDocLine("/** " + columnRemarks + " */");
        }
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (introspectedColumn == null || method == null) {
            return;
        }
        // 获取字段基本信息
        String remarks = introspectedColumn.getRemarks();
        String javaProperty = introspectedColumn.getJavaProperty();
        // 决定使用什么作为注释（按优先级）
        String commentText;
        if (remarks != null && !remarks.trim().isEmpty()) {
            commentText = remarks;  // 优先使用数据库注释
        } else {
            commentText = javaProperty;  // 降级使用Java属性名
        }
        // 生成注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * @return " + commentText);
        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (introspectedColumn == null || method == null) {
            return;
        }
        // 获取字段注释
        String remarks = introspectedColumn.getRemarks();
        String propertyName = introspectedColumn.getJavaProperty();

        method.addJavaDocLine("/**");
        if (remarks != null && !remarks.isEmpty()) {
            method.addJavaDocLine(" * 设置" + remarks);
            method.addJavaDocLine(" * @param " + propertyName + " " + remarks);
        } else {
            method.addJavaDocLine(" * 设置" + propertyName);
            method.addJavaDocLine(" * @param " + propertyName + " " + propertyName);
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        // Mapper方法注释 - 只对关键方法添加简单说明
        String methodName = method.getName();
        if (methodName.startsWith("select") || methodName.startsWith("insert") ||
                methodName.startsWith("update") || methodName.startsWith("delete")) {
            method.addJavaDocLine("/** MyBatis Generator " + methodName + " */");
        }
    }
}