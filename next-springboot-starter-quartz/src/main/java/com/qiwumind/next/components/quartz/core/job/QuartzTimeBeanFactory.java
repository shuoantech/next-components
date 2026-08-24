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

package com.qiwumind.next.components.quartz.core.job;


import com.qiwumind.next.components.context.helper.SpringContextHelper;
import com.qiwumind.next.components.quartz.core.JobTask;
import com.qiwumind.next.components.quartz.core.dto.SysJob;
import com.qiwumind.next.components.quartz.core.util.ScheduleUtils;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import com.qiwumind.next.components.redis.core.lock.JedisLockService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class QuartzTimeBeanFactory implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(QuartzTimeBeanFactory.class);
    private AbstractQuartzJob quartzJob;
    private Scheduler scheduler;
    private JedisCache jedisCache;
    private JedisLockService jedisLockService;
    private JobTask jobTask;

    //暂不支持多版本共存定时生效
    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.validState(scheduler != null, " scheduler 不能为null, 请配置注入 ");
        JobTask task = SpringContextHelper.getBean(JobTask.class);
        Validate.validState(task != null, " taskData 不能为null, 请配置注入  ");
        jobTask = task;
        scheduler.clear();
        List<SysJob> jobList = jobTask.queryJobDetail();
        for (SysJob job : jobList) {
            addJob(job);
        }
    }

    public void addJob(final SysJob sysJob) {
        final String jcv = jedisCache.getValueByKey(sysJob.getUniqueKey());
        if (StringUtils.isBlank(jcv)) {
            jedisCache.setValueByKey(sysJob.getUniqueKey(), sysJob.getCronExpression(), 0);
        }
        try {
            ScheduleUtils.createScheduleJob(scheduler, sysJob);
//            quartzManager.addJob(cronKey, group, cronKey, group, DefaultQuartzTask.class,
//                    DateUtils.getCron(DateUtils.parseDatePartString(cronValue), 0), jobTask);
        } catch (final Exception e) {
            logger.error("", e);
        }

    }



    public JedisLockService getJedisLockService() {
        return jedisLockService;
    }

    public JedisCache getJedisCache() {
        return jedisCache;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public AbstractQuartzJob getQuartzJob() {
        return quartzJob;
    }

    public void setJedisLockService(JedisLockService jedisLockService) {
        this.jedisLockService = jedisLockService;
    }

    public void setJedisCache(JedisCache jedisCache) {
        this.jedisCache = jedisCache;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setQuartzJob(AbstractQuartzJob quartzJob) {
        this.quartzJob = quartzJob;
    }
}