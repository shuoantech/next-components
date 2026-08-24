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

import com.qiwumind.next.components.wechat.core.WechatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.web.bind.annotation.*;

/**
 * 微信鉴权关注
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wx")
public class WechatPortalController {
    private final WxMpService wxMpService;
    private final WechatUserService wechatUserService;

    /**
     * 服务器URL验证接口（GET请求）
     * 微信服务器配置时调用，验证服务器有效性
     */
    @GetMapping("/portal")
    public String checkSignature(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.info("URL验证成功 echostr={}", echostr);
            return echostr;
        }
        log.warn("URL验证失败，signature不匹配");
        return "非法请求";
    }

    /**
     * 接收用户消息/事件（POST请求）
     */
    @PostMapping("/portal")
    public String handleMessage(
            @RequestBody String requestBody,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam(value = "encrypt_type", required = false) String encType,
            @RequestParam(value = "msg_signature", required = false) String msgSignature) {

        log.info("收到微信消息：{}", requestBody);
        // 1. 校验签名
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.error("消息签名校验失败");
            return "非法请求";
        }
        // 2. 解密消息（安全模式）
        WxMpXmlMessage inMessage;
        if ("aes".equalsIgnoreCase(encType)) {
            // 安全模式需要解密
            inMessage = WxMpXmlMessage.fromEncryptedXml(
                    requestBody, wxMpService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature
            );
        } else {
            // 明文模式
            inMessage = WxMpXmlMessage.fromXml(requestBody);
        }
        log.info("解析后的消息：msgType={}, event={}, openId={}, eventKey={}",
                inMessage.getMsgType(), inMessage.getEvent(),
                inMessage.getFromUser(), inMessage.getEventKey());

        // 3. 处理事件
        WxMpXmlOutMessage outMessage = null;
        if ("event".equals(inMessage.getMsgType())) {
            outMessage = handleEvent(inMessage);
        } else if ("text".equals(inMessage.getMsgType())) {
            outMessage = handleTextMessage(inMessage);
        }
        // 4. 返回响应消息（安全模式需加密）
        if (outMessage == null) {
            return "";
        }
        if ("aes".equalsIgnoreCase(encType)) {
            return outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }
        return outMessage.toXml();
    }

    /**
     * 处理微信事件（关注、扫码、取消关注等）
     */
    private WxMpXmlOutMessage handleEvent(WxMpXmlMessage inMessage) {
        try {
            String eventType = inMessage.getEvent();
            String openId = inMessage.getFromUser();
            switch (eventType) {
                case "subscribe":  // 用户首次关注
                    wechatUserService.handleSubscribeEvent(wxMpService, openId);
                    return handleSubscribeEvent(openId, inMessage);
                case "SCAN":  // 已关注用户扫描带参二维码
                    return handleScanEvent(openId, inMessage);
                case "unsubscribe":  // 用户取消关注
                    wechatUserService.handleSubscribeStatus(openId, false);
                    return null;
                default:
                    log.info("未处理的事件类型：{}", eventType);
                    return null;
            }
        } catch (Exception e) {
            log.error("【获取OpenId失败】", e);
            return null;
        }
    }

    /**
     * 处理关注事件 - 关注即自动订阅订单通知
     */
    private WxMpXmlOutMessage handleSubscribeEvent(String openId, WxMpXmlMessage inMessage) {
        String replyContent = "欢迎关注XXX";
        return WxMpXmlOutMessage.TEXT()
                .content(replyContent)
                .fromUser(inMessage.getToUser())
                .toUser(openId)
                .build();
    }

    /**
     * 处理已关注用户的扫码事件
     */
    private WxMpXmlOutMessage handleScanEvent(String openId, WxMpXmlMessage inMessage) {
//        log.info("已关注用户扫码，openId={}, eventKey={}", openId, eventKey);
        try {     // 解析业务参数，更新绑定关系
            wechatUserService.handleSubscribeEvent(wxMpService, openId);
        } catch (Exception e) {
            log.error("【获取OpenId失败】", e);
            return null;
        }
        return WxMpXmlOutMessage.TEXT()
                .content("扫码成功，账号已绑定")
                .fromUser(inMessage.getToUser())
                .toUser(openId)
                .build();
    }


    /**
     * 解析eventKey中的场景值
     * 首次关注格式：qrscene_123
     * 已关注扫码格式：123
     */
    private String parseSceneFromEventKey(String eventKey) {
        if (eventKey == null) return null;
        if (eventKey.startsWith("qrscene_")) {
            return eventKey.substring(8);
        }
        return eventKey;
    }

    private WxMpXmlOutMessage handleTextMessage(WxMpXmlMessage inMessage) {
        // 处理用户发送的文本消息（可选）
        return WxMpXmlOutMessage.TEXT()
                .content("感谢您的消息，我们会尽快回复！")
                .fromUser(inMessage.getToUser())
                .toUser(inMessage.getFromUser())
                .build();
    }


}