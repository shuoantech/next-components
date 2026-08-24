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

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

public class EnhancedPaginationPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        // 获取实体类类型
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // 注意：PageParam使用了泛型，需要完整导入
        FullyQualifiedJavaType pageParamType = new FullyQualifiedJavaType(
                "com.qiwumind.next.components.common.dto.PageParam<" + entityType.getShortName() + ">"
        );
        // 添加分页查询方法
        addSelectByPageMethod(interfaze, entityType, pageParamType);
        // 添加统计方法
        addCountByPageMethod(interfaze, pageParamType);

        addSelectByConditionMethod(interfaze, entityType);
        // 添加批量操作方法
        addBatchMethods(interfaze, entityType);
        // 添加必要的导入
        addImports(interfaze);
        return super.clientGenerated(interfaze, introspectedTable);
    }

    /**
     * 添加完整分页查询方法
     */
    private void addSelectByPageMethod(Interface interfaze, FullyQualifiedJavaType entityType,
                                       FullyQualifiedJavaType pageParamType) {
        // 返回值 List<Entity>
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(entityType);

        Method deepmethod = new Method("selectByDeepPage");
        deepmethod.setVisibility(JavaVisibility.PUBLIC);
        deepmethod.setReturnType(returnType);
        deepmethod.setAbstract(true); // 设置为抽象方法，确保没有方法体
        // 添加参数和注解
        Parameter deepparam = new Parameter(pageParamType, "pageParam");
//        deepparam.addAnnotation("@Param(\"pageParam\")");
        deepmethod.addParameter(deepparam);
        // 添加方法注释
        deepmethod.addJavaDocLine("/**");
        deepmethod.addJavaDocLine(" * 高效分页查询");
        deepmethod.addJavaDocLine(" * @param pageParam 分页参数（包含查询条件和分页信息）");
        deepmethod.addJavaDocLine(" * @return 分页结果列表");
        deepmethod.addJavaDocLine(" */");
//        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        interfaze.addMethod(deepmethod);

    }

    private void addCountByPageMethod(Interface interfaze,
                                      FullyQualifiedJavaType pageParamType) {
        // 注意：PageParam使用了泛型，需要完整导入
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("java.lang.Long");
        Method method = new Method("countByDeepPage");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(returnType);
        method.setAbstract(true); // 设置为抽象方法，确保没有方法体

        Parameter param = new Parameter(pageParamType, "pageParam");
//        param.addAnnotation("@Param(\"pageParam\")");
        method.addParameter(param);
        // 添加方法注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 统计分页查询总数");
        method.addJavaDocLine(" * @param pageParam 分页参数（包含查询条件）");
        method.addJavaDocLine(" * @return 总记录数");
        method.addJavaDocLine(" */");

        interfaze.addMethod(method);


    }

    /**
     * 添加完整分页查询方法
     */
    private void addSelectByConditionMethod(Interface interfaze, FullyQualifiedJavaType entityType) {
        // 返回值 List<Entity>
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(entityType);

        Method method = new Method("selectByCondition");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(returnType);
        method.setAbstract(true); // 设置为抽象方法，确保没有方法体

        // 注意：PageParam使用了泛型，需要完整导入
        FullyQualifiedJavaType pageParamType = new FullyQualifiedJavaType("java.util.Map<String,Object>");
        Parameter param = new Parameter(pageParamType, "map");
        method.addParameter(param);

        // 添加方法注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * list 查询");
        method.addJavaDocLine(" * @param map 参数（包含查询条件 ）");
        method.addJavaDocLine(" * @return 结果列表");
        method.addJavaDocLine(" */");

        interfaze.addMethod(method);

    }

    private void addBatchMethods(Interface interfaze, FullyQualifiedJavaType entityType) {
        // 批量插入
        Method batchInsert = new Method("batchInsert");
        batchInsert.setVisibility(JavaVisibility.PUBLIC);

        batchInsert.setReturnType(FullyQualifiedJavaType.getIntInstance());
        batchInsert.setAbstract(true); // 设置为抽象方法

        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        listType.addTypeArgument(entityType);
        Parameter param = new Parameter(listType, "list");
        param.addAnnotation("@Param(\"list\")");
        batchInsert.addParameter(param);

        batchInsert.addJavaDocLine("/**");
        batchInsert.addJavaDocLine(" * 批量插入");
        batchInsert.addJavaDocLine(" * @param list 实体列表");
        batchInsert.addJavaDocLine(" * @return 影响行数");
        batchInsert.addJavaDocLine(" */");


        interfaze.addMethod(batchInsert);


//        // 批量更新
//        Method batchUpdate = new Method("batchUpdate");
//        batchUpdate.setVisibility(JavaVisibility.PUBLIC);
//        batchUpdate.setReturnType(FullyQualifiedJavaType.getIntInstance());
//        batchUpdate.addParameter(new Parameter(listType, "list"));
//        interfaze.addMethod(batchUpdate);

        // 批量删除
//        Method batchDelete = new Method("batchDelete");
//        batchDelete.setVisibility(JavaVisibility.PUBLIC);
//        batchDelete.setReturnType(FullyQualifiedJavaType.getIntInstance());
//        batchDelete.setAbstract(true); // 设置为抽象方法
//
//        FullyQualifiedJavaType idListType = FullyQualifiedJavaType.getNewListInstance();
//        idListType.addTypeArgument(FullyQualifiedJavaType.getCriteriaInstance());
//
//        Parameter paramdel = new Parameter(idListType, "ids");
//        paramdel.addAnnotation("@Param(\"list\")");
//        batchDelete.addParameter(paramdel);
//
//        batchDelete.addJavaDocLine("/**");
//        batchDelete.addJavaDocLine(" * 批量删除（根据主键）");
//        batchDelete.addJavaDocLine(" * @param ids 主键ID列表");
//        batchDelete.addJavaDocLine(" * @return 影响行数");
//        batchDelete.addJavaDocLine(" */");
//
//
//        interfaze.addMethod(batchDelete);
    }

    private void addImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        interfaze.addImportedType(new FullyQualifiedJavaType("com.qiwumind.next.components.common.dto.PageParam"));


    }
}