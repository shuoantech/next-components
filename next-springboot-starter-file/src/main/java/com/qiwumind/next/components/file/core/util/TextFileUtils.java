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



import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.qiwumind.next.components.file.core.enums.HeadEnum;
import com.qiwumind.next.components.file.core.valueobject.XmlFile;
import com.qiwumind.next.components.file.core.xml.XmlFileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * 文本文件处理
 *
 * @author liks 2019年4月18日 下午4:59:04
 */
public class TextFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileUtils.class);

    /**
     * @param targetfile
     * @param xmlFile
     * @return
     */
    public static List<Map<String, String>> readFile(final String targetfile, final XmlFile xmlFile) {
        final List<Map<String, String>> list = Lists.newLinkedList();
        final String charsetName = xmlFile.getXmlFileProperty().getTargetencoding();
        final File file = new File(targetfile);
        try {
            final LineIterator it = FileUtils.lineIterator(file, charsetName);
            while (it.hasNext()) {
                final String valueString = it.next();
                final Map<String, String> map = XmlFileUtil.readValue(xmlFile, valueString);
                list.add(map);
            }
        } catch (final IOException e1) {
            LOGGER.info("", e1);
        }
        return list;
    }

    /**
     * @param targetfileName 文件名称
     * @param xmlFile xml解析字段
     * @param paramMapList 每个文件保存的数据
     * @return
     */
    public static Boolean writeFile(final String targetfileName, final XmlFile xmlFile,
                                    final List<Map<String, String>> paramMapList) {
        final int count = xmlFile.getXmlFileProperty().getCount();
        if (count == Integer.MAX_VALUE) {
            writeSingleFile(targetfileName, xmlFile, paramMapList);
        } else {
            createdManyFile(targetfileName, xmlFile, paramMapList, count);
        }
        return true;
    }

    /**
     * @param targetfileName 文件名称
     * @param xmlFile xml解析字段
     * @param paramMapList 查询传入的值
     * @return
     */
    public static List<File> createdManyFile(final String targetfileName, final XmlFile xmlFile,
                                             final List<Map<String, String>> paramMapList, final int count) {
        final List<File> files = Lists.newLinkedList();
        if (CollectionUtils.isEmpty(paramMapList)) {
            files.add(new File(targetfileName));
            return files;
        }

        if (paramMapList.size() <= count) {
            final File file = writeSingleFile(targetfileName, xmlFile, paramMapList);
            files.add(file);
            return files;
        } else {
            Preconditions.checkArgument(targetfileName.contains("."), "targetfileName文件名需含有文件格式类型，且有且只允许.类型");
            final String targetfile = targetfileName.substring(0, targetfileName.lastIndexOf("."));
            final String targetfileType = targetfileName.substring(targetfileName.lastIndexOf("."));

            int page = paramMapList.size() / count;
            final int remainder = paramMapList.size() % count;
            if (remainder > 0) {
                page = page + 1;
            }
            for (int i = 0; i < page; i++) {
                int toIndex = (i + 1) * count;
                if (i + 1 == page) {
                    toIndex = paramMapList.size() - count * (page - i - 1);
                }
                final File file = writeSingleFile(targetfile + i + targetfileType, xmlFile,
                        paramMapList.subList(i * count, toIndex));
                files.add(file);
            }

        }
        return files;
    }

    /**
     * 写入文件 ---包含文件头字段，head=Y
     * 
     * @param targetfileName 文件名称
     * @param xmlFile xml解析字段
     * @param paramMapList 查询传入的值
     * @return
     */
    public static File writeSingleFile(final String targetfileName, final XmlFile xmlFile,
                                       final List<Map<String, String>> paramMapList) {
        final String charsetName = xmlFile.getXmlFileProperty().getTargetencoding();
        final HeadEnum head = xmlFile.getXmlFileProperty().getHead();

        final File file = new File(targetfileName);
        try (final OutputStream os = new FileOutputStream(file, true);
                final BufferedOutputStream bos = new BufferedOutputStream(os);
                final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bos, charsetName))) {
            if (head == HeadEnum.Y) {
                bufferedWriter.write(XmlFileUtil.fixHeadValue(xmlFile));
                bufferedWriter.newLine();
            }
            for (final Map<String, String> paramMap : paramMapList) {
                bufferedWriter.write(XmlFileUtil.fixValue(xmlFile, paramMap));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            LOGGER.info("***写文件完毕*** file={}", targetfileName);
        } catch (final IOException e1) {
            LOGGER.info("", e1);
        }
        return file;

    }

}
