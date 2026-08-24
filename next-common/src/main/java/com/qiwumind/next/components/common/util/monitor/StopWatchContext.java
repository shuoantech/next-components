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

package com.qiwumind.next.components.common.util.monitor;



import org.apache.commons.lang3.time.StopWatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计时器Context
 */
public class StopWatchContext {

    private static final ThreadLocal<Map<String, StopWatch>> WATCH_CTX = new ThreadLocal<Map<String, StopWatch>>() {
        @Override
        protected Map<String, StopWatch> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    /**
     * 初始化指定Key的StopWatch
     * @param key 计时器键
     */
    public static void initWatch(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        if (!contextMap.containsKey(key)) {
            contextMap.put(key, new StopWatch());
        }
    }

    /**
     * 开始计时
     * @param key 计时器键
     */
    public static void start(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        StopWatch stopWatch = contextMap.get(key);
        if (stopWatch != null && !stopWatch.isStarted()) {
            stopWatch.start();
        }
    }

    /**
     * 停止计时
     * @param key 计时器键
     */
    public static void stop(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        StopWatch stopWatch = contextMap.get(key);
        if (stopWatch != null && stopWatch.isStarted()) {
            stopWatch.stop();
        }
    }

    /**
     * 获取计时时间
     * @param key 计时器键
     * @return 计时时间(毫秒)
     */
    public static long getTime(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        StopWatch stopWatch = contextMap.get(key);
        return stopWatch != null ? stopWatch.getTime() : 0;
    }

    /**
     * 计时器是否已开始计时
     * @param key 计时器键
     * @return 是否已开始计时
     */
    public static boolean isStarted(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        StopWatch stopWatch = contextMap.get(key);
        return stopWatch != null && stopWatch.isStarted();
    }

    /**
     * 重置StopWatch(getTime以后需重置以备复用)
     * @param key 计时器键
     */
    public static void reset(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        StopWatch stopWatch = contextMap.get(key);
        if (stopWatch != null) {
            stopWatch.reset();
        }
    }

    /**
     * 移除指定Key的StopWatch
     * @param key 计时器键
     */
    public static void remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        contextMap.remove(key);
    }

    /**
     * 清理当前线程的所有StopWatch
     */
    public static void clear() {
        WATCH_CTX.remove();
    }

    /**
     * 获取当前线程的所有StopWatch数量
     * @return StopWatch数量
     */
    public static int size() {
        return WATCH_CTX.get().size();
    }

    /**
     * 检查是否存在指定Key的StopWatch
     * @param key 计时器键
     * @return 是否存在
     */
    public static boolean containsKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, StopWatch> contextMap = WATCH_CTX.get();
        return contextMap.containsKey(key);
    }

    /**
     * 在虚拟线程中执行任务并计时
     * @param key 计时器键
     * @param task 要执行的任务
     * @return 任务执行结果
     */
    public static <T> T executeWithVirtualThread(String key, java.util.concurrent.Callable<T> task) throws Exception {
        initWatch(key);
        start(key);
        try {
            return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().submit(task).get();
        } finally {
            stop(key);
        }
    }

    /**
     * 在虚拟线程中执行任务并计时
     * @param key 计时器键
     * @param task 要执行的任务
     */
    public static void executeWithVirtualThread(String key, Runnable task) {
        initWatch(key);
        start(key);
        try {
            java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().execute(task);
        } finally {
            stop(key);
        }
    }

}
