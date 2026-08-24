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

import com.itextpdf.text.pdf.BaseFont;
import com.qiwumind.next.components.pdf.autoconfigure.PdfProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 字体加载器，由 {@link com.qiwumind.next.components.pdf.autoconfigure.PdfAutoConfiguration} 管理。
 * <p>
 * 加载优先级：服务器目录字体 → classpath 字体 → 自定义字体配置 → 系统兜底字体
 *
 * @author qiwumind
 */
@Slf4j
public class FontLoader {

    private final PdfProperties pdfProperties;

    public FontLoader(PdfProperties pdfProperties) {
        this.pdfProperties = pdfProperties;
    }

    // 已加载的字体路径列表
    private final List<String> loadedFontPaths = new ArrayList<>();

    // 已加载的 AWT 字体列表（用于 JFreeChart 等）
    private final List<java.awt.Font> loadedAwtFonts = new ArrayList<>();

    // 字体名称映射（中文名 → 英文名）
    private final Map<String, String> fontNameMap = new ConcurrentHashMap<>();

    // 默认字体配置（服务器路径优先，classpath 兜底）
    private static final List<DefaultFontConfig> DEFAULT_FONT_CONFIGS = List.of(
            new DefaultFontConfig("simsun.ttc", "宋体", "SimSun"),
            new DefaultFontConfig("simsun.ttf", "宋体", "SimSun"),
            new DefaultFontConfig("simkai.ttf", "楷体", "KaiTi"),
            new DefaultFontConfig("msyh.ttf", "微软雅黑", "Microsoft YaHei"),
            new DefaultFontConfig("PingFang.ttf", "PingFang SC", "PingFang SC")
    );

    public void init() {
        log.info("========== 开始加载 PDF 字体 ==========");
        // 初始化字体名称映射
        initFontNameMap();
        // 1. 创建并扫描服务器字体目录
        String serverFontDir = pdfProperties.getFonts().getServerDir();
        log.info("服务器字体目录: {}", serverFontDir);
        createAndScanServerFontDir(serverFontDir);
        // 2. 加载自定义字体配置
        loadCustomFonts();
        // 3. 加载默认字体配置（服务器路径 + classpath 兜底）
        int count = loadDefaultFonts(serverFontDir);
        // 4. 加载 AWT 字体
        loadAwtFonts();

        log.info("PDF 字体加载完成，成功加载 {} 个字体", count);
        log.info("已加载字体路径: {}", loadedFontPaths);
        log.info("已加载 AWT 字体: {}",
                loadedAwtFonts.stream().map(java.awt.Font::getName).collect(Collectors.toList()));
        log.info("========== PDF 字体加载结束 ==========");
    }

    // ==================== 字体名映射 ====================

    private void initFontNameMap() {
        fontNameMap.put("宋体", "SimSun");
        fontNameMap.put("楷体", "KaiTi");
        fontNameMap.put("微软雅黑", "Microsoft YaHei");
        fontNameMap.put("PingFang SC", "PingFang SC");
        fontNameMap.put("SimSun", "SimSun");
        fontNameMap.put("SimHei", "SimHei");
        fontNameMap.put("KaiTi", "KaiTi");
        fontNameMap.put("Microsoft YaHei", "Microsoft YaHei");
    }

    // ==================== 服务器字体目录 ====================

    private void createAndScanServerFontDir(String serverFontDir) {
        File dir = new File(serverFontDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("创建字体目录: {}", serverFontDir);
                log.warn("字体目录为空，请将字体文件放入: {}", serverFontDir);
            } else {
                log.warn("创建字体目录失败: {}", serverFontDir);
            }
            return;
        }
        if (!dir.isDirectory()) {
            log.warn("字体路径不是目录: {}", serverFontDir);
            return;
        }

