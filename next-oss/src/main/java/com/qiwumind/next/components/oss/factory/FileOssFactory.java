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

package com.qiwumind.next.components.oss.factory;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import com.google.common.base.Preconditions;
import com.qiwumind.next.components.oss.OssConfigDTO;
import com.qiwumind.next.components.oss.util.UnicodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * 类FileOssFactory.java的实现描述
 * 
 * @author liks 2019年3月7日 上午10:40:10
 */
@Slf4j
public class FileOssFactory implements InitializingBean {
    private static final Logger          LOG           = LoggerFactory.getLogger(FileOssFactory.class);
    private final Map<String, OSSClient> channelOssMap = new HashMap<String, OSSClient>();
    private String                       ossConfig;
    private final Set<OssConfigDTO>      ossConfigSet  = new HashSet<OssConfigDTO >();

    public void setOssConfig(final String ossConfig) {
        this.ossConfig = ossConfig;
    }

    /**
     * 是否已连接
     * 
     * @return
     */
    public OSSClient isConnected(final String channel) {
        final OSSClient client = channelOssMap.get(channel);
        LOG.info("oss={},channelType={} ", client, channel);
        if (null != client) {
            return client;
        } else {
            final OssConfigDTO configDTO = channelConfig(channel);
            Preconditions.checkArgument(configDTO!=null, "configDTO 不能为空");
            final OSSClient channelClient = getChannelOss(configDTO.getEndpoint(), configDTO.getAccessKeyId(),
                    configDTO.getAccessKeySecret());
            channelOssMap.put(channel, channelClient);
            return channelClient;
        }
    }

    private OssConfigDTO channelConfig(final String channel) {
        final Iterator<OssConfigDTO> iterator = ossConfigSet.iterator();
        while (iterator.hasNext()) {
            final OssConfigDTO ossConfigDTO = iterator.next();
            if (ossConfigDTO.getChannel().equals(channel)) {
                return ossConfigDTO;
            }
        }
        return null;
    }

    /**
     * getUrlByKey
     * 
     * @param key
     * @param expiration
     * @return
     */
    public URL getUrlByKey(final String channel, final String key, final Date expiration) {
        final OssConfigDTO configDTO = channelConfig(channel);
        final OSSClient client = isConnected(channel);
        return client.generatePresignedUrl(configDTO.getBucket(), key, expiration);
    }

    /**
     * 获得临时下载链接
     *
     * @param key
     * @return
     */
    public String getUrl(final String channel, final String key) {
        //设置URL过期时间为10年  3600l* 1000*24*365*10
        final Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        final URL url = getUrlByKey(channel, key, expiration);
        if (url != null) {
            return UnicodeUtils.unicode2String(url.toString());
        }
        return null;
    }

    /**
     * 获取链接
     * 
     * @return
     */
    public OSSClient getChannelOss(final String endpoint, final String accessKeyId, final String accessKeySecret) {
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * uploadFile
     * 
     * @param key
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadFile(final String channel, final String key, final File file) throws IOException {
        // 创建上传Object的Metadata
        final ObjectMetadata meta = new ObjectMetadata();
        final InputStream content = new FileInputStream(file);
        // 必须设置ContentLength
        meta.setContentLength(file.length());

        final OSSClient client = isConnected(channel);
        final OssConfigDTO configDTO = channelConfig(channel);
        // 上传Object.
        final PutObjectResult result = client.putObject(configDTO.getBucket(), key, content, meta);
        content.close();
        return result.getETag();
    }

    /**
     * @param key
     * @param file
     * @return
     * @throws IOException
     */
    public String downloadFile(final String channel, final String key, final File file) throws IOException {
        // 创建上传Object的Metadata
        final ObjectMetadata meta = new ObjectMetadata();
        final InputStream content = new FileInputStream(file);
        // 必须设置ContentLength
        meta.setContentLength(file.length());

        final OSSClient client = isConnected(channel);
        final OssConfigDTO configDTO = channelConfig(channel);
        // 上传Object.
        final PutObjectResult result = client.putObject(configDTO.getBucket(), key, content, meta);
        content.close();
        return result.getETag();
    }

    /**
     * 从OSS下载文件
     * 
     * @param channel
     * @param filepdf
     * @param savefile
     * @throws IOException
     */
    public void downloadFile(final String channel, final String filepdf, final String savefile) {
        try {
            final File file = new File(savefile);
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            final OSSClient client = isConnected(channel);
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            final OssConfigDTO configDTO = channelConfig(channel);

            final OSSObject ossObject = client.getObject(configDTO.getBucket(), filepdf);
            //获取到OSS文件 

            final InputStream is = ossObject.getObjectContent();
            final BufferedInputStream bis = new BufferedInputStream(is);
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            final int buffer = 1024; // 定义缓冲区的大小
            int length = 0;
            final byte[] b = new byte[buffer];

            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
            }
            bos.flush();
            bos.close();
            // 下载后删除oss的临时文件
            // client.deleteObject(bucketName, objectkey);
        } catch (final IOException e) {
            log.error("cd directory:{} fail:{}", filepdf, e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final String[] config = this.ossConfig.split(",");
        OssConfigDTO ossConfigDto = null;
        for (final String val : config) {
            LOG.info("ossConfig={}", val);
            final String[] address = val.split(":");
            if (address.length != 5) {
                throw new IllegalArgumentException(
                        "ossConfig配置不正确 应包含 (渠道:endpoint:accessKeyId:accessKeySecret:bucket) 多渠道采用,分割");
            }
            ossConfigDto = new OssConfigDTO(address[0], address[1], address[2], address[3], address[4]);
            ossConfigSet.add(ossConfigDto);
        }

    }

}
