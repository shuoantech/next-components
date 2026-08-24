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



import com.aliyun.openservices.ons.api.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

/**
 * ons-client v1.8.4.Final 版本资源隔离优化（实例化支持）
 * 
 * @author   2021年8月13日 上午10:52:16
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractOnsSender implements InitializingBean {

    /**
     * ONS topic
     */
    protected String   topic;

    /**
     * ONS CID
     */
    protected String   groupId;
    /**
     * AccessKey
     */
    protected String   accessKey;
    /**
     * SecretKey
     */
    protected String   secretKey;
    /**
     * ONS Addr
     */
    protected String   namesrvAddr;

    protected String   defaultTag = "*";

    protected Producer producer;
    protected int      delaySeconds;    //ons的 延迟时间
    public static final String  DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

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
        Producer producer = ONSFactory.createProducer(properties);
        producer.start();
        this.producer = producer;
        log.info("OnsSender启动。。。。producer={}", producer);

    }

    /***
     * 发送消息
     *
     * @param tag 标签
     * @param msg 发送的信息
     * @return 发送结果
     */
    public SendResult sendMsg(final String tag, final String msg) {
        try {
            log.info("消息标签={};,时间={}", tag,
                    DateFormatUtils.format(new Date(), DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
            return sendMsg(tag, msg.getBytes(StandardCharsets.UTF_8.name()));
        } catch (final UnsupportedEncodingException e) {
            log.error("发送消息失败", e);
            return null;
        }
    }

    /**
     * sendMsg
     *
     * @param tag
     * @param body
     * @return
     */
    public SendResult sendMsg(final String tag, final byte[] body) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(topic)
                || (StringUtils.isBlank(tag) && StringUtils.isBlank(defaultTag)) || ArrayUtils.isEmpty(body)) {
            throw new IllegalArgumentException("需要groupId、topic、tag、msg信息");
        }
        if (producer == null) {
            throw new IllegalArgumentException(String.format("[%s]非法，请检查groupId", groupId));
        }
        try {
            final Message sendMsg = new Message(topic, StringUtils.isNotBlank(tag) ? tag : defaultTag, body);
            return producer.send(sendMsg);
        } catch (final Exception e) {
            log.error("发送消息失败,消息：{},错误：{}", new String(body), e);
            throw new IllegalAccessError("发送消息失败,原因：" + e);
        }
    }

    /**
     * 延时发送消息
     * 
     * @param tag
     * @param body
     * @return
     */
    public SendResult delayTimeSendMsg(final String tag, String body) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(topic)
                || (StringUtils.isBlank(tag) && StringUtils.isBlank(defaultTag)) || StringUtils.isBlank(body)) {
            throw new IllegalArgumentException("需要groupId、topic、tag、msg信息");
        }
        if (producer == null) {
            throw new IllegalArgumentException(String.format("[%s]非法，请检查groupId", groupId));
        }
        try {
            final Message sendMsg = new Message(topic, StringUtils.isNotBlank(tag) ? tag : defaultTag,
                    body.getBytes(StandardCharsets.UTF_8.name()));
            long delayTime = System.currentTimeMillis() + delaySeconds * 1000;
            sendMsg.setStartDeliverTime(delayTime);

            SendResult sendResult = producer.send(sendMsg);
            log.info("发送任务状态检查消息，messageId:{} " + sendResult.getMessageId());
            return sendResult;
        } catch (final Exception e) {
            log.error("发送消息失败,消息：{},错误：{}", new String(body), e);
            throw new IllegalAccessError("发送消息失败,原因：" + e);
        }
    }
}
