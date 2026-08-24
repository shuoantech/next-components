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

package com.qiwumind.next.components.common.event;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.qiwumind.next.components.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * 事件监听类， 通过反射调用传入event中的方法
 */
@Slf4j
public class EventListener {

    /**
     * @param event
     * @throws BusinessException
     */
    @Subscribe
    @AllowConcurrentEvents
    public void listen(Event event) {
        if (null == event) {
            log.error("EventListener.listen() event is null, do nothing");
            return;
        }
        //目标方法参数列表
        Object[] args = event.getArgs();
        //目标方法参数类型列表
        Class<?>[] parameterTypes = event.getClazzs();

        if (null == parameterTypes && null != args && args.length > 0) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }
        //取bean的方法对象
        Method method = null;
        try {
            method = event.getBean().getClass().getDeclaredMethod(event.getMethodName(), parameterTypes);
            method.setAccessible(true);
            method.invoke(event.getBean(), args);
        } catch (NoSuchMethodException e) {
            log.error("NoSuchMethodException={}", e);
        } catch (SecurityException e) {
            log.error("SecurityException={}", e);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException={}", e);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException={}", e);
        } catch (InvocationTargetException e) {
            log.error("InvocationTargetException={}", e);
        }
    }
}
