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

package com.qiwumind.next.components.oss;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * oss 连接
 * 
 * @author liks 2019年9月19日 下午5:28:52
 */
public class OssStsSpringProviderBean {
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    private final OssStsConfig ossConfigVO = new OssStsConfig();

    /**
     * 默认有效期半小时
     * 
     * @param objectname object文件名称
     * @param roleArn 需要授权的角色名称
     * @param policyEnum
     * @return
     */
    public OssStsConfig getSts(final String objectname, final String roleArn, final PolicyEnum policyEnum) {
        return this.getSts(objectname, roleArn, null, policyEnum);
    }

    /**
     * 获取链接
     * 
     * @param objectname object文件名称
     * @param roleArn 需要授权的角色名称
     * @param expirationTime 有效期
     * @param policyEnum
     * @return
     */
    public OssStsConfig getSts(final String objectname, final String roleArn, final Long expirationTime,
                               final PolicyEnum policyEnum) {
        try {
            Credentials credentials = null;
            if (expirationTime == null || expirationTime == 0L) {
                credentials = OssStsUtils.createSTS(this.ossConfigVO.getBucket(), objectname, roleArn,
                        this.ossConfigVO.getAccessKeyId(), this.ossConfigVO.getAccessKeySecret(), policyEnum,
                        this.ossConfigVO.getStsEndpoint());
            } else {
                credentials = OssStsUtils.createSTS(this.ossConfigVO.getBucket(), objectname, roleArn,
                        this.ossConfigVO.getAccessKeyId(), this.ossConfigVO.getAccessKeySecret(), expirationTime,
                        policyEnum, this.ossConfigVO.getStsEndpoint());
            }
            final OssStsConfig configVO = new OssStsConfig();
            configVO.setAccessKeyId(credentials.getAccessKeyId());
            configVO.setAccessKeySecret(credentials.getAccessKeySecret());
            configVO.setBucket(this.ossConfigVO.getBucket());
            configVO.setEndpoint(this.ossConfigVO.getEndpoint());
            configVO.setExpiration(credentials.getExpiration());
            configVO.setSecurityToken(credentials.getSecurityToken());
            return configVO;
        } catch (final ClientException e) {
            this.logger.error("", e);
            System.out.println(e);

        }
        return null;
    }

    public void setAccessKeyId(final String accessKeyId) {
        this.ossConfigVO.setAccessKeyId(accessKeyId);
    }

    public void setAccessKeySecret(final String accessKeySecret) {
        this.ossConfigVO.setAccessKeySecret(accessKeySecret);
    }

    public void setBucket(final String bucket) {
        this.ossConfigVO.setBucket(bucket);
    }

    public void setEndpoint(final String endpoint) {
        this.ossConfigVO.setEndpoint(endpoint);
    }

    public void setStsEndpoint(final String stsEndpoint) {
        this.ossConfigVO.setStsEndpoint(stsEndpoint);
    }

}
