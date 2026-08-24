package ${servicePackage?replace('service', 'model')};

import java.io.Serializable;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;

import javax.validation.constraints.*;
<#list columns as column>
    <#if column.javaType == "Date" || column.javaType == "LocalDate" || column.javaType == "LocalDateTime">
import com.fasterxml.jackson.annotation.JsonFormat;
        <#break>
    </#if>
</#list>
/**
* ${modelTypeAddDtoType} 新增DTO
* @author Auto Generator
* @date 创建时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}
*/
public class ${modelTypeAddDtoType}  implements Serializable {

    private static final long serialVersionUID = 1L;
<#-- 遍历所有字段 -->
<#list columns as column>
    <#-- 生成字段注释 -->
    /** ${column.remarks}  <#if !column.nullable> 必填<#else> 可为空，非必填项</#if> */
    <#if !column.nullable>
        <#if column.javaType == "String">
    @NotBlank(message = "${column.javaProperty!}不能为空")
            <#if column.length??  >
    @Size(max = ${column.length}, message = "${(column.remarks)!column.javaProperty}长度不能超过${column.length}个字符")
            </#if>
        <#else>
    @NotNull(message = "${(column.remarks)!column.javaProperty}不能为空")
        </#if>
    <#else>
    <#-- 可选项：为字符串类型添加Size注解 column.length?? && -->
        <#if column.javaType == "String" >
    @Size(max = ${column.length},message = "${(column.remarks)!column.javaProperty}长度不能超过${column.length}个字符")
        </#if>
    </#if>
    <#-- 数字类型的范围验证 -->
    <#if column.javaType == "Long" || column.javaType == "Integer" || column.javaType == "Short" || column.javaType == "Byte">
         <#if !column.nullable>
    @Min(value = 0, message = "${(column.remarks)!column.javaProperty}不能小于0")
        </#if>
    <#elseif column.javaType == "Date" || column.javaType == "LocalDate">
    @JsonFormat(pattern = "yyyy-MM-dd")
    <#elseif column.javaType == "LocalDateTime">
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    </#if>
    private ${column.javaType} ${column.javaProperty};
</#list>

<#list columns as column>
    public ${column.javaType} get${column.javaProperty?cap_first}() {
        return ${column.javaProperty};
    }
    public void set${column.javaProperty?cap_first}(${column.javaType} ${column.javaProperty}) {
        this.${column.javaProperty} = ${column.javaProperty};
    }

</#list>
    @Override
    public String toString() {
        return "${modelTypeAddDtoType}  {" +
                <#list columns as column>
                "${column.javaProperty}=" + ${column.javaProperty} +
                </#list>
                "}";
    }
}
