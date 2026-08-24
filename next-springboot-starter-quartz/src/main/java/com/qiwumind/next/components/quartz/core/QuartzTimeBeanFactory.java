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
//import com.alibaba.fastjson2.JSONObject;
//import com.qiwumind.next.components.redis.core.cache.JedisCache;
//import com.qiwumind.next.components.redis.core.lock.JedisLockService;
//import com.qiwumind.next.components.common.util.date.DateUtils;
//import com.qiwumind.next.components.common.util.serializer.SerializationUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.Validate;
//import org.quartz.Scheduler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//
//import java.text.ParseException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class QuartzTimeBeanFactory implements InitializingBean {
//    private static Logger logger = LoggerFactory.getLogger(QuartzTimeBeanFactory.class);
//    private QuartzManager quartzManager;
//    private String group;
//    private Map<String, AbstractTaskData> taskmap = new HashMap<String, AbstractTaskData>();
//    private Scheduler scheduler;
//    private String key;
//    private JedisCache jedisCache;
//    private JedisLockService jedisLockService;
//
//    //暂不支持多版本共存定时生效
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Validate.validState(StringUtils.isNotBlank(key), "key 不能为null, 请先存存");
//        Validate.validState(scheduler != null, "scheduler 不能为null, 请先存存");
//        quartzManager = new QuartzManager(scheduler);
//    }
//
//    public void addJob(final String cronKey, final String cronValue) {
//        Validate.validState(taskmap.size() > 0, "taskmap 不能为空, 请先存存");
//        // judge job is exists redis?
//        final String jcv = jedisCache.getValueByKey(cronKey);
//        if (StringUtils.isBlank(jcv)) {
//            jedisCache.setValueByKey(cronKey, cronValue, 0);
//        }
//        final AbstractTaskData taskData = taskmap.get(cronKey);
//        try {
//            quartzManager.addJob(cronKey, group, cronKey, group, DefaultQuartzTask.class,
//                    DateUtils.getCron(DateUtils.parseDatePartString(cronValue), 0), taskData);
//        } catch (final ParseException e) {
//            logger.error("", e);
//        }
//
//    }
//
//    public void modifyJob(final String cronKey, final String cronValue) {
//        // judge job is exists redis?
//        Validate.validState(taskmap.size() > 0, "taskmap 不能为空, 请先存存");
//        /*final Jedis jedis = JedisUtils.getJedis(envJedisPool);
//        final String jcv = jedis.hget(key, cronKey);*/
//        final String jcv = jedisCache.hget(key, cronKey);
//        Validate.validState(StringUtils.isNotBlank(jcv), " jcv不能为空, 请先检查数据");
//        try {
//            if (!cronValue.equals(SerializationUtils.getObjFromStr(jcv))) {
//                if (DateUtils.compareTime(DateUtils.parseDatePartString(cronValue))) {
//                    final AbstractTaskData taskData = taskmap.get(cronKey);
//                    if (quartzManager.getJob(cronKey, group)) {
//                        quartzManager.modifyJobTime(cronKey, group,
//                                DateUtils.getCron(DateUtils.parseDatePartString(cronValue), 0));
//                    } else {
//                        quartzManager.addJob(cronKey, group, cronKey, group, DefaultQuartzTask.class,
//                                DateUtils.getCron(DateUtils.parseDatePartString(cronValue), 0), taskData);
//                    }
//                    jedisCache.hset(key, cronKey, cronValue);
//                    // jedis.hset(key, cronKey, cronValue);
//                }
//            }
//        } catch (final ParseException e) {
//            logger.error("", e);
//        }
//
//    }
//
//    public void removeJob(final String key, final String cronKey) {
//        // judge job is exists redis?
//        Validate.validState(taskmap.size() > 0, "taskmap 不能为空, 请先存存");
//        if (jedisCache.exists(key)) {
//          /*  final Jedis jedis = JedisUtils.getJedis(envJedisPool);
//            jedis.hdel(key, cronKey);*/
//            jedisCache.hdel(key, cronKey);
//            JSONObject.toJSONString(jedisCache.hgetAll(key));
//            quartzManager.removeJob(cronKey, group, cronKey, group);
//        }
//    }
//
//
//    public String getGroup() {
//        return group;
//    }
//
//    public void setGroup(final String group) {
//        this.group = group;
//    }
//
//    public Scheduler getScheduler() {
//        return scheduler;
//    }
//
//    public void setScheduler(final Scheduler scheduler) {
//        this.scheduler = scheduler;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(final String key) {
//        this.key = key;
//    }
//
//    public void setTaskmap(final Map<String, AbstractTaskData> taskmap) {
//        this.taskmap = taskmap;
//    }
//
//    public Map<String, AbstractTaskData> getTaskmap() {
//        return taskmap;
//    }
//
//    public JedisCache getJedisCache() {
//        return jedisCache;
//    }
//
//    public void setJedisCache(final JedisCache jedisCache) {
//        this.jedisCache = jedisCache;
//    }
//
//    public QuartzManager getQuartzManager() {
//        return quartzManager;
//    }
//
//    public JedisLockService getJedisLockService() {
//        return jedisLockService;
//    }
//
//    public void setJedisLockService(JedisLockService jedisLockService) {
//        this.jedisLockService = jedisLockService;
//    }
//}
