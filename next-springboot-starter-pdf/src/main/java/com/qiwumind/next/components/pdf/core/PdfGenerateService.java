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

package com.qiwumind.next.components.pdf.core;

import com.qiwumind.next.components.pdf.autoconfigure.PdfProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * PDF 生成服务，基于 FreeMarker 模板 + Flying Saucer 渲染引擎（由 {@link com.qiwumind.next.components.pdf.autoconfigure.PdfAutoConfiguration} 管理）。
 * <p>
 * 支持三种输出方式：
 * <ul>
 *   <li>{@link #generateToFile(String, String, Map)} — 输出到文件</li>
 *   <li>{@link #generateToStream(String, OutputStream, Map)} — 输出到流</li>
 *   <li>{@link #generateToResponse(String, String, Map, HttpServletResponse)} — 输出到 HTTP 响应</li>
 * </ul>
 *
 * @author qiwumind
 */
@Slf4j
public class PdfGenerateService {

    private final Configuration freemarkerConfiguration;
    private final FontLoader fontLoader;
    private final PdfProperties pdfProperties;

    public PdfGenerateService(Configuration freemarkerConfiguration,
                               FontLoader fontLoader,
                               PdfProperties pdfProperties) {
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.fontLoader = fontLoader;
        this.pdfProperties = pdfProperties;
    }

    /**
     * 生成 PDF 并保存到指定文件路径。
     *
     * @param templateName FreeMarker 模板名（如 "report" 对应 templates/report.ftl）
     * @param filePath     PDF 文件完整路径（如 /data/reports/销售报告.pdf）
     * @param dataModel    模板数据模型
     * @return 生成的 PDF 文件对象
     */
    public File generateToFile(String templateName, String filePath, Map<String, Object> dataModel) throws Exception {
        ensureParentDir(filePath);
        File file = new File(filePath);
        try (OutputStream os = new FileOutputStream(file)) {
            render(templateName, dataModel, os);
            log.info("PDF 已保存至: {}", file.getAbsolutePath());
        }
        // 调试模式：同时保存 HTML
        if (pdfProperties.getExport().isDebugEnabled()) {
            saveDebugHtml(templateName, filePath, dataModel);
        }
        return file;
    }

    /**
     * 生成 PDF 并写入输出流。
     *
     * @param templateName FreeMarker 模板名
     * @param dataModel    模板数据模型
     * @param os           输出流（调用方负责关闭）
     */
    public void generateToStream(String templateName, OutputStream os, Map<String, Object> dataModel) throws Exception {
        render(templateName, dataModel, os);
    }

    /**
     * 生成 PDF 并直接写入 HTTP 响应（用于 Web 下载）。
     *
     * @param templateName FreeMarker 模板名
     * @param fileName     下载文件名（不含 .pdf 后缀）
     * @param dataModel    模板数据模型
     * @param response     HttpServletResponse
     */
    public void generateToResponse(String templateName, String fileName,
                                    Map<String, Object> dataModel,
                                    HttpServletResponse response) throws Exception {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + ".pdf\"; filename*=UTF-8''" + encodedFileName + ".pdf");
        try (OutputStream os = response.getOutputStream()) {
            render(templateName, dataModel, os);
        }
    }

    /**
     * 仅渲染 FreeMarker 模板为 HTML 字符串（用于调试或预览）。
     */
    public String renderHtml(String templateName, Map<String, Object> dataModel) throws IOException, TemplateException {
        Template template = freemarkerConfiguration.getTemplate(templateName);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, dataModel);
    }

    // ==================== 内部方法 ====================

    /**
     * 核心渲染流程：FTL → HTML → PDF
     */
    private void render(String templateName, Map<String, Object> dataModel, OutputStream os) throws Exception {
        // 1. FTL → HTML
        String htmlContent = renderHtml(templateName, dataModel);
        // 2. HTML → PDF
        ITextRenderer renderer = new ITextRenderer();
        fontLoader.registerToRenderer(renderer);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(os);
        renderer.finishPDF();
        log.debug("PDF 生成成功，模板: {}", templateName);
    }

    private static void ensureParentDir(String filePath) {
        Path parentDir = Paths.get(filePath).getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                log.warn("创建父目录失败: {}", parentDir, e);
            }
        }
    }

    private void saveDebugHtml(String templateName, String filePath, Map<String, Object> dataModel) {
        try {
            String html = renderHtml(templateName, dataModel);
            Path htmlPath = Paths.get(filePath.replaceAll("\\.pdf$", ".html"));
            Files.writeString(htmlPath, html, StandardCharsets.UTF_8);
            log.debug("调试 HTML 已保存: {}", htmlPath);
        } catch (Exception e) {
            log.warn("保存调试 HTML 失败", e);
        }
    }
}
