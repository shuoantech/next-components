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

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import com.qiwumind.next.components.hologres.core.infra.enums.TableViewEnum;
import com.qiwumind.next.components.hologres.core.infra.util.KMP;
import com.qiwumind.next.components.hologres.core.model.ResultTableComment;
import com.qiwumind.next.components.hologres.core.model.ResultTableViewField;

/**
 * Hologres 查询客户端。
 * <p>
 * 提供聚合查询、列表查询、元数据查询等功能。
 *
 * @author KS.Li
 */
public class QueryClientBean {

    private static final int DEFAULT_LIMIT = 500;

    private final DataSource dataSource;

    public QueryClientBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 聚合查询（需指定返回字段别名）。
     *
     * @deprecated 请使用 {@link #aggrQuery(String)} 替代
     */
    @Deprecated
    public Map<String, Object> aggrQuery(String sql, List<String> fieldAlias) throws SQLException {
        if (fieldAlias == null || fieldAlias.isEmpty()) {
            throw new SQLException("查询结果字段别名不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                for (String field : fieldAlias) {
                    map.put(field, rs.getString(field));
                }
            }
        }
        return map;
    }

    /**
     * 聚合查询（自动获取返回字段）。
     */
    public Map<String, Object> aggrQuery(String sql) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData rsmd = pst.getMetaData();
            int count = rsmd.getColumnCount();
            List<String> fieldAlias = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                fieldAlias.add(rsmd.getColumnName(i));
            }
            while (rs.next()) {
                for (String field : fieldAlias) {
                    map.put(field, rs.getString(field));
                }
            }
        }
        return map;
    }

    /**
     * 列表查询（需指定返回字段）。
     *
     * @deprecated 请使用 {@link #listQuery(String)} 替代
     */
    @Deprecated
    public List<Map<String, Object>> listQuery(String sql, List<String> fields) throws SQLException {
        if (fields == null || fields.isEmpty()) {
            throw new SQLException("查询结果字段别名不能为空");
        }
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String field : fields) {
                    map.put(field, rs.getString(field));
                }
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 列表查询（自动获取返回字段，默认限制 500 条）。
     */
    public List<Map<String, Object>> listQuery(String sql) throws SQLException {
        sql = sql.trim();
        String tmpSql = sql.toLowerCase();
        if (KMP.kmpSearch(tmpSql, "limit") == -1) {
            sql += " LIMIT %d OFFSET 0".formatted(DEFAULT_LIMIT);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData rsmd = pst.getMetaData();
            int count = rsmd.getColumnCount();

            List<String> fields = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                fields.add(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String field : fields) {
                    map.put(field, rs.getString(field));
                }
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 查询指定 schema 下的所有表名。
     */
    public List<ResultTableViewField.TableView> queryTables(String schemaName) throws SQLException {
        List<ResultTableViewField.TableView> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(TableViewEnum.TABLE.getSql())) {
            pst.setString(1, schemaName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // 使用无参构造 + setter
                ResultTableViewField.TableView tableView = new ResultTableViewField.TableView();
                tableView.setSchemanme(rs.getString("schemaname"));
                tableView.setTablename(rs.getString("tablename"));
                result.add(tableView);
            }
        }
        return result;
    }
    /**
     * 查询指定 schema 下的所有视图名。
     */
    public List<ResultTableViewField.TableView> queryViews(String schemaName) throws SQLException {
        List<ResultTableViewField.TableView> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(TableViewEnum.VIEW.getSql())) {
            pst.setString(1, schemaName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // 使用无参构造 + setter
                ResultTableViewField.TableView tableView = new ResultTableViewField.TableView();
                tableView.setSchemanme(rs.getString("schemaname"));
                tableView.setTablename(rs.getString("viewname"));
                result.add(tableView);
            }
        }
        return result;
    }

    /**
     * 查询表字段结构和注释。
     */
    public List<ResultTableViewField.TableField> queryFields(String schemaName, String tableOrViewName)
            throws SQLException {
        String sql = """
                SELECT a.attnum, a.attname AS field, t.typname AS type, a.attlen AS length,
                       a.atttypmod AS lengthvar, a.attnotnull AS notnull, b.description AS comment
                FROM pg_class c, pg_attribute a
                LEFT OUTER JOIN pg_description b ON a.attrelid = b.objoid AND a.attnum = b.objsubid,
                     pg_namespace s, pg_type t
                WHERE c.relname = ? AND s.nspname = ? AND a.attnum > 0
                  AND a.attrelid = c.oid AND a.atttypid = t.oid AND s.oid = c.relnamespace
                ORDER BY a.attnum
                """;
        List<ResultTableViewField.TableField> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, tableOrViewName);
            pst.setString(2, schemaName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result.add(new ResultTableViewField.TableField(
                        rs.getString("field"), rs.getString("type"), rs.getString("comment")));
            }
        }
        return result;
    }

    /**
     * 查询表注释。
     */
    public ResultTableComment queryTableComment(String schemaName, String tableName) throws SQLException {
        String sql = """
                SELECT a.oid, a.relname AS name, b.description AS comment
                FROM pg_class a
                LEFT OUTER JOIN pg_description b ON b.objsubid = 0 AND a.oid = b.objoid
                WHERE a.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = ?)
                  AND a.relname = ?
                ORDER BY a.relname
                """;
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, schemaName);
            pst.setString(2, tableName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new ResultTableComment(rs.getString("name"), rs.getString("comment"));
            }
        }
        return new ResultTableComment(null, null);
    }

    /**
     * 获取查询 SQL 结果集的列名列表。
     */
    public List<String> listQueryColumn(String sql) throws SQLException {
        String exeSql = sql.trim().toLowerCase();
        List<String> columnList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(exeSql)) {
            ResultSetMetaData rsmd = pst.getMetaData();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                columnList.add(rsmd.getColumnName(i));
            }
        }
        return columnList;
    }
}
