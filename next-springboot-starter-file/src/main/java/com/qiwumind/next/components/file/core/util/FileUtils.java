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

package com.qiwumind.next.components.file.core.util;



import com.qiwumind.next.components.file.core.valueobject.XmlFile;
import com.qiwumind.next.components.file.core.xml.XmlFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

/**
 * @author liks 2019年7月8日 下午2:21:03
 */
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * @param targetfile
     * @param xmlFile
     * @param paramMapList 每个文件保存的数据
     * @return
     */
    public static File createdFile(final String targetfile, final XmlFile xmlFile,
                                   final List<Map<String, String>> paramMapList) {
        return createdSingleFile(targetfile, xmlFile, paramMapList);

    }

    /**
     * @param targetfileName 文件名称
     * @param xmlFile xml解析字段
     * @param paramMapList 查询传入的值
     * @return
     */
    public static File createdSingleFile(final String targetfileName, final XmlFile xmlFile,
                                         final List<Map<String, String>> paramMapList) {
        final String charsetName = xmlFile.getXmlFileProperty().getTargetencoding();
        final File file = new File(targetfileName);
        try (final OutputStream os = new FileOutputStream(file);
                final BufferedOutputStream bos = new BufferedOutputStream(os);
                final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bos, charsetName))) {
            for (final Map<String, String> paramMap : paramMapList) {
                bufferedWriter.write(XmlFileUtil.fixValue(xmlFile, paramMap));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();

        } catch (final IOException e1) {
            LOGGER.info("", e1);
        }
        return file;
    }

    /**
     * 利用NIO的Channel通道传输 ---文件copy ，效率> Files.copy >经典文件流copy 2.2G文件 19829ms
     * <37049 ms <未测
     *
     * @param sourceFile
     * @param targetFile
     */
    public static void copy(final File sourceFile, final File targetFile) {
        try (RandomAccessFile fileInputStream = new RandomAccessFile(sourceFile, "r");
                RandomAccessFile fileOutputStream = new RandomAccessFile(targetFile, "rw");
                final FileChannel channelSource = fileInputStream.getChannel();
                final FileChannel channelTarget = fileOutputStream.getChannel();) {
            long totalsize = channelSource.size();
            long position = 0;
            while (totalsize > 0) {
                final long bufferSize = channelSource.transferTo(position, totalsize, channelTarget);
                position += bufferSize;
                totalsize -= bufferSize;
            }
        } catch (final Exception e) {
            LOGGER.info("", e);
        }
    }

    /**
     * 读文件
     *
     * @param filename 文件全路径
     * @param callback 读取的数据处理函数
     */
//    public static void readFile(String filename, String charsetName, CallBack<LineNumberString> callback) {
//        MappedByteBufferRead mbbrlutil = null;
//        try {
//            mbbrlutil = new MappedByteBufferRead(filename, charsetName);
//            String line = null;
//            while ((line = mbbrlutil.readLine()) != null) {
//                final LineNumberString lineNumberString = new LineNumberString();
//                lineNumberString.setLine(line);
//                lineNumberString.setLineNumber(mbbrlutil.getLineNumber());
//                callback.call(lineNumberString);
//            }
//        } catch (final Exception e) {
//            LOGGER.error("readFile error ={}", e);
//            IOUtil.closeMappedByteBuffer(mbbrlutil.getMbb());
//
//        }
//    }

//    @FunctionalInterface
//    public interface CallBack<T> {
//        void call(T t);
//    }

    public static class LineNumberString {
        private String line;
        private int    lineNumber = 0;

        /**
         * @return the line
         */
        public String getLine() {
            return line;
        }

        /**
         * @param line the line to set
         */
        public void setLine(final String line) {
            this.line = line;
        }

        /**
         * @return the lineNumber
         */
        public int getLineNumber() {
            return lineNumber;
        }

        /**
         * @param lineNumber the lineNumber to set
         */
        public void setLineNumber(final int lineNumber) {
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return "LineNumberString [line=" + line + ", lineNumber=" + lineNumber + "]";
        }

    }

}
