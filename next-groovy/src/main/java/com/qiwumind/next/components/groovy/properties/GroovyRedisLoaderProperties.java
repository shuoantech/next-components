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

package com.qiwumind.next.components.groovy.properties;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.qiwumind.next.components.common.constant.SystemConstants;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * GroovyRedisLoaderProperties
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ConfigurationProperties(prefix = SystemConstants.Prefix.Groovy.REDIS_LOADER)
public class GroovyRedisLoaderProperties implements InitializingBean, EnvironmentAware {

    public static final String PREFIX = SystemConstants.Prefix.Groovy.REDIS_LOADER;

    /**
     * 环境信息
     */
    private Environment        environment;

    /**
     * 脚本组，以 namespace 来区分不同的应用，同时在Redis里也能够按服务来区分脚本方便查看管理(该值一般和应用名称保持一致即可)
     */
    private String             namespace;

    /**
     * 开启基于 Redis 的脚本加载器
     */
    private boolean            enable = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 如果没有配置namespace，则默认和应用名保持一致
        if (StringUtils.isBlank(this.namespace)) {
            this.namespace = this.environment.getProperty("spring.application.name");
        }
        // 强校验
        if (StringUtils.isBlank(this.namespace)) {
            throw new UnsupportedOperationException(SystemConstants.Prefix.Groovy.REDIS_LOADER + ".namespace can not be null.");
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
