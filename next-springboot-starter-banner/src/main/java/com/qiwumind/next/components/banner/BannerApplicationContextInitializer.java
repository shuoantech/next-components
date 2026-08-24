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

package com.qiwumind.next.components.banner;




import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.qiwumind.next.components.banner.layout.BannerConstant;
import com.qiwumind.next.components.banner.layout.Description;
import com.qiwumind.next.components.banner.layout.DescriptionBanner;
import com.qiwumind.next.components.banner.layout.LogoBanner;
import com.qiwumind.next.components.common.constant.CommonConstants;
import com.taobao.text.Color;

public class BannerApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String LOGGER_CLASSPATH_LOCATION = "META-INF/maven/com.qiwumind/next-components-starter-banner/pom.properties";

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            String nextVersion = CommonConstants.NEXT_VERSION_DEFAULT_VALUE;
            try {
                Resource appResource = this.resourceLoader.getResource(LOGGER_CLASSPATH_LOCATION);
                if (appResource.exists()) {
                    Properties appProperties = new Properties();
                    appProperties.load(new InputStreamReader(appResource.getInputStream()));
                    nextVersion = appProperties.getProperty("version");
                }
            } catch (Exception e) {
            }

            boolean linksShow = true;
            String deployEnv = System.getProperty(CommonConstants.DEPLOY_ENV);
            if (StringUtils.isNotEmpty(deployEnv)) {
                linksShow = StringUtils.equals(CommonConstants.DEPLOY_ENV_LOCAL, deployEnv);
            }
            LogoBanner logoBanner = new LogoBanner(BannerApplicationContextInitializer.class, "/logo.txt",
                    "Welcome to " + CommonConstants.NEXT_NAME, 1, 6,
                    new Color[]{Color.green, Color.red, Color.cyan, Color.blue, Color.yellow}, true);

            this.show(logoBanner, nextVersion, linksShow);
            System.setProperty(BannerConstant.BANNER_SHOWN, "false");
        }
    }

    private void show(LogoBanner logoBanner, String nextVersion, boolean linksShow) {
        String bannerShown = System.getProperty(BannerConstant.BANNER_SHOWN, "true");
        if (!Boolean.valueOf(bannerShown)) {
            return;
        }

        System.out.println("");
        String bannerShownAnsiMode = System.getProperty(BannerConstant.BANNER_SHOWN_ANSI_MODE, "false");
        if (Boolean.valueOf(bannerShownAnsiMode)) {
            System.out.println(logoBanner.getBanner());
        } else {
            System.out.println(logoBanner.getPlainBanner());
        }

        List<Description> descriptions = new ArrayList<>();


        descriptions.add(new Description(CommonConstants.FRAMEWORK + ":",
                CommonConstants.NEXT_NAME + " " + CommonConstants.FRAMEWORK + " (" +
                        CommonConstants.SPRING_BOOT_FRAMEWORK + ":" + CommonConstants.SPRING_BOOT_FRAMEWORK_VERSION + ")",
                0, 1));
        descriptions.add(new Description(CommonConstants.VERSION + ":", nextVersion, 0, 1));

        //only show in   company
        if (linksShow) {
            descriptions.add(new Description(CommonConstants.GITLAB + ":", CommonConstants.GITLAB_VALUE, 0, 1));
            descriptions.add(new Description(CommonConstants.DOCS + ":", CommonConstants.DOCS_VALUE, 0, 1));
        }

        DescriptionBanner descriptionBanner = new DescriptionBanner();
        System.out.println(descriptionBanner.getBanner(descriptions));
    }
}
