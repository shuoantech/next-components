<#-- 通用转换器模板 -->
package ${servicePackage?replace('service', 'converter')};

import ${servicePackage?replace('service', 'model')}.${modelTypeDtoType};
import ${servicePackage?replace('service', 'model')}.${modelTypeAddDtoType};
import ${entityPackage}.${modelType};
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * $!{businessName}对象转换器
 *
* @author Auto Generator
* @date 创建时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 */
@Mapper(componentModel = "spring")
public interface ${modelTypeConverter}{
    /**
     * 将表单对象转换为实体对象
     *
     * @param formData 表单对象
     * @return 实体对象
     */
    ${modelType} toEntity(${modelTypeAddDtoType} formData);
    /**
     * 将dto对象转换为实体对象
     *
     * @param formData 对象
     * @return 实体对象
     */
    ${modelType} toEntity(${modelTypeDtoType} formData);
    /**
     * 将实体对象转换为dto对象
     *
     * @param  entity 实体对象
     * @return 表单对象
     */
    ${modelTypeDtoType} toDto(${modelType} entity);

     /**
     * 将实体对象列表转换为VO对象列表
     *
     * @param entityList 实体对象列表
     * @return VO对象列表
     */
    List<${modelTypeDtoType}> toVoList(List<${modelType}> entityList);

    /**
     * 将表单对象列表转换为实体对象列表
     *
     * @param formList 表单对象列表
     * @return 实体对象列表
     */
    List<${modelType}> toEntityList(List<${modelTypeDtoType}> formList);
}