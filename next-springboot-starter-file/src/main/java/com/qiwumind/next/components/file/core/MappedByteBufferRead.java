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

package com.qiwumind.next.components.file.core;



import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 */
@Slf4j
public class MappedByteBufferRead {

    private FileInputStream  fis;
    private FileChannel      fc;
    private MappedByteBuffer mbb;           //DirectByteBuffer
    private int              currentReadPos;
    private int              limit;
    private String           charsetName;
    /** The current line number */
    private int              lineNumber = 0;

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public MappedByteBuffer getMbb() {
        return mbb;
    }

    public MappedByteBufferRead(final File file) {
        try {
            if (!file.exists() || !file.isFile()) {
                throw new Exception("指定文件不存在或者不是一个文件");
            }
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            limit = mbb.limit();
        } catch (final Exception e) {
            log.error("MappedByteBufferRead error ={}", e);
        }
    }

    public MappedByteBufferRead(final String filePath) {
        this(new File(filePath));
        this.charsetName = "UTF-8";
    }

    public MappedByteBufferRead(final String filePath, final String charsetName) {
        this(new File(filePath));
        this.charsetName = charsetName;
    }

    /**
     * 指定每行的容量，最大字节数 如果存在行超过指定最大字节
     *
     * @param capacity
     * @return
     * @throws Exception
     */
    public String readLine(final int capacity) throws Exception {
        synchronized (this) {
            // 分配DirectByteBuffer的方法是：
            //   ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(100);
            //    如上开辟了 100 字节的直接内存空间。开辟了一段直接的内存，并不会占用 JVM 的内存空间。  DirectByteBuffer,这里注意事堆外内存，注意内存的释放
            //   ByteBuffer.allocateDirect(int capacity);//可以看到分配内存是通过unsafe.allocateMemory()来实现的，这个unsafe默认情况下java代码是没有能力可以调用到的，不过你可以通过反射的手段得到实例进而做操作，当然你需要保证的是程序的稳定性，既然叫unsafe的，就是告诉你这不是安全的，其实并不是不安全，而是交给程序员来操作，它可能会因为程序员的能力而导致不安全，而并非它本身不安全。
            ByteBuffer bb = ByteBuffer.allocate(capacity == 0 ? 1024 : capacity);//分配 HeapByteBuffer 的方法是：
            try {
                if (currentReadPos >= limit) {
                    return null;
                }
                while (currentReadPos < limit) {
                    final byte b = mbb.get();
                    currentReadPos++;
                    if (System.getProperty("line.separator").equals("\r\n") && b == 13) {
                        mbb.get();
                        currentReadPos++;
                        break;
                    } else if (b == 10 || b == 13) {
                        break;
                    } else {
                        bb.put(b);
                    }
                }

                lineNumber++;
                String arr = rightTrim(new String(bb.array(), charsetName));
                return arr;
            } catch (final Exception e) {
                throw e;
            }
        }
    }

    /**
     * 默认1024字节 每行,从第一行开始读取
     *
     * @return
     * @throws Exception
     */
    public String readLine() throws Exception {
        return readLine(0);
    }

    private String rightTrim(final String s) {
        final char[] cs = s.toCharArray();
        int pos = 0;
        for (int i = cs.length - 1; i >= 0; i--) {
            final String tostr = String.valueOf(cs[i]);
            if (tostr.trim().length() != 0) {
                pos = i;
                break;
            }
        }
        return s.substring(0, pos + 1);
    }

    public String readLine2(final int capacity) throws Exception {
        synchronized (this) {
            try {
                if (currentReadPos >= limit) {
                    return null;
                }
                //   ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(100);
                //    如上开辟了 100 字节的直接内存空间。开辟了一段直接的内存，并不会占用 JVM 的内存空间。  DirectByteBuffer
                final ByteBuffer bb = ByteBuffer.allocate(capacity == 0 ? 1024 : capacity);
                while (currentReadPos < limit) {
                    final byte b = mbb.get();
                    currentReadPos++;
                    if (System.getProperty("line.separator").equals("\r\n") && b == 13) {
                        mbb.get();
                        currentReadPos++;
                        break;
                    } else if (b == 10 || b == 13) {
                        break;
                    } else {
                        bb.put(b);
                    }
                }
                lineNumber++;
                return rightTrim(new String(bb.array(), charsetName));

            } catch (final Exception e) {
                throw e;
            }
        }
    }

    /**
     * 关闭资源，释放文件描述符和映射缓冲区
     */
    public void close() {
        try {
            // 清理 MappedByteBuffer，释放堆外内存
            if (mbb != null) {
                cleanMappedByteBuffer(mbb);
            }
        } catch (Exception e) {
            log.warn("清理 MappedByteBuffer 失败", e);
        }
        try {
            if (fc != null) {
                fc.close();
            }
        } catch (Exception e) {
            log.warn("关闭 FileChannel 失败", e);
        }
        try {
            if (fis != null) {
                fis.close();
            }
        } catch (Exception e) {
            log.warn("关闭 FileInputStream 失败", e);
        }
    }

    /**
     * 通过反射释放 MappedByteBuffer 占用的堆外内存
     */
    private void cleanMappedByteBuffer(java.nio.MappedByteBuffer buffer) {
        try {
            java.lang.reflect.Method cleanerMethod = buffer.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(buffer);
            java.lang.reflect.Method cleanMethod = cleaner.getClass().getMethod("clean");
            cleanMethod.setAccessible(true);
            cleanMethod.invoke(cleaner);
        } catch (Exception e) {
            log.warn("反射清理 MappedByteBuffer 失败", e);
        }
    }
}
