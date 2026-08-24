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

package com.qiwumind.next.components.common.util;


import org.springframework.util.DigestUtils;

import java.util.UUID;

public class SplitRdsUtils {
    public static void main(final String[] args) {
        //4库256表  ft_zaxd_core_account_02.fin_biz_account_0131
        final int i = 57281018;
        final int db = i % 256;
        final int tb = i % 256 / 64; //4库256表，每库64个表

        System.out.println("***分库=" + tb + "  ***分表=" + db);

        System.out.println("***分库=" + schema(57281018L, 4, 256) + " ***分表=" + table(57281018L, 256));

//        final long hash = "ZYZAD1001002".hashCode();
//        System.out.println("***分库=" + schema(hash, 4, 256) + " ***分表=" + table(hash, 256));
//        System.out.println("***分库=" + schema("ZYZAD1001002", 4, 256) + " ***分表=" + table("ZYZAD1001002", 256));
//        //        ***分库=2  ***分表=131 DataEase@123456 504c8c8dfcbbe5b50d676ad65ef43909


        String fingerprint = DigestUtils.md5DigestAsHex("Bi@1weiya".getBytes());

        System.out.println("DataEase@123456=  " + fingerprint);
        System.out.println(UUID.randomUUID().toString());



    }

    /**
     * 分库schema <br/>
     * #db=value%总分表数/每个库的分表数量
     *
     * @param value
     */
    public static String schema(final Long value, final int dbCount, final int tablecount) {
        final long mod = value % tablecount / (tablecount / dbCount);
        return String.format("%02d", mod);
    }

    /**
     * 分表table <br/>
     * #tb=value%总分表数 <br/>
     *
     * @param value
     * @param tablecount
     * @return
     */
    public static String table(final Long value, final int tablecount) {
        final long mod = value % tablecount;
        return String.format("%04d", mod);
    }

    /**
     * @param value
     * @param dbCount
     * @param tablecount
     * @return
     */
    public static String schema(final String value, final int dbCount, final int tablecount) {
        return schema(Long.valueOf(value.hashCode()), dbCount, tablecount);
    }

    /**
     * @param value
     * @param tablecount
     * @return
     */
    public static String table(final String value, final int tablecount) {
        return table(Long.valueOf(value.hashCode()), tablecount);
    }
}
