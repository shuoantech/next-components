package com.qiwumind.next.components.common.util.bean;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.qiwumind.next.components.common.result.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * Bean 映射工具类
 * <p>
 * 基于 Dozer 实现对象转换，提供：
 * 1. 单例 Mapper，避免重复创建开销
 * 2. 批量转换预分配容量，减少扩容开销
 * 3. 分页结果转换
 * 4. 类型安全转换方法
 *
 * @author liks
 * @version 2.1
 */
public class BeanMapperUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanMapperUtils.class);

    /**
     * Dozer Mapper 单例
     */
    private static final Mapper MAPPER = DozerBeanMapperBuilder.create().build();

    // ==================== 基础转换 ====================

    public static <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return MAPPER.map(source, destinationClass);
    }

    /**
     * 将多个源对象合并到已有的目标对象（全字段合并）
     * <p>
     * 合并规则：按 sources 顺序依次映射，后面的覆盖前面的同名字段
     *
     * @param target  目标对象（会被修改）
     * @param sources 多个源对象（按顺序覆盖）
     * @param <T>     目标类型
     * @return 目标对象（链式调用）
     */
    @SafeVarargs
    public static <T> void merge(T target, Object... sources) {
        if (target == null) {
            throw new IllegalArgumentException("target 不能为 null");
        }
        if (sources == null || sources.length == 0) {
            return;
        }
        for (Object source : sources) {
            if (source == null) {
                continue;
            }
            MAPPER.map(source, target);
        }
    }

    /**
     * 智能转换：自动判断用 map 还是 merge
     * <p>
     * - 1 个源对象 → 用 map() 创建新对象
     * - 多个源对象 → 用 merge() 合并
     *
     * @param targetClass 目标类型
     * @param sources     源对象列表
     * @param <T>         目标类型
     * @return 转换后的对象
     */
    @SafeVarargs
    public static <T> T merge(Class<T> targetClass, Object... sources) {
        if (sources == null || sources.length == 0) {
            return null;
        }
        if (sources.length == 1) {
            // 单个源 → 用 map() 创建新对象
            return MAPPER.map(sources[0], targetClass);
        }
        // 多个源 → 先取第一个创建，再追加其余的
        T target = MAPPER.map(sources[0], targetClass);
        for (int i = 1; i < sources.length; i++) {
            if (sources[i] != null) {
                MAPPER.map(sources[i], target);
            }
        }
        return target;
    }

    public static <T> T mapIfPresent(Optional<?> optional, Class<T> destinationClass) {
        return optional.map(o -> map(o, destinationClass)).orElse(null);
    }

    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            result.add(source == null ? null : MAPPER.map(source, destinationClass));
        }
        return result;
    }

    public static <T> List<T> mapArray(Object[] sourceArray, Class<T> destinationClass) {
        if (sourceArray == null || sourceArray.length == 0) {
            return new ArrayList<>();
        }
        return mapList(Arrays.asList(sourceArray), destinationClass);
    }

    // ==================== 分页转换 ====================

    public static <S, T> PageResult<T> mapPage(PageResult<S> page, Class<T> destinationClass) {
        if (page == null) {
            return null;
        }

        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNo(page.getPageNo());
        result.setPageSize(page.getPageSize());
        result.setList(mapList(page.getList(), destinationClass));
        return result;
    }

    public static <S, T> PageResult<T> mapPage(Collection<S> sourceList, Class<T> destinationClass,
                                               long total, int pageNo, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    public static <S, T> PageResult<T> mapPage(Collection<S> sourceList, Class<T> destinationClass,
                                               PageResult<?> page) {
        PageResult<T> result = new PageResult<>();
        if (page != null) {
            result.setTotal(page.getTotal());
            result.setPageNo(page.getPageNo());
            result.setPageSize(page.getPageSize());
        }
        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    public static <S, T> PageResult<T> mapPageSimple(Collection<S> sourceList, Class<T> destinationClass, Long total) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total != null ? total : 0L);
        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    // ==================== 快捷方法 ====================

    public static <T> PageResult<T> emptyPage() {
        return PageResult.empty();
    }

    public static <T> PageResult<T> emptyPage(Long total) {
        return PageResult.empty(total);
    }

    public static <T> PageResult<T> emptyPage(Long total, Integer pageNo, Integer pageSize) {
        return PageResult.empty(total, pageNo, pageSize);
    }

    // ==================== 类型安全转换 ====================

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Clob) {
            try {
                Clob clob = (Clob) obj;
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException e) {
                logger.error("Clob 转 String 失败", e);
                return null;
            }
        }
        return obj.toString();
    }

    public static Long toLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.valueOf((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static Integer toInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof String) {
            try {
                return Integer.valueOf((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Number) {
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        }
        if (obj instanceof String) {
            try {
                return new BigDecimal((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static Boolean toBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof String) {
            return Boolean.valueOf((String) obj);
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue() != 0;
        }
        return null;
    }

    // ==================== 字段命名转换（保留原有功能） ====================

    public static String convertUpper(String str) {
        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c) && i > 0) {
                buffer.append('_');
            }
            buffer.append(Character.toUpperCase(c));
        }
        return buffer.toString();
    }

    public static String convertLower(String str) {
        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_' && i < chars.length - 1) {
                buffer.append(Character.toUpperCase(chars[++i]));
            } else {
                buffer.append(Character.toLowerCase(c));
            }
        }
        return buffer.toString();
    }
}