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

package com.qiwumind.next.components.crypto.core.license.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * License 信息实体
 */
@Data
public class LicenseInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 授权编号
     */
    private String licenseId;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 产品版本
     */
    private String productVersion;
    
    /**
     * 授权类型：TRIAL-试用，OFFICIAL-正式
     */
    private String licenseType;
    
    /**
     * 授权开始时间
     */
    private Date startTime;
    
    /**
     * 授权结束时间
     */
    private Date endTime;
    
    /**
     * 硬件指纹（用于绑定机器）
     */
    private String hardwareFingerprint;
    
    /**
     * 最大用户数
     */
    private Integer maxUsers;
    
    /**
     * 扩展字段（JSON 格式）
     */
    private String extra;
    
    /**
     * 签名值
     */
    private String signature;
}