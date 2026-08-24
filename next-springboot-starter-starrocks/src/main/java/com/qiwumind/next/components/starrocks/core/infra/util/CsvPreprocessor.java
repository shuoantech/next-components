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



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * CSV数据预处理工具
 */
public class CsvPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(CsvPreprocessor.class);
    
    /**
     * 验证CSV文件格式
     */
    public static boolean validateCsvFile(File csvFile, int expectedColumns) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                logger.error("CSV文件为空: {}", csvFile.getName());
                return false;
            }
            
            int actualColumns = firstLine.split(",", -1).length;
            if (actualColumns != expectedColumns) {
                logger.error("列数不匹配: 期望={}, 实际={}, 文件={}", 
                        expectedColumns, actualColumns, csvFile.getName());
                return false;
            }
            
            logger.info("CSV文件验证通过: {}, 列数: {}", csvFile.getName(), actualColumns);
            return true;
        }
    }
    
    /**
     * 修复CSV文件 - 移除HTTP头等无效数据
     */
    public static File repairCsvFile(File originalFile, int expectedColumns) throws IOException {
        File tempFile = File.createTempFile("repaired_", ".csv");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            String line;
            boolean dataStarted = false;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null) {
                // 跳过HTTP头信息
                if (line.startsWith("--") || 
                    line.startsWith("Content-") || 
                    line.trim().isEmpty()) {
                    continue;
                }
                
                // 检查列数
                String[] columns = line.split(",", -1);
                if (columns.length == expectedColumns) {
                    writer.write(line);
                    writer.newLine();
                    dataStarted = true;
                    lineCount++;
                } else if (dataStarted) {
                    logger.warn("跳过列数不匹配的行: 期望={}, 实际={}, 行: {}", 
                            expectedColumns, columns.length, line.substring(0, Math.min(100, line.length())));
                }
            }
            
            logger.info("CSV文件修复完成: 原始文件={}, 修复后行数={}", 
                    originalFile.getName(), lineCount);
            
            return tempFile;
        }
    }
    
    /**
     * 处理空值问题
     */
    public static File handleNullValues(File csvFile, Map<Integer, String> defaultValues) throws IOException {
        File tempFile = File.createTempFile("null_handled_", ".csv");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", -1);
                // 处理空值
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].isEmpty() || "NULL".equalsIgnoreCase(columns[i])) {
                        if (defaultValues.containsKey(i)) {
                            columns[i] = defaultValues.get(i);
                        } else if (i == 0) { // part_date列
                            // 从文件名或当前日期生成默认值
                            columns[i] = "\"2025-11-26\"";
                        }
                    }
                }
                
                writer.write(String.join(",", columns));
                writer.newLine();
            }
        }
        
        return tempFile;
    }
}
