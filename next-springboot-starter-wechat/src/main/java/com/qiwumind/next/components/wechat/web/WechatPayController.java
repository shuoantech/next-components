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

//package com.qiwumind.next.components.wechat.web;
//
//import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
//import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
//import com.qiwumind.next.components.common.result.Result;
//import com.qiwumind.next.components.wechat.core.WechatPayService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
///**
// * 微信支付
// */
//@Slf4j
//@RestController
//@RequestMapping("/api/wechat/pay")
//@RequiredArgsConstructor
//public class WechatPayController {
//    private final  WechatPayService wechatPayService;
//
//    /**
//     * 微信支付下单请求
//     *
//     * @param ordersDTO
//     * @param request
//     * @return
//     */
//    @PostMapping("/createOrder")
//    public Result<WxJsapiPayParamsDTO> createOrder(@RequestBody OrdersDTO ordersDTO, HttpServletRequest request) {
//
//        return wechatPayService.initiateWxPay(ordersDTO, user.getOpenId(), ip);
//
//    }
//
//    /**
//     * 微信支付下单回调
//     *
//     * @return
//     */
//    @PostMapping("/notify")
//    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        log.info("收到微信支付回调");
//        Result<WxPayOrderNotifyResult> result = wechatPayService.handleNotify(request);
//        if (result.getData() != null && result.getCode().equals("000000")) {
//            WxPayOrderNotifyResult notifyResult = result.getData();
//            wechatPayService.processWxPaySuccess(notifyResult);
//            response.getWriter().write(wechatPayService.successResponse());
//        } else {
//            String errorMsg = "处理失败";
//            if (result.getMsg() != null) {
//                errorMsg = result.getMsg();
//            }
//            response.getWriter().write(wechatPayService.failResponse(errorMsg));
//        }
//    }
//
//    /**
//     * 微信支付订单查询
//     *
//     * @return
//     */
//    @GetMapping("/queryOrder")
//    public Result<WxPayOrderQueryResult> queryOrder(@RequestParam("outTradeNo") String outTradeNo) {
//        return wechatPayService.queryOrder(outTradeNo);
//    }
//    /**
//     * 微信支付订单关闭
//     *
//     * @return
//     */
//    @PostMapping("/closeOrder")
//    public Result<Void> closeOrder(@RequestParam("outTradeNo") String outTradeNo) {
//        return wechatPayService.closeOrder(outTradeNo);
//    }
//
//
//}