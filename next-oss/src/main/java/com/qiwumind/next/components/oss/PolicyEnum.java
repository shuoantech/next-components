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



import org.apache.commons.lang3.StringUtils;

/**
 * policy 枚举
 * 
 * @author liks 2019年9月24日 下午8:56:03
 */
public enum PolicyEnum {
    /**
     * 上传policy
     */
    UPLOAD {
        @Override
        public String policy(final String bucket, final String objectname) {
            return createPolicyUpload(bucket, objectname);
        }
    },
    /**
     * 上传、下载policy
     */
    UPLOAD_DOWNLOAD {
        @Override
        public String policy(final String bucket, final String objectname) {
            return createPolicyUploadDownload(bucket, objectname);
        }
    },
    /**
     * 下载policy
     */
    DOWNLOAD {
        @Override
        public String policy(final String bucket, final String objectname) {
            return createPolicyDownload(bucket, objectname);
        }
    },
    /**
     * all policy
     */
    ALL {
        @Override
        public String policy(final String bucket, final String objectname) {
            return createPolicyAll(bucket);
        }
    };

    private PolicyEnum() {
    }

    /**
     * @param bucket
     * @param objectname bucket下的文件夹
     * @return
     */
    public abstract String policy(String bucket, final String objectname);

    /** 授权策略 */
    private static final String POLICY_ALL             = "{\"Version\":\"1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"oss:GetObject\",\"oss:PutObject\",\"oss:DeleteObject\",\"oss:ListParts\",\"oss:AbortMultipartUpload\",\"oss:ListObjects\"],\"Resource\":[\"acs:oss:*:*:%s/*\" ,\"acs:oss:*:*:%s\"]}]}";
    /** 授权策略 */
    private static final String POLICY_UPLOAD          = "{\"Version\":\"1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"oss:PutObject\"],\"Resource\":[\"acs:oss:*:*:%s/%s/*\"]}]}";
    /** 授权策略 */
    private static final String POLICY_DOWNLOAD        = "{\"Version\":\"1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"oss:GetObject\",\"oss:ListObjects\"],\"Resource\":[\"acs:oss:*:*:%s/%s/*\"]}]}";
    /** 授权策略 */
    private static final String POLICY_UPLOAD_DOWNLOAD = "{\"Version\":\"1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"oss:PutObject\",\"oss:GetObject\",\"oss:ListObjects\"],\"Resource\":[\"acs:oss:*:*:%s/%s/*\"]}]}";

    /**
     * 生成访问策略,参考官方文档
     *
     * @param bucketName 可以访问的文件夹
     * @return policy 信息
     * @link 
     *       https://help.aliyun.com/document_detail/28664.html?spm=a2c4g.11186623
     *       .2.12.9f685328GbGIBv
     */
    private static String createPolicyAll(final String bucketName) {
        return String.format(POLICY_ALL, bucketName, bucketName);
    }

    private static String createPolicyUpload(final String bucketName, final String objectname) {
        isTrue(StringUtils.isNotBlank(objectname), "objectname 不能为空");
        return String.format(POLICY_UPLOAD, bucketName, objectname);
    }

    private static String createPolicyDownload(final String bucketName, final String objectname) {
        isTrue(StringUtils.isNotBlank(objectname), "objectname 不能为空");
        return String.format(POLICY_DOWNLOAD, bucketName, objectname);
    }

    private static String createPolicyUploadDownload(final String bucketName, final String objectname) {
        isTrue(StringUtils.isNotBlank(objectname), "objectname 不能为空");
        return String.format(POLICY_UPLOAD_DOWNLOAD, bucketName, objectname);
    }

    private static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

}
