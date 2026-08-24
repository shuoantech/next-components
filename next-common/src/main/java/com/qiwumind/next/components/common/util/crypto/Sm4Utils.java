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
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Base64;

/**
 * @Author 国密4工具类
 * @Description 数据加密解密
 **/
public class Sm4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    // 16-byte key for SM4
    public static final byte[] keys = new byte[]{64, 74, 104, 120, 50, 48, 50, 52, 35, 36, 37, 94, 38, 42, 33, 43};
    // 16-byte IV for CBC mode
    public static final byte[] ivs = new byte[]{64, 74, 104, 120, 50, 48, 50, 52, 35, 36, 37, 94, 38, 42, 33, 43};

    /**
     * 设置一个标识符，标识@SM4@- 开头的字符串是经过SM4加密的需要解密
     */
    public static final String SM4_PREFIX = "SM4:";

    /**
     * 对字符串进行加密
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) throws Exception {
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = encrypt(keys, ivs, dataToEncrypt);
        return java.util.Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String encrypt(byte[] key, byte[] iv, String data) throws Exception {
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = encrypt(key, iv, dataToEncrypt);
        return java.util.Base64.getEncoder().encodeToString(encryptedData);
    }

    public static byte[] encrypt(byte[] key, byte[] iv, byte[] data) throws Exception {
        SM4Engine engine = new SM4Engine();
        CBCModeCipher cbcBlockCipher = CBCBlockCipher.newInstance(engine);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcBlockCipher);
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(true, params);
        byte[] temp = new byte[cipher.getOutputSize(data.length)];
        int len = cipher.processBytes(data, 0, data.length, temp, 0);
        len += cipher.doFinal(temp, len);

        byte[] out = new byte[len];
        System.arraycopy(temp, 0, out, 0, len);
        return out;
    }

    /**
     * 对字符串进行解密
     *
     * @param encryptValue
     * @return
     */
    public static String decrypt(String encryptValue) throws Exception {
        // 解密时，需要去除加密标识符
        byte[] decryptedData = decrypt(keys, ivs,Base64.getDecoder().decode(encryptValue));

        return new String(decryptedData);
    }

    public static byte[] decrypt(byte[] key, byte[] iv, byte[] data) throws Exception {
        SM4Engine engine = new SM4Engine();
        CBCModeCipher cbcBlockCipher = CBCBlockCipher.newInstance(engine);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcBlockCipher);
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(false, params);
        byte[] temp = new byte[cipher.getOutputSize(data.length)];
        int len = cipher.processBytes(data, 0, data.length, temp, 0);
        len += cipher.doFinal(temp, len);
        byte[] out = new byte[len];
        System.arraycopy(temp, 0, out, 0, len);
        return out;
    }


    //SM4-加密
    public static String encryptSM4(String key, String plaintext) {
        //指明加密算法和秘钥
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", HexUtil.decodeHex(key));
        return sm4.encryptHex(plaintext);
    }

    //SM4-解密
    public static String decryptSM4(String key, String ciphertext) {
        //指明加密算法和秘钥
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", HexUtil.decodeHex(key));
        String decryptStr = sm4.decryptStr(ciphertext);
        return decryptStr;
    }


    public static void main(String[] args) throws Exception {
        byte[] key = "0123456789abcdef".getBytes(); // 16-byte key for SM4
        byte[] iv = "abcdef9876543210".getBytes(); // 16-byte IV for CBC mode
        byte[] dataToEncrypt = "Hello, Bouncy Castle SM4!".getBytes();

        byte[] encryptedData = encrypt(key, iv, dataToEncrypt);
        System.out.println("Encrypted Data: " + java.util.Base64.getEncoder().encodeToString(encryptedData));

        byte[] decryptedData = decrypt(key, iv, encryptedData);
        System.out.println("Decrypted Data: " + new String(decryptedData));

        String sss = encrypt("hermxx@0609#1");
        System.out.println("Encrypted Data2222: " + sss);

        System.out.println("decrypt Data2222: " + decrypt("WYSM4:2HiIEECMDpHIyL2z+xC4Yg==".substring(6)));

        String resp = "16f4d106102a08f03dad9f0e0ee3525b60f6b5954c0525e7738aa9e7e2a47b282560ed82c4f5a8ebb477a7fd3cac2d298af905fad24ac4d1f3f0e644867645a69382acfd8ac66f08beb9eab42cb1a653d4d900bd258e41b4ee25567e4facc9495dc7fb6bdf612b47652b0f925f08911d630ef62d4a9d371d87057866034855d2";
        String r = decryptSM4("CE39D76F1B318B35F7467346F3D358D4", resp);
        System.out.println(r);

    }
}
