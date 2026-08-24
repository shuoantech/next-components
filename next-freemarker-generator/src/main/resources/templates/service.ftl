package ${servicePackage};

import ${entityPackage}.${modelType};
import ${servicePackage?replace('service', 'model')}.${modelTypeDtoType};
import ${servicePackage?replace('service', 'model')}.${modelTypeAddDtoType};
import java.util.Date;
import java.util.List;
import com.qiwumind.next.components.core.bean.*;

/**
 * ${tableRemark!''} 服务接口
 * @author Auto Generator
 */
public interface ${serviceType} {

   /**
      * 深度分页查询${tableRemark!''} 数据量大时查询高效，比 PageHelper 优 带条件的分页查询
      * @param pageNum 页码
      * @param pageSize 每页大小
      * @param condition 查询条件
      * @param orderBy 排序字段
      * @param startTime 开始时间
      * @param endTime 结束时间
      * @return 分页结果
      */
   PageResult<${modelTypeDtoType}> listByDeepPageWithCondition(int pageNum, int pageSize,
                                                      ${modelTypeDtoType} condition,
                                                      String orderBy,
                                                      Date startTime,
                                                      Date endTime);

    /**
     * 分页查询${tableRemark!''}
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<${modelTypeDtoType}> listByPage(int pageNum, int pageSize);


     /**
     * 根据示例对象分页查询
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param params 示例对象
     * @return 分页结果
     */
    PageResult<${modelTypeDtoType}> listByPageWithParams(int pageNum, int pageSize, ${modelTypeDtoType} params);


    /**
     * 根据主键查询详情
     * @param ${pkJavaProperty} 主键
     * @return 实体
     */
    ${modelTypeDtoType} getById(${pkJavaType} ${pkJavaProperty});

    /**
     * 根据条件查询列表（不分页）
     * @param condition 查询条件
     * @return 数据列表
     */
    List<${modelTypeDtoType}> selectByConditions(${modelTypeDtoType} condition);
    /**
     * 根据条件查询列表（不分页）
     * @param condition 查询条件
     * @return 数据列表
     */
    List<${modelType}> selectByLikeCondition(${modelTypeDtoType} condition);
    /**
     * 根据条件查询
     * @param condition 查询条件
     * @return 数据列表
     */
   ${modelType} selectOneByCondition(${modelTypeDtoType} condition);

   /**
   * 根据条件查询统计数量
   * @param condition 查询条件
   * @return 数据量
   */
    Long selectCountByConditions(${modelTypeDtoType} condition) ;


    /**
     * 新增
     * @param ${modelVarName} 实体
     * @return 成功条数或主键id，当主键自动生成时成功条数，主键外部赋予时则返回主键
     */
    int save(${modelTypeAddDtoType} ${modelVarName});

    /**
     * 更新
     * @param ${modelVarName} 实体
     * @return 成功条数
     */
    int update(${modelTypeDtoType} ${modelVarName});

    /**
     * 根据主键删除
     * @param ${pkJavaProperty} 主键
     * @return 成功条数
     */
    int logicalDeleteById(${pkJavaType} ${pkJavaProperty});

    /**
     * 批量保存
     * @param list 实体列表
     * @return 成功条数
     */
    int batchInsert(List<${modelTypeDtoType}> list);

}