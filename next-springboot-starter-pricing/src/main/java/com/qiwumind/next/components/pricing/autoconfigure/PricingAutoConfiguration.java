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

package com.qiwumind.next.components.pricing.autoconfigure;

import com.qiwumind.next.components.pricing.core.engine.ComputeService;
import com.qiwumind.next.components.pricing.core.meta.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 定价引擎的 Spring Boot 自动配置。
 * <p>
 * 自动注册 Aviator 函数并将 {@link ComputeService} 暴露为 Spring 管理的单例 Bean。
 *
 * <h3>使用方式：</h3>
 * 将 {@code next-springboot-starter-pricing} 添加到 classpath 后，
 * 在需要的地方注入 {@code ComputeService}：
 * <pre>{@code
 * @Autowired
 * private ComputeService computeService;
 *
 * public void doPricing() {
 *     ComputeRespBO result = computeService.compute(priceBO, activityList);
 * }
 * }</pre>
 *
 * <h3>禁用自动配置：</h3>
 * <pre>{@code
 * qiwumind.pricing.enabled = false
 * }</pre>
 */
@AutoConfiguration
@EnableConfigurationProperties(PricingProperties.class)
@ConditionalOnProperty(prefix = PricingProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class PricingAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PricingAutoConfiguration.class);

    /**
     * 在应用启动时预注册所有 Aviator 自定义函数。
     * 通过 Bean 初始化提前完成注册，而非懒加载类的方式。
     */
    public PricingAutoConfiguration() {
        FunctionRegistry.registerAll();
        log.info("Pricing engine initialized - Aviator functions registered");
    }

    /**
     * 将 ComputeService 暴露为 Spring 管理的单例 Bean。
     * <p>
     * 该服务是无状态的且线程安全的，因此单个实例适用于所有并发定价请求。
     *
     * @return 单例 ComputeService 实例
     */
    @Bean
    @ConditionalOnMissingBean(ComputeService.class)
    public ComputeService computeService() {
        return new ComputeService();
    }
}
