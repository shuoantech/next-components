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
//
//import java.util.Date;
//
//// ScheduledMessage.java
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ScheduledMessage {
//    private Long id;
//    private String jobId;
//    private Long userId;
//    private String messageTitle;
//    private String messageContent;
//    private String messageType; // NOTIFICATION, ACTIVITY, REMINDER
//    private String targetUsers; // ALL, SPECIFIC, CONDITIONAL
//    private String userCondition; // SQL或条件表达式
//
//    // 调度配置
//    private ScheduleType scheduleType; // ONCE, DAILY, WEEKLY, MONTHLY, CRON
//    private String cronExpression;
//    private Date executeTime;
//    private Integer repeatCount;
//    private Integer intervalMinutes;
//
//    // 状态
//    private JobStatus status;
//    private Date createTime;
//    private Date updateTime;
//
//    public static enum ScheduleType {
//        ONCE, HOURLY, DAILY, WEEKLY, MONTHLY, CRON
//    }
//
//    public static enum JobStatus {
//        DRAFT, SCHEDULED, RUNNING, PAUSED, COMPLETED, CANCELLED, FAILED
//    }
//}
