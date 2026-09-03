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

package com.qiwumind.next.components.crypto.core.datasource;

import com.qiwumind.next.components.crypto.core.Enc;
import com.qiwumind.next.components.crypto.core.EncException;
import org.springframework.util.StringUtils;

/**
 * 数据源密码解密器。
 * <p>
 * 密文格式与模块其他加解密能力保持一致：以 {@link Enc#PREFIX_ENC}（{@code enc_}）开头，
 * 形如 {@code enc_test_xxxxxxxx}（其中 {@code test} 为 {@code DEPLOY_ENV} 环境变量，默认 test）。
 * 实际 AES 运算委托 {@link Enc#decryptData(String)}，密钥/算法见 {@code AESUtil}。
 * </p>
 */
public class DatasourcePasswordDecryptor {

    /**
     * 判断给定配置值是否为本模块密文。
     */
    public static boolean isCipher(String value) {
        return StringUtils.hasText(value) && value.startsWith(Enc.PREFIX_ENC);
    }

    /**
     * 解密数据源密码。
     * <ul>
     *     <li>若入参为空或不是密文（明文配置），原样返回，保证向后兼容；</li>
     *     <li>若入参为密文，调用 {@link Enc#decryptData(String)} 解密；</li>
     *     <li>解密失败（前缀非法 / 密文被篡改 / 环境不匹配）抛出
     *         {@link DatasourcePasswordDecryptException}，且不打印明文。</li>
     * </ul>
     *
     * @param cipherText yml 中的配置值
     * @return 明文密码
     */
    public static String decrypt(String cipherText) {
        if (!isCipher(cipherText)) {
            return cipherText;
        }
        try {

            return Enc.decryptData(cipherText.trim());
        } catch (EncException e) {
            throw new DatasourcePasswordDecryptException(
                    "数据库密码密文解密失败，请确认密文由相同 DEPLOY_ENV 环境生成且未被篡改。", e);
        } catch (IllegalArgumentException e) {
            throw new DatasourcePasswordDecryptException("数据库密码密文格式非法：" + e.getMessage(), e);
        }
    }


}
