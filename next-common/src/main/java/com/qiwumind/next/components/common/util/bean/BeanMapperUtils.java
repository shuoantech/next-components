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



import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类BeanMapper.java的实现描述：
 */
public class BeanMapperUtils {
    private static Logger logger = LoggerFactory.getLogger(BeanMapperUtils.class);
    private static Mapper dozer = DozerBeanMapperBuilder.create()
            .build();
//    private static DozerBeanMapper dozer = new DozerBeanMapper();

    /**
     * 构造新的destinationClass实例对象，通过source对象中的字段内容
     * 映射到destinationClass实例对象中，并返回新的destinationClass实例对象。
     *
     * @param source           源数据对象
     * @param destinationClass 要构造新的实例对象Class
     * @return
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return dozer.map(source, destinationClass);
    }

    /**
     * @param sourceList
     * @param destinationClass
     * @return
     */
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        final List<T> destinationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceList)) {
            return destinationList;
        }
        for (final Iterator<?> i$ = sourceList.iterator(); i$.hasNext(); ) {
            final Object sourceObject = i$.next();
            final T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }
//
//    private static Map<String, Object> getBeanPropertyAndChangeIntoMap(final Object obj,
//                                                                       final Class<? extends Object> abClass) {
//        final Map<String, Object> dataMap = new HashMap<String, Object>();
//        Class<?> clazz = obj.getClass();
//        int length = 0;
//        Field[] arrayFields = new Field[length];
//        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
//            try {
//                final Field[] arrayField = clazz.getDeclaredFields();
//                length += arrayField.length;
//                final Field[] tmp = new Field[length];
//                // src:源数组; srcPos:源数组要复制的起始位置;dest:目的数组; destPos:目的数组放置的起始位置;
//                // length:复制的长度
//                System.arraycopy(arrayField, 0, tmp, 0, arrayField.length);
//                System.arraycopy(arrayFields, 0, tmp, arrayFields.length, arrayFields.length);
//                arrayFields = tmp;
//            } catch (final Exception e) {
//                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
//                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
//                // clazz.getSuperclass(),最后就不会进入到父类中了
//            }
//        }
//        for (int i = 0; i < arrayFields.length; i++) {
//            final Field field = arrayFields[i];
//            try {
//                field.setAccessible(true);
//                Object value = field.get(obj);
//                if (value == null) {
//                    continue;
//                }
//                if (value instanceof BaseDTO) {
//                    value = bean2Map(obj);
//                }
//                //                else if (value instanceof List) {
//                //                    value = beanList2MapList((List) value);
//                //                }
//                String name = field.getName();
//                // 此处需屏蔽掉Mybatis自动生成的bean中附带的 以 CGLIB 为开头包含'$'的属性
//                if (name.indexOf('$') == -1) {
//                    name = (abClass == BaseDTO.class ? name : convertUpper(name));
//                    dataMap.put(name, value);
//                }
//                dataMap.put(name, value);
//            } catch (final Exception e) {
//            }
//        }
//        return dataMap;
//    }

    /**
     * @param obj
     * @return
     */
//    public static Map<String, Object> bean2Map(final Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        final Map<String, Object> dataMap = new HashMap<String, Object>();
//        dataMap.putAll(getBeanPropertyAndChangeIntoMap(obj, null));
//        return dataMap;
//    }

    /**
     * @param beanList
     * @return
     */
//    public static List<Map<String, Object>> beanList2MapList(final List<?> beanList) {
//        final List<Map<String, Object>> rsplist = new ArrayList<Map<String, Object>>();
//        for (final Object bean : beanList) {
//            rsplist.add(bean2Map(bean));
//        }
//        return rsplist;
//    }

    /**
     * 不支持嵌套对象
     *
     * @return
     */
//    public static <T> T map2Bean(final Map<String, Object> map, final Class<T> clazz) {
//        if (map == null) {
//            return null;
//        }
//        T bean = null;
//        try {
//            bean = clazz.newInstance();
//            final Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                final Entry<String, Object> entry = iterator.next();
//                if (entry.getValue() != null) {
//                    final Method method = getSetMethodOfEntry(entry, clazz);
//                    if (method == null) {
//                        logger.debug("have not found the set method of " + entry.getKey() + "("
//                                + entry.getValue().getClass().toString() + ")");
//                    } else {
//                        Object obj = entry.getValue();
//                        if (obj instanceof BaseDTO) {
//                            obj = map2Bean(map, obj.getClass());
//                        }
//                        //                        else if (obj instanceof List) {
//                        //                            obj = new ArrayList<Map<String, Object>>((List) obj);
//                        //                        }
//                        method.invoke(bean, obj);
//                    }
//                }
//            }
//        } catch (final InstantiationException e) {
//            logger.error("...产生实例化错误", e);
//        } catch (final IllegalAccessException e) {
//            logger.error("" + e);
//        } catch (final IllegalArgumentException e) {
//            logger.error("" + e);
//        } catch (final InvocationTargetException e) {
//            logger.error("" + e);
//        }
//        return bean;
//    }
//
//    /**
//     * 将 Map对象转化为JavaBean 此方法已经测试通过
//     *
//     * @author wyply115
//     * @param type 要转化的类型
//     * @param map
//     * @return Object对象
//     * @version 2016年3月20日 11:03:01
//     */
//    public static <T> T convertMap2Bean(final Map<String, Object> map, final Class<T> clazz) {
//        if (map == null || map.size() == 0) {
//            return null;
//        }
//        T bean = null;
//        try {
//            bean = clazz.newInstance();
//            org.apache.commons.beanutils.BeanUtils.populate(bean, map);
//        } catch (InstantiationException | IllegalAccessException e1) {
//        } catch (final InvocationTargetException e) {
//        }
//        return bean;
//    }
//
//    /**
//     * 将 List<Map>对象转化为List<JavaBean>
//     *
//     * @param maplist
//     * @param clazz
//     * @return
//     */
//    public static <T> List<T> mapList2BeanList(final List<Map<String, Object>> maplist, final Class<T> clazz) {
//        final List<T> rspList = new ArrayList<T>();
//        if (CollectionUtils.isEmpty(maplist)) {
//            return rspList;
//        }
//        for (final Map<String, Object> entry : maplist) {
//            rspList.add(map2Bean(entry, clazz));
//        }
//        return rspList;
//    }
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

    /**
     * 获取一般类类型的set函数
     *
     * @param methodName
     * @param clazz
     * @param entry
     * @return
     * @throws NoSuchMethodException
     */
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

    /**
     * 获取封装类参数的基本类
     *
     * @param obj
     * @return
     */

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

    /**
     * @param str
     * @return
     */
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

    /**
     * @param str
     * @return
     */
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
