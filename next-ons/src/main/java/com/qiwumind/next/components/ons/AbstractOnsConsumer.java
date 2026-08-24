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

package com.qiwumind.next.components.ons;



import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * 类AbstractOnsConsumer.java的实现描述：ONS监听基类,ons-client v1.8.4.Final
 * 版本资源隔离优化（实例化支持）
 * 
 * @author   2021年8月13日 上午10:52:16
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractOnsConsumer implements InitializingBean, MessageListener {

    /**
     * ONS topic
     */
    protected String topic;

    /**
     * ONS CID
     */
    protected String groupId;

    /**
     * TAG
     */
    protected String tag = "*";

    /**
     * AccessKey
     */
    protected String accessKey;

    /**
     * SecretKey
     */
    protected String secretKey;

    /**
     * ONS Addr
     */
    protected String namesrvAddr;

    /**
     * 创建bean时，启动监听
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始启动消息监听");
        final Properties properties = new Properties();
        properties.put(PropertyKeyConst.GROUP_ID, groupId);
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        // 推荐配置。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, namesrvAddr);
        // 兼容配置（不推荐继续使用，建议逐渐升级为推荐配置）。
        //  properties.put(PropertyKeyConst.ONSAddr, "xxxx");
        final Consumer consumer = ONSFactory.createConsumer(properties);
        doConsume(consumer);
    }

    /**
     * 模板方式消费消息
     *
     * @param consumer 消费者
     */
    private void doConsume(final Consumer consumer) {
        consumer.subscribe(topic, tag, this);
        consumer.start();
        log.info("消息监听启动成功{},gid={}", topic, groupId);
    }

}
