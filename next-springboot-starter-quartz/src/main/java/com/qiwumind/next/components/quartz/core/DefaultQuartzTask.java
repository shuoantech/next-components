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
//import org.quartz.*;
//
///**
// * 定时任务
// */
//public class DefaultQuartzTask implements Job {
//    /**
//     *
//     */
//    public DefaultQuartzTask() {
//    }
//
//    @Override
//    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        //执行任务
//        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
//        final JobDataMap dataMap = jobDetail.getJobDataMap();
//        final String uk = jobDetail.getKey().getName() + "|+|" + jobDetail.getKey().getGroup();
//        final TaskData quartzTask = (TaskData) dataMap.get(uk);
//        // 调用接口函数
//        quartzTask.execute(uk);
//
//    }
//}
