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



import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class CsvReaderUtil {

    private static final String DEFAULT_DELIMITER = ",";
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final int DEFAULT_BUFFER_SIZE = 8192; // 8KB

    /**
     * 读取 CSV 文件到 List
     */
    public static List<String[]> readCsv(File file) throws IOException {
        return readCsv(file, DEFAULT_DELIMITER, 0);
    }

    /**
     * 读取 CSV 文件到 List，指定分隔符
     */
    public static List<String[]> readCsv(File file, String delimiter) throws IOException {
        return readCsv(file, delimiter, 0);
    }

    private static String getStringBuilderLine(String[] listSize) {
        String oneRow = "";
        for (String one : listSize) {
            oneRow = oneRow + one + "\t";
        }
        return oneRow.substring(0, oneRow.length() - 1);
    }

    public static String readCsv2Str(InputStream inputStream, String delimiter, int skipLines) throws IOException {
        String result="";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= skipLines) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = parseLine(line, delimiter);
                String line2 = getStringBuilderLine(fields);
                result=result+line2+"\n";
            }
            result= result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static List<String[]> readCsv(InputStream inputStream, String delimiter, int skipLines) throws IOException {
        List<String[]> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= skipLines) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = parseLine(line, delimiter);
                result.add(fields);
            }
        }
        return result;
    }

    /**
     * 读取 CSV 文件到 List，跳过指定行数
     */
    public static List<String[]> readCsv(File file, String delimiter, int skipLines) throws IOException {
        List<String[]> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= skipLines) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = parseLine(line, delimiter);
                result.add(fields);
            }
        }
        log.info("Read {} lines from CSV file: {}", result.size(), file.getName());
        return result;
    }

    /**
     * 流式读取 CSV 文件，适用于大文件
     */
    public static void readCsvStream(File file, Consumer<String[]> consumer) throws IOException {
        readCsvStream(file, DEFAULT_DELIMITER, 0, consumer);
    }

    /**
     * 流式读取 CSV 文件，指定分隔符和跳过的行数
     */
    public static void readCsvStream(File file, String delimiter, int skipLines,
                                     Consumer<String[]> consumer) throws IOException {
        AtomicInteger lineCount = new AtomicInteger(0);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= skipLines) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = parseLine(line, delimiter);
                consumer.accept(fields);
                lineCount.incrementAndGet();
            }
        }
        log.info("Stream processed {} lines from CSV file: {}", lineCount.get(), file.getName());
    }

    /**
     * 批量读取 CSV 文件
     */
    public static void readCsvBatch(File file, Consumer<List<String[]>> batchConsumer) throws IOException {
        readCsvBatch(file, DEFAULT_DELIMITER, 0, DEFAULT_BATCH_SIZE, batchConsumer);
    }

    /**
     * 批量读取 CSV 文件，可配置参数
     */
    public static void readCsvBatch(File file, String delimiter, int skipLines, int batchSize,
                                    Consumer<List<String[]>> batchConsumer) throws IOException {
        List<String[]> batch = new ArrayList<>(batchSize);
        AtomicInteger totalCount = new AtomicInteger(0);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= skipLines) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = parseLine(line, delimiter);
                batch.add(fields);

                if (batch.size() >= batchSize) {
                    batchConsumer.accept(batch);
                    totalCount.addAndGet(batch.size());
                    batch = new ArrayList<>(batchSize);
                }
            }

            // 处理最后一批数据
            if (!batch.isEmpty()) {
                batchConsumer.accept(batch);
                totalCount.addAndGet(batch.size());
            }
        }
        log.info("Batch processed {} lines from CSV file: {}", totalCount.get(), file.getName());
    }

    /**
     * 使用 Java 8 Stream 读取 CSV
     */
    public static Stream<String[]> readCsvAsStream(File file) throws IOException {
        return readCsvAsStream(file, DEFAULT_DELIMITER, 0);
    }

    public static Stream<String[]> readCsvAsStream(File file, String delimiter, int skipLines) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8),
                DEFAULT_BUFFER_SIZE);

        return reader.lines()
                .skip(skipLines)
                .filter(line -> !line.trim().isEmpty())
                .map(line -> parseLine(line, delimiter))
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.error("Error closing stream", e);
                    }
                });
    }

    /**
     * 解析单行 CSV 数据
     */
    private static String[] parseLine(String line, String delimiter) {
        // 处理引号包围的字段
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 转义的双引号
                    field.append('"');
                    i++; // 跳过下一个双引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                fields.add(field.toString().trim());
                field.setLength(0); // 清空 StringBuilder
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());
        return fields.toArray(new String[0]);
    }


    /**
     * 获取 CSV 文件头
     */
    public static String[] getHeader(File file) throws IOException {
        return getHeader(file, DEFAULT_DELIMITER);
    }

    public static String[] getHeader(File file, String delimiter) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                return parseLine(headerLine, delimiter);
            }
        }
        return new String[0];
    }

    /**
     * 统计 CSV 文件行数（不包括空行和跳过的行）
     */
    public static long countLines(File file, int skipLines) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(skipLines)
                    .filter(line -> !line.trim().isEmpty())
                    .count();
        }
    }

    /**
     * 清理临时文件
     */
    public static void cleanupTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            try {
                Files.delete(tempFile.toPath());
            } catch (IOException e) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }
}
