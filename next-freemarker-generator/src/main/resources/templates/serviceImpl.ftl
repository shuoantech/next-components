package ${servicePackage}.impl;

import ${entityPackage}.${modelType};
import ${servicePackage?replace('service', 'model')}.${modelTypeDtoType};
import ${servicePackage?replace('service', 'model')}.${modelTypeAddDtoType};
import ${mapperPackage}.${mapperType};
import ${servicePackage}.${serviceType};
import ${servicePackage?replace('service', 'converter')}.${modelTypeConverter};
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import com.qiwumind.next.components.core.bean.*;
import com.qiwumind.next.components.util.bean.BeanMapperUtils;
import com.qiwumind.next.components.core.exception.BusinessRuntimeException;
import java.time.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
/**
 * ${tableRemark!''} 服务实现类
 * @author Auto Generator
 * @date 创建时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 */
@Service
@RequiredArgsConstructor
public class ${serviceImplType} implements ${serviceType} {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private final ${mapperType} ${mapperType?uncap_first};
    private final ${modelTypeConverter} ${modelTypeConverter?uncap_first};

    // ========== 分页查询方法 ==========
    @Override
    public PageResult<${modelTypeDtoType}> listByDeepPageWithCondition(int pageNum, int pageSize,
                                                                 ${modelTypeDtoType} condition,
                                                                 String orderBy,
                                                                 Date startTime,
                                                                 Date endTime) {
        // 参数校验
        Assert.isTrue(pageNum > 0, "页码必须大于0");
        Assert.isTrue(pageSize > 0 && pageSize <= 100, "每页大小必须在1-100之间");
        // 方式1：使用实体类对象作为查询条件
        ${modelType} queryParam=BeanMapperUtils.map(condition,${modelType}.class);
        PageParam<${modelType}> pageParam = new PageParam<>(queryParam, pageNum, pageSize);
        // 执行查询
        List<${modelType}> list =  ${mapperClassName}.selectByDeepPage(pageParam);
        long total=  ${mapperClassName}.countByDeepPage(pageParam);
        List<${modelTypeDtoType}> resultList =${modelTypeConverter?uncap_first}.toVoList(list);
        return PageResult.data(resultList,total,pageNum,pageSize);
    }

    @Override
    public PageResult<${modelTypeDtoType}> listByPage(int pageNum, int pageSize) {
        return listByDeepPageWithCondition(pageNum, pageSize, null, null, null, null);
    }

    @Override
    public PageResult<${modelTypeDtoType}> listByPageWithParams(int pageNum, int pageSize, ${modelTypeDtoType} params) {
        return listByDeepPageWithCondition(pageNum, pageSize, params, null, null, null);
    }

    <#if queryColumns?? && queryColumns?size gt 0>
    @Override
    public List<${modelTypeDtoType}> selectByConditions(${modelTypeDtoType} condition) {

        ${modelType} params =${modelTypeConverter?uncap_first}.toEntity(condition);
        List<${modelType}> list =  ${mapperClassName}.selectByCondition(params);
        List<${modelTypeDtoType}> resultList =${modelTypeConverter?uncap_first}.toVoList(list);
        return resultList;
    }
    </#if>

    <#if queryColumns?? && queryColumns?size gt 0>
    @Override
    public Long selectCountByConditions(${modelTypeDtoType} condition) {
        Map<String, Object> params = new HashMap<>();
        <#list queryColumns as column>
        if (condition.get${column.javaProperty?cap_first}() != null) {
            params.put("${column.javaProperty}", condition.get${column.javaProperty?cap_first}());
        }
        </#list>
        Long count =  ${mapperClassName}.countByCondition(params);
        return count;
    }
    </#if>

    <#if queryColumns?? && queryColumns?size gt 0>
    @Override
    public List<${modelType}> selectByLikeCondition(${modelTypeDtoType} condition) {
        ${modelType} entity =${modelTypeConverter?uncap_first}.toEntity(condition);
        List<${modelType}> list =  ${mapperClassName}.selectByCondition(entity);
        return list;
    }
    </#if>

