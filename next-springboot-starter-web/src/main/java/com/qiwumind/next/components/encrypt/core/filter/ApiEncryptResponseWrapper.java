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

package com.qiwumind.next.components.encrypt.core.filter;

import cn.hutool.crypto.asymmetric.AsymmetricEncryptor;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import com.qiwumind.next.components.encrypt.config.ApiEncryptProperties;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 加密响应 {@link HttpServletResponseWrapper} 实现类
 * @author qiwumind
 */
public class ApiEncryptResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final ServletOutputStream servletOutputStream;
    private final PrintWriter printWriter;

    public ApiEncryptResponseWrapper(HttpServletResponse response) {
        super(response);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.servletOutputStream = this.getOutputStream();
        this.printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream));
    }

    public void encrypt(ApiEncryptProperties properties,
                        SymmetricEncryptor symmetricEncryptor,
                        AsymmetricEncryptor asymmetricEncryptor) throws IOException {
        // 1.1 清空 body
        HttpServletResponse response = (HttpServletResponse) this.getResponse();
        response.resetBuffer();
        // 1.2 获取 body
        this.flushBuffer();
        byte[] body = byteArrayOutputStream.toByteArray();

        // 2. 添加加密 header 标识
        this.addHeader(properties.getHeader(), "true");
        // 特殊：特殊：https://juejin.cn/post/6867327674675625992
        this.addHeader("Access-Control-Expose-Headers", properties.getHeader());

        // 3.1 加密 body
        String encryptedBody = symmetricEncryptor != null ? symmetricEncryptor.encryptBase64(body)
                : asymmetricEncryptor.encryptBase64(body, KeyType.PublicKey);
        // 3.2 输出加密后的 body：（设置 header 要放在 response 的 write 之前）
        response.getWriter().write(encryptedBody);
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (servletOutputStream != null) {
            servletOutputStream.flush();
        }
        if (printWriter != null) {
            printWriter.flush();
        }
    }

    @Override
    public void reset() {
        byteArrayOutputStream.reset();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }

            @Override
            @SuppressWarnings("NullableProblems")
            public void write(byte[] b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            @Override
            @SuppressWarnings("NullableProblems")
            public void write(byte[] b, int off, int len) {
                byteArrayOutputStream.write(b, off, len);
            }

        };
    }

}