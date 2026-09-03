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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 生成器配置属性（前缀 pdf）
 *
 * @author qiwumind
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = SystemConstants.Prefix.PDF)
public class PdfProperties {

    /**
     * 字体配置
     */
    private Fonts fonts = new Fonts();
    /**
     * 导出配置
     */
    private Export export = new Export();

    // ==================== 内部配置类 ====================
    /**
     * 字体相关配置
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Fonts {

        /**
         * 服务器字体目录，用于加载系统中文字体
         */
        private String serverDir = "/opt/server/fonts";

        /**
         * classpath 字体资源目录
         */
        private String classpathDir = "classpath:/fonts/";

        /**
         * 自定义字体配置列表（服务器路径优先，classpath 兜底）
         */
        private List<FontItem> custom = new ArrayList<>();
    }

    /**
     * 单个字体配置项
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class FontItem {

        /**
         * 字体文件路径（绝对路径或 classpath 相对路径）
         */
        @NotEmpty
        private String path;

        /**
         * 字体中文名称（如：宋体、楷体）
         */
        private String name;

        /**
         * 字体英文名称/家族名（如：SimSun、KaiTi）
         */
        private String family;
    }

    /**
     * PDF 导出相关配置
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Export {

        /**
         * 是否启用调试模式（保留中间 HTML 文件）
         */
        private boolean debugEnabled = false;

        /**
         * 调试输出目录
         */
        private String debugDir = System.getProperty("java.io.tmpdir");

        /**
         * PDF 默认页面大小（如 A4、A3）
         */
        private String pageSize = "A4";

        /**
         * PDF 页面 DPI，影响渲染精度
         */
        private int dpi = 96;
    }
}
