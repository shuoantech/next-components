///*
// * MIT License
// *
// * Copyright (c) 2026 qiwumind
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.  Author: liks
// * Email: 307039176@qq.com
// */
//
//package com.qiwumind.next.components.signature.core;
//
//import cn.hutool.core.map.MapUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.crypto.digest.DigestUtil;
//import com.qiwumind.next.components.signature.core.annotation.ApiSignature;
//import com.qiwumind.next.components.signature.core.aop.ApiSignatureAspect;
//import com.qiwumind.next.components.signature.core.redis.ApiSignatureRedisDAO;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
///**
// * {@link ApiSignatureTest} 的单元测试
// */
//@ExtendWith(MockitoExtension.class)
//public class ApiSignatureTest {
//
//    @InjectMocks
//    private ApiSignatureAspect apiSignatureAspect;
//
//    @Mock
//    private ApiSignatureRedisDAO signatureRedisDAO;
//
//    @Test
//    public void testSignatureGet() throws IOException {
//        // 搞一个签名
//        Long timestamp = System.currentTimeMillis();
//        String nonce = IdUtil.randomUUID();
//        String appId = "xxxxxx";
//        String appSecret = "yyyyyy";
//        String signString = "k1=v1&v1=k1testappId=xxxxxx&nonce=" + nonce + "&timestamp=" + timestamp + "yyyyyy";
//        String sign = DigestUtil.sha256Hex(signString);
//
//        // 准备参数
//        ApiSignature apiSignature = mock(ApiSignature.class);
//        when(apiSignature.appId()).thenReturn("appId");
//        when(apiSignature.timestamp()).thenReturn("timestamp");
//        when(apiSignature.nonce()).thenReturn("nonce");
//        when(apiSignature.sign()).thenReturn("sign");
//        when(apiSignature.timeout()).thenReturn(60);
//        when(apiSignature.timeUnit()).thenReturn(TimeUnit.SECONDS);
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getHeader(eq("appId"))).thenReturn(appId);
//        when(request.getHeader(eq("timestamp"))).thenReturn(String.valueOf(timestamp));
//        when(request.getHeader(eq("nonce"))).thenReturn(nonce);
//        when(request.getHeader(eq("sign"))).thenReturn(sign);
//        when(request.getParameterMap()).thenReturn(MapUtil.<String, String[]>builder()
//                .put("v1", new String[]{"k1"}).put("k1", new String[]{"v1"}).build());
//        when(request.getContentType()).thenReturn("application/json");
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test")));
//        // mock 方法
//        when(signatureRedisDAO.getAppSecret(eq(appId))).thenReturn(appSecret);
//        when(signatureRedisDAO.setNonce(eq(appId), eq(nonce), eq(120), eq(TimeUnit.SECONDS))).thenReturn(true);
//
//        // 调用
//        boolean result = apiSignatureAspect.verifySignature(apiSignature, request);
//        // 断言结果
//        assertTrue(result);
//    }
//
//}