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

package com.qiwumind.next.components.compute.core.enums;



import lombok.Getter;

/**
 * 类TailEnum.java的实现描述：TODO 类实现描述
 */
@Getter
public enum TailEnum {

    NOT_THE_TAIL_HEAD(1, "算头不算尾"),
    HEAD_AND_TAIL(2, "算头又算尾"), ;

    /**
     * code
     */
    private int    code;
    /**
     * message
     */
    private String message;

    /**
     * 构造方法
     * 
     * @param code
     * @param message
     */
    TailEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return
     */
    public byte getByteCode() {
        return (byte) code;
    }

    /**
     * @param code
     * @return
     */
    public static TailEnum parseByCode(int code) {
        for (TailEnum tail : TailEnum.values()) {
            if (tail.getCode() == code)
                return tail;
        }
        return null;
    }
}
