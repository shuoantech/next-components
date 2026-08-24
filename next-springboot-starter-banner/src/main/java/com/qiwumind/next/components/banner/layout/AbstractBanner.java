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

package com.qiwumind.next.components.banner.layout;



import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.taobao.text.util.RenderUtil;

public abstract class AbstractBanner {
    // Resource类
    protected Class<?> resourceClass;

    // Resource位置
    protected String   resourceLocation;

    // 默认旗标文本
    protected String   defaultBanner;

    // 最终旗标文本
    protected String   banner;

    public AbstractBanner(Class<?> resourceClass, String resourceLocation, String defaultBanner) {
        this.resourceClass = resourceClass;
        this.resourceLocation = resourceLocation;
        this.defaultBanner = defaultBanner;
    }

    protected void initialize() {
        InputStream inputStream = null;
        String bannerText = null;
        try {
            if (this.resourceLocation != null) {
                inputStream = this.resourceClass.getResourceAsStream(this.resourceLocation);
                bannerText = IOUtils.toString(inputStream, BannerConstant.ENCODING_UTF_8);
            }
        } catch (Exception e) {

        } finally {
            this.banner = this.generateBanner(bannerText);

            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    public String getBanner() {
        return this.banner;
    }

    // 显示成非ansi模式
    public String getPlainBanner() {
        return RenderUtil.ansiToPlainText(this.banner);
    }

    protected abstract String generateBanner(String bannerText);
}
