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

package com.qiwumind.next.components.common.util.http;


import cn.hutool.json.JSONObject;
import com.qiwumind.next.components.common.util.json.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 类Http4ClientUtils.java的实现描述 ，保留jdk8支持，建议后续替换
 *
 * @author liks 2019年7月30日 下午1:42:40
 */
@Slf4j
public class Http4ClientUtils {
    private static Logger                             logger     = LoggerFactory.getLogger(Http4ClientUtils.class);
    private static PoolingHttpClientConnectionManager manager    = null;
    private static CloseableHttpClient                httpClient = null;

    static {
        //注册访问协议相关的Socket工厂
        final ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        final LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSystemSocketFactory();
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf).build();
        //HttpConnection 工厂:配置写请求/解析响应处理器
        final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory = new ManagedHttpClientConnectionFactory(
                DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
        //DNS 解析器
        final DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
        //创建池化连接管理器
        manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);
        //默认为Socket配置
        final SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        manager.setDefaultSocketConfig(defaultSocketConfig);
        manager.setMaxTotal(300); //设置整个连接池的最大连接数
        //每个路由的默认最大连接，每个路由实际最大连接数由DefaultMaxPerRoute控制，而MaxTotal是整个池子的最大数
        //设置过小无法支持大并发(ConnectionPoolTimeoutException) Timeout waiting for connection from pool
        manager.setDefaultMaxPerRoute(200);//每个路由的最大连接数
        //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
        manager.setValidateAfterInactivity(5 * 1000);
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(final String url) {
        return doGet(url, Collections.emptyMap());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return 返回json字符串
     */
    public static String doGet(final String url, final Map<String, Object> params) {
        final long a = System.currentTimeMillis();
        String apiUrl = url;
        final StringBuffer param = new StringBuffer();
        int i = 0;
        for (final String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        final CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        HttpGet httpPost = null;
        try {
            httpPost = new HttpGet(apiUrl);
            response = httpClient.execute(httpPost);
            final int status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.SC_OK) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 读取服务器返回过来的json字符串数据
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                //不推荐使用CloseableHttpResponse.close关闭连接，他将直接关闭Socket，导致长连接不能复用
                EntityUtils.consume(response.getEntity());
            }
            final StringBuilder logsb = new StringBuilder();
            logsb.append("##################\n");
            logsb.append("# 发送报文：" + getPlanText(params) + "\n");
            logsb.append("# 响应代码：" + status + "\n");
            logsb.append("# 响应报文：" + result + "\n");
            logsb.append("# 耗时：" + (System.currentTimeMillis() - a) + "\n");
            logsb.append("########################################################################\n");
            logger.info(logsb.toString());
            return result;
        } catch (final IOException e) {
            try {
                if (null != response) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (final IOException e1) {
                logger.error(e.getMessage(), e1);
            }
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url 接口URL
     * @param params 参数map
     * @param headersMap 请求header消息头
     * @return
     */
    public static String doPost(final String url, final Map<String, Object> params, final Map<String, String> headersMap) {
        final long a = System.currentTimeMillis();
        String result = null;
        final HttpPost httpPost = new HttpPost(url);
        final CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            //头部请求信息
            final Header[] headers = initHeader(headersMap);
            if (headers != null) {
                for (final Header header : headers) {
                    httpPost.addHeader(header);
                }
            }
            // 设置类型
            final String json = JacksonUtils.toJsonString(params);
            httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            final int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                //不推荐使用CloseableHttpResponse.close关闭连接，他将直接关闭Socket，导致长连接不能复用
                EntityUtils.consume(response.getEntity());
            }

            final StringBuilder logsb = new StringBuilder();
            logsb.append("##################\n");
            logsb.append("# 发送报文：" + getPlanText(params) + "\n");
            logsb.append("# 响应代码：" + status + "\n");
            logsb.append("# 响应报文：" + result + "\n");
            logsb.append("# 耗时：" + (System.currentTimeMillis() - a) + "\n");
            logsb.append("########################################################################\n");
            logger.info(logsb.toString());
            return result;
        } catch (final Exception e) {
            try {
                if (null != response) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (final IOException e1) {
                logger.error(e.getMessage(), e1);
            }
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    //请求头部信息
    private static Header[] initHeader(final Map<String, String> header) {
        Header[] headers = null;
        if (header != null) {
            final Set<Map.Entry<String, String>> entrySet = header.entrySet();
            final int dataLength = entrySet.size();
            headers = new Header[dataLength];
            int i = 0;
            for (final Map.Entry<String, String> anEntrySet : entrySet) {
                final Map.Entry<String, String> entry = anEntrySet;
                headers[i++] = new BasicHeader(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return headers;
    }

    /**
     * 参数Map格式化
     *
     * @param map
     * @return
     */
    public static String getPlanText(final Map<String, Object> map) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            //默认请求配置
            final RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(3 * 1000) //设置连接超时时间，3s
                    .setSocketTimeout(10 * 1000) //设置等待数据超时时间，10s
                    .setConnectionRequestTimeout(5 * 1000) //设置从连接池获取连接的等待超时时间
                    .build();
            //创建HttpClient
            httpClient = HttpClients.custom().setConnectionManager(manager).setConnectionManagerShared(false) //连接池不是共享模式
                    .evictIdleConnections(60L, TimeUnit.SECONDS) //定期回收空闲连接
                    .evictExpiredConnections()// 定期回收过期连接
                    .setConnectionTimeToLive(60, TimeUnit.SECONDS) //连接存活时间，如果不设置，则根据长连接信息决定
                    .setDefaultRequestConfig(defaultRequestConfig) //设置默认请求配置
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE) //连接重用策略，即是否能keepAlive
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE) //长连接配置，即获取长连接生产多长时间
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)) //设置重试次数，默认是3次，当前是禁用掉（根据需要开启）
                    .build();
            //JVM 停止或重启时，关闭连接池释放掉连接(跟数据库连接池类似)
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if (httpClient != null) {
                            httpClient.close();
                        }
                    } catch (final IOException e) {
                        logger.error("error when close httpClient:{}", e);
                    }

                }
            });
        }
        //DefaultHttpRequestRetryHandler代替
        //        final HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
        //            @Override
        //            public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
        //                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
        //                    return false;
        //                }
        //                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
        //                    return true;
        //                }
        //                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
        //                    return false;
        //                }
        //                if (exception instanceof InterruptedIOException) {// 超时
        //                    return false;
        //                }
        //                if (exception instanceof UnknownHostException) {// 目标服务器不可达
        //                    return false;
        //                }
        //                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
        //                    return false;
        //                }
        //                if (exception instanceof SSLException) {// ssl握手异常
        //                    return false;
        //                }
        //
        //                final HttpClientContext clientContext = HttpClientContext.adapt(context);
        //                final HttpRequest request = clientContext.getRequest();
        //                // 如果请求是幂等的，就再次尝试
        //                if (!(request instanceof HttpEntityEnclosingRequest)) {
        //                    return true;
        //                }
        //                return false;
        //            }
        //
        //        };
        return httpClient;
    }


    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param jsonObject
     * @param headerMap
     * @return
     */
    public static String doPost(final String url, final JSONObject jsonObject, final Map<String, String> headerMap) {

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        final HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach((k, v) -> httpPost.setHeader(k, v)); // header设定
        }

        StringEntity entity = null;
        httpPost.setURI(java.net.URI.create(url));
        try {
            entity = new StringEntity(JacksonUtils.toJsonString(jsonObject), "utf-8");
        } catch (final Exception e) {
            log.error("requestBody to String exception:", e);
            return null;
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json;charset=utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {
            String resultStr = null;
            response = httpclient.execute(httpPost);
            final int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                final HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    resultStr = EntityUtils.toString(responseEntity);
                }
            } else {
                log.error("http post return:" + status + "(" + url + ")");
                return null;
            }
            return resultStr;
        } catch (final Exception e) {
            log.error("httpclient.execute() exception.", e);
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (final IOException e) {
                    log.error("response.close() exception", e);
                }
            }
            try {
                httpclient.close();
            } catch (final IOException e) {
                log.error("httpclient.close() exception", e);
            }
        }
    }


}
