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

package com.qiwumind.next.components.wechat.core.dto;

import com.qiwumind.next.components.wechat.core.enums.PayType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付请求参数
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PayRequest {
    
    // 必填参数
    private String outTradeNo;          // 商户订单号
    private BigDecimal totalAmount;     // 订单金额（元）
    private String body;                // 商品描述
    private String spbillCreateIp;      // 用户IP
    
    // 支付类型
    private PayType payType;  // 支付场景类型
    
    // JSAPI/小程序支付必填
    private String openId;              // 用户openId（公众号/小程序支付必填）
    
    // H5支付必填
    private String wapUrl;              // WAP网站URL
    private String wapName;             // WAP网站名
    
    // 可选参数
    private String detail;              // 商品详情
    private String attach;              // 附加数据
    private String notifyUrl;           // 回调地址（不传则使用默认）
    private String timeStart;           // 交易起始时间
    private String timeExpire;          // 交易结束时间
    private String goodsTag;            // 订单优惠标记


}