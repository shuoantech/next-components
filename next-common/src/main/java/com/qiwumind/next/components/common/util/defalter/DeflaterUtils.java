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

package com.qiwumind.next.components.common.util.defalter;


import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.common.util.vintageloss.VintageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class DeflaterUtils {

    /**
     * 压缩 ：zipString 方法接收一个未压缩的字符串，
     * 使用 Deflater 类对其进行压缩，并将压缩后的字节数组通过 Base64 编码转换为字符串，以便在网络传输或存储中使用。
     */
    public static String zipString(String unzipString) {
        /**
         *     https://www.yiibai.com/javazip/javazip_deflater.html#article-start
         *     0 ~ 9 压缩等级 低到高
         *     public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别。
         *     public static final int BEST_SPEED = 1;                  压缩级别最快的压缩。
         *     public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别。
         *     public static final int DEFAULT_STRATEGY = 0;            默认压缩策略。
         *     public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)。
         *     public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据。
         *     public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。
         *     public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略。
         *     public static final int NO_COMPRESSION = 0;              不压缩的压缩级别。
         *     public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式。
         *     public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
         */
        if (StringUtils.isBlank(unzipString)) {
            return unzipString;
        }
        //使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        //设置压缩输入数据。
        deflater.setInput(unzipString.getBytes());
        //当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();

        final byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

        while (!deflater.finished()) {
            //压缩输入数据并用压缩数据填充指定的缓冲区。
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        //关闭压缩器并丢弃任何未处理的输入。
        deflater.end();

//        return new BASE64Encoder().encodeBuffer(outputStream.toByteArray());
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());

    }

    /**
     * 解压缩：
     * unzipString 方法接收一个经过压缩和 Base64 编码的字符串，
     * 首先使用 Base64 解码将其转换为字节数组，然后使用 Inflater 类进行解压缩，最终返回解压缩后的字符串。
     */
    public static String unzipString(String zipString) {
//        byte[] decode = new byte[0];
//        try {
//            decode = new sun.misc.BASE64Decoder().decodeBuffer(zipString);
//        } catch (IOException e) {
//            log.error("unzip error", e);
//        }
        byte[] decode = Base64.getDecoder().decode(zipString);
        //创建一个新的解压缩器
        Inflater inflater = new Inflater();
        //设置解压缩的输入数据。
        inflater.setInput(decode);

        final byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        try {
            //finished() 如果已到达压缩数据流的末尾，则返回true。
            while (!inflater.finished()) {
                //将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            log.error("unzip error", e);
            return null;
        } finally {
            //关闭解压缩器并丢弃任何未处理的输入。
            inflater.end();
        }

        return outputStream.toString();
    }

    public static void main(String[] args) {
        VintageUtils.VintageRepay vr = new VintageUtils.VintageRepay();
        VintageUtils.vintage(1, 78, vr);
        System.out.println("vintage 1=" + vr);
        String sr = zipString(BaseDTO.toJson(vr)) ;

        System.out.println("zipString=" + sr);

        System.out.println("unzipString=" + unzipString(sr));

    }

}
