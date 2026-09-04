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

/**
 * 微信支付配置属性
 * 支持 API v2 和 v3
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ConfigurationProperties(prefix = SystemConstants.Prefix.ThirdParty.WECHAT_PAY)
public class WechatPayProperties {

    /**
     * 是否启用微信支付
     */
    private Boolean enabled = true;

    /**
     * API 版本: V2 或 V3
     */
    private String apiVersion = "V3";

    /**
     * 公众号/小程序/APP的AppId
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥（V2 API使用）
     */
    private String mchKey;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 交易类型，默认JSAPI
     */
    private String tradeType = "JSAPI";

    /**
     * 签名类型，默认MD5（V2）/ SHA256（V3）
     */
    private String signType = "MD5";

    /**
     * HTTP请求超时时间（毫秒）
     */
    private Integer httpTimeout = 5000;

    // ==================== V3 API 配置 ====================

    /**
     * 商户证书路径（V3 API使用，退款/分账等操作必需）
     */
    private String certPath;

    /**
     * 商户证书序列号（V3 API使用）
     */
    private String certSerialNo;

    /**
     * APIv3密钥（V3 API使用，回调解密必需）
     */
    private String apiV3Key;

    /**
     * 私钥路径（V3 API使用）
     */
    private String privateKeyPath;

    // ==================== 可选配置 ====================

    /**
     * 子商户号（服务商模式）
     */
    private String subMchId;

    /**
     * 子商户AppId（服务商模式）
     */
    private String subAppId;

    /**
     * 是否沙箱模式
     */
    private Boolean useSandboxEnv = false;
}
