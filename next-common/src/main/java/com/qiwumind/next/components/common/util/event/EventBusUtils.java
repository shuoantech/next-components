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

package com.qiwumind.next.components.common.util.event;



import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.AsyncEventBus;
import com.qiwumind.next.components.common.event.Event;
import com.qiwumind.next.components.common.event.EventListener;

/**
 * 基于guava eventbus的调用工具类
 */
public class EventBusUtils {
    /**
     * 异步调用bean的methodName方法
     * 
     * @param bean
     * @param methodName
     * @param args
     */
    public static void invoke(Object bean, String methodName, Object[] args) {
        invoke(bean, methodName, args, null);
    }

    /**
     * 异步调用bean的methodName方法
     * 
     * @param bean
     * @param methodName
     * @param args
     * @param clazzs
     */
    public static void invoke(Object bean, String methodName, Object[] args, Class<?>[] clazzs) {
        // new一个事件
        Event event = new Event(bean, methodName, args, clazzs);
        // 将事件提交到bus上
        eventBus.post(event);
    }

    private static int                corePoolSize = Runtime.getRuntime().availableProcessors();
  
    /**
     * 多线程,使用固定线程池大小，非指定线程池大小可能大致OOM
     */
    private static ThreadPoolExecutor executor     = new ThreadPoolExecutor(corePoolSize, 30, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000), new MyThreadFactory());

    /**
     * event bus
     */
    private static AsyncEventBus      eventBus     = new AsyncEventBus("default-enventBus", executor);
    /**
     * 事件监听器
     */
    private static EventListener      listener     = new EventListener();
    static {
        // 注册监听器到bus
        eventBus.register(listener);
        // 启动线程
        executor.prestartAllCoreThreads();
    }

    static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(Thread.currentThread().getName() + "-eventbus-common-thread");
            return thread;
        }

    }
}
