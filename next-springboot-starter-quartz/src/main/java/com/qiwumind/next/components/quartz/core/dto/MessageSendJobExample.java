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
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//// MessageSendJobExample.java
//@Slf4j
//public class MessageSendJobExample extends MessageSendJob {
//    @Override
//    protected void execute(JobExecutionContext context, ScheduledMessage message) {
//        try {
//            // 执行消息发送
//            MessageSendResult  result = sendMessageToUsers(message);
//            // 记录执行日志
////            recordExecutionLog(context, message, result, null);
//            log.info("消息发送任务执行成功: {}, 发送用户数: {}",
//                    message.getMessageTitle(), result.getSuccessCount());
//
//        } catch (Exception e) {
//            log.error("消息发送任务执行失败: {}", message.getMessageTitle(), e);
////            recordExecutionLog(context, message, null, e);
////            handleJobFailure(context, message, e);
//        }
//    }
//
//    private MessageSendResult sendMessageToUsers(ScheduledMessage message) {
//        MessageSendResult result = new MessageSendResult();
//        // 获取目标用户
//        List<Long> targetUserIds = new ArrayList<>();;//getTargetUsers(message);
//        if (targetUserIds.isEmpty()) {
//            log.warn("未找到目标用户，跳过发送: {}", message.getMessageTitle());
//            return result;
//        }
//        // 分批发送
//        List<List<Long>> userBatches = Lists.partition(targetUserIds, 100); // 每批100用户
//        for (List<Long> batch : userBatches) {
//            try {
////                int batchSuccess = sendBatchMessage(message, batch);
////                result.incrementSuccess(batchSuccess);
//            } catch (Exception e) {
//                log.error("批次消息发送失败: {}", batch.size(), e);
//                result.incrementFailed(batch.size());
//            }
//        }
//
//        return result;
//    }
//
////    private List<Long> getTargetUsers(ScheduledMessage message) {
////        switch (message.getTargetUsers()) {
////            case "ALL":
////                return userService.getAllActiveUserIds();
////            case "SPECIFIC":
////                return parseSpecificUserIds(message.getUserCondition());
////            case "CONDITIONAL":
////                return userService.getUserIdsByCondition(message.getUserCondition());
////            default:
////                return Collections.emptyList();
////        }
////    }
//
////    private int sendBatchMessage(ScheduledMessage message, List<Long> userIds) {
////        List<UserMessage> userMessages = userIds.stream()
////                .map(userId -> UserMessage.builder()
////                        .userId(userId)
////                        .title(message.getMessageTitle())
////                        .content(message.getMessageContent())
////                        .messageType(message.getMessageType())
////                        .source("SCHEDULED")
////                        .build())
////                .collect(Collectors.toList());
////
////        return messageService.batchInsertMessages(userMessages);
////    }
//
////    private void recordExecutionLog(JobExecutionContext context, ScheduledMessage message,
////                                  MessageSendResult result, Exception error) {
////        // 保存执行记录到数据库
////        JobExecutionLog log = JobExecutionLog.builder()
////                .jobName(context.getJobDetail().getKey().getName())
////                .jobGroup(context.getJobDetail().getKey().getGroup())
////                .messageId(message.getId())
////                .fireTime(context.getFireTime())
////                .nextFireTime(context.getNextFireTime())
////                .successCount(result != null ? result.getSuccessCount() : 0)
////                .failedCount(result != null ? result.getFailedCount() : 0)
////                .errorMessage(error != null ? error.getMessage() : null)
////                .status(error == null ? "SUCCESS" : "FAILED")
////                .build();
////
////        jobExecutionLogService.save(log);
////    }
//
////    private void handleJobFailure(JobExecutionContext context, ScheduledMessage message, Exception e) {
////        // 根据失败策略处理
////        if (shouldRetry(context)) {
////            throw new JobExecutionException(e, true); // 重新执行
////        } else {
////            // 更新任务状态为失败
////            scheduledMessageService.updateStatus(message.getId(), ScheduledMessage.JobStatus.FAILED);
////        }
////    }
//
//    private boolean shouldRetry(JobExecutionContext context) {
//        Integer retryCount = (Integer) context.get("retryCount");
//        return retryCount == null || retryCount < 3;
//    }
//}
