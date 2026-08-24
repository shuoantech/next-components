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

package com.qiwumind.next.components.common.exception;



/**
 * @author liks 2019年7月8日 下午2:20:11
 */
public class BusinessRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 9156868885319477760L;

    private String            errorCode;
    private String            errorMessage;

    /**
     * @return
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * @param errorCode
     */
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BusinessRuntimeException() {
        super();
    }

    /**
     * @param cause
     */
    public BusinessRuntimeException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * @param message
     */
    public BusinessRuntimeException(final String errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessRuntimeException(final String errorCode, final String message, final Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public BusinessRuntimeException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
