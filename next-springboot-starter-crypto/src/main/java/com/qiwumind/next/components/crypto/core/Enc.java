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

package com.qiwumind.next.components.crypto.core;


import java.nio.charset.StandardCharsets;

import com.qiwumind.next.components.common.util.crypto.AESUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Enc {
    private static final String DEPLOY_ENV_KEY_NAME = "DEPLOY_ENV";
    private static final String DEPLOY_ENV_DEFAULT = "test";
    private static String deployEnv;
    public static final String PREFIX_ENC = "enc_";
    private static String prefix = PREFIX_ENC;

    public Enc() {
    }

    public static String encryptData(String inData) throws EncException {
        if (inData == null) {
            throw new NullPointerException();
        } else {
            byte[] bytes = inData.getBytes(StandardCharsets.UTF_8);
            String hexString = new String(Hex.encodeHex(bytes));
            String output = aes_encrypt(hexString);
            return prefix + output;
        }
    }

    public static String decryptData(String inData) throws EncException {
        if (inData == null) {
            throw new NullPointerException();
        } else if (!inData.startsWith(prefix)) {
            throw buildExceptionForIllegalInput(inData);
        } else {
            String decryptStr = inData.substring(prefix.length());
            String hexString = aes_decrypt(decryptStr);
            byte[] bytes;
            try {
                bytes = Hex.decodeHex(hexString.toCharArray());
            } catch (DecoderException e) {
                throw new EncException("对解密结果进行16进制字符串解码失败, 请核对密文是否正确", e);
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private static IllegalArgumentException buildExceptionForIllegalInput(String inData) {
        if (!inData.startsWith(prefix)) {
            throw new IllegalArgumentException("密文不合法, 密文必须以\"enc_\"开头");
        } else {
            int index = inData.indexOf("_", prefix.length());
            if (index < 0) {
                return new IllegalArgumentException("不是合法的密文");
            } else {
                return new IllegalArgumentException(String.format("当前环境%s无法解密该密文%s", deployEnv, inData));
            }
        }
    }

    public static native String aes_encrypt(String var0, int length0, int length1) throws EncException;

    public static native String aes_decrypt(String var0, int length0, int length1) throws EncException;

    public static String aes_encrypt(String var0) throws EncException {
        return AESUtil.encrypt(var0);
    }

    public static String aes_decrypt(String var0) throws EncException {
        return AESUtil.decrypt(var0);
    }


    static {
//        String[] libFileNames = new String[]{"C:\\Windows\\wyenc.dll", "/usr/local/lib/libwyenc.so", "/usr/local/lib/libzaenc.dylib"};
//        try {
//            System.load("enc");
//        } catch (UnsatisfiedLinkError e  ) {
//            String file = null;
//            for (int i = 0; i < libFileNames.length; ++i) {
//                if ((new File(libFileNames[i])).exists()) {
//                    file = libFileNames[i];
//                    break;
//                }
//            }
//            if (file == null) {
//                throw e;
//            }
//            System.load(file);
//        }

        deployEnv = System.getProperty("DEPLOY_ENV");
        if (deployEnv == null || deployEnv.isEmpty()) {
            deployEnv = System.getenv("DEPLOY_ENV");
        }

        if (deployEnv == null || deployEnv.isEmpty()) {
            deployEnv = "test";
        }

        prefix = prefix + deployEnv + "_";
    }


    public static void main(String[] args) {
        System.out.println(prefix);
    }
}
