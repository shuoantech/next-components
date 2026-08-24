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
//import com.google.common.base.Splitter;
//import com.qiwumind.next.components.redis.core.cache.JedisCache;
//import com.qiwumind.next.components.redis.core.lock.JedisLockService;
//import com.qiwumind.next.components.common.util.serializer.SerializationUtils;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.Validate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.UUID;
//
//@Setter
//@Getter
//public abstract class AbstractTaskData /*implements TaskData*/ {
//    private static final Logger log = LoggerFactory.getLogger(AbstractTaskData.class);
//
//    private QuartzTimeBeanFactory quartzTimeBeanFactory;
//    private String group;
//
//    protected AbstractTaskData() {
//    }
//
//    @Override
//    public void execute(final String ukc) {
//        Validate.validState(StringUtils.isNotBlank(group), "group 不能为null, 请先存存");
//        //judge cron 是否一致
//        final List<String> list = Splitter.on("|+|").splitToList(ukc);
//        Validate.validState(list.size()==2, "ukc 配置有误 , 请先核对存存");
//        final String key = list.get(0);
//        final String strValue = list.get(1);
//
//        JedisCache jedisCache = quartzTimeBeanFactory.getJedisCache();
//        final String jcv = jedisCache.getValueByKey(group() + ":" + key);
//        if (StringUtils.isBlank(jcv)) {
//            return;
//        }
//        String value = UUID.randomUUID().toString();
//        JedisLockService jedisLockService = quartzTimeBeanFactory.getJedisLockService();
//
//        jedisLockService.tryLock(group() + ":" + key, value, 10 * 1000);
//        if (SerializationUtils.getObjFromStr(jcv).equals(strValue)) {
//            log.info("******run {}.{}******", key, group);
//            run(key);
//        } else {
//            log.info("******removeJob {}.{}****** ", key, group);
//            quartzTimeBeanFactory.removeJob(group(), key);
//        }
//        jedisLockService.releaseLock(group() + ":" + key, value);
//
//    }
//
//}
