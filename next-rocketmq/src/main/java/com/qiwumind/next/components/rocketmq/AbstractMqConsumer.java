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
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;

@Getter
@Setter
public abstract class AbstractMqConsumer implements InitializingBean, MessageListener {
    /** */
    protected String topic;
    /** **/
    protected String groupId;

    protected String accessKey;
    /**
     * SecretKey
     */
    protected String secretKey;
    /** */
    protected String endpoints;
    protected String defaultTag = "*";
    protected PushConsumer pushConsumer;

    /**
     * 创建bean时，启动监听
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        pushConsumer = buildPushConsumer();
    }


    private PushConsumer buildPushConsumer() throws ClientException {
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

        FilterExpression filterExpression = new FilterExpression(defaultTag, FilterExpressionType.TAG);
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the consumer group name.
                .setConsumerGroup(groupId)
                // Set the subscription for the consumer.
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .setMessageListener(messageView -> {
                    // Handle the received message and return consume result.
                    return consume(messageView);
                })
                .build();

        return pushConsumer;
    }


}
