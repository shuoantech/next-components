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

package com.qiwumind.next.components.groovy.executor;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.SmartLifecycle;

import com.qiwumind.next.components.groovy.properties.GroovyEngineProperties;
import com.qiwumind.next.components.groovy.helper.RefreshScriptHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 自动定时刷新脚本执行器
 * </p>
 */
@Slf4j
public class AutoRefreshScriptExecutor implements SmartLifecycle {

    private final GroovyEngineProperties groovyEngineProperties;
    private final RefreshScriptHelper refreshScriptHelper;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ScheduledExecutorService register;

    public AutoRefreshScriptExecutor(GroovyEngineProperties groovyEngineProperties,
                                     RefreshScriptHelper refreshScriptHelper) {
        this.groovyEngineProperties = groovyEngineProperties;
        this.refreshScriptHelper = refreshScriptHelper;
    }

    @Override
    public void start() {
        // bean初始化完毕后会被吊起，是否吊起需要看isAutoStartup返回值，如果返回false则start不会被吊起
        if (!this.isRunning.compareAndSet(false, true)) {
            log.error("Note AutoRefreshScriptExecutor already started, skip.");
            return;
        }
        log.info("AutoRefreshScriptExecutor thread start.");
        // 启动刷新线程
        register =
                new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                        .namingPattern("enhance-groovy-engine-executor")
                        .daemon(true)
                        .build());
        // 定时任务线程池定时刷新
        register.scheduleAtFixedRate(this::refreshScript,
                this.groovyEngineProperties.getInitialDelay(),
                this.groovyEngineProperties.getPollingCycle(),
                TimeUnit.SECONDS);
        log.info("AutoRefreshScriptExecutor thread complete.");
    }

    @Override
    public void stop() {
        // 容器关闭后，spring容器发现当前对象实现了SmartLifecycle，就调用stop(Runnable)，如果只是实现了Lifecycle，就调用stop()
        log.warn("container is stopping, stop auto refresh script now.");
        this.isRunning.compareAndSet(true, false);
        // 关闭定时任务线程池，防止线程泄漏
        if (register != null) {
            register.shutdownNow();
            try {
                if (!register.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("定时任务线程池未能在5秒内终止");
                }
            } catch (InterruptedException e) {
                log.warn("等待线程池终止时被中断", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        // 组件是否在运行中
        return this.isRunning.get();
    }

    @Override
    public boolean isAutoStartup() {
        // 当容器没有启动过时才吊起start
        return !this.isRunning.get();
    }

    @Override
    public int getPhase() {
        // 返回值决定start方法在众多Lifecycle实现类中的执行顺序(stop也是)
        return 0;
    }

    /**
     * 刷新脚本
     */
    private void refreshScript() {
        if (this.isRunning.get()) {
            this.refreshScriptHelper.refreshAll();
        } else {
            log.warn("can not refresh script because isRunning status is false.");
        }
    }
}
