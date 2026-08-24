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



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * oss 连接
 * 
 * @author liks 2019年9月19日 下午5:28:52
 */
public class OssUtils {
    private static final Logger             logger        = LoggerFactory.getLogger(OssUtils.class);
    private static final Map<String, OSS>   channelOssMap = new ConcurrentHashMap<>();

    /**
     * 是否已连接
     * 
     * @return
     */
    private static OSS getClient(final OssStsConfig ossConfigVO) {
        final String index = ossConfigVO.getBucket().concat("-").concat(ossConfigVO.getAccessKeyId());
        OSS client = channelOssMap.get(index);
        logger.debug("oss={},channelType={} ", client, index);
        if (null != client) {
            return client;
        } else {
            client = getOss(ossConfigVO.getEndpoint(), ossConfigVO.getAccessKeyId(),
                    ossConfigVO.getAccessKeySecret(), ossConfigVO.getSecurityToken());
            channelOssMap.put(index, client);
            return client;
        }
    }

    private static void shutdown(final OssStsConfig ossConfigVO) {
        final String index = ossConfigVO.getBucket().concat("-").concat(ossConfigVO.getAccessKeyId());
        final OSS client = channelOssMap.get(index);
        if (null != client) {
            client.shutdown();
            channelOssMap.remove(index);
        }
    }

    /**
     * 关闭所有连接
     */
    public static void shutdownAll() {
        for (OSS client : channelOssMap.values()) {
            try {
                client.shutdown();
            } catch (Exception e) {
                logger.error("关闭OSS客户端失败", e);
            }
        }
        channelOssMap.clear();
    }

    /**
     * uploadFile
     * 
     * @param key
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFile(final OssStsConfig ossConfigVO, final String key, final File file)
            throws IOException {
        // 创建上传Object的Metadata
        final ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.length());
        final OSS client = getClient(ossConfigVO);
        // 上传Object.
        try (InputStream content = new FileInputStream(file)) {
            final PutObjectResult result = client.putObject(ossConfigVO.getBucket(), key, content, meta);
            return result.getETag();
        }
    }

    /**
     * 从OSS下载文件
     * 
     * @param filename
     * @param ossConfigVO objectname+文件
     * @param savefile
     * @throws IOException
     */
    public static void downloadFile(final OssStsConfig ossConfigVO, final String filename, final String savefile)
            throws IOException {
        final File file = new File(savefile);
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        final OSS client = getClient(ossConfigVO);
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        final OSSObject ossObject = client.getObject(ossConfigVO.getBucket(), filename);
        //获取到OSS文件 
        try (InputStream is = ossObject.getObjectContent();
             BufferedInputStream bis = new BufferedInputStream(is);
             FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream)) {
            final int buffer = 1024; // 定义缓冲区的大小
            int length;
            final byte[] b = new byte[buffer];
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
            }
        }
    }

    /**
     * 获取链接
     * 
     * @return
     */
    private static OSS getOss(final String endpoint, final String accessKeyId, final String accessKeySecret,
                                    final String securityToken) {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);
    }
}
