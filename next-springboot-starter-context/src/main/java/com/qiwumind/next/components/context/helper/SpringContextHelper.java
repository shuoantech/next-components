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

package com.qiwumind.next.components.context.helper;



import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 类SpringContextUtil.java的实现描述
 *
 * @author liks 2018年8月17日 下午4:16:02
 */
public class SpringContextHelper implements ApplicationContextAware, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(SpringContextHelper.class);
    private static ApplicationContext applicationContext;
    private static DefaultListableBeanFactory springFactory;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (applicationContext != null) {
            logger.warn("SpringContextUtil中的ApplicationContext被覆盖, 原有ApplicationContext为:" + applicationContext);
        }
        applicationContext = context;

        if (applicationContext instanceof AbstractRefreshableApplicationContext) {
            AbstractRefreshableApplicationContext springContext = (AbstractRefreshableApplicationContext) applicationContext;
            SpringContextHelper.setFactory((DefaultListableBeanFactory) springContext.getBeanFactory());
        } else if (applicationContext instanceof GenericApplicationContext) {
            GenericApplicationContext springContext = (GenericApplicationContext) applicationContext;
            SpringContextHelper.setFactory(springContext.getDefaultListableBeanFactory());
        }
    }

    private static void setFactory(DefaultListableBeanFactory springFactory) {
        SpringContextHelper.springFactory = springFactory;
    }

    public static DefaultListableBeanFactory getSpringFactory() {
        return springFactory;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @param <T>
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 获取对象
     *
     * @param requiredType
     * @return
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> requiredType) throws BeansException {
        assertContextInjected();
        return (T) applicationContext.getBean(requiredType);
    }

    /**
     * @param type
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws NoSuchBeanDefinitionException {
        assertContextInjected();
        return applicationContext.getBeansOfType(type);
    }

    /**
     * @param <T>
     * @param name
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getType(String name) throws NoSuchBeanDefinitionException {
        assertContextInjected();
        return (Class<T>) applicationContext.getType(name);
    }

    /**
     * 获取类型为requiredType的对象
     * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     *
     * @param name         bean注册名
     * @param requiredType 返回对象类型
     * @return
     * @return Object 返回requiredType类型对象
     * @throws BeansException
     */
    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        assertContextInjected();
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        assertContextInjected();
        return applicationContext.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        assertContextInjected();
        return applicationContext.getAliases(name);
    }

    @Override
    public void destroy() throws Exception {
        logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        applicationContext = null;
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null,
                "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextUtil.");
    }


    /**
     * 异步从 ApplicationContextHelper 获取 bean 对象并设置到目标对象中，在某些启动期间需要初始化的bean可采用此方法。
     * <p>
     * 适用于实例方法注入。
     *
     * @param type         bean type
     * @param target       目标类对象
     * @param setterMethod setter 方法，target 中需包含此方法名，且类型与 type 一致
     * @param <T>          type
     */
    public static <T> void asyncInstanceSetter(Class<T> type, Object target, String setterMethod) {
        if (setByMethod(type, target, setterMethod)) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "sync-setter"));
        executorService.scheduleAtFixedRate(() -> {
            boolean success = setByMethod(type, target, setterMethod);
            if (success) {
                executorService.shutdown();
            } else {
                if (counter.addAndGet(1) > 240) {
                    logger.error("Setter field [{}] in [{}] failure because timeout.", setterMethod, target.getClass().getName());
                    executorService.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 异步从 ApplicationContextHelper 获取 bean 对象并设置到目标对象中，在某些启动期间需要初始化的bean可采用此方法。
     * <br>
     * 一般可用于向静态类注入实例对象。
     *
     * @param type        bean type
     * @param target      目标类
     * @param targetField 目标字段
     */
    public static void asyncStaticSetter(Class<?> type, Class<?> target, String targetField) {
        if (setByField(type, target, targetField)) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "sync-setter"));
        executorService.scheduleAtFixedRate(() -> {
            boolean success = setByField(type, target, targetField);
            if (success) {
                executorService.shutdown();
            } else {
                if (counter.addAndGet(1) > 240) {
                    logger.error("Setter field [{}] in [{}] failure because timeout.", targetField, target.getName());
                    executorService.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static boolean setByMethod(Class<?> type, Object target, String targetMethod) {
        if (SpringContextHelper.getContext() != null) {
            try {
                Object obj = SpringContextHelper.getContext().getBean(type);
                Method method = target.getClass().getDeclaredMethod(targetMethod, type);
                method.setAccessible(true);
                method.invoke(target, obj);
                logger.info("Async set field [{}] in [{}] success by method.", targetMethod, target.getClass().getName());
                return true;
            } catch (NoSuchMethodException e) {
                logger.error("Not found method [{}] in [{}].", targetMethod, target.getClass().getName(), e);
            } catch (NoSuchBeanDefinitionException e) {
                logger.error("Not found bean [{}] for [{}].", type.getName(), target.getClass().getName(), e);
            } catch (Exception e) {
                logger.error("Async set field [{}] in [{}] failure by method.", targetMethod, target.getClass().getName(), e);
            }
        }
        return false;
    }

    private static boolean setByField(Class<?> type, Class<?> target, String targetField) {
        if (SpringContextHelper.getContext() != null) {
            try {
                Object obj = SpringContextHelper.getContext().getBean(type);
                Field field = target.getDeclaredField(targetField);
                field.setAccessible(true);
                field.set(target, obj);
                logger.info("Async set field [{}] in [{}] success by field.", targetField, target.getName());
                return true;
            } catch (NoSuchFieldException e) {
                logger.error("Not found field [{}] in [{}].", targetField, target.getName(), e);
            } catch (NoSuchBeanDefinitionException e) {
                logger.error("Not found bean [{}] for [{}].", type.getName(), target.getName(), e);
            } catch (Exception e) {
                logger.error("Async set field [{}] in [{}] failure by field.", targetField, target.getName(), e);
            }
        }
        return false;
    }
}
