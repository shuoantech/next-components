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

package com.qiwumind.next.components.datasecure.utils;



import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.qiwumind.next.components.datasecure.common.DataSecureConstants;
import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;
import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum.RuleEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 类SensitiveProcessUtils.java的实现描述：敏感信息脱敏工具类
 * 
 * @author DataSecureConstants 2017年3月28日 下午5:36:06
 */
@Slf4j
public class SensitiveProcessUtils {

    /**
     * 按敏感信息格式化转换字符串信息
     * 
     * @param sensitiveFormat
     * @param info
     * @return
     */
    public static String shield(SensitiveRulesEnum sensitiveFormat, String info) {
        //默认不显示/不记录
        if (StringUtils.isBlank(info) || sensitiveFormat == null) {
            return info;
        }
        String sensitiveInfo = "N.A.";
        try {
            SensitiveRulesEnum.RuleEnum rule = sensitiveFormat.getRule();
            if (rule.isShow()) {
                switch (rule) {
                    case RULE_EMAIL:
                        int emailIndex = info.indexOf(DataSecureConstants.IDENTIFIER_AT);
                        if (emailIndex == -1) {
                            return info;
                        }
                        sensitiveInfo = sensitiveStr(rule, info.substring(0, emailIndex)) + info.substring(emailIndex);
                        break;
                    case RULE_HASH:
                        sensitiveInfo = DigestUtils.md5Hex(info);
                        break;
                    default:
                        sensitiveInfo = sensitiveStr(rule, info);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("shield catch防异常", e);
        }
        return sensitiveInfo;
    }

    /**
     * 按照脱敏规则处理
     * 
     * @param sensitiveFormat 脱敏规则
     * @param info 待脱敏数据
     * @return
     */
    private static String sensitiveStr(RuleEnum sensitiveFormat, String info) {
        StringBuffer sb = new StringBuffer();
        //如果屏蔽信息不存在或长度小于(BeforeIndex + AfterIndex), 返回屏蔽所有信息
        int sensitiveIndex = sensitiveFormat.getBeforeIndex() + sensitiveFormat.getAfterIndex();
        if (StringUtils.isBlank(info)) {
            return info;
        } else if (info.length() <= sensitiveIndex) {
            sensitiveFormat = RuleEnum.RULE_DESENSITIZED_1_;
            sensitiveIndex = sensitiveFormat.getBeforeIndex() + sensitiveFormat.getAfterIndex();
        }
        sb.append(StringUtils.substring(info, 0, sensitiveFormat.getBeforeIndex()));
        //添加所需要的屏蔽的*号
        for (int i = 0; i < sensitiveFormat.getPaddingStar(); i++) {
            sb.append(DataSecureConstants.IDENTIFIER_STAR);
        }
        sb.append(info.substring(info.length() - sensitiveFormat.getAfterIndex(), info.length()));

        return sb.toString();
    }

    /**
     * JSON字符串脱敏
     * 
     * @param jsonVal 待脱敏字符串
     * @param fields 脱敏字段及规则
     * @return
     */
    public static String jsonShield(String jsonVal, Map<String, SensitiveRulesEnum> fields) {
        try {
            if (MapUtils.isNotEmpty(fields) && StringUtils.isNotBlank(jsonVal)) {
                if (jsonVal.charAt(0) == DataSecureConstants.LEFT_BRACKET) {
                    jsonVal = JSON.toJSONString(JSON.parseArray(jsonVal));
                } else {
                    jsonVal = JSON.toJSONString(JSON.parseObject(jsonVal));
                }
                for (String fieldName : fields.keySet()) {
                    String fieldRegex = MessageFormat.format(DataSecureConstants.REGEX_JSON, fieldName);
                    Matcher matcher = Pattern.compile(fieldRegex).matcher(jsonVal);
                    while (matcher.find()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(DataSecureConstants.DOUBLE_QUOTATION).append(matcher.group(1))
                                .append(DataSecureConstants.MARK_JSON)
                                .append(shield(fields.get(fieldName), matcher.group(2)))
                                .append(DataSecureConstants.DOUBLE_QUOTATION);
                        jsonVal = StringUtils.replace(jsonVal, matcher.group(0), sb.toString());
                        sb = null;
                    }
                }
            }
            return jsonVal;
        } catch (Exception e) {
            log.info("[jsonShield]JSON字符串脱敏异常,注意JSON格式", e);
            return jsonVal;
        }
    }

    /**
     * 纯数据脱敏
     * 
     * @param srcData
     * @param fields
     * @return
     */
    public static String dataShield(String srcData, Map<String, SensitiveRulesEnum> fields) {
        try {
            if (MapUtils.isNotEmpty(fields) && StringUtils.isNotBlank(srcData)) {
                for (String fieldName : fields.keySet()) {
                    Matcher matcher = Pattern.compile(DataSecureConstants.REGEX_EQUAL).matcher(srcData);
                    while (matcher.find()) {
                        if (StringUtils.equals(matcher.group(1).trim(), fieldName)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(matcher.group(1)).append(DataSecureConstants.MARK_EQUAL)
                                    .append(SensitiveProcessUtils.shield(fields.get(fieldName), matcher.group(2)));
                            srcData = StringUtils.replace(srcData, matcher.group(0), sb.toString());
                            sb = null;
                        }
                    }
                    String fieldRegex = MessageFormat.format(DataSecureConstants.REGEX_JSON, fieldName);
                    matcher = Pattern.compile(fieldRegex).matcher(srcData);
                    while (matcher.find()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(DataSecureConstants.DOUBLE_QUOTATION).append(matcher.group(1))
                                .append(DataSecureConstants.MARK_JSON)
                                .append(SensitiveProcessUtils.shield(fields.get(fieldName), matcher.group(2)))
                                .append(DataSecureConstants.DOUBLE_QUOTATION);
                        srcData = StringUtils.replace(srcData, matcher.group(0), sb.toString());
                        sb = null;
                    }
                }
            }
            return srcData;
        } catch (Exception e) {
            return srcData;
        }
    }
}
