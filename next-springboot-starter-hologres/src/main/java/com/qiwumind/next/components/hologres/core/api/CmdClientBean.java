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

package com.qiwumind.next.components.hologres.core.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import com.qiwumind.next.components.hologres.core.infra.util.CollectUtils;

/**
 * Hologres 命令客户端。
 * <p>
 * 提供批量写入、删除、数据导出（OSS）等操作。
 *
 * @author KS.Li
 */
public class CmdClientBean {

    private static final int DEFAULT_BATCH_SIZE = 256;

    private final DataSource dataSource;

    public CmdClientBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * INSERT INTO SELECT 操作。
     *
     * @param schemaName schema 名称
     * @param table      目标表名
     * @param sql        SELECT 查询语句，返回字段需与 INSERT 目标表字段顺序一致
     */
    public void batchInsertSelect(String schemaName, String table, String sql) throws Exception {
        String insertSql = "INSERT INTO %s.%s %s".formatted(schemaName, table, sql);
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.executeUpdate();
        }
    }

    /**
     * 批量写入数据。
     *
     * @param schemaName schema 名称
     * @param table      目标表名
     * @param data       数据列表，Map 中按表结构字段顺序填充
     */
    public void batchInsert(String schemaName, String table, List<Map<Integer, Object>> data) throws Exception {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        int size = data.get(0).size();
        StringBuilder placeholders = new StringBuilder("?");
        for (int i = 1; i < size; i++) {
            placeholders.append(",?");
        }
        String sql = "INSERT INTO %s.%s VALUES (%s)".formatted(schemaName, table, placeholders);
        doBatchExecute(sql, data);
    }

    /**
     * 通过 Prepared Statement 批量写入数据。
     *
     * @param sql  INSERT 语句（含占位符）
     * @param data 数据列表，Map 中按表结构字段顺序填充
     */
    public void batchInsert(String sql, List<Map<Integer, Object>> data) throws Exception {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        doBatchExecute(sql, data);
    }

    private void doBatchExecute(String sql, List<Map<Integer, Object>> data) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            List<List<Map<Integer, Object>>> splitList = CollectUtils.splitList(data, DEFAULT_BATCH_SIZE);
            for (List<Map<Integer, Object>> subList : splitList) {
                for (Map<Integer, Object> row : subList) {
                    for (Map.Entry<Integer, Object> entry : row.entrySet()) {
                        stmt.setObject(entry.getKey(), entry.getValue());
                    }
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    /**
     * 根据条件删除数据。
     *
     * @param sql       DELETE 语句
     * @param containPk true 时 SQL WHERE 条件中必须且仅包含全部主键（开启 fixed dispatcher 提升效率）
     * @return 影响的行数
     */
    public int delete(String sql, boolean containPk) throws SQLException {
        if (containPk) {
            sql = "set hg_experimental_enable_fixed_dispatcher_for_delete =on;\n" + sql;
        }
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    /**
     * 将 SELECT 结果数据写入 OSS（CSV 格式）。
     *
     * @param sql             查询 SQL
     * @param accessKeyId     OSS AccessKeyId
     * @param accessKeySecret OSS AccessKeySecret
     * @param endpoint        OSS Endpoint
     * @param bucketName      OSS Bucket 名称
     * @param dirName         OSS 目录名
     * @param fileName        CSV 文件名
     * @param batchSize       批量写入条数
     * @return 写入文件的数据条数
     */
    public int copyDataCsv2Oss(String sql, String accessKeyId, String accessKeySecret, String endpoint,
                                String bucketName, String dirName, String fileName, Long batchSize)
            throws SQLException {
        String template = "COPY (%s) TO PROGRAM 'hg_dump_to_oss"
                + " --AccessKeyId %s --AccessKeySecret %s"
                + " --Endpoint %s --BucketName %s --DirName %s --FileName %s --BatchSize %s'"
                + " (DELIMITER ',', HEADER true, FORMAT CSV)";
        String exeSql = template.formatted(sql, accessKeyId, accessKeySecret, endpoint,
                bucketName, dirName, fileName, batchSize);
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(exeSql)) {
            return pst.executeUpdate();
        }
    }
}
