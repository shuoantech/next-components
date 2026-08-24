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

package com.qiwumind.next.components.quartz.autoconfigure;


import com.qiwumind.next.components.common.constant.SystemConstants;
import com.qiwumind.next.components.quartz.core.JobTask;
import com.qiwumind.next.components.quartz.core.constant.ScheduleConstants;
import com.qiwumind.next.components.quartz.core.job.AbstractQuartzJob;
import com.qiwumind.next.components.quartz.core.job.QuartzTimeBeanFactory;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import com.qiwumind.next.components.redis.core.lock.JedisLockService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzAutoConfiguration {


    private final Logger log = LoggerFactory.getLogger(QuartzAutoConfiguration.class);
    @Bean("quartzTimeBeanFactory")
    @ConditionalOnMissingBean(QuartzTimeBeanFactory.class)
    public QuartzTimeBeanFactory getQuartzTime(final Scheduler scheduler, JedisCache jedisCache) {
        final QuartzTimeBeanFactory timeBeanFactory = new QuartzTimeBeanFactory();
        try {
//            timeBeanFactory.setQuartzJob(quartzConfiguration.getKey());
//            timeBeanFactory.setJedisLockService(quartzConfiguration.getGroup());
            timeBeanFactory.setJedisCache(jedisCache);
            timeBeanFactory.setScheduler(scheduler);

            timeBeanFactory.afterPropertiesSet();
        } catch (final Exception e) {
            log.info("", e);
        }
        log.info("************* load quartzTimeBeanFactory  end  **************");
        return timeBeanFactory;
    }

    @Bean
    @ConditionalOnProperty(prefix = SystemConstants.GLOBAL, name = "quartz-open", havingValue = "true", matchIfMissing = false)
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //  1. 关联数据源，实现持久化
        factory.setDataSource(dataSource);
        // quartz参数
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceName", "QiwumindScheduler");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        // 线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        // JobStore配置
        prop.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        // 集群配置
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "10");
        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");

        // sqlserver 启用
        // prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");
        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        // 3. 设置Quartz原生参数，如线程池大小
        factory.setQuartzProperties(prop);

        factory.setSchedulerName("QiwumindScheduler");
        // 5. 延迟2秒启动，确保其他Bean初始化完毕
        factory.setStartupDelay(2);
        //  6. 将Spring的ApplicationContext放入调度器上下文，便于Job中获取
        factory.setApplicationContextSchedulerContextKey(ScheduleConstants.APPLICATIONCONTEXT_KEY);
        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        // 设置自动启动，默认为true
        factory.setAutoStartup(true);
        log.info("************  load SchedulerFactoryBean  end  ********** ");
        return factory;
    }
}
