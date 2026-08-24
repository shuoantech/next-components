package ${servicePackage?replace('service', 'model')};

import com.qiwumind.next.components.core.bean.BaseEntity;
import java.io.Serializable;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
* ${modelTypeDtoType} 数据传输对象DTO
* @author Auto Generator
* @date 创建时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}
*/
public class ${modelTypeDtoType} extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    <#list columns as column>
     /** ${column.remarks}  */
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
        return "${modelTypeDtoType} {" +
                <#list columns as column>
                "${column.javaProperty}=" + ${column.javaProperty} +
                </#list>
                "}";
    }
}
