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

package com.qiwumind.next.components.starrocks.core.infra.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * URL文件下载工具类
 */
public class UrlFileDownloader {
    private static final Logger logger = LoggerFactory.getLogger(UrlFileDownloader.class);

    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 60000;
    private static final int BUFFER_SIZE = 8192;

    /**
     * 从URL下载文件到临时目录
     */
    public static File downloadFromUrl(String fileUrl) throws IOException {
        return downloadFromUrl(fileUrl, null, null);
    }

    /**
     * 从URL下载文件到指定目录
     */
    public static File downloadFromUrl(String fileUrl, String targetDir, String fileName)
            throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setInstanceFollowRedirects(true);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP请求失败, 状态码: " + responseCode + ", URL: " + fileUrl);
            }
            // 获取文件名
            String actualFileName = getFileName(fileUrl, fileName, connection);
            // 创建目标目录
            File downloadDir = getDownloadDirectory(targetDir);
            File outputFile = new File(downloadDir, actualFileName);
            // 下载文件
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            logger.info("文件下载成功: {} -> {}, 文件大小: {} bytes",
                    fileUrl, outputFile.getAbsolutePath(), totalBytes);
            return outputFile;
        } finally {
            // 关闭资源
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn("关闭输出流失败", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("关闭输入流失败", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 使用NIO方式下载（更高效）
     */
    public static File downloadFromUrlWithNIO(String fileUrl, String targetDir, String fileName)
            throws IOException {

        URL url = new URL(fileUrl);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setInstanceFollowRedirects(true);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP请求失败, 状态码: " + responseCode + ", URL: " + fileUrl);
            }
            // 获取文件名
            String actualFileName = getFileName(fileUrl, fileName, connection);
            // 创建目标目录
            File downloadDir = getDownloadDirectory(targetDir);
            File outputFile = new File(downloadDir, actualFileName);
            // 使用Files.copy下载
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            long fileSize = outputFile.length();
            logger.info("NIO文件下载成功: {} -> {}, 文件大小: {} bytes",
                    fileUrl, outputFile.getAbsolutePath(), fileSize);
            return outputFile;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取文件名
     */
    private static String getFileName(String fileUrl, String customFileName, HttpURLConnection connection) throws MalformedURLException {
        // 如果指定了文件名，使用指定的
        if (customFileName != null && !customFileName.trim().isEmpty()) {
            return customFileName;
        }
        // 从Content-Disposition头获取文件名
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null) {
            String fileName = extractFileNameFromContentDisposition(contentDisposition);
            if (fileName != null) {
                return fileName;
            }
        }
        // 从URL路径获取文件名
        String urlPath = new URL(fileUrl).getPath();
        if (urlPath != null && !urlPath.isEmpty()) {
            String fileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);
            if (!fileName.isEmpty()) {
                return fileName;
            }
        }
        // 默认文件名
        return "downloaded_file_" + System.currentTimeMillis() + ".csv";
    }

    /**
     * 从Content-Disposition头提取文件名
     */
    private static String extractFileNameFromContentDisposition(String contentDisposition) {
        if (contentDisposition == null) return null;

        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("filename=")) {
                String fileName = part.substring("filename=".length());
                // 移除引号
                if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }
                return fileName;
            }
        }
        return null;
    }

    /**
     * 获取下载目录
     */
    private static File getDownloadDirectory(String customDir) throws IOException {
        File downloadDir;

        if (customDir != null && !customDir.trim().isEmpty()) {
            downloadDir = new File(customDir);
        } else {
            // 使用系统临时目录
            String tempDir = System.getProperty("java.io.tmpdir");
            downloadDir = new File(tempDir, "starrocks_download");
        }

        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            throw new IOException("创建下载目录失败: " + downloadDir.getAbsolutePath());
        }

        return downloadDir;
    }

    /**
     * 验证下载的文件
     */
    public static boolean validateDownloadedFile(File file, long minSize) {
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.length() < minSize) {
            logger.warn("文件大小异常: {} bytes, 最小要求: {} bytes", file.length(), minSize);
            return false;
        }

        return true;
    }

    /**
     * 清理临时文件
     */
    public static void cleanupTempFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                try {
                    boolean deleted = file.delete();
                    if (deleted) {
                        logger.debug("临时文件已删除: {}", file.getAbsolutePath());
                    } else {
                        logger.warn("删除临时文件失败: {}", file.getAbsolutePath());
                    }
                } catch (SecurityException e) {
                    logger.warn("没有权限删除文件: {}", file.getAbsolutePath(), e);
                }
            }
        }
    }
}
