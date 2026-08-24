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

package com.qiwumind.next.components.crypto.core.db;

import com.qiwumind.next.components.crypto.core.Enc;
import com.qiwumind.next.components.crypto.core.EncException;

/**
 * 字段级加解密服务，对 {@link Enc} 做幂等、安全的封装：
 * <ul>
 *     <li>{@link #encrypt(String)}：已加密（以 enc_ 前缀）或 null 直接返回，避免重复加密；</li>
 *     <li>{@link #decrypt(String)}：非加密内容、或解密失败时原样返回，避免影响业务读取。</li>
 * </ul>
 */
public class CryptoFieldService {

    /**
     * 加密明文。已加密或为空时原样返回。
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.startsWith(Enc.PREFIX_ENC)) {
            return plaintext;
        }
        try {
            return Enc.encryptData(plaintext);
        } catch (EncException e) {
            // 加密失败宁可中断写入，也不允许明文落库
            throw new IllegalStateException("字段加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解密密文。非密文或解密失败时原样返回（不抛异常，保证业务可读）。
     */
    public String decrypt(String ciphertext) {
        if (ciphertext == null || !ciphertext.startsWith(Enc.PREFIX_ENC)) {
            return ciphertext;
        }
        try {
            return Enc.decryptData(ciphertext);
        } catch (EncException e) {
            return ciphertext;
        }
    }
}