    @Override
    public ${modelType} selectOneByCondition(${modelTypeDtoType} condition){
        ${modelType} entity =${modelTypeConverter?uncap_first}.toEntity(condition);
        return  ${mapperClassName}.selectOneByCondition(entity);
    }

    @Override
    public ${modelTypeDtoType} getById(${pkJavaType} ${pkJavaProperty}) {
       Assert.notNull(${pkJavaProperty}, "主键不能为空");
       ${modelType} model= ${mapperClassName}.selectByPrimaryKey(${pkJavaProperty});
       ${modelTypeDtoType} result= ${modelTypeConverter?uncap_first}.toDto(model);
       return result;
    }

    @Override
    public int save(${modelTypeAddDtoType} ${modelVarName}) {
        Assert.notNull(${modelVarName}, "保存对象不能为空");
        ${modelType} moduleAdd=BeanMapperUtils.map(${modelVarName},${modelType}.class);
        ${modelTypeDtoType} moduleDto=BeanMapperUtils.map(${modelVarName},${modelTypeDtoType}.class);

        // TODO 这里查询参数根据实际情况进行调整
        long qryCount=this.selectCountByConditions(moduleDto);
        if(qryCount>0){
            logger.info("根据查询条件{} 数据已经存在，不允许新增，请核对数据后操作",BaseDTO.toString(moduleDto));
            return 0;
        }

        <#if pkJavaProperty?? && (pkJavaType == 'Long'|| pkJavaType == 'Integer')>
             <#if !pkAutoIncrement >
        if(${modelVarName}.get${pkJavaProperty?cap_first}()==null){
            long id=sequence.nextValue();
            ${modelVarName}.set${pkJavaProperty?cap_first}(${pkJavaProperty});
        }

        int count=${mapperClassName}.insertSelective(moduleAdd);
        if(count<1){
            throw new BusinessRuntimeException("999999","数据插入异常");
        }
        return id;
            </#if>
        int count=${mapperClassName}.insertSelective(moduleAdd);
        if(count<1){
            throw new BusinessRuntimeException("999999","数据插入异常");
        }
        return count;
        </#if>

    }

    @Override
    public int update(${modelTypeDtoType} ${modelVarName}) {
        Assert.notNull(${modelVarName}, "更新对象不能为空");
        <#if pkJavaProperty??>
        Assert.notNull(${modelVarName}.getId(), "主键不能为空");
        ${modelType} moduleUpdate=BeanMapperUtils.map(${modelVarName},${modelType}.class);
        ${modelType} existing = ${mapperClassName}.selectByPrimaryKey(moduleUpdate.getId());
        Assert.notNull(existing, "更新的数据不存在");
        </#if>

        return ${mapperClassName}.updateByPrimaryKeySelective(moduleUpdate);
    }

    @Override
    public int logicalDeleteById(${pkJavaType} ${pkJavaProperty}) {
        Assert.notNull(${pkJavaProperty}, "主键不能为空");

        <#if pkJavaProperty??>
        ${modelType} existing = ${mapperClassName}.selectByPrimaryKey(${pkJavaProperty});
        Assert.notNull(existing, "删除的数据不存在");
        </#if>
        ${modelType} ${modelVarName} =new ${modelType}();
        ${modelVarName}.setId(${pkJavaProperty});
        ${modelVarName}.setIsDeleted("Y");
        ${modelVarName}.setGmtModified(LocalDateTime.now());
        return ${mapperClassName}.updateByPrimaryKeySelective(${modelVarName});
    }

    @Override
    public int batchInsert(List<${modelTypeDtoType}> list) {
        Assert.notEmpty(list, "保存列表不能为空");
       // List<${modelType}> resultList =BeanMapperUtils.mapList(list,${modelType}.class);
        List<${modelType}> resultList =${modelTypeConverter?uncap_first}.toEntityList(list);

        return ${mapperClassName}.batchInsert(resultList);
    }


}