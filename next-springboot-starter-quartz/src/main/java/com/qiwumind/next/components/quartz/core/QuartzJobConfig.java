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

///*
// * Next Components V2
// * Copyright 2026 liks
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Author: liks
// * Email: 307039176@qq.com
// */
//
//package com.qiwumind.next.components.quartz.core;
//
//
//
//import lombok.Data;
//import org.apache.commons.lang3.StringUtils;
//import org.quartz.Job;
//import org.quartz.TimeOfDay;
//import org.quartz.Trigger;
//import org.quartz.CronExpression;
//
//import java.util.Date;
//import java.util.Map;
//
///**
// * 任务配置类
// */
//@Data
//public class QuartzJobConfig {
//    private String jobName;
//    private String jobGroup;
//    private String triggerName;
//    private String triggerGroup;
//    private String description;
//    private String triggerDescription;
//    private Class<? extends Job> jobClass;
//    private Map<String, Object> jobData;
//
//    // 调度配置
//    private ScheduleType scheduleType;
//    private String cronExpression;
//    private Integer repeatCount;
//    private Integer intervalInSeconds;
//    private TimeOfDay startTimeOfDay;
//    private TimeOfDay endTimeOfDay;
//    private Date startTime;
//    private Date endTime;
//
//    // 高级配置
//    private Integer priority = Trigger.DEFAULT_PRIORITY;
//    private Integer misfireInstruction;
//    private boolean durable = true;
//    private boolean requestRecovery = true;
//    private boolean overwriteExisting = false;
//
//    public void validate() {
//        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(jobGroup)) {
//            throw new IllegalArgumentException("任务名称和任务组不能为空");
//        }
//        if (jobClass == null) {
//            throw new IllegalArgumentException("任务类不能为空");
//        }
//        if (scheduleType == null) {
//            throw new IllegalArgumentException("调度类型不能为空");
//        }
//
//        switch (scheduleType) {
//            case CRON:
//                if (StringUtils.isBlank(cronExpression)) {
//                    throw new IllegalArgumentException("Cron表达式不能为空");
//                }
//                if (!CronExpression.isValidExpression(cronExpression)) {
//                    throw new IllegalArgumentException("无效的Cron表达式: " + cronExpression);
//                }
//                break;
//            case SIMPLE:
//                if (intervalInSeconds == null || intervalInSeconds <= 0) {
//                    throw new IllegalArgumentException("简单调度需要设置有效的间隔时间");
//                }
//                break;
//            case DAILY:
//                if (intervalInSeconds == null || intervalInSeconds <= 0) {
//                    throw new IllegalArgumentException("每日调度需要设置有效的间隔时间");
//                }
//                break;
//        }
//    }
//
//    public static enum ScheduleType {
//        CRON, SIMPLE, DAILY
//    }
//}
