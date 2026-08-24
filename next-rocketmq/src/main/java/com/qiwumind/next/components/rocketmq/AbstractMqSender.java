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

package com.qiwumind.next.components.rocketmq;



import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
@Slf4j
public abstract class AbstractMqSender implements InitializingBean, DisposableBean {
    /**
     *
     */
    protected String topic;
    /**
     * AccessKey
     */
    protected String accessKey;
    /**
     * SecretKey
     */
    protected String secretKey;
    /**
     *
     */
    protected String endpoints;
    protected String defaultTag = "*";
    protected Producer producer;

    /**
     * 创建bean时，启动监听
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始启动消息监听");
        producer = buildProducer(null, topic);
        log.info("mq Sender启动。。。。producer={}", producer);

    }

    @Override
    public void destroy() throws Exception {
        producer.close();
    }

    /**
     * @param tag
     * @param msg
     * @return
     */
    public SendReceipt sendMsg(final String tag, final String msg) {
        byte[] body = msg.getBytes(StandardCharsets.UTF_8);
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setTag(StringUtils.isBlank(tag) ? defaultTag : tag)
                .setKeys(UUID.randomUUID().toString())
                .setBody(body)
                .build();
        try {
            final SendReceipt sendReceipt = producer.send(message);
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
            return sendReceipt;
        } catch (Throwable t) {
            log.error("Failed to send message", t);
        }
        return null;
    }

    /***
     * 发送消息
     *
     * @param tag 标签
     * @param msg 发送的信息
     * @param minute    延迟消息 分钟级别（绝对时间戳）
     * @return 发送结果
     */
    public SendReceipt sendMsg(final String tag, final String msg, int minute) {

        byte[] body = msg.getBytes(StandardCharsets.UTF_8);
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(StringUtils.isBlank(tag) ? defaultTag : tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys(UUID.randomUUID().toString())
                .setDeliveryTimestamp(System.currentTimeMillis() + 60000 * minute)  // 1分钟后
                .setBody(body)
                .build();
        try {
            final SendReceipt sendReceipt = producer.send(message);
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
            return sendReceipt;
        } catch (Throwable t) {
            log.error("Failed to send message", t);
        }
        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        return null;

    }


    private Producer buildProducer(TransactionChecker checker, String... topics) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        // Credential provider is optional for client configuration.
        // This parameter is necessary only when the server ACL is enabled. Otherwise,
        // it does not need to be set by default.
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                // .enableSsl(false)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        final ProducerBuilder builder = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch
                // the topic route before message publishing.
                .setTopics(topics);
        if (checker != null) {
            // Set the transaction checker.
            builder.setTransactionChecker(checker);
        }
        return builder.build();
    }


}
