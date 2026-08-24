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




import com.qiwumind.next.components.common.exception.BusinessRuntimeException;

/**
 * DecryptSecretKey
 */
public class DecryptKeyUtil {

    public static String decryptSecretKey(String secretKey) {
        try {
            if (secretKey.startsWith(Enc.PREFIX_ENC)) {
                if (secretKey.startsWith(Enc.PREFIX_ENC+"local_")) {
                    // 支持 local
                    String tmpSecretKey = secretKey.substring(secretKey.indexOf("_", 4));
                    return Enc.decryptData(Enc.PREFIX_ENC+"local_" + tmpSecretKey.trim());
                } else {
                    return Enc.decryptData(secretKey.trim());
                }
            } else {
                return secretKey;
            }
        } catch (EncException e) {
            throw new BusinessRuntimeException("999999","[ configcenter] decrypt secretKey failed", e);
        }
    }

    public static String encryptData(String secretKey) {
        try {
            return Enc.encryptData(secretKey.trim());
        } catch (EncException e) {
            throw new BusinessRuntimeException("999999","[configcenter] decrypt secretKey failed", e);
        }
    }

    public static void main(String args[]) {
        String encryData = encryptData("test11111");
        System.out.println("encrypt == " + encryData);

        String decrypt = decryptSecretKey("enc_test_A5009761B9B67DAC809CC17292971D4F5E2E85055EB652350EDE91F8F0E10055");
        System.out.println("decrypt == " + decrypt);
    }
}
