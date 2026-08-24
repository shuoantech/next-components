<#-- 模板参数说明：
    mapperPackage: Mapper接口的包名
    mapperClassName: Mapper类名
    entityPackage: 实体类包名
    entityClassName: 实体类名
    tableName: 数据库表名
    baseColumnList: 基础字段列表
    columns: 字段列表（包含javaProperty, columnName, jdbcType, javaType）
    keywordColumns: 参与关键词搜索的字段列表
-->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPackage}.${mapperClassName}">

    <!-- ========== 基础结果映射 ========== -->
    <resultMap id="BaseResultMap" type="${entityPackage}.${entityClassName}">
    <#list columns as column>
        <result column="${column.columnName}" property="${column.javaProperty}" jdbcType="${column.jdbcType}"/>
    </#list>
    </resultMap>

    <!-- ========== 基础字段列表 ========== -->
    <sql id="Base_Column_List">
        <#list columns as column>${column.columnName}<#if column_has_next>, </#if></#list>
    </sql>

    <!-- ========== 分页查询条件构建 ========== -->
    <sql id="Page_Where_Clause">
        <where> is_deleted='N'
            <!-- 1. 实体类对象条件：遍历所有字段，自动生成非空判断 -->
            <if test="condition != null">
                <#list columns as column>
                    <#if column.javaType == 'java.lang.String'>
                <if test="condition.${column.javaProperty} != null and condition.${column.javaProperty} != ''">
                    AND ${column.columnName} = ${r"#{condition."}${column.javaProperty}${r"}"}
                </if>
                    <#else>
                <if test="condition.${column.javaProperty} != null">
                    AND ${column.columnName} = ${r"#{condition."}${column.javaProperty}${r"}"}
                </if>
                    </#if>
                </#list>
            </if>

            <!-- 2. 全局关键词搜索
            <#if keywordColumns?? && keywordColumns?size gt 0>
            <if test="keyword != null and keyword != ''">
                AND (
                <#list keywordColumns as column>
                    ${column} LIKE CONCAT('%', ${r"#{keyword}"}, '%')
                    <#if column_has_next> OR </#if>
                </#list>
                )
            </if>
            </#if>
            -->

            <!-- 3. 时间范围查询 -->
            <if test="startTime != null and startTime != ''">
                AND gmt_created <![CDATA[ >= ]]> ${r"#{startTime}"}
            </if>
            <if test="endTime != null and endTime != ''">
                AND gmt_created <![CDATA[ <= ]]> ${r"#{endTime}"}
            </if>

            <!-- 4. 动态SQL条件 -->
            <if test="dynamicConditions != null and dynamicConditions != ''">
                ${r"${dynamicConditions}"}
            </if>
        </where>
    </sql>

    <!-- ========== 分页查询 ========== -->
    <select id="selectByPage" resultMap="BaseResultMap" parameterType="com.example.common.PageParam">
        SELECT
        <include refid="Base_Column_List" />
        FROM ${tableName}
        <include refid="Page_Where_Clause" />

        <!-- 排序处理 -->
        <if test="orderByColumn != null and orderByColumn != ''">
            ORDER BY ${r"${orderByColumn}"} ${r"${orderByType}"}
        </if>
    </select>

    <!-- ========== 统计总数 ========== -->
    <select id="countByPage" resultType="java.lang.Long" parameterType="com.example.common.PageParam">
        SELECT COUNT(1)
        FROM ${tableName}
        <include refid="Page_Where_Clause" />
    </select>

    <!-- ========== 批量插入 ========== -->
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO ${tableName} (
            <include refid="Base_Column_List" />
        ) VALUES
        <foreach collection="list" item="item" index="index" separator=",">
            (<#list columns as column>${r"#{item."}${column.javaProperty}${r"}"}<#if column_has_next>, </#if></#list>)
        </foreach>
    </insert>



</mapper>