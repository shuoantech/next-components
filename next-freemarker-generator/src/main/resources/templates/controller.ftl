package ${controllerPackage};

import ${servicePackage?replace('service', 'model')}.${modelTypeDtoType};
import ${servicePackage?replace('service', 'model')}.${modelTypeAddDtoType};
import ${servicePackage}.${serviceType};
import com.qiwumind.next.components.core.bean.PageParam;
import com.qiwumind.next.components.core.bean.PageResult;
import com.qiwumind.next.components.core.bean.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ${tableRemark!''} controller
 * @author Auto Generator
 * @date 创建时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/${modelVarName}")
public class ${controllerType} {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private final ${serviceType}  ${serviceType?uncap_first};

    /**
     * 分页查询
     * @param pageParam  查询参数
     * @return 分页结果
     */
    @PostMapping("/pageList")
    public PageResult<${modelTypeDtoType}> listByPage(@RequestBody PageParam<${modelTypeDtoType}>  pageParam) {
        PageResult<${modelTypeDtoType}> pageResult = ${serviceVarName}.listByPageWithParams(pageParam.getPageNum(),
                    pageParam.getPageSize(),pageParam.getCondition());

        return pageResult;
    }



    /**
     * 根据主键查询详情
     * @param ${pkJavaProperty} 主键
     * @return 实体
     */
    @GetMapping("/{${pkJavaProperty}}")
    public Result<${modelTypeDtoType}> getById(@PathVariable ${pkJavaType} ${pkJavaProperty}) {
        ${modelTypeDtoType} result = ${serviceVarName}.getById(${pkJavaProperty});
        return result != null ? Result.success(result) : Result.empty();
    }

    /**
     * 新增
     * @param ${modelVarName} 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Integer> save(@Valid @RequestBody ${modelTypeAddDtoType} ${modelVarName}) {
        int result = ${serviceVarName}.save(${modelVarName});
        return result > 0 ? Result.success(result):Result.fail("999999","新增保存失败，请核对数据");
    }

    /**
     * 更新
     * @param ${modelVarName} 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Integer> update(@Valid @RequestBody ${modelTypeDtoType} ${modelVarName}) {
        int result = ${serviceVarName}.update(${modelVarName});
        return result > 0 ? Result.success(result):Result.fail("999999","更新失败，请核对数据");
    }

    /**
     * 根据主键逻辑删除
     * @param ${pkJavaProperty} 主键
     * @return 操作结果
     */
    @PutMapping("/{${pkJavaProperty}}")
    public Result<Integer> logicalDeleteById(@PathVariable ${pkJavaType} ${pkJavaProperty}) {
        int result = ${serviceVarName}.logicalDeleteById(${pkJavaProperty});
        return result > 0 ? Result.success(result):Result.fail("999999","根据主键删除失败，请核对数据");

    }

    /**
     * 批量保存
     * @param list 实体列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    public Result<Integer> batchInsert(@Valid @RequestBody List<${modelTypeDtoType}> list) {
        int result = ${serviceVarName}.batchInsert(list);
        return result > 0 ? Result.success(result):Result.fail("999999","批量保存失败，请核对数据");

    }


}