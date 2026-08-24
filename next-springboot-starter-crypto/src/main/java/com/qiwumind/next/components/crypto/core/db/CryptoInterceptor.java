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

package com.qiwumind.next.components.crypto.core.db;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.qiwumind.next.components.crypto.core.db.annotation.CryptoField;

/**
 * 基于 MyBatis 拦截器的字段级加解密，业务零侵入：
 * <ul>
 *     <li>{@code ParameterHandler.setParameters}：入库前对 {@link CryptoField} 字段加密；
 *         执行完成后<b>还原为明文</b>，避免污染调用方持有的实体对象。</li>
 *     <li>{@code ResultSetHandler.handleResultSets}：出库后对 {@link CryptoField} 字段解密。</li>
 * </ul>
 *
 * 仅依赖字段注解，不要求修改任何 SQL / XML / Mapper，与业务数据完全解耦。
 */
@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class CryptoInterceptor implements Interceptor {

    /** 反射遍历的保护深度，防止极深嵌套导致栈溢出 */
    private static final int MAX_DEPTH = 5;

    private final CryptoFieldService cryptoFieldService;
    private final Map<Class<?>, List<Field>> cryptoFieldCache = new ConcurrentHashMap<>();

    public CryptoInterceptor(CryptoFieldService cryptoFieldService) {
        this.cryptoFieldService = cryptoFieldService;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof ParameterHandler) {
            ParameterHandler parameterHandler = (ParameterHandler) target;
            Object parameterObject = parameterHandler.getParameterObject();
            // 入库前加密
            processFields(parameterObject, cryptoFieldService::encrypt, 0);
            try {
                return invocation.proceed();
            } finally {
                // 还原调用方实体为明文，消除副作用
                processFields(parameterObject, cryptoFieldService::decrypt, 0);
            }
        }
        // 出库后解密
        Object result = invocation.proceed();
        processFields(result, cryptoFieldService::decrypt, 0);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(java.util.Properties properties) {
        // 暂无可配置项
    }

    private List<Field> resolveCryptoFields(Class<?> clazz) {
        return cryptoFieldCache.computeIfAbsent(clazz, c -> {
            List<Field> fields = new ArrayList<>();
            Class<?> current = c;
            while (current != null && current != Object.class) {
                for (Field field : current.getDeclaredFields()) {
                    if (field.isAnnotationPresent(CryptoField.class) && field.getType() == String.class) {
                        field.setAccessible(true);
                        fields.add(field);
                    }
                }
                current = current.getSuperclass();
            }
            return fields;
        });
    }

    @SuppressWarnings("unchecked")
    private void processFields(Object obj, Function<String, String> fn, int depth) {
        if (obj == null || depth > MAX_DEPTH) {
            return;
        }
        if (obj instanceof Collection) {
            for (Object item : (Collection<Object>) obj) {
                processFields(item, fn, depth + 1);
            }
            return;
        }
        if (obj instanceof Map) {
            for (Object value : ((Map<Object, Object>) obj).values()) {
                processFields(value, fn, depth + 1);
            }
            return;
        }
        if (isSimpleType(obj.getClass())) {
            return;
        }
        for (Field field : resolveCryptoFields(obj.getClass())) {
            try {
                Object value = field.get(obj);
                if (value instanceof String) {
                    String transformed = fn.apply((String) value);
                    if (transformed != null && !transformed.equals(value)) {
                        field.set(obj, transformed);
                    }
                }
            } catch (IllegalAccessException ignored) {
                // 跳过不可访问字段
            }
        }
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz.isEnum();
    }
}
