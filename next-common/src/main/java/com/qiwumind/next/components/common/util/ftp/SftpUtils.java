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

package com.qiwumind.next.components.common.util.ftp;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 */
public class SftpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SftpUtils.class);

    /**
     * 连接sftp服务器
     * @param host 主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static ChannelSftp connectByPassword(final String host, final int port, final String username,
                                                final String password) {
        ChannelSftp sftp = null;
        try {
            final JSch jsch = new JSch();
            final Session sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            sshSession.setTimeout(30000);
            final Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            final Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return sftp;
    }

    /**
     * 上传文件
     * 
     * @param remotePath 上传的目录
     * @param localPath 要上传的文件
     * @param sftp
     */
    public static void upload(final String remotePath, final String localPath, final ChannelSftp sftp) {
        LOG.info("upload file remotePath:{},localPath:{}", remotePath, localPath);
        FileInputStream fis = null;
        try {
            sftp.cd(remotePath);
            final File file = new File(localPath);
            fis = new FileInputStream(file);
            sftp.put(fis, file.getName());
        } catch (final Exception e) {
            LOG.error("upload file error:{}", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
            //sftp.disconnect();
        }
    }

    /**
     * 上传文件
     * 
     * @param remotePath 上传的目录
     * @param localPath 要上传的文件
     * @param sftp
     */
    public static void upload(final String remotePath, final File localPath, final ChannelSftp sftp) {
        LOG.info("upload file remotePath:{},file:{}", remotePath, localPath.getName());
        FileInputStream fis = null;
        try {
            sftp.cd(remotePath);
            fis = new FileInputStream(localPath);
            sftp.put(fis, localPath.getName());
        } catch (final Exception e) {
            LOG.error("upload file error:{}", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
            //sftp.disconnect();
        }
    }

    /**
     * 下载文件
     * 
     * @param remotePath 下载完整目录
     * @param remoteFileName 下载的文件名称
     * @param localPath 存在本地的完整路径
     * @param sftp
     */
    public static void download(final String remotePath, final String remoteFileName, final String localPath,
                                final ChannelSftp sftp) {
        LOG.info("down load file remotePath:{},remoteFileName:{},localPath:{}", remotePath, remoteFileName, localPath);
        FileOutputStream fos = null;
        try {
            sftp.cd(remotePath);
            final File file = new File(localPath);
            (file.getParentFile()).mkdirs();
            fos = new FileOutputStream(file);
            sftp.get(remoteFileName, fos);
        } catch (final Exception e) {
            LOG.error("down load file error:{}", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fos);
            // sftp.disconnect();
        }
    }

    /**
     * 删除文件
     * 
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sftp
     */
    public static void delete(final String directory, final String deleteFile, final ChannelSftp sftp) {
        try {
            boolean flag = true;
            sftp.cd(directory);
            try {
                sftp.rm(deleteFile);
            } catch (final SftpException sException) {
                if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                    flag = false;
                }
            }
            if (flag) {
                LOG.info(directory + deleteFile + " 文件已删除！");
            } else {
                LOG.info(directory + deleteFile + " 文件不存在,无需删除！");
            }
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            //sftp.disconnect();
        }
    }

    /**
     * @param directory
     * @param sftp
     * @param flag:ture多层目录常见 flag：false
     *            单个目录创建，由于涉及到权限问题上级目录无法创建，所以自己实现单个目录创建，传true
     */
    public static void cdMkdir(String directory, final ChannelSftp sftp, final Boolean flag) {
        if (flag) {
            try {
                try {
                    sftp.cd(directory);
                } catch (final SftpException sException) {
                    LOG.info(directory + " 路径不存在！");
                    if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                        sftp.mkdir(directory);
                        sftp.cd(directory);
                        LOG.info(directory + " 路径已创建！");
                    }
                }
            } catch (final SftpException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            try {
                final String now = sftp.pwd();
                directory = directory.substring(now.length());
                final String[] dirs = directory.split("/");
                for (int i = 1; i < dirs.length; i++) {
                    final boolean dirExists = openDir(dirs[i], sftp);
                    if (!dirExists) {
                        sftp.mkdir(dirs[i]);
                        sftp.cd(dirs[i]);
                        LOG.info(dirs[i] + " 路径已创建！");
                    }

                }
                sftp.cd(now);
            } catch (final SftpException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }

    /**
     * 创建并打开文件目录。
     * 
     * @param directory
     * @param sftp
     */
    public static void cdMkdir(final String directory, final ChannelSftp sftp) {
        final String[] dirs = directory.split("/");
        try {
            final String now = sftp.pwd();
            for (int i = 1; i < dirs.length; i++) {
                final boolean dirExists = openDir(dirs[i], sftp);
                if (!dirExists) {
                    sftp.mkdir(dirs[i]);
                    sftp.cd(dirs[i]);
                    LOG.info(dirs[i] + " 路径已创建！");
                }
            }
            sftp.cd(now);
        } catch (final SftpException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 打开指定目录
     * 
     * @param directory directory
     * @return 是否打开目录
     */
    public static boolean openDir(final String directory, final ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            return true;
        } catch (final SftpException e) {
            LOG.info(directory + " 路径不存在！");
            return false;
        }
    }

    /**
     * 查看文件是否存在
     * 
     * @param directory:远程目录
     * @param sftp
     * @param fileName
     * @return
     * @throws
     */
    public static boolean fileIsExist(final String directory, final String fileName, final ChannelSftp sftp) {
        boolean exits = true;
        try {
            final boolean dirExists = openDir(directory, sftp);
            if (!dirExists) {
                sftp.mkdir(directory);
            }
            sftp.cd(directory);
            sftp.ls(fileName);
        } catch (final SftpException sException) {
            exits = false;
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
                LOG.info("文件不存在!path:" + directory + File.separator + fileName);
            }
        }
        return exits;
    }
}
