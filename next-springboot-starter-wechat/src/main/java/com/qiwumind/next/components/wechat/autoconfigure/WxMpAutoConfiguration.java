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

import com.qiwumind.next.components.wechat.core.WechatUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 微信公众号配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {WxMpProperties.class})
public class WxMpAutoConfiguration {
    private WxMpProperties properties;

    public WxMpAutoConfiguration(WxMpProperties wxMpProperties) {
        this.properties = wxMpProperties;
    }


    /**
     * 获取微信公众号服务
     */
    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getAppId());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());

        WxMpService service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(config);
        log.info("微信公众平台服务初始化完成，AppId: {}", properties.getAppId());

        return service;
    }


    /**
     * 获取模板消息服务
     */
    @Bean
    public WxMpTemplateMsgService wxMpTemplateMsgService(WxMpService wxMpService) {
        return wxMpService.getTemplateMsgService();
    }

    /**
     * 配置自定义微信用户服务
     */
    @Bean
    @ConditionalOnMissingBean
    public WechatUserService wechatUserService(WxMpService wxMpService) {
        return new WechatUserService(wxMpService);
    }
}
