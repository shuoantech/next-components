///*
// * MIT License
// *
// * Copyright (c) 2026 qiwumind
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.  Author: liks
// * Email: 307039176@qq.com
// */
//
//package com.qiwumind.next.components.common.util.ftp;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Properties;
//import java.util.Vector;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.github.mwiede.jsch.Channel;
//import com.github.mwiede.jsch.ChannelSftp;
//import com.github.mwiede.jsch.JSch;
//import com.github.mwiede.jsch.Session;
//import com.github.mwiede.jsch.SftpException;
//
///**
// * SFTP 工具类
// * 基于 com.github.mwiede:jsch（活跃维护分支，无已知安全漏洞）
// *
// * <p>使用示例：
// * <pre>
// * ChannelSftp sftp = null;
// * try {
// *     sftp = SftpUtils.connectByPassword("192.168.1.100", 22, "admin", "password");
// *     SftpUtils.upload("/remote/path", "/local/file.txt", sftp);
// *     SftpUtils.download("/remote/path", "file.txt", "/local/download/", sftp);
// * } finally {
// *     SftpUtils.disconnectQuietly(sftp);
// * }
// * </pre>
// *
// * @author liks
// */
//public final class SftpUtils {
//
//    private static final Logger LOG = LoggerFactory.getLogger(SftpUtils.class);
//
//    /**
//     * 默认连接超时时间（毫秒）
//     */
//    private static final int DEFAULT_TIMEOUT = 30000;
//
//    /**
//     * 默认端口
//     */
//    private static final int DEFAULT_PORT = 22;
//
//    /**
//     * 默认缓冲区大小
//     */
//    private static final int DEFAULT_BUFFER_SIZE = 8192;
//
//    private SftpUtils() {
//        // 工具类禁止实例化
//    }
//
//    /**
//     * 连接 SFTP 服务器（密码方式）
//     *
//     * @param host     主机地址
//     * @param port     端口
//     * @param username 用户名
//     * @param password 密码
//     * @return ChannelSftp
//     */
//    public static ChannelSftp  connectByPassword(final String host, final int port, final String username,
//                                                final String password) {
//        return connectByPassword(host, port, username, password, DEFAULT_TIMEOUT);
//    }
//
//    /**
//     * 连接 SFTP 服务器（密码方式，自定义超时）
//     *
//     * @param host     主机地址
//     * @param port     端口
//     * @param username 用户名
//     * @param password 密码
//     * @param timeout  超时时间（毫秒）
//     * @return ChannelSftp
//     */
//    public static ChannelSftp connectByPassword(final String host, final int port, final String username,
//                                                final String password, final int timeout) {
//        try {
//            final JSch jsch = new JSch();
//            final Session session = jsch.getSession(username, host, port);
//            session.setPassword(password);
//            session.setTimeout(timeout);
//
//            final Properties config = new Properties();
//            // 注意：生产环境应配置真实的主机密钥验证
//            // 可通过 jsch.setKnownHosts("~/.ssh/known_hosts") 配置
//            config.put("StrictHostKeyChecking", "no");
//            // 禁用不安全的密码算法（增强安全性）
//            config.put("kex", "diffie-hellman-group-exchange-sha256,diffie-hellman-group14-sha256");
//            config.put("cipher.s2c", "aes256-ctr,aes192-ctr,aes128-ctr");
//            session.setConfig(config);
//
//            session.connect();
//            final Channel channel = session.openChannel("sftp");
//            channel.connect();
//            LOG.info("SFTP connection established: {}:{}", host, port);
//            return (ChannelSftp) channel;
//        } catch (final Exception e) {
//            LOG.error("Failed to connect SFTP server: {}:{}", host, port, e);
//            throw new RuntimeException("Failed to connect SFTP server: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 连接 SFTP 服务器（密码方式，使用默认端口22）
//     *
//     * @param host     主机地址
//     * @param username 用户名
//     * @param password 密码
//     * @return ChannelSftp
//     */
//    public static ChannelSftp connectByPassword(final String host, final String username, final String password) {
//        return connectByPassword(host, DEFAULT_PORT, username, password);
//    }
//
//    /**
//     * 上传文件（使用本地文件路径）
//     *
//     * @param remotePath 远程目录
//     * @param localPath  本地文件路径
//     * @param sftp       ChannelSftp
//     */
//    public static void upload(final String remotePath, final String localPath, final ChannelSftp sftp) {
//        if (localPath == null || localPath.isEmpty()) {
//            throw new IllegalArgumentException("Local path cannot be null or empty");
//        }
//        final File file = new File(localPath);
//        upload(remotePath, file, sftp);
//    }
//
//    /**
//     * 上传文件（使用 File 对象）
//     *
//     * @param remotePath 远程目录
//     * @param localFile  本地文件
//     * @param sftp       ChannelSftp
//     */
//    public static void upload(final String remotePath, final File localFile, final ChannelSftp sftp) {
//        if (localFile == null) {
//            throw new IllegalArgumentException("Local file cannot be null");
//        }
//        if (!localFile.exists() || !localFile.isFile()) {
//            throw new IllegalArgumentException("Local file does not exist or is not a file: " + localFile.getPath());
//        }
//        LOG.info("Upload file, remotePath: {}, file: {}", remotePath, localFile.getName());
//
//        ensureDirectoryExists(remotePath, sftp);
//
//        try (FileInputStream fis = new FileInputStream(localFile)) {
//            sftp.put(fis, localFile.getName(), ChannelSftp.OVERWRITE);
//            LOG.info("File uploaded successfully: {}", localFile.getName());
//        } catch (final Exception e) {
//            LOG.error("Upload file failed, remotePath: {}, file: {}", remotePath, localFile.getName(), e);
//            throw new RuntimeException("Upload file failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 上传文件（使用字节数组）
//     *
//     * @param remotePath   远程目录
//     * @param remoteFileName 远程文件名
//     * @param content      文件内容
//     * @param sftp         ChannelSftp
//     */
//    public static void upload(final String remotePath, final String remoteFileName, final byte[] content,
//                              final ChannelSftp sftp) {
//        if (content == null || content.length == 0) {
//            throw new IllegalArgumentException("File content cannot be null or empty");
//        }
//        LOG.info("Upload file from bytes, remotePath: {}, fileName: {}", remotePath, remoteFileName);
//
//        ensureDirectoryExists(remotePath, sftp);
//
//        try {
//            sftp.put(new java.io.ByteArrayInputStream(content), remoteFileName, ChannelSftp.OVERWRITE);
//            LOG.info("File uploaded successfully: {}", remoteFileName);
//        } catch (final Exception e) {
//            LOG.error("Upload file from bytes failed, remotePath: {}, fileName: {}", remotePath, remoteFileName, e);
//            throw new RuntimeException("Upload file from bytes failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 下载文件
//     *
//     * @param remotePath     远程目录
//     * @param remoteFileName 远程文件名
//     * @param localPath      本地保存路径（包含文件名）
//     * @param sftp           ChannelSftp
//     */
//    public static void download(final String remotePath, final String remoteFileName, final String localPath,
//                                final ChannelSftp sftp) {
//        if (remoteFileName == null || remoteFileName.isEmpty()) {
//            throw new IllegalArgumentException("Remote file name cannot be null or empty");
//        }
//        if (localPath == null || localPath.isEmpty()) {
//            throw new IllegalArgumentException("Local path cannot be null or empty");
//        }
//        LOG.info("Download file, remotePath: {}, remoteFileName: {}, localPath: {}", remotePath, remoteFileName, localPath);
//
//        final File localFile = new File(localPath);
//        final File parentDir = localFile.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//            if (!parentDir.mkdirs()) {
//                LOG.warn("Failed to create parent directories: {}", parentDir.getPath());
//            }
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(localFile)) {
//            sftp.cd(remotePath);
//            sftp.get(remoteFileName, fos);
//            LOG.info("File downloaded successfully: {}", remoteFileName);
//        } catch (final Exception e) {
//            LOG.error("Download file failed, remotePath: {}, remoteFileName: {}", remotePath, remoteFileName, e);
//            throw new RuntimeException("Download file failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 下载文件（返回字节数组）
//     *
//     * @param remotePath     远程目录
//     * @param remoteFileName 远程文件名
//     * @param sftp           ChannelSftp
//     * @return 文件内容字节数组
//     */
//    public static byte[] download(final String remotePath, final String remoteFileName, final ChannelSftp sftp) {
//        if (remoteFileName == null || remoteFileName.isEmpty()) {
//            throw new IllegalArgumentException("Remote file name cannot be null or empty");
//        }
//        LOG.info("Download file to bytes, remotePath: {}, remoteFileName: {}", remotePath, remoteFileName);
//
//        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
//            sftp.cd(remotePath);
//            sftp.get(remoteFileName, baos);
//            LOG.info("File downloaded successfully: {}, size: {} bytes", remoteFileName, baos.size());
//            return baos.toByteArray();
//        } catch (final Exception e) {
//            LOG.error("Download file to bytes failed, remotePath: {}, remoteFileName: {}", remotePath, remoteFileName, e);
//            throw new RuntimeException("Download file to bytes failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param directory  文件所在目录
//     * @param deleteFile 要删除的文件名
//     * @param sftp       ChannelSftp
//     */
//    public static void delete(final String directory, final String deleteFile, final ChannelSftp sftp) {
//        if (deleteFile == null || deleteFile.isEmpty()) {
//            throw new IllegalArgumentException("Delete file name cannot be null or empty");
//        }
//        LOG.info("Delete file, directory: {}, file: {}", directory, deleteFile);
//
//        try {
//            sftp.cd(directory);
//            try {
//                sftp.rm(deleteFile);
//                LOG.info("File deleted successfully: {}/{}", directory, deleteFile);
//            } catch (final SftpException sException) {
//                if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
//                    LOG.warn("File does not exist, no need to delete: {}/{}", directory, deleteFile);
//                } else {
//                    throw sException;
//                }
//            }
//        } catch (final Exception e) {
//            LOG.error("Delete file failed, directory: {}, file: {}", directory, deleteFile, e);
//            throw new RuntimeException("Delete file failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 删除目录（递归删除）
//     *
//     * @param directory 远程目录路径
//     * @param sftp      ChannelSftp
//     */
//    public static void deleteDirectory(final String directory, final ChannelSftp sftp) {
//        if (directory == null || directory.isEmpty()) {
//            throw new IllegalArgumentException("Directory cannot be null or empty");
//        }
//        LOG.info("Delete directory recursively: {}", directory);
//
//        try {
//            @SuppressWarnings("unchecked")
//            final Vector<ChannelSftp.LsEntry> files = sftp.ls(directory);
//            for (final ChannelSftp.LsEntry entry : files) {
//                final String fileName = entry.getFilename();
//                if (".".equals(fileName) || "..".equals(fileName)) {
//                    continue;
//                }
//                final String fullPath = directory + "/" + fileName;
//                if (entry.getAttrs().isDir()) {
//                    deleteDirectory(fullPath, sftp);
//                } else {
//                    sftp.rm(fullPath);
//                }
//            }
//            sftp.rmdir(directory);
//            LOG.info("Directory deleted successfully: {}", directory);
//        } catch (final Exception e) {
//            LOG.error("Delete directory failed: {}", directory, e);
//            throw new RuntimeException("Delete directory failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 确保目录存在（自动递归创建不存在的目录）
//     *
//     * @param directory 目录路径
//     * @param sftp      ChannelSftp
//     */
//    public static void ensureDirectoryExists(final String directory, final ChannelSftp sftp) {
//        if (directory == null || directory.isEmpty()) {
//            return;
//        }
//
//        try {
//            // 尝试直接切换
//            sftp.cd(directory);
//            return;
//        } catch (final SftpException e) {
//            if (ChannelSftp.SSH_FX_NO_SUCH_FILE != e.id) {
//                throw new RuntimeException("Failed to access directory: " + directory, e);
//            }
//        }
//
//        // 逐级创建目录
//        final String[] parts = directory.split("/");
//        try {
//            final String currentDir = sftp.pwd();
//            for (final String part : parts) {
//                if (part.isEmpty()) {
//                    continue;
//                }
//                try {
//                    sftp.cd(part);
//                } catch (final SftpException se) {
//                    if (ChannelSftp.SSH_FX_NO_SUCH_FILE == se.id) {
//                        sftp.mkdir(part);
//                        sftp.cd(part);
//                        LOG.info("Directory created: {}", part);
//                    } else {
//                        throw se;
//                    }
//                }
//            }
//            sftp.cd(currentDir);
//        } catch (final Exception e) {
//            LOG.error("Failed to ensure directory exists: {}", directory, e);
//            throw new RuntimeException("Failed to create directory: " + directory, e);
//        }
//    }
//
//    /**
//     * 检查文件是否存在
//     *
//     * @param directory 远程目录
//     * @param fileName  文件名
//     * @param sftp      ChannelSftp
//     * @return true-存在，false-不存在
//     */
//    public static boolean fileExists(final String directory, final String fileName, final ChannelSftp sftp) {
//        try {
//            sftp.cd(directory);
//            @SuppressWarnings("unchecked")
//            final Vector<ChannelSftp.LsEntry> files = sftp.ls(fileName);
//            return files != null && !files.isEmpty();
//        } catch (final SftpException e) {
//            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
//                return false;
//            }
//            LOG.error("Failed to check file existence: {}/{}", directory, fileName, e);
//            throw new RuntimeException("Failed to check file existence: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 检查目录是否存在
//     *
//     * @param directory 远程目录
//     * @param sftp      ChannelSftp
//     * @return true-存在，false-不存在
//     */
//    public static boolean directoryExists(final String directory, final ChannelSftp sftp) {
//        try {
//            sftp.cd(directory);
//            return true;
//        } catch (final SftpException e) {
//            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
//                return false;
//            }
//            LOG.error("Failed to check directory existence: {}", directory, e);
//            throw new RuntimeException("Failed to check directory existence: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 获取文件大小
//     *
//     * @param directory 远程目录
//     * @param fileName  文件名
//     * @param sftp      ChannelSftp
//     * @return 文件大小（字节），文件不存在返回 -1
//     */
//    public static long getFileSize(final String directory, final String fileName, final ChannelSftp sftp) {
//        try {
//            sftp.cd(directory);
//            @SuppressWarnings("unchecked")
//            final Vector<ChannelSftp.LsEntry> files = sftp.ls(fileName);
//            if (files != null && !files.isEmpty()) {
//                return files.get(0).getAttrs().getSize();
//            }
//            return -1;
//        } catch (final SftpException e) {
//            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
//                return -1;
//            }
//            LOG.error("Failed to get file size: {}/{}", directory, fileName, e);
//            throw new RuntimeException("Failed to get file size: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 获取文件列表
//     *
//     * @param directory 远程目录
//     * @param sftp      ChannelSftp
//     * @return 文件列表
//     */
//    @SuppressWarnings("unchecked")
//    public static Vector<ChannelSftp.LsEntry> listFiles(final String directory, final ChannelSftp sftp) {
//        try {
//            sftp.cd(directory);
//            return sftp.ls(".");
//        } catch (final Exception e) {
//            LOG.error("Failed to list files in directory: {}", directory, e);
//            throw new RuntimeException("Failed to list files: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 重命名/移动文件
//     *
//     * @param sourcePath     源文件路径
//     * @param targetPath     目标文件路径
//     * @param sftp           ChannelSftp
//     */
//    public static void rename(final String sourcePath, final String targetPath, final ChannelSftp sftp) {
//        try {
//            sftp.rename(sourcePath, targetPath);
//            LOG.info("File renamed/moved: {} -> {}", sourcePath, targetPath);
//        } catch (final Exception e) {
//            LOG.error("Failed to rename/move file: {} -> {}", sourcePath, targetPath, e);
//            throw new RuntimeException("Failed to rename/move file: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 关闭 SFTP 连接（安全关闭，忽略异常）
//     *
//     * @param sftp ChannelSftp
//     */
//    public static void disconnectQuietly(final ChannelSftp sftp) {
//        if (sftp == null) {
//            return;
//        }
//        try {
//            final Session session = sftp.getSession();
//            if (sftp.isConnected()) {
//                sftp.disconnect();
//            }
//            if (session != null && session.isConnected()) {
//                session.disconnect();
//            }
//            LOG.debug("SFTP connection closed");
//        } catch (final Exception e) {
//            LOG.warn("Error closing SFTP connection: {}", e.getMessage());
//        }
//    }
//
//    /**
//     * 关闭 SFTP 连接
//     *
//     * @param sftp ChannelSftp
//     * @throws IOException 关闭异常
//     */
//    public static void disconnect(final ChannelSftp sftp) throws IOException {
//        if (sftp == null) {
//            return;
//        }
//        try {
//            final Session session = sftp.getSession();
//            if (sftp.isConnected()) {
//                sftp.disconnect();
//            }
//            if (session != null && session.isConnected()) {
//                session.disconnect();
//            }
//            LOG.debug("SFTP connection closed");
//        } catch (final Exception e) {
//            throw new IOException("Failed to close SFTP connection: " + e.getMessage(), e);
//        }
//    }
//}