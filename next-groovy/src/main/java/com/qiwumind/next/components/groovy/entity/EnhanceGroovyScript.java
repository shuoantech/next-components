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

package com.qiwumind.next.components.groovy.entity;



import com.qiwumind.next.components.context.helper.SpringContextHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import com.google.common.base.Joiner;
import com.qiwumind.next.components.groovy.properties.GroovyMysqlLoaderProperties;

import lombok.Data;

/**
 * groovy脚本表（表名：enhance_groovy_script）
 */
@Data
public class EnhanceGroovyScript {
    /**
     * key分隔符
     */
    private static String KEY_SEPARATOR = null;

    /**
     * 命名空间，用于区分不同的应用
     */
    private String        namespace;
    /**
     * 平台码，用于区分不同的平台
     */
    private String        platformCode;
    /**
     * 产品码
     */
    private String        productCode;
    /**
     * 渠道编码
     */
    private String        channelCode;
    /**
     * 业务编码
     */
    private String        businessCode;

    /**
     * 是否启用（ON代表是，OFF代表否）
     */
    private String        status;
    /**
     * 脚本内容
     */
    private String        scriptContent;
    /**
     * 扩展信息，用于后续扩展
     */
    private String        extendInfo;
    /**
     * 租户编码，用于区分不同的租户
     */
    private String        talent;
    /**
     * 版本号
     */
    private Integer       version;

    public EnhanceGroovyScript() {
    }

    /**
     * 构建唯一key
     */
    public String buildOnlyKey() {
        if (StringUtils.isBlank(KEY_SEPARATOR)) {
            KEY_SEPARATOR = SpringContextHelper.getContext().getBean(GroovyMysqlLoaderProperties.class)
                    .getKeySeparator();
        }

        // 这5个字段值构成了唯一key，可以唯一确定一个groovy脚本
        return Joiner.on(KEY_SEPARATOR).join(this.namespace, this.platformCode, this.productCode, this.channelCode,
                this.businessCode);

    }

    /**
     * 将scriptQuery转换为EnhanceGroovyScript
     */
    public EnhanceGroovyScript queryConverter(@NonNull ScriptQuery scriptQuery) {

        return this.queryConverter(scriptQuery.getUniqueKey());
    }

    /**
     * 将字符串转换为查询条件
     */
    public EnhanceGroovyScript queryConverter(@NonNull String queryStr) {
        if (StringUtils.isBlank(KEY_SEPARATOR)) {
            KEY_SEPARATOR = SpringContextHelper.getContext().getBean(GroovyMysqlLoaderProperties.class)
                    .getKeySeparator();
        }
        // 按下指定分隔符切割
        String[] split = queryStr.split(KEY_SEPARATOR);
        if (split.length != 5) {
            throw new UnsupportedOperationException("uniqueKey length must be 5.");
        }
        // 【命名空间 + 平台编码 + 产品码 + 渠道码 + 业务code】唯一确定一个脚本项
        EnhanceGroovyScript groovyScript = new EnhanceGroovyScript();
        groovyScript.setNamespace(split[0]);
        groovyScript.setPlatformCode(split[1]);
        groovyScript.setProductCode(split[2]);
        groovyScript.setChannelCode(split[3]);
        groovyScript.setBusinessCode(split[4]);

        return groovyScript;
    }

}
