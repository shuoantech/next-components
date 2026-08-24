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

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.qiwumind.next.components.wechat.core.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付自动配置类
 * 支持 API v2 和 v3
 * for Spring Boot 3.5.5
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({WxPayService.class, WechatPayService.class})
@EnableConfigurationProperties(WechatPayProperties.class)
@ConditionalOnProperty(prefix = SystemConstants.Prefix.ThirdParty.WECHAT_PAY, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WechatPayAutoConfiguration {

    private final WechatPayProperties properties;

    public WechatPayAutoConfiguration(WechatPayProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置微信支付基础配置
     */
    @Bean
    @ConditionalOnMissingBean
    public WxPayConfig wxPayConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(properties.getAppId());
        payConfig.setMchId(properties.getMchId());
        payConfig.setMchKey(properties.getMchKey());
        payConfig.setNotifyUrl(properties.getNotifyUrl());
        
        if (properties.getTradeType() != null) {
            payConfig.setTradeType(properties.getTradeType());
        }
        if (properties.getSignType() != null) {
            payConfig.setSignType(properties.getSignType());
        }
        if (properties.getHttpTimeout() != null) {
            payConfig.setHttpConnectionTimeout(properties.getHttpTimeout());
            payConfig.setHttpTimeout(properties.getHttpTimeout());
        }
        
        if (properties.getCertPath() != null && !properties.getCertPath().isEmpty()) {
            payConfig.setKeyPath(properties.getCertPath());
        }
        
        if (properties.getSubMchId() != null) {
            payConfig.setSubMchId(properties.getSubMchId());
        }
        if (properties.getSubAppId() != null) {
            payConfig.setSubAppId(properties.getSubAppId());
        }
        
        payConfig.setUseSandboxEnv(properties.getUseSandboxEnv());
        return payConfig;
    }
   
    /**
     * 配置微信支付服务
     */
    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxPayService(WxPayConfig wxPayConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        log.info("微信支付服务初始化完成, API版本: {}", properties.getApiVersion());
        return wxPayService;
    }

    /**
     * 配置自定义微信支付服务
     */
    @Bean
    @ConditionalOnMissingBean
    public WechatPayService wechatPayService(WxPayService wxPayService, WxPayConfig wxPayConfig) {
        return new WechatPayService(wxPayService, wxPayConfig);
    }
}
