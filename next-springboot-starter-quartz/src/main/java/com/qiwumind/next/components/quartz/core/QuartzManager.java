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

package com.qiwumind.next.components.quartz.core;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

/**
 * 定时任务配置管理中心
 *
 * @author liks 2019年11月27日 下午1:31:02
 */
public class QuartzManager {

    private Scheduler scheduler;

    public QuartzManager() {
        super();
    }

    public QuartzManager(final Scheduler scheduler) {
        super();
        this.scheduler = scheduler;
    }
    /**
     //     * @param cron       时间设置，参考quartz说明文档
     //     * @param quartzTask task参数
     //     * @Description: 添加一个定时任务到default组
     //     */
//    public void addDefaultGroupJob(final String jobName, final String cron, final TaskData quartzTask) {
//        addJob(jobName, Scheduler.DEFAULT_GROUP, jobName, Scheduler.DEFAULT_GROUP, DefaultQuartzTask.class, cron,
//                quartzTask);
//    }
//

    /**
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass         任务
     * @param cron             时间设置，参考quartz说明文档
     * @param quartzTask       task参数
     * @Description: 添加一个定时任务
     */
    public void addJob(final String jobName, final String jobGroupName, final String triggerName,
                       final String triggerGroupName, final Class<? extends Job> jobClass, final String cron,
                       final JobTask quartzTask) {
        // 1. 参数校验
        validateJobParameters(jobName, jobGroupName, triggerName, triggerGroupName, cron, jobClass, quartzTask);
        try {
            // 2. 构建JobDataMap - 优化数据结构
            final JobDataMap jobMap = new JobDataMap();
            jobMap.put(jobName + "|+|" + jobGroupName, quartzTask);
            jobMap.put(jobName, quartzTask);
            // 任务名，任务组，任务执行类
            final JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .usingJobData(jobMap).build();
            // 触发器
            final TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            // 创建Trigger对象
            final CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建JobKey标识
     */
    private String buildJobKey(String jobName, String jobGroupName) {
        return String.format("%s::%s", jobGroupName, jobName);
    }
    /**
     * 参数校验
     */
    private void validateJobParameters(String jobName, String jobGroupName,
                                       String triggerName, String triggerGroupName,
                                       String cron, Class<? extends Job> jobClass,
                                       JobTask quartzTask) {
        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroupName)) {
            throw new IllegalArgumentException("任务名称和任务组不能为空");
        }
        if (StringUtils.isBlank(cron)) {
            throw new IllegalArgumentException("Cron表达式不能为空");
        }
        if (jobClass == null) {
            throw new IllegalArgumentException("任务类不能为空");
        }
        if (quartzTask == null) {
            throw new IllegalArgumentException("任务数据不能为空");
        }
        // 验证Cron表达式格式
        if (!CronExpression.isValidExpression(cron)) {
            throw new IllegalArgumentException("无效的Cron表达式: " + cron);
        }
    }

    /**
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param cron             时间设置，参考quartz说明文档
     * @Description: 修改一个任务的触发时间
     */
    public void modifyJobTime(final String triggerName, final String triggerGroupName, final String cron) {
        try {
            final TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            final String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /** 方式一 ：调用 rescheduleJob 开始 */
                // 触发器
                final TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                scheduler.rescheduleJob(triggerKey, trigger);
                /** 方式一 ：调用 rescheduleJob 结束 */

                /** 方式二：先删除，然后在创建一个新的Job */
                //JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
                //Class<? extends Job> jobClass = jobDetail.getJobClass();
                //removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
                //addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
                /** 方式二 ：先删除，然后在创建一个新的Job */
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @Description: 移除一个任务
     */
    public void removeJob(final String jobName, final String jobGroupName, final String triggerName,
                          final String triggerGroupName) {
        try {
            final TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param jobName
     * @param jobGroupName
     * @Description: 获得一个任务
     */
    public boolean getJob(final String jobName, final String jobGroupName) {
        try {
            return scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName)) == null ? false : true;
        } catch (final SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @Description:启动所有定时任务
     */
    public void init() {
        try {
            scheduler.start();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
