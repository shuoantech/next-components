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

package com.qiwumind.next.components.wechat.web;

import com.qiwumind.next.components.wechat.autoconfigure.WxMpProperties;
import com.qiwumind.next.components.wechat.core.WechatUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 微信认证接口,如果前端进行了该2步操作，后端可省
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wechat/auth")
public class WechatAuthController {
    private final WxMpService wxMpService;
    private final WechatUserService wechatUserService;
    private final WxMpProperties wxMpProperties;

    /**
     * 第一步：发起静默授权请求（服务号菜单直接指向此接口）
     * 用户点击菜单后，会跳转到这个地址，然后重定向到微信授权页面
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param state    可选参数，用于传递自定义参数（如跳转目标页面）
     */
    @GetMapping("/authorize")
    public void authorize(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "state", required = false) String state) throws IOException {
        String redirectUri = wxMpProperties.getDomain() + "/api/wechat/auth/callback";
        log.info("【发起静默授权】回调地址: {}", redirectUri);

        String url = wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(redirectUri, WxConsts.OAuth2Scope.SNSAPI_BASE, state);
        log.info("【微信静默授权】重定向URL: {}", url);

        response.sendRedirect(url);
    }

    /**
     * 第二步：微信回调接口（微信服务器会自动调用）
     * 获取code后换取openId，然后重定向回前端页面，带上token参数
     *
     * @param code     微信返回的授权码
     * @param state    自定义参数（用于跳转目标页面）
     * @param response HttpServletResponse
     */
    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletResponse response) throws IOException {
        try {
            log.info("【微信回调】收到code: {}, state: {}", code, state);

            WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service()
                    .getAccessToken(code);
            String openId = accessToken.getOpenId();
            log.info("【获取OpenId成功】openId: {}", openId);
            wechatUserService.handleSubscribeEvent(wxMpService,openId);
            String token = wechatUserService.token(openId);
            log.info("【微信静默登录成功】token: {}", token);
            String redirectUrl = wxMpProperties.getDomain();
            if (state != null && !state.isEmpty()) {
                redirectUrl += "?redirect=" + URLEncoder.encode(state, "UTF-8");
            }
            redirectUrl += "#token=" + token + "&openId=" + openId;
            log.info("【重定向到前端】URL: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("【获取OpenId失败】", e);
            String errorUrl = wxMpProperties.getDomain() + "?error=auth_failed";
            response.sendRedirect(errorUrl);
        }
    }


}