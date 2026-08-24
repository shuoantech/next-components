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

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * @describe: OSS临时访问凭证授权
 * @version 1.0
 */
public class OssStsUtils {
    /** "cn-hangzhou"这个region可用, 不要使用填写其他region的值 */
    //"cn-hangzhou";
    private static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    /**
     * 用户自定义参数。此参数用来区分不同的Token，可用于用户级别的访问审计
     * 支持输入2~32个字符，请输入至少2个字符，如果只有1个字符，会出现错误。
     */
    private static final String ROLE_SESSION_NAME  = "%s-session";
    /** 有效期 秒 */
    private static final Long   EXPIRATION         = (long) (30 * 60);
    private static final String REGEX              = "^[a-zA-Z0-9\\.@\\-_]+$";

    /**
     * @param objectname
     * @return
     */
    private static String createRoleSessionName(final String objectname) {
        isTrue(StringUtils.isNotBlank(objectname), "objectname 不能为空");
        String session = String.format(ROLE_SESSION_NAME, objectname);
        session = com.qiwumind.next.components.oss.util.StringUtils.get(session, REGEX);
        if (session.length() > 32) {
            session = session.substring(0, 32);
        }
        return session;
    }

    /**
     * 创建临时授权-- 过期时间默认半小时
     *
     * @param bucket
     * @param objectname object文件名称
     * @param roleArn 需要授权的角色名称
     * @param accessKeyId 账号
     * @param accessKeySecret 密码
     * @param policyEnum policy 枚举
     * @return
     * @throws ClientException
     */
    public static Credentials createSTS(final String bucket, final String objectname, final String roleArn,
                                        final String accessKeyId, final String accessKeySecret,
                                        final PolicyEnum policyEnum, String stsEndPoint)
            throws ClientException {
        return createSTS(bucket, objectname, roleArn, accessKeyId, accessKeySecret, EXPIRATION, policyEnum,
                stsEndPoint);
    }

    /**
     * 创建临时授权
     *
     * @param bucket
     * @param objectname object文件名称
     * @param roleArn 需要授权的角色名称
     * @param accessKeyId 账号
     * @param accessKeySecret 密码
     * @param expirationTime 过期时间，单位为秒,null或0时 默认半小时
     * @param policyEnum policy 枚举
     * @return
     * @throws ClientException
     */
    public static Credentials createSTS(final String bucket, final String objectname, final String roleArn,
                                        final String accessKeyId, final String accessKeySecret,
                                        final Long expirationTime, final PolicyEnum policyEnum, String stsEndPoint)
            throws ClientException {
        final String policy = policyEnum.policy(bucket, objectname);
        final String roleSessionName = (StringUtils.isNotBlank(objectname) && objectname.matches(REGEX))
                ? createRoleSessionName(objectname)
                : createRoleSessionName(bucket);

        return createVPCSTS(policy, roleArn, roleSessionName, accessKeyId, accessKeySecret, expirationTime,
                stsEndPoint);
    }

    /**
     * 创建STS
     *
     * @param policy 授权策略
     * @param roleArn 需要授权的角色名称
     * @param accessKeyId 账号
     * @param accessKeySecret 密码
     * @param expirationTime 过期时间，单位为秒
     * @return
     * @throws ClientException
     */
    private static Credentials createSTS(final String policy, final String roleArn, final String roleSessionName,
                                         final String accessKeyId, final String accessKeySecret,
                                         final Long expirationTime, String stsEndpoint)
            throws ClientException {
        final IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        final DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setDurationSeconds(expirationTime);
        //        request.setVersion(STS_API_VERSION);
        request.setMethod(MethodType.POST);
        /** 必须是https请求 */
        request.setProtocol(ProtocolType.HTTPS);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policy);
        //实体用户获取角色身份的安全令牌的方法
        final AssumeRoleResponse response = client.getAcsResponse(request);
        return response.getCredentials();
    }

    /**
     * 创建STS
     *
     * @param policy 授权策略
     * @param roleArn 需要授权的角色名称
     * @param accessKeyId 账号
     * @param accessKeySecret 密码
     * @param expirationTime 过期时间，单位为秒
     * @return
     * @throws ClientException
     */
    private static Credentials createVPCSTS(final String policy, final String roleArn, final String roleSessionName,
                                            final String accessKeyId, final String accessKeySecret,
                                            final Long expirationTime, String stsEndpoint)
            throws ClientException {
        //构造default profile（参数留空，无需添加Region ID）
        IClientProfile profile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret);
        //用profile构造client
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setDurationSeconds(expirationTime);
        request.setSysEndpoint(stsEndpoint);
        request.setSysMethod(MethodType.POST);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policy); // Optional
        final AssumeRoleResponse response = client.getAcsResponse(request);
        return response.getCredentials();
    }

    private static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
