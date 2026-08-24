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
//package com.qiwumind.next.components.quartz.core.dto;
//
//
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.quartz.CronExpression;
//
//import java.util.Date;
//import java.util.Map;
//
//// MessageJobConfig.java
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class MessageJobConfig {
//    private String jobName;
//    private String jobGroup;
//    private String triggerName;
//    private String triggerGroup;
//    private ScheduledMessage message;
//    private Date startTime;
//    private Date endTime;
//    private Integer priority;
//    private Map<String, Object> jobData;
//
//    public void validate() {
//        if (message == null) {
//            throw new IllegalArgumentException("消息内容不能为空");
//        }
//        if (StringUtils.isBlank(jobName)) {
//            throw new IllegalArgumentException("任务名称不能为空");
//        }
//        if (StringUtils.isBlank(message.getMessageTitle())) {
//            throw new IllegalArgumentException("消息标题不能为空");
//        }
//        // 验证调度配置
//        validateScheduleConfig();
//    }
//
//    private void validateScheduleConfig() {
//        switch (message.getScheduleType()) {
//            case CRON:
//                if (StringUtils.isBlank(message.getCronExpression())) {
//                    throw new IllegalArgumentException("Cron表达式不能为空");
//                }
//                if (!CronExpression.isValidExpression(message.getCronExpression())) {
//                    throw new IllegalArgumentException("无效的Cron表达式: " + message.getCronExpression());
//                }
//                break;
//            case ONCE:
//                if (message.getExecuteTime() == null) {
//                    throw new IllegalArgumentException("执行时间不能为空");
//                }
//                if (message.getExecuteTime().before(new Date())) {
//                    throw new IllegalArgumentException("执行时间不能早于当前时间");
//                }
//                break;
//            case HOURLY:
//            case DAILY:
//            case WEEKLY:
//            case MONTHLY:
//                if (message.getExecuteTime() == null) {
//                    throw new IllegalArgumentException("基准执行时间不能为空");
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("基准执行时间不能为空");
//        }
//    }
//
//    public String buildJobName() {
//        return "MSG_JOB_" + (jobName != null ? jobName : message.getId());
//    }
//
//    public String buildTriggerName() {
//        return "MSG_TRIGGER_" + System.currentTimeMillis();
//    }
//}
