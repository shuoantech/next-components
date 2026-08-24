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

package com.qiwumind.next.components.wechat.core;


import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WechatUserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WxMpService wxMpService;
    public WechatUserService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
    }
    /**
     * 处理用户是否关注事件
     *
     * @param openId
     */
    public WxMpUser handleSubscribeEvent(WxMpService wxMpService, String openId) throws WxErrorException {
        return wxMpService.getUserService().userInfo(openId);
    }

    /**
     * 处理用户是否关注事件；子类集成实现即可
     *
     * @param openId
     * @param flag   true 关注  false 取消关注
     */
    public void handleSubscribeStatus(String openId, Boolean flag) {
    }

    /**
     * 子类集成实现即可
     * @param openId
     */
    public String token(String openId) {
        return null;
    }


    /**
     * 检查用户是否仍关注公众号
     */
    public boolean isUserSubscribed(String openId) {
        try {
            me.chanjar.weixin.mp.bean.result.WxMpUser wxUser = wxMpService.getUserService().userInfo(openId);
            return wxUser != null && wxUser.getSubscribe();
        } catch (Exception e) {
            logger.error("查询用户关注状态失败，openId={}", openId);
            return false;
        }
    }
}
