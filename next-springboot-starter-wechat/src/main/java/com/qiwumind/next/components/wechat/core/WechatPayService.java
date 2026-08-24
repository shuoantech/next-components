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

import cn.hutool.core.util.IdUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.qiwumind.next.components.common.result.Result;
import com.qiwumind.next.components.wechat.core.dto.*;
import com.qiwumind.next.components.wechat.core.enums.PayType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信支付服务 - 支持多端场景和 API V3
 */
public class WechatPayService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private WxPayService wxPayService;
    private WxPayConfig wxPayConfig;

    public WechatPayService(WxPayService wxPayService, WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
        this.wxPayService = wxPayService;
    }

    /**
     * 统一下单入口 - 自动识别支付场景
     */
    public Result<PayResult> unifiedOrder(PayRequest payRequest) {
        PayType payType = payRequest.getPayType();
        log.info("开始创建支付订单: outTradeNo={}, payType={}, amount={}",
                payRequest.getOutTradeNo(), payType, payRequest.getTotalAmount());

        return switch (payType) {
            case JSAPI -> createJsapiOrder(payRequest);
            case APP -> createAppOrder(payRequest);
            case MWEB -> createMwebOrder(payRequest);
            case NATIVE -> createNativeOrder(payRequest);
            case MINIPROGRAM -> createMiniProgramOrder(payRequest);
            default -> Result.fail("999999", "不支持的支付类型");
        };
    }

    public Result<PayResult> createJsapiOrder(PayRequest payRequest) {
        return createOrder(payRequest, PayType.JSAPI, payRequest.getOpenId(), null);
    }

    public Result<PayResult> createMiniProgramOrder(PayRequest payRequest) {
        return createOrder(payRequest, PayType.MINIPROGRAM, payRequest.getOpenId(), null);
    }

    public Result<PayResult> createAppOrder(PayRequest payRequest) {
        return createOrder(payRequest, PayType.APP, null, null);
    }

    public Result<PayResult> createMwebOrder(PayRequest payRequest) {
        return createOrder(payRequest, PayType.MWEB, null,
                buildH5SceneInfo(payRequest.getWapUrl(), payRequest.getWapName()));
    }

    public Result<PayResult> createNativeOrder(PayRequest payRequest) {
        return createOrder(payRequest, PayType.NATIVE, null, null);
    }

    private Result<PayResult> createOrder(PayRequest payRequest, PayType payType, String openId, String sceneInfo) {
        try {
            WxPayUnifiedOrderRequest request = buildUnifiedOrderRequest(payRequest, payType, openId, sceneInfo);
            WxPayUnifiedOrderResult wxResult = wxPayService.unifiedOrder(request);

            if (!isSuccess(wxResult)) {
                return handleError(wxResult, payRequest.getOutTradeNo());
            }

            PayParams payParams = buildPayParams(payType, wxResult.getPrepayId());
            log.info("{}支付下单成功: outTradeNo={}, prepayId={}",
                    payType.getDescription(), payRequest.getOutTradeNo(), wxResult.getPrepayId());

            return Result.success(PayResult.builder()
                    .payType(payType)
                    .prepayId(wxResult.getPrepayId())
                    .payParams(payParams)
                    .mwebUrl(wxResult.getMwebUrl())
                    .codeUrl(wxResult.getCodeURL())
                    .build());

        } catch (WxPayException e) {
            log.error("{}支付下单异常: outTradeNo={}, error={}",
                    payType.getDescription(), payRequest.getOutTradeNo(), e.getMessage(), e);
            return Result.fail("999999", "支付服务异常: " + e.getMessage());
        }
    }

    private WxPayUnifiedOrderRequest buildUnifiedOrderRequest(
            PayRequest payRequest, PayType payType, String openId, String sceneInfo) {

        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();

        request.setOutTradeNo(payRequest.getOutTradeNo());
        request.setTotalFee(convertToFen(payRequest.getTotalAmount()));
        request.setBody(payRequest.getBody());
        request.setDetail(payRequest.getDetail());
        request.setAttach(payRequest.getAttach());
        request.setSpbillCreateIp(payRequest.getSpbillCreateIp());
        request.setNotifyUrl(getNotifyUrl(payRequest));
        request.setTimeStart(payRequest.getTimeStart());
        request.setTimeExpire(payRequest.getTimeExpire());
        request.setGoodsTag(payRequest.getGoodsTag());
        request.setTradeType(payType.getTradeType());

        if (openId != null) {
            request.setOpenid(openId);
        }
        if (sceneInfo != null) {
            request.setSceneInfo(sceneInfo);
        }

        return request;
    }

    private PayParams buildPayParams(PayType payType, String prepayId) {
        return switch (payType) {
            case JSAPI -> buildJsapiPayParams(prepayId);
            case MINIPROGRAM -> buildMiniProgramPayParams(prepayId);
            case APP -> buildAppPayParams(prepayId);
            case MWEB, NATIVE -> buildEmptyPayParams(prepayId);
        };
    }

    private PayParams buildJsapiPayParams(String prepayId) {
        String timeStamp = generateTimestamp();
        String nonceStr = generateNonceStr();
        String packageStr = "prepay_id=" + prepayId;
        String signType = "MD5";

        WxJsapiPayParams jsapiPayParams = WxJsapiPayParams.builder()
                .appId(wxPayConfig.getAppId())
                .timeStamp(timeStamp)
                .nonceStr(nonceStr)
                .packageStr(packageStr)
                .signType(signType)
                .build();

        String paySign = generatePaySign(jsapiPayParams);
        jsapiPayParams.setPaySign(paySign);

        PayParams params = PayParams.builder()
                .jsapiPayParams(jsapiPayParams)
                .build();

        log.debug("JSAPI支付参数: prepayId={}, timeStamp={}, nonceStr={}", prepayId, timeStamp, nonceStr);
        return params;
    }

    private PayParams buildMiniProgramPayParams(String prepayId) {
        return buildJsapiPayParams(prepayId);
    }

    /**
     * 构建APP支付参数
     * APP支付签名参数（按字典序排序）：appid, partnerid, prepayid, package, noncestr, timestamp
     */
    private PayParams buildAppPayParams(String prepayId) {
        String timestamp = generateTimestamp();
        String nonceStr = generateNonceStr();

        WxAppPayParams appPayParams = WxAppPayParams.builder()
                .partnerId(wxPayConfig.getMchId())
                .prepayId(prepayId)
                .timestamp(timestamp)
                .build();

        // ===== 修复：使用 TreeMap 确保参数按字典序排序 =====
        Map<String, String> signMap = new TreeMap<>();
        signMap.put("appid", wxPayConfig.getAppId());
        signMap.put("partnerid", wxPayConfig.getMchId());
        signMap.put("prepayid", prepayId);
        signMap.put("package", "Sign=WXPay");
        signMap.put("noncestr", nonceStr);
        signMap.put("timestamp", timestamp);

        String sign = SignUtils.createSign(signMap, wxPayConfig.getMchKey());
        appPayParams.setSign(sign);

        PayParams params = PayParams.builder()
                .appPayParams(appPayParams)
                .build();

        log.debug("APP支付参数: prepayId={}, timestamp={}", prepayId, timestamp);
        return params;
    }

    private PayParams buildEmptyPayParams(String prepayId) {
        return PayParams.builder()
                .prepayId(prepayId)
                .build();
    }

    /**
     * 生成JSAPI签名
     * 微信支付签名要求：参数按字典序排序（ASCII码升序）
     * 使用 TreeMap 自动按 key 排序
     */
    private String generatePaySign(WxJsapiPayParams params) {
        Map<String, String> signMap = new TreeMap<>();
        signMap.put("appId", params.getAppId());
        signMap.put("nonceStr", params.getNonceStr());
        signMap.put("package", params.getPackageStr());
        signMap.put("signType", params.getSignType());
        signMap.put("timeStamp", params.getTimeStamp());
        return SignUtils.createSign(signMap, wxPayConfig.getMchKey());
    }

    private String getNotifyUrl(PayRequest payRequest) {
        return payRequest.getNotifyUrl() != null ? payRequest.getNotifyUrl() : wxPayConfig.getNotifyUrl();
    }

    private String buildH5SceneInfo(String wapUrl, String wapName) {
        String safeUrl = wapUrl != null ? wapUrl : "";
        String safeName = wapName != null ? wapName : "支付";
        return String.format("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\":\"%s\",\"wap_name\":\"%s\"}}", safeUrl, safeName);
    }

    private int convertToFen(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).intValue();
    }

    private String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    private String generateNonceStr() {
        return IdUtil.fastSimpleUUID().substring(0, 32);
    }

    private boolean isSuccess(WxPayUnifiedOrderResult result) {
        return "SUCCESS".equals(result.getReturnCode()) && "SUCCESS".equals(result.getResultCode());
    }

    private Result<PayResult> handleError(WxPayUnifiedOrderResult result, String outTradeNo) {
        log.error("微信支付统一下单失败: outTradeNo={}, returnMsg={}, errCode={}, errDes={}",
                outTradeNo, result.getReturnMsg(), result.getErrCode(), result.getErrCodeDes());

        String errMsg = result.getErrCodeDes();
        if (errMsg == null) {
            errMsg = "下单失败：" + result.getReturnMsg();
        }
        return Result.fail("999999", errMsg);
    }

    public Result<WxPayOrderNotifyResult> handleNotify(HttpServletRequest request) {
        String xmlContent = extractXmlContent(request);
        log.info("微信支付回调通知: outTradeNo={}", extractOutTradeNoFromXml(xmlContent));

        try {
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlContent);

            if (!"SUCCESS".equals(notifyResult.getReturnCode())) {
                log.warn("微信支付回调返回码失败: outTradeNo={}, returnMsg={}",
                        notifyResult.getOutTradeNo(), notifyResult.getReturnMsg());
                return Result.fail("FAIL", notifyResult.getReturnMsg());
            }

            if (!verifySign(notifyResult)) {
                log.warn("微信支付回调签名验证失败: outTradeNo={}", notifyResult.getOutTradeNo());
                return Result.fail("SIGN_ERROR", "签名验证失败");
            }

            if (!"SUCCESS".equals(notifyResult.getResultCode())) {
                log.warn("微信支付业务结果失败: outTradeNo={}, errCode={}, errMsg={}",
                        notifyResult.getOutTradeNo(), notifyResult.getErrCode(), notifyResult.getErrCodeDes());
                return Result.fail(notifyResult.getErrCode(), notifyResult.getErrCodeDes());
            }

            log.info("微信支付回调验证成功: outTradeNo={}, transactionId={}, openId={}",
                    notifyResult.getOutTradeNo(), notifyResult.getTransactionId(), notifyResult.getOpenid());

            return Result.success(notifyResult);

        } catch (Exception e) {
            log.error("微信支付回调处理异常", e);
            return Result.fail("999999", "回调处理异常: " + e.getMessage());
        }
    }

    private String extractXmlContent(HttpServletRequest request) {
        StringBuilder xmlData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlData.append(line);
            }
        } catch (IOException e) {
            log.error("读取微信支付回调请求体异常", e);
        }
        return xmlData.toString();
    }

    private String extractOutTradeNoFromXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            return "unknown";
        }
        int start = xml.indexOf("<out_trade_no>");
        int end = xml.indexOf("</out_trade_no>");
        if (start != -1 && end != -1) {
            return xml.substring(start + 14, end);
        }
        return "unknown";
    }

    /**
     * 验证回调签名
     * 使用 TreeMap 确保参数按字典序排序
     */
    private boolean verifySign(WxPayOrderNotifyResult notifyResult) {
        try {
            // 使用 TreeMap 确保参数按字典序排序
            Map<String, String> params = new TreeMap<>();
            params.put("appid", notifyResult.getAppid());
            params.put("bank_type", notifyResult.getBankType());
            params.put("cash_fee", String.valueOf(notifyResult.getCashFee()));
            params.put("fee_type", notifyResult.getFeeType());
            params.put("is_subscribe", notifyResult.getIsSubscribe());
            params.put("mch_id", notifyResult.getMchId());
            params.put("nonce_str", notifyResult.getNonceStr());
            params.put("openid", notifyResult.getOpenid());
            params.put("out_trade_no", notifyResult.getOutTradeNo());
            params.put("result_code", notifyResult.getResultCode());
            params.put("return_code", notifyResult.getReturnCode());
            params.put("time_end", notifyResult.getTimeEnd());
            params.put("total_fee", String.valueOf(notifyResult.getTotalFee()));
            params.put("trade_type", notifyResult.getTradeType());
            params.put("transaction_id", notifyResult.getTransactionId());

            if (notifyResult.getCouponFee() != null && notifyResult.getCouponFee().intValue() != 0) {
                params.put("coupon_fee", String.valueOf(notifyResult.getCouponFee()));
            }

            String sign = SignUtils.createSign(params, wxPayConfig.getMchKey());
            return sign.equalsIgnoreCase(notifyResult.getSign());
        } catch (Exception e) {
            log.error("签名验证异常", e);
            return false;
        }
    }

    public String successResponse() {
        return WxPayNotifyResponse.success("OK");
    }

    public String failResponse(String message) {
        return WxPayNotifyResponse.fail(message);
    }

    public Result<WxPayOrderQueryResult> queryOrder(String outTradeNo) {
        try {
            WxPayOrderQueryResult result = wxPayService.queryOrder(null, outTradeNo);
            log.info("微信支付订单查询: outTradeNo={}, tradeState={}", outTradeNo, result.getTradeState());

            if ("SUCCESS".equals(result.getReturnCode()) && "SUCCESS".equals(result.getResultCode())) {
                return Result.success(result);
            } else {
                return Result.fail(result.getErrCode(), result.getErrCodeDes());
            }
        } catch (WxPayException e) {
            log.error("微信支付订单查询异常: outTradeNo={}", outTradeNo, e);
            return Result.fail("999999", "查询异常: " + e.getMessage());
        }
    }

    public Result<Void> closeOrder(String outTradeNo) {
        try {
            wxPayService.closeOrder(outTradeNo);
            log.info("微信支付订单关闭成功: outTradeNo={}", outTradeNo);
            return Result.success();
        } catch (WxPayException e) {
            log.error("微信支付订单关闭异常: outTradeNo={}", outTradeNo, e);
            return Result.fail("999999", e.getErrCodeDes());
        }
    }

    // ==================== V3 API 功能：退款 ====================

    public Result<WxPayRefundV3Result> createRefund(RefundRequest refundRequest) {
        try {
            WxPayRefundV3Request request = new WxPayRefundV3Request();
            request.setOutTradeNo(refundRequest.getOutTradeNo());
            request.setOutRefundNo(refundRequest.getOutRefundNo());
            request.setReason(refundRequest.getReason());

            WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
            amount.setRefund(convertToFen(refundRequest.getRefundAmount()));
            amount.setTotal(convertToFen(refundRequest.getTotalAmount()));
            request.setAmount(amount);

            if (refundRequest.getNotifyUrl() != null) {
                request.setNotifyUrl(refundRequest.getNotifyUrl());
            }

            WxPayRefundV3Result result = wxPayService.refundV3(request);
            log.info("微信支付退款成功: outTradeNo={}, outRefundNo={}",
                    refundRequest.getOutTradeNo(), refundRequest.getOutRefundNo());
            return Result.success(result);

        } catch (WxPayException e) {
            log.error("微信支付退款异常: outTradeNo={}, error={}",
                    refundRequest.getOutTradeNo(), e.getMessage(), e);
            return Result.fail("999999", "退款失败: " + e.getMessage());
        }
    }

    /**
     * 子类集成实现即可
     * @param notifyResult
     */
    public void processWxPaySuccess(WxPayOrderNotifyResult notifyResult){

    }

}