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

package com.qiwumind.next.components.common.util.string;



public class ByteUtils {

    public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f'};

    private ByteUtils() {
        // Utility class
    }

    public static byte[] fromHexString(String s) {
        int len = s.length();
        //
        // // Data length must be even
        // if (len % 2 != 0) {
        // throw new IllegalArgumentException("Hex string has an odd number of
        // characters");
        // }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String toHexString(byte[] input) {
        StringBuilder sb = new StringBuilder();
        for (byte b : input) {
            sb.append(HEX_CHARS[(b >>> 4) & 0x0f]);
            sb.append(HEX_CHARS[b & 0x0f]);
        }
        return sb.toString();
    }


    public static String toHexString(byte[] input, String prefix, String separator) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < input.length; i++) {
            sb.append(HEX_CHARS[(input[i] >>> 4) & 0x0f]);
            sb.append(HEX_CHARS[input[i] & 0x0f]);
            if (i < input.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

}
