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

package com.qiwumind.next.components.datasecure.utils;



import java.io.UnsupportedEncodingException;

import com.qiwumind.next.components.datasecure.common.config.DataSecureConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 类SymmetryUtil.java的实现描述：数据加解密算法工具类
 */
@Slf4j
public class SymmetryUtil {
    private static byte[] initKey(byte[] b_key) {
        byte state[] = new byte[256];
        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    private static byte[] RC4(byte[] input) {
        int x = 0;
        int y = 0;
        int xorIndex;
        byte[] result = new byte[input.length];
        byte[] key = initKey(SecretKeyUtil.getSecretKey());
        if (key == null) {
            log.error("RC4 加密密钥初始化失败，返回原始数据");
            return input; // 密钥为空时直接返回原始数据
        }
        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

    /**
     * 加密
     * 
     * @param data 原始数据
     * @return
     */
    public static String encryption(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        String encryptData = "";
        try {
            encryptData = new String(Base64.encodeBase64(RC4(data.getBytes("GBK"))));
        } catch (UnsupportedEncodingException e) {
            log.error("RC4 encry UnsupportedEncodingException {}", e);
        }
        return encryptData;
    }

    /**
     * 解密
     * 
     * @param data 加密后数据
     * @return
     */
    public static String decryption(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        String decryptData = "";
        try {
            decryptData = new String(RC4(Base64.decodeBase64(data.getBytes())), "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error("RC4 encry UnsupportedEncodingException {}", e);
        }
        return decryptData;
    }


    public static void main(String[] args) {
        DataSecureConfig config=new DataSecureConfig();
        config.setSecretkey("aaaa");
        String certno="341203198507103133";
        String  decryption=SymmetryUtil.decryption(certno);
        System.out.println(decryption);

        String  encryption=SymmetryUtil.encryption(decryption);
        System.out.println(encryption);


    }
}
