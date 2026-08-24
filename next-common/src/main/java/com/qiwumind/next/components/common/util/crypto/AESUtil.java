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

package com.qiwumind.next.components.common.util.crypto;



import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.commons.lang3.StringUtils;

public class AESUtil {
    private static final String AES_KEY = "AES_KEY";
    private static final byte[] KEY_BYTES;

    private static String keyStr = "ad1725339b2dd0a68903c57b635942ca";

    static {
        KEY_BYTES = new byte[16];
        int i = 0;
        for (byte b : keyStr.getBytes()) {
            KEY_BYTES[i++ % 16] ^= b;
        }
    }

    public static String encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return HexUtil.encodeHexStr(SecureUtil.aes(KEY_BYTES).encrypt(content), false);
    }

    public static String decrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return SecureUtil.aes(KEY_BYTES).decryptStr(content);
    }

    public static void main(String[] args) {
        String encrypted = encrypt("测试assss");
        System.out.println(encrypted);
        System.out.println(decrypt(encrypted));
    }
}
