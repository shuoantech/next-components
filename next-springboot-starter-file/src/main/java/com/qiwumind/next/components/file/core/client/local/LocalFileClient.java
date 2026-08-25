package com.qiwumind.next.components.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.qiwumind.next.components.common.exception.ErrorCode;
import com.qiwumind.next.components.common.exception.util.ServiceExceptionUtil;
import com.qiwumind.next.components.file.core.client.AbstractFileClient;
import com.qiwumind.next.components.file.core.util.FilePathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;



/**
 * 本地文件客户端
 * @author qiwumind
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        try {
            return FileUtil.readBytes(filePath);
        } catch (IORuntimeException ex) {
            if (ex.getMessage().startsWith("File not exist:")) {
                return null;
            }
            throw ex;
        }
    }

    private String getFilePath(String path) {
        FilePathUtils.validatePath(path);
        Path basePath = Paths.get(config.getBasePath()).toAbsolutePath().normalize();
        Path filePath = basePath.resolve(path).normalize();
        if (!filePath.startsWith(basePath)) {
            ErrorCode FILE_PATH_INVALID = new ErrorCode(1_001_003_003, "文件路径不正确");
            throw ServiceExceptionUtil.exception(FILE_PATH_INVALID);
        }
        return filePath.toString();
    }

}