        File[] fontFiles = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".ttf") || lower.endsWith(".ttc")
                    || lower.endsWith(".otf") || lower.endsWith(".woff")
                    || lower.endsWith(".woff2");
        });

        if (fontFiles == null || fontFiles.length == 0) {
            log.warn("服务器字体目录下没有找到字体文件: {}", serverFontDir);
            return;
        }

        log.info("在 {} 找到 {} 个字体文件", serverFontDir, fontFiles.length);
        for (File fontFile : fontFiles) {
            String fontPath = fontFile.getAbsolutePath();
            if (!loadedFontPaths.contains(fontPath)) {
                loadedFontPaths.add(fontPath);
                log.info("发现字体文件: {}", fontPath);
            }
        }
    }

    // ==================== 加载自定义字体 ====================

    private void loadCustomFonts() {
        List<PdfProperties.FontItem> customFonts = pdfProperties.getFonts().getCustom();
        if (customFonts.isEmpty()) {
            return;
        }
        log.info("加载 {} 个自定义字体配置", customFonts.size());
        for (PdfProperties.FontItem item : customFonts) {
            String fontPath = resolveFontPath(item.getPath());
            if (fontPath != null && !loadedFontPaths.contains(fontPath)) {
                loadedFontPaths.add(fontPath);
                log.info("自定义字体已注册: {} → {}", item.getName(), fontPath);
            }
        }
    }

    // ==================== 加载默认字体 ====================

    private int loadDefaultFonts(String serverFontDir) {
        String classpathFontDir = pdfProperties.getFonts().getClasspathDir();
        int count = 0;

        for (DefaultFontConfig config : DEFAULT_FONT_CONFIGS) {
            // 服务器路径优先
            String serverPath = serverFontDir + File.separator + config.fileName;
            String resolved = resolveFontPath(serverPath);
            // 兜底：classpath 字体
            if (resolved == null) {
                String classpathPath = classpathFontDir + "/" + config.fileName;
                resolved = resolveFontPath(classpathPath);
            }
            if (resolved == null) {
                continue;
            }
            if (loadedFontPaths.contains(resolved)) {
                log.debug("字体已加载，跳过: {}", resolved);
                continue;
            }
            loadedFontPaths.add(resolved);
            count++;
            log.info("字体已注册: {} ({}) → {}", config.name, config.family, resolved);
        }
        return count;
    }

    // ==================== AWT 字体 ====================

    private void loadAwtFonts() {
        for (String fontPath : loadedFontPaths) {
            try {
                java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(fontPath));
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
                loadedAwtFonts.add(font);
                log.debug("AWT 字体注册成功: {} → {}", font.getName(), fontPath);
            } catch (Exception e) {
                log.debug("注册 AWT 字体失败: {}", fontPath, e);
            }
        }
    }

    // ==================== 公共 API ====================

    /**
     * 为 ITextRenderer 注册所有已加载字体
     */
    public void registerToRenderer(ITextRenderer renderer) {
        ITextFontResolver fontResolver = renderer.getFontResolver();
        int successCount = 0;

        for (String fontPath : loadedFontPaths) {
            if (registerSingleFont(fontResolver, fontPath)) {
                successCount++;
            }
        }

        if (successCount == 0) {
            log.warn("未加载任何中文字体，PDF 中文可能无法正常显示");
            tryLoadSystemFonts(fontResolver);
        } else {
            log.info("成功为渲染器注册 {} 个中文字体", successCount);
        }
    }

    /**
     * 获取 AWT Font 对象（用于 JFreeChart 等图形组件）
     */
    public java.awt.Font getAwtFont(String fontName, int style, int size) {
        String mappedName = fontNameMap.getOrDefault(fontName, fontName);

        // 从已加载的 AWT 字体中查找
        for (java.awt.Font font : loadedAwtFonts) {
            if (font.getFamily().equals(mappedName) || font.getName().equals(mappedName)
                    || font.getFamily().equals(fontName) || font.getName().equals(fontName)) {
                return font.deriveFont(style, size);
            }
        }

        // 尝试系统字体
        java.awt.Font systemFont = new java.awt.Font(mappedName, style, size);
        if (!systemFont.getFamily().equals("Dialog") && !systemFont.getFamily().equals("SansSerif")) {
            return systemFont;
        }

        // 兜底
        log.warn("未找到字体 '{}'，使用默认字体", fontName);
        return new java.awt.Font("Serif", style, size);
    }

    /**
     * 获取字体文件路径
     */
    public String getFontFilePath(String fontName) {
        String mappedName = fontNameMap.getOrDefault(fontName, fontName);
        // 从已加载路径匹配
        for (String fontPath : loadedFontPaths) {
            String fileName = new File(fontPath).getName().toLowerCase();
            for (String key : fontNameMap.keySet()) {
                if (fileName.contains(key.toLowerCase()) || fileName.contains(mappedName.toLowerCase())) {
                    return fontPath;
                }
            }
        }
        // 从配置中查找兜底
        for (DefaultFontConfig config : DEFAULT_FONT_CONFIGS) {
            if (config.name.equals(fontName) || config.family.equals(fontName)) {
                return resolveFontPath(config.fileName);
            }
        }
        return null;
    }

    public List<String> getLoadedFontPaths() {
        return List.copyOf(loadedFontPaths);
    }

    // ==================== 静态工具方法 ====================

    /**
     * 清理字体临时缓存
     */
    public static void cleanTempCache() {
        String tempDir = System.getProperty("java.io.tmpdir") + "/pdf-fonts/";
        File dir = new File(tempDir);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            dir.delete();
        }
    }

    // ==================== 内部方法 ====================

    private static boolean registerSingleFont(ITextFontResolver resolver, String fontPath) {
        if (fontPath == null || fontPath.isEmpty()) {
            return false;
        }
        try {
            File fontFile = new File(fontPath);
            if (!fontFile.exists() || !fontFile.isFile()) {
                return false;
            }
            resolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            log.debug("字体注册成功: {}", fontPath);
            return true;
        } catch (Exception e) {
            log.warn("字体注册失败: {} - {}", fontPath, e.getMessage());
            return false;
        }
    }

    private static void tryLoadSystemFonts(ITextFontResolver resolver) {
        log.info("尝试加载系统字体作为兜底...");
        String[] paths = {
                "/System/Library/Fonts/PingFang.ttc",
                "/System/Library/Fonts/STHeiti Light.ttc",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "C:/Windows/Fonts/msyh.ttf",
                "C:/Windows/Fonts/simsun.ttc"
        };
        for (String path : paths) {
            if (registerSingleFont(resolver, path)) {
                log.info("系统字体加载成功: {}", path);
                return;
            }
        }
    }

    /**
     * 解析字体路径（支持绝对路径和 classpath 路径）
     */
    private static String resolveFontPath(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return null;
        }

        // 方式1：直接文件路径
        File file = new File(resourcePath);
        if (file.exists() && file.isFile()) {
            return file.getAbsolutePath();
        }

        // 方式2：从 classpath 读取
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                try {
                    return resource.getFile().getAbsolutePath();
                } catch (IOException e) {
                    // Jar 包中运行，提取到临时目录
                    return extractToTemp(resourcePath);
                }
            }
        } catch (Exception ignored) {
            // classpath 不存在
        }
        return null;
    }

    private static String extractToTemp(String resourcePath) {
        String tempDir = System.getProperty("java.io.tmpdir") + "/pdf-fonts/";
        String fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
        File tempFile = new File(tempDir + fileName);

        if (tempFile.exists() && tempFile.length() > 0) {
            return tempFile.getAbsolutePath();
        }

        try (InputStream is = FontLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            }
            File dir = new File(tempDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            log.info("字体已提取到临时目录: {}", tempFile.getAbsolutePath());
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("提取字体失败: {}", resourcePath, e);
            return null;
        }
    }

    // ==================== 内部类型 ====================

    private record DefaultFontConfig(String fileName, String name, String family) {
    }
}
