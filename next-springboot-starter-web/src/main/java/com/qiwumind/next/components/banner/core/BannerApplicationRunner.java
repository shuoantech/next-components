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

package com.qiwumind.next.components.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 * @author qiwumind
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "----------------------------------------------------------");

//            // 数据报表
//            if (isNotPresent("com.qiwumind.module.report.framework.security.config.SecurityConfiguration")) {
//                System.out.println("[报表模块 qiwumind-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
//            }
//            // 工作流
//            if (isNotPresent("com.qiwumind.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
//                System.out.println("[工作流模块 qiwumind-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
//            }
//            // 商城系统
//            if (isNotPresent("com.qiwumind.module.trade.framework.web.config.TradeWebConfiguration")) {
//                System.out.println("[商城系统 qiwumind-module-mall - 已禁用][参考 https://doc.iocoder.cn/mall/build/ 开启]");
//            }
//            // ERP 系统
//            if (isNotPresent("com.qiwumind.module.erp.framework.web.config.ErpWebConfiguration")) {
//                System.out.println("[ERP 系统 qiwumind-module-erp - 已禁用][参考 https://doc.iocoder.cn/erp/build/ 开启]");
//            }
//            // WMS 仓库管理系统
//            if (isNotPresent("com.qiwumind.module.wms.framework.web.config.WmsWebConfiguration")) {
//                System.out.println("[WMS 仓库管理系统 qiwumind-module-wms - 已禁用][参考 https://doc.iocoder.cn/wms/build/ 开启]");
//            }
//            // CRM 系统
//            if (isNotPresent("com.qiwumind.module.crm.framework.web.config.CrmWebConfiguration")) {
//                System.out.println("[CRM 系统 qiwumind-module-crm - 已禁用][参考 https://doc.iocoder.cn/crm/build/ 开启]");
//            }
//            // MES 系统
//            if (isNotPresent("com.qiwumind.module.mes.framework.web.config.MesWebConfiguration")) {
//                System.out.println("[MES 系统 qiwumind-module-mes - 已禁用][参考 https://doc.iocoder.cn/mes/build/ 开启]");
//            }
//            // 微信公众号
//            if (isNotPresent("com.qiwumind.module.mp.framework.mp.config.MpConfiguration")) {
//                System.out.println("[微信公众号 qiwumind-module-mp - 已禁用][参考 https://doc.iocoder.cn/mp/build/ 开启]");
//            }
//            // 支付平台
//            if (isNotPresent("com.qiwumind.module.pay.framework.pay.config.PayConfiguration")) {
//                System.out.println("[支付系统 qiwumind-module-pay - 已禁用][参考 https://doc.iocoder.cn/pay/build/ 开启]");
//            }
//            // AI 大模型
//            if (isNotPresent("com.qiwumind.module.ai.framework.web.config.AiWebConfiguration")) {
//                System.out.println("[AI 大模型 qiwumind-module-ai - 已禁用][参考 https://doc.iocoder.cn/ai/build/ 开启]");
//            }
//            // IoT 物联网
//            if (isNotPresent("com.qiwumind.module.iot.framework.web.config.IotWebConfiguration")) {
//                System.out.println("[IoT 物联网 qiwumind-module-iot - 已禁用][参考 https://doc.iocoder.cn/iot/build/ 开启]");
//            }
//            // IM 即时通讯
//            if (isNotPresent("com.qiwumind.module.im.framework.web.config.ImWebConfiguration")) {
//                System.out.println("[IM 即时通讯 qiwumind-module-im - 已禁用][参考 https://doc.iocoder.cn/im/build/ 开启]");
//            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
