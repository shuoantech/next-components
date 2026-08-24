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

package com.qiwumind.next.components.pdf.autoconfigure;

import com.qiwumind.next.components.pdf.core.FontLoader;
import com.qiwumind.next.components.pdf.core.PdfGenerateService;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * PDF 生成器自动配置。
 * <p>
 * 通过 {@code next.pdf.enabled=true}（默认启用）来控制是否激活 PDF 生成能力。
 * 需要 FreeMarker 和 Flying Saucer（iText5）在 classpath 中。
 *
 * @author qiwumind
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(PdfProperties.class)
@ConditionalOnClass({Configuration.class, org.xhtmlrenderer.pdf.ITextRenderer.class})
@ConditionalOnProperty(prefix = SystemConstants.Prefix.PDF, name = "enabled", havingValue = "true", matchIfMissing = true)
public class PdfAutoConfiguration {

    private final PdfProperties pdfProperties;

    public PdfAutoConfiguration(PdfProperties pdfProperties) {
        this.pdfProperties = pdfProperties;
    }

    /**
     * 字体加载器 Bean，initMethod 确保容器启动时完成字体扫描。
     */
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public FontLoader fontLoader() {
        log.info("PDF 字体加载器已注册");
        return new FontLoader(pdfProperties);
    }

    /**
     * PDF 生成服务 Bean，依赖 Spring Boot 自动配置的 FreeMarker {@link Configuration}。
     */
    @Bean
    @ConditionalOnMissingBean
    public PdfGenerateService pdfGenerateService(Configuration freemarkerConfiguration,
                                                  FontLoader fontLoader) {
        log.info("PDF 生成服务已注册");
        return new PdfGenerateService(freemarkerConfiguration, fontLoader, pdfProperties);
    }
}
