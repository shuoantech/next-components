<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPackage}.${mapperClassName}">


    <!-- ========== 查询条件构建 ========== -->
    <sql id="Where_Clause">
        <!-- 1. 实体类对象条件：遍历所有字段，自动生成非空判断 -->
        <#list columns as column>
            <#if column.javaType == 'java.lang.String'>
        <if test="${column.javaProperty} != null and ${column.javaProperty} != ''">
            AND ${column.columnName} = ${r"#{"}${column.javaProperty}${r"}"}
        </if>
            <#else>
        <if test="${column.javaProperty} != null">
            AND ${column.columnName} = ${r"#{"}${column.javaProperty}${r"}"}
        </if>
            </#if>
        </#list>
        <!-- 3. 时间范围查询 -->
        <if test="startTime != null and startTime != ''">
            AND gmt_created <![CDATA[ >= ]]> ${r"#{startTime}"}
        </if>
        <if test="endTime != null and endTime != ''">
            AND gmt_created <![CDATA[ <= ]]> ${r"#{endTime}"}
        </if>
    </sql>


    <!-- ========== 模糊查询条件构建 ========== -->
    <sql id="Like_Where_Clause">
       <!-- 1. 实体类对象条件：遍历所有字段，自动生成非空判断 -->
       <#list columns as column>
           <#if column.javaType == 'java.lang.String'>
       <if test="${column.javaProperty} != null and ${column.javaProperty} != ''">
           AND ${column.columnName} like  CONCAT(${r"#{"}${column.javaProperty}${r"}"}, '%')
       </if>
           <#else>
       <if test="${column.javaProperty} != null">
           AND ${column.columnName} like  CONCAT(${r"#{"}${column.javaProperty}${r"}"}, '%')
       </if>
           </#if>
       </#list>
    </sql>

    <!-- ========== 分页查询条件构建 ========== -->
    <sql id="Deep_Page_Where_Clause">
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
    </sql>

    <!-- 深度分页优化查询 -->
    <select id="selectByDeepPage" parameterType="com.qiwumind.next.components.core.bean.PageParam"
        resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List" />
        FROM ${tableName}
        where id <![CDATA[ >= ]]> (
            select id from ${tableName}
            where is_deleted='N'
            <include refid="Deep_Page_Where_Clause" />
            <if test="orderByColumn != null and orderByColumn != '' ">
               ORDER BY ${r"${orderByColumn}"} ${r"${orderByType}"}
            </if>
            limit  ${r"${cursorOffset}"} ,1
        )
        <include refid="Deep_Page_Where_Clause" />
        limit 0, ${r"${pageSize}"}
    </select>

    <!--  深度分页优化查询统计总数   -->
    <select id="countByDeepPage" resultType="java.lang.Long"
        parameterType="com.qiwumind.next.components.core.bean.PageParam">
        SELECT COUNT(*)
        FROM ${tableName}
        where is_deleted='N'
        <include refid="Deep_Page_Where_Clause" />
    </select>

    <!-- ========== 列表查询 ========== -->
    <select id="selectByCondition" resultMap="BaseResultMap"
        parameterType="${entityPackage}.${modelType}">
        SELECT
        <include refid="Base_Column_List" />
        FROM ${tableName}
        where is_deleted='N'
        <include refid="Where_Clause" />
        <if test="orderBy != null and orderBy != '' ">
           order by ${r"${orderBy}"}
        </if>
    </select>

      <!-- ========== 单个对象查询 ========== -->
    <select id="selectOneByCondition" resultMap="BaseResultMap"
        parameterType="${entityPackage}.${modelType}">
        SELECT
        <include refid="Base_Column_List" />
        FROM ${tableName}
        where is_deleted='N'
        <include refid="Where_Clause" />
        limit 1
    </select>

    <!--  根据条件查询总数  -->
    <select id="countByCondition" resultType="java.lang.Long"
        parameterType="${entityPackage}.${modelType}">
        SELECT COUNT(*)
        FROM ${tableName}
        where is_deleted='N'
        <include refid="Where_Clause" />
    </select>

     <!-- ========== 列表查询 ========== -->
    <select id="selectByLikeCondition" resultMap="BaseResultMap"
        parameterType="${entityPackage}.${modelType}">
        SELECT
        <include refid="Base_Column_List" />
        FROM ${tableName}
        where is_deleted='N'
        <include refid="Like_Where_Clause" />
        <if test="orderBy != null and orderBy != '' ">
           order by ${r"${orderBy}"}
        </if>
    </select>

    <!-- ========== 批量插入 ========== -->
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO ${tableName} (
            <include refid="Base_Column_List" />
        ) VALUES
        <foreach collection="list" item="item" index="index" separator=",">
            (<#list columns as column>
            ${r"#{item."}${column.javaProperty}${r"}"}<#if column_has_next>,</#if>
            </#list>)
        </foreach>
    </insert>

</mapper>