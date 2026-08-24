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

package com.qiwumind.next.components.compute.autoconfigure;


import com.qiwumind.next.components.compute.core.BusinessDateGenService;
import com.qiwumind.next.components.compute.core.plugin.*;
import com.qiwumind.next.components.common.constant.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = SystemConstants.Prefix.COMPUTE, name = "enable", havingValue = "true")
public class ComputeAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(FixedRepayDayService.class)
    public FixedRepayDayService fixedRepayDayService() {
        FixedRepayDayService fixedRepayDayService = new FixedRepayDayService();
        return fixedRepayDayService;
    }

    /**
     *
     */
    @Bean
    @ConditionalOnMissingBean(DEBXDailyService.class)
    public DEBXDailyService debxDailyService() {
        BusinessDateGenService businessDateGenService = new BusinessDateGenService();
        DEBXDailyService debxDailyService = new DEBXDailyService(businessDateGenService);
        return debxDailyService;
    }

    /**
     *
     */
    @Bean
    @ConditionalOnMissingBean(DEBXService.class)
    public DEBXService debxService() {
        DEBXService debxService = new DEBXService();
        return debxService;
    }

    @Bean
    @ConditionalOnMissingBean(AprRateComputeService.class)
    public AprRateComputeService aprRateComputeService() {
        log.info("******load AprRateComputeService**********");
        return new AprRateComputeService();
    }

    @Bean("irrRateComputeService")
    @ConditionalOnMissingBean(IrrRateComputeService.class)
    public IrrRateComputeService irrRateComputeService() {
        log.info("******load IrrRateComputeService**********");
        return new IrrRateComputeService();
    }
}
