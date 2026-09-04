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

package com.qiwumind.next.components.wechat.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号相关配置属性
 *
 * @date 2023-09-07
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(SystemConstants.Prefix.ThirdParty.WECHAT_MP)
public class WxMpProperties {

    /**
     * 公众号开发信息：appId
     */
    private String appId;

    /**
     * 公众号开发信息：appSecret
     */
    private String secret;

    /**
     * 服务器配置：token
     */
    private String token;

    /**
     * 服务器配置：消息加解密密钥EncodingAESKey
     */
    private String aesKey;

    private String domain;
    private TemplateId templateId;
    private Url url;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor
    public static class TemplateId {
        private String paySuccess;
        private String orderFinish;
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor
    public static class Url {
        private String orderDetail;
        private String orderComment;
    }
}
