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

package com.qiwumind.next.components.starrocks.core.infra.util;



import java.util.ArrayList;
import java.util.List;

/**
 * 类CollectUtils.java的实现描述：集合处理
 * 
 * @author hege 2021年8月13日 上午10:56:02
 */
public class CollectUtils {

    /**
     * 切分list
     * 
     * @param sourceList
     * @param groupSize 每组多少个
     * @return
     */
    public static <T> List<List<T>> splitList(final List<T> sourceList, final int groupSize) {
        final int length = sourceList.size();
        // 计算可以分成多少组
        final int num = (length + groupSize - 1) / groupSize;
        final List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            final int fromIndex = i * groupSize;
            // 结束位置
            final int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }

    /**
     * @param args
     */
    /*
     * public static void main(final String[] args) { final List<String>
     * sourceList= Lists.newArrayList(); final int num = (1 + 40 - 1) / 40;
     * for(int i=0;i<num;i++){ sourceList.add("source"+i); }
     * System.out.println(sourceList); final List<List<String>>
     * ll=splitList(sourceList, 20); for(int i=0;i<ll.size();i++){
     * System.out.println(ll.get(i).size()); } }
     */
}
