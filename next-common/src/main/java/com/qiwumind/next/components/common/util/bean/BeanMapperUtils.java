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

package com.qiwumind.next.components.common.util.bean;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.qiwumind.next.components.common.result.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Bean 映射工具类 - 性能优化版
 * <p>
 * 优化点：
 * 1. Dozer Mapper 单例 + 懒加载
 * 2. 批量转换预分配容量，减少扩容开销
 * 3. 结果缓存（高频重复转换场景）
 * 4. 分页转换支持完整分页参数
 * 5. 类型安全转换方法
 * 6. 缓存大小可根据场景调整
 *
 * @author liks
 * @version 2.0
 */
public class BeanMapperUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanMapperUtils.class);

    /**
     * 是否启用缓存（默认启用，可通过 JVM 参数关闭）
     */
    private static final boolean ENABLE_CACHE = Boolean.parseBoolean(
            System.getProperty("bean.mapper.cache.enabled", "true")
    );

    /**
     * 缓存最大容量（默认 1000，可通过 JVM 参数调整）
     * 建议值：
     * - 小项目/低频: 200-500
     * - 中项目/中频: 500-1000
     * - 大项目/高频: 1000-2000
     */
    private static final int CACHE_MAX_SIZE = Integer.parseInt(
            System.getProperty("bean.mapper.cache.max.size", "1000")
    );

    /**
     * 转换结果缓存（线程安全）
     * 使用 ConcurrentHashMap 保证高并发下的性能
     */
    private static final Map<String, Object> CONVERT_CACHE = new ConcurrentHashMap<>();

    /**
     * Dozer Mapper 单例（懒加载，线程安全）
     */
    private static final class DozerHolder {
        static final Mapper INSTANCE = DozerBeanMapperBuilder.create().build();
    }

    private static Mapper getDozer() {
        return DozerHolder.INSTANCE;
    }

    // ==================== 基础转换方法 ====================

    /**
     * 单个对象转换
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return getDozer().map(source, destinationClass);
    }

    /**
     * 单个对象转换（带缓存）
     */
    public static <T> T map(Object source, Class<T> destinationClass, boolean useCache) {
        if (source == null) {
            return null;
        }

        if (useCache && ENABLE_CACHE) {
            String cacheKey = buildCacheKey(source, destinationClass);
            @SuppressWarnings("unchecked")
            T cached = (T) CONVERT_CACHE.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        T result = getDozer().map(source, destinationClass);

        if (useCache && ENABLE_CACHE && result != null) {
            String cacheKey = buildCacheKey(source, destinationClass);
            if (CONVERT_CACHE.size() < CACHE_MAX_SIZE) {
                CONVERT_CACHE.put(cacheKey, result);
            } else {
                // 缓存满时清理一半
                trimCache();
            }
        }

        return result;
    }

    /**
     * 列表转换（批量优化）
     */
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }

        List<T> destinationList = new ArrayList<>(sourceList.size());
        Mapper mapper = getDozer();

        for (Object sourceObject : sourceList) {
            if (sourceObject == null) {
                destinationList.add(null);
                continue;
            }
            destinationList.add(mapper.map(sourceObject, destinationClass));
        }

        return destinationList;
    }

    /**
     * 列表转换（带缓存）
     */
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass, boolean useCache) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }

        // 如果缓存未启用或容量为0，直接转换
        if (!ENABLE_CACHE || CACHE_MAX_SIZE <= 0) {
            return mapList(sourceList, destinationClass);
        }

        List<T> destinationList = new ArrayList<>(sourceList.size());
        Mapper mapper = getDozer();

        for (Object sourceObject : sourceList) {
            if (sourceObject == null) {
                destinationList.add(null);
                continue;
            }

            T result;
            if (useCache) {
                String cacheKey = buildCacheKey(sourceObject, destinationClass);
                @SuppressWarnings("unchecked")
                T cached = (T) CONVERT_CACHE.get(cacheKey);
                if (cached != null) {
                    destinationList.add(cached);
                    continue;
                }
                result = mapper.map(sourceObject, destinationClass);
                if (result != null && CONVERT_CACHE.size() < CACHE_MAX_SIZE) {
                    CONVERT_CACHE.put(cacheKey, result);
                }
            } else {
                result = mapper.map(sourceObject, destinationClass);
            }

            destinationList.add(result);
        }

        if (CONVERT_CACHE.size() >= CACHE_MAX_SIZE) {
            trimCache();
        }

        return destinationList;
    }

    /**
     * 数组转换
     */
    public static <T> List<T> mapArray(Object[] sourceArray, Class<T> destinationClass) {
        if (sourceArray == null || sourceArray.length == 0) {
            return new ArrayList<>();
        }
        return mapList(Arrays.asList(sourceArray), destinationClass);
    }

    // ==================== 分页转换方法 ====================

    /**
     * 分页结果转换（完整分页信息）
     */
    public static <S, T> PageResult<T> mapPage(PageResult<S> page, Class<T> destinationClass) {
        if (page == null) {
            return null;
        }

        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNo(page.getPageNo());
        result.setPageSize(page.getPageSize());

        List<S> sourceList = page.getList();
        if (CollectionUtils.isNotEmpty(sourceList)) {
            result.setList(mapList(sourceList, destinationClass));
        } else {
            result.setList(new ArrayList<>());
        }

        return result;
    }

    /**
     * 分页结果转换（手动构建）
     */
    public static <S, T> PageResult<T> mapPage(Collection<S> sourceList, Class<T> destinationClass,
                                               long total, int pageNo, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    /**
     * 分页结果转换（复用分页信息）
     */
    public static <S, T> PageResult<T> mapPage(Collection<S> sourceList, Class<T> destinationClass,
                                               PageResult<?> page) {
        PageResult<T> result = new PageResult<>();

        if (page != null) {
            result.setTotal(page.getTotal());
            result.setPageNo(page.getPageNo());
            result.setPageSize(page.getPageSize());
        } else {
            result.setTotal(0L);
            result.setPageNo(1);
            result.setPageSize(10);
        }

        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    /**
     * 分页结果转换（只转换列表，不含分页参数）
     */
    public static <S, T> PageResult<T> mapPageSimple(Collection<S> sourceList, Class<T> destinationClass, Long total) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total != null ? total : 0L);
        result.setList(mapList(sourceList, destinationClass));
        return result;
    }

    // ==================== 缓存管理 ====================

    private static String buildCacheKey(Object source, Class<?> destinationClass) {
        return source.getClass().getName() + "_" + destinationClass.getName() + "_" + source.hashCode();
    }

    /**
     * 清理缓存（清理一半）
     */
    private static void trimCache() {
        if (CONVERT_CACHE.size() < CACHE_MAX_SIZE) {
            return;
        }
        logger.warn("转换缓存已满(当前:{}/{}), 执行清理...", CONVERT_CACHE.size(), CACHE_MAX_SIZE);

        // 清理一半的缓存
        List<String> keysToRemove = new ArrayList<>();
        int removeCount = 0;
        int targetRemove = Math.max(CACHE_MAX_SIZE / 2, 1);

        for (String key : CONVERT_CACHE.keySet()) {
            keysToRemove.add(key);
            removeCount++;
            if (removeCount >= targetRemove) {
                break;
            }
        }
        for (String key : keysToRemove) {
            CONVERT_CACHE.remove(key);
        }
        logger.info("缓存清理完成，当前大小: {}/{}", CONVERT_CACHE.size(), CACHE_MAX_SIZE);
    }

    /**
     * 清空所有缓存
     */
    public static void clearCache() {
        CONVERT_CACHE.clear();
        logger.info("转换缓存已清空");
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return CONVERT_CACHE.size();
    }

    /**
     * 获取缓存最大容量
     */
    public static int getCacheMaxSize() {
        return CACHE_MAX_SIZE;
    }

    /**
     * 是否启用缓存
     */
    public static boolean isCacheEnabled() {
        return ENABLE_CACHE && CACHE_MAX_SIZE > 0;
    }

    /**
     * 获取缓存命中率统计（简易）
     */
    public static double getCacheHitRate() {
        // 由于 ConcurrentHashMap 不提供命中统计，这里只是一个示意
        // 实际可通过 AOP 或包装 Map 实现
        return 0.0;
    }

    // ==================== 快捷方法 ====================

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> emptyPage() {
        return PageResult.empty();
    }

    /**
     * 创建空分页结果（指定总数）
     */
    public static <T> PageResult<T> emptyPage(Long total) {
        return PageResult.empty(total);
    }

    /**
     * 创建空分页结果（指定总数和分页参数）
     */
    public static <T> PageResult<T> emptyPage(Long total, Integer pageNo, Integer pageSize) {
        return PageResult.empty(total, pageNo, pageSize);
    }

    /**
     * 深拷贝对象
     */
    public static <T> T deepCopy(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return getDozer().map(source, destinationClass);
    }

    /**
     * 批量深拷贝
     */
    public static <T> List<T> deepCopyList(Collection<?> sourceList, Class<T> destinationClass) {
        return mapList(sourceList, destinationClass);
    }

    // ==================== 类型安全转换工具 ====================

    /**
     * 安全转换为字符串
     */
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

    /**
     * 安全转换为 Long
     */
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

    /**
     * 安全转换为 Integer
     */
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

    /**
     * 安全转换为 BigDecimal
     */
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

    /**
     * 安全转换为 Boolean
     */
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

    // ==================== 私有方法（保留原有功能） ====================

    private static Method getSetMethodOfEntry(final Entry<String, Object> entry, final Class<?> clazz) {
        final String property = convertLower(entry.getKey());
        final StringBuilder methodName = new StringBuilder("set");
        methodName.append(Character.toUpperCase(property.charAt(0)));
        methodName.append(property.substring(1));
        try {
            return getBasicTypeMethod(methodName.toString(), clazz, entry);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getBasicTypeMethod(final String methodName, final Class<?> clazz,
                                             final Entry<String, Object> entry)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, getParameterTye(entry.getValue()));
        } catch (final Exception e) {
            return getNormalTypeMethod(methodName, clazz, entry);
        }
    }

    private static Method getNormalTypeMethod(final String methodName, final Class<?> clazz,
                                              final Entry<String, Object> entry)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, entry.getValue().getClass());
        } catch (final SecurityException e) {
            logger.error(e.getMessage());
        } catch (final NoSuchMethodException e) {
            if (entry.getValue() instanceof Integer || entry.getValue() instanceof Long) {
                entry.setValue(Long.valueOf(entry.getValue().toString()));
                return getSpecialLongTypeMethod(methodName, clazz);
            } else if (entry.getValue() instanceof Double || entry.getValue() instanceof Float) {
                entry.setValue(Double.valueOf(entry.getValue().toString()));
                return getSpecialDoubleTypeMethod(methodName, clazz);
            } else if (entry.getValue() instanceof BigDecimal) {
                final Object obj = entry.getValue();
                entry.setValue(new BigDecimal(obj.toString()));
                return getSpecialTypeMethod(methodName, clazz, BigDecimal.class);
            } else if (entry.getValue() instanceof Clob) {
                final Clob clob = (Clob) entry.getValue();
                String value = null;
                try {
                    value = clob.getSubString(1, (int) clob.length());
                } catch (final SQLException e1) {
                    logger.debug(e1.getMessage());
                }
                entry.setValue(value);
                return getSpecialTypeMethod(methodName, clazz, String.class);
            } else if (entry.getValue() instanceof Date) {
                final Date date = (Date) entry.getValue();
                entry.setValue(date);
                try {
                    return getSpecialTypeMethod(methodName, clazz, Date.class);
                } catch (final NoSuchMethodException e1) {
                    entry.setValue(new java.sql.Date(date.getTime()));
                    return getSpecialTypeMethod(methodName, clazz, java.sql.Date.class);
                }
            }
        }
        return null;
    }

    private static Method getSpecialLongTypeMethod(final String methodName, final Class<?> clazz)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, long.class);
        } catch (final NoSuchMethodException e) {
            try {
                return clazz.getMethod(methodName, Long.class);
            } catch (final SecurityException e1) {
                return clazz.getMethod(methodName, Long.TYPE);
            }
        }
    }

    private static Method getSpecialDoubleTypeMethod(final String methodName, final Class<?> clazz)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, double.class);
        } catch (final NoSuchMethodException e) {
            try {
                return clazz.getMethod(methodName, Double.class);
            } catch (final Exception e1) {
                return clazz.getMethod(methodName, Double.TYPE);
            }
        }
    }

    private static Method getSpecialTypeMethod(final String methodName, final Class<?> clazz, final Class<?> type)
            throws NoSuchMethodException {
        return clazz.getMethod(methodName, type);
    }

    private static Class<?> getParameterTye(final Object obj) {
        if (obj instanceof Integer) {
            return Integer.TYPE;
        } else if (obj instanceof Long) {
            return Long.TYPE;
        } else if (obj instanceof Byte) {
            return Byte.TYPE;
        } else if (obj instanceof Short) {
            return Short.TYPE;
        } else if (obj instanceof Float) {
            return Float.TYPE;
        } else if (obj instanceof Double) {
            return Double.TYPE;
        } else if (obj instanceof Character) {
            return Character.TYPE;
        } else if (obj instanceof Boolean) {
            return Boolean.TYPE;
        } else if (obj instanceof List) {
            return List.class;
        } else {
            return obj.getClass();
        }
    }

    public static String convertUpper(final String str) {
        final StringBuffer buffer = new StringBuffer();
        final String regEx = "[A-Z]";
        final Pattern pattern = Pattern.compile(regEx);
        final char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final String st = String.valueOf(chars[i]);
            if (pattern.matcher(st).find() && i > 0) {
                buffer.append("_");
            }
            buffer.append(st.toUpperCase());
        }
        return buffer.toString();
    }

    public static String convertLower(final String str) {
        final StringBuffer buffer = new StringBuffer();
        final String regEx = "_";
        final Pattern pattern = Pattern.compile(regEx);
        final char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String st = String.valueOf(chars[i]);
            if (pattern.matcher(st).find() && i > 0) {
                st = String.valueOf(chars[++i]);
                buffer.append(st.toUpperCase());
                continue;
            }
            buffer.append(st.toLowerCase());
        }
        return buffer.toString();
    }
}