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

package com.qiwumind.next.components.starrocks.core.api;




import com.qiwumind.next.components.starrocks.core.dto.ResultTableComment;
import com.qiwumind.next.components.starrocks.core.dto.TableField;
import com.qiwumind.next.components.starrocks.core.infra.util.KMP;

import javax.sql.DataSource;

import java.sql.*;
import java.util.*;

/**
 * 查询相关操作 - 通过FE的9030端口
 */
public class StarRocksQueryBean {

    private DataSource dataSource;

    public StarRocksQueryBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 聚合查询使用，可以采用aggrQuery（sql）替代
     *
     * @param sql
     * @param fieldAlias sql的返回字段别名
     * @return
     * @throws SQLException
     */
    @Deprecated
    public Map<String, Object> aggrQuery(String sql, List<String> fieldAlias) throws SQLException {
        if (fieldAlias == null || fieldAlias.isEmpty()) {
            throw new SQLException("sql result field Alias is null");
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
     * 聚合查询使用
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public Map<String, Object> aggrQuery(String sql) throws SQLException {

        Map<String, Object> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData rsmd = pst.getMetaData();// 获取结果集元数据
            int count = rsmd.getColumnCount(); // 获取结果集元数据列数

            List<String> fieldAlias = new ArrayList<>();
            // 遍历属性名称
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
     * 行信息查询使用,大数据量查询时请优化好sql ,可以采用listQuery（sql）替代
     *
     * @param sql
     * @param fields sql的返回字段集合
     * @return
     * @throws SQLException
     */
    @Deprecated
    public List<Map<String, Object>> listQuery(String sql, List<String> fields) throws SQLException {
        if (fields == null || fields.isEmpty()) {
            throw new SQLException("sql result field Alias is null");
        }
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            Map<String, Object> map = null;
            while (rs.next()) {
                map = new LinkedHashMap<>();
                for (String field : fields) {
                    map.put(field, rs.getString(field));
                }
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 行信息查询使用,大数据量查询时请优化好sql
     *
     * @param sql 默认查询500条
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> listQuery(String sql) throws SQLException {
        sql = sql.trim();
        String tmpsql = sql.toLowerCase();
        if (KMP.KmpSearch(tmpsql, "limit") == -1) {
            sql += " limit 500 OFFSET 0  ";
        }

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData rsmd = pst.getMetaData();// 获取结果集元数据
            int count = rsmd.getColumnCount(); // 获取结果集元数据列数

            List<String> fields = new ArrayList<>();
            // 遍历属性名称
            for (int i = 1; i <= count; i++) {
                fields.add(rsmd.getColumnName(i));
            }
            Map<String, Object> map = null;
            while (rs.next()) {
                map = new LinkedHashMap<>();
                for (String field : fields) {
                    map.put(field, rs.getString(field));
                }
                list.add(map);
            }
        }
        return list;
    }


    /**
     * 查询所有的表表名
     *
     * @param schemaname
     * @throws SQLException
     */
//	public List<ResultTableViewField.TableView> querytables(String schemaname) throws SQLException {
//		String sql = TableViewEnum.TABLE.getSql();
//		sql = String.format(sql, schemaname);
//		List<ResultTableViewField.TableView> result = new ArrayList<>();
//		try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
//			ResultSetMetaData rsmd = pst.getMetaData();// 获取结果集元数据
//			ResultSet rs = pst.executeQuery();
////            int count = rsmd.getColumnCount(); //获取结果集元数据列数
////            System.out.println("表一共有：" + count + "列");
//			// 遍历属性名称
//			ResultTableViewField.TableView resultSetField = null;
//			List<String> fields = Arrays.asList(rsmd.getColumnName(1), rsmd.getColumnName(2));
//			while (rs.next()) {
//				resultSetField = new ResultTableViewField.TableView();
//				resultSetField.setSchemanme(rs.getString("schemaname"));
//				resultSetField.setTablename(rs.getString("tablename"));
//				result.add(resultSetField);
//
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * 查询视图的表名
//	 *
//	 * @param schemaname
//	 * @throws SQLException
//	 */
//	public List<ResultTableViewField.TableView> queryviews(String schemaname) throws SQLException {
//		String sql = TableViewEnum.VIEW.getSql();
//		sql = String.format(sql, schemaname);
//		List<ResultTableViewField.TableView> result = new ArrayList<>();
//		try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
//			ResultSetMetaData rsmd = pst.getMetaData();// 获取结果集元数据
//			ResultSet rs = pst.executeQuery();
////            int count = rsmd.getColumnCount(); //获取结果集元数据列数
////            System.out.println("表一共有：" + count + "列");
//			// 遍历属性名称
//			ResultTableViewField.TableView resultSetField = null;
//			List<String> fields = Arrays.asList(rsmd.getColumnName(1), rsmd.getColumnName(2));
//			while (rs.next()) {
//				resultSetField = new ResultTableViewField.TableView();
//				resultSetField.setSchemanme(rs.getString("schemaname"));
//				resultSetField.setTablename(rs.getString("viewname"));
//				result.add(resultSetField);
//
//			}
//		}
//		return result;
//	}


    /**
     * 查询表结构和字段注释
     *
     * @param schemaname
     * @throws SQLException
     */
    public List<TableField> queryFields(String schemaname, String tablename)
            throws SQLException {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT " +
                "FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                "ORDER BY ORDINAL_POSITION";

        sql = String.format(sql, schemaname, tablename);
        List<TableField> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            // 遍历属性名称
            TableField resultSetField = null;
            while (rs.next()) {
                resultSetField = new TableField();
                resultSetField.setName(rs.getString("field"));
                resultSetField.setType(rs.getString("type"));
                resultSetField.setComment(rs.getString("comment"));
                result.add(resultSetField);
            }
        }
        return result;
    }

    /**
     * 检查表是否存在
     */
    public boolean tableExists(String schemaname, String tablename) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        sql = String.format(sql, schemaname, tablename);

        List<Map<String, Object>> result = this.listQuery(sql);

        return !result.isEmpty() && (Long) result.get(0).get("count") > 0;
    }

    /**
     * 查询表注释
     *
     * @param schemaname
     * @param tablename
     * @return
     * @throws SQLException
     */
//	public ResultTableComment queryTableComment(String schemaname, String tablename) throws SQLException {
//		String sql = " SELECT a.oid,a.relname AS name,b.description AS comment "
//				+ " FROM pg_class a LEFT OUTER JOIN pg_description b ON b.objsubid=0 AND a.oid = b.objoid "
//				+ " WHERE a.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='%s') "
//				+ " and a.relname = '%s' ORDER BY a.relname ";
//		sql = String.format(sql, schemaname, tablename);
//		ResultTableComment result = new ResultTableComment();
//		try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
////            ResultSetMetaData rsmd = pst.getMetaData();//获取结果集元数据
//			ResultSet rs = pst.executeQuery();
//			// int count = rsmd.getColumnCount(); //获取结果集元数据列数
//			// System.out.println("表一共有：" + count + "列");
//			// 遍历属性名称
////            Arrays.asList(rsmd.getColumnName(1), rsmd.getColumnName(2));
//			while (rs.next()) {
//				result.setName(rs.getString("name"));
//				result.setComment(rs.getString("comment"));
//			}
//		}
//		return result;
//	}


    /**
     * @Author: mkq
     * @Desc: 获取查询sql结果集中的列名
     **/
    public List<String> listQueryColumn(String sql) throws SQLException {
        String exeSql = sql.trim().toLowerCase();
        List<String> ColumnList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(exeSql)) {
            /** 获取结果集元数据*/
            ResultSetMetaData rsmd = pst.getMetaData();
            /** 获取结果集元数据列数*/
            int count = rsmd.getColumnCount();
            /** 遍历属性信息*/
            for (int i = 1; i <= count; i++) {
                ColumnList.add(rsmd.getColumnName(i));
            }
        }
        return ColumnList;
    }
}
