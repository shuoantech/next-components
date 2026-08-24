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

package com.qiwumind.next.components.license.core.exception;

public class LicenseException extends RuntimeException {

    private final ErrorCode errorCode;

    public LicenseException(String message) {
        super(message);
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }

    public LicenseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LicenseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public enum ErrorCode {
        LICENSE_NOT_FOUND("LICENSE_NOT_FOUND", "License文件不存在"),
        SIGNATURE_INVALID("SIGNATURE_INVALID", "签名验证失败"),
        LICENSE_EXPIRED("LICENSE_EXPIRED", "License已过期"),
        LICENSE_NOT_YET_VALID("LICENSE_NOT_YET_VALID", "License尚未生效"),
        BINDING_MISMATCH("BINDING_MISMATCH", "绑定信息不匹配"),
        FEATURE_NOT_AUTHORIZED("FEATURE_NOT_AUTHORIZED", "功能未授权"),
        GRACE_PERIOD_EXPIRED("GRACE_PERIOD_EXPIRED", "宽限期已过"),
        GENERAL_ERROR("GENERAL_ERROR", "License错误");

        private final String code;
        private final String message;

        ErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}