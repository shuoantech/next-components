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

package com.qiwumind.next.components.sequence.single.util;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;

import javax.sql.DataSource;

import com.qiwumind.next.components.sequence.single.exception.SingleSequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SeqUtil {
    private static final Logger log                     = LoggerFactory.getLogger(SeqUtil.class);
    private static final String selectInitLockForUpdate = "select `value` from sequence_init_lock where id = 1 for update";

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                log.debug("Could not close JDBC Statement", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC Statement", e);
            }
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                log.debug("Could not close JDBC Connection", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC Connection", e);
            }
        }
    }

    public static String getSeqInsertSql(String tableName, String nameColumnName, String valueColumnName,
                                         String gmtCreate, String gmtModified) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("insert into ").append(tableName).append("(");
        buffer.append(nameColumnName).append(",");
        buffer.append(valueColumnName).append(",");
        buffer.append(gmtModified).append(",");
        buffer.append(gmtCreate).append(") values(?,?,?,?)");
        return buffer.toString();
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                log.debug("Could not close JDBC ResultSet", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC ResultSet", e);
            }
        }
    }

    public static String getSeqSelectSql(String tableName, String nameColumnName, String valueColumnName) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("select ").append(valueColumnName);
        buffer.append(" from ").append(tableName);
        buffer.append(" where ").append(nameColumnName).append(" = ?");
        return buffer.toString();
    }

    public static String getSeqUpdateSql(String tableName, String nameColumnName, String valueColumnName,
                                         String gmtModified) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("update ").append(tableName);
        buffer.append(" set ").append(valueColumnName).append(" = ? ,");
        buffer.append(gmtModified).append(" = ? ");
        buffer.append(" where ");
        buffer.append(nameColumnName).append(" = ? and ");
        buffer.append(valueColumnName).append(" = ? ");
        return buffer.toString();
    }

    public static int updateSeqValue(DataSource dataSource, String updateSql, long newValue, String seqName,
                                     long oldValue) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(updateSql);
            stmt.setLong(1, newValue);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, seqName);
            stmt.setLong(4, oldValue);
            int affectedRows = stmt.executeUpdate();
            return affectedRows;
        } catch (SQLException e) {
            throw new SingleSequenceException(e);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(conn);
        }
    }

    public static Long selectSeqValue(DataSource dataSource, String selectSql, String seqName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, seqName);
            rs = stmt.executeQuery();
            boolean hasNext = rs.next();
            //            Long localLong;
            if (!hasNext) {
                return null;
            }
            return Long.valueOf(rs.getLong(1));
        } catch (SQLException e) {
            throw new SingleSequenceException(e);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(conn);
        }
    }

    public static Long selectSeqValueNotCloseConnection(Connection conn, String selectSql, String seqName) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, seqName);
            rs = stmt.executeQuery();
            boolean hasNext = rs.next();
            //            Long localLong;
            if (!hasNext) {
                return null;
            }
            return Long.valueOf(rs.getLong(1));
        } catch (SQLException e) {
            throw new SingleSequenceException(e);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public static int insertSeq(DataSource dataSource, String insertSql, String seqName, long startValue)
            throws SingleSequenceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, seqName);
            stmt.setLong(2, startValue);
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, timeStamp);
            stmt.setTimestamp(4, timeStamp);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SingleSequenceException(
                        "faild to auto adjust init value at  " + seqName + " update affectedRow =0");
            }

            if (log.isDebugEnabled()) {
                log.debug("插入初值:" + seqName + ", value:" + startValue);
            }
            return affectedRows;
        } catch (SQLException e) {
            log.error("由于SQLException,插入初值自适应失败！dbGroupIndex:，sequence Name：" + seqName + "   value:" + startValue, e);
            throw new SingleSequenceException(
                    "由于SQLException,插入初值自适应失败！dbGroupIndex:，sequence Name：" + seqName + "   value:" + startValue, e);

        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(conn);
        }
    }

    public static int insertSeqForLock(DataSource dataSource, String insertSql, String seqName, long startValue,
                                       String selectSql)
            throws SingleSequenceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PreparedStatement stmtLock = null;
        ResultSet rsLock = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stmtLock = conn.prepareStatement(selectInitLockForUpdate);
            rsLock = stmtLock.executeQuery();
            if (!rsLock.next()) {
                throw new SingleSequenceException("表:sequence_init_lock 缺少id=1的数据");

            }

            if (log.isDebugEnabled()) {
                log.debug("初始化sequence:" + seqName + ",获取到初始化sequence的锁");
            }

            Long oldValue = selectSeqValueNotCloseConnection(conn, selectSql, seqName);
            Timestamp timeStamp;
            if (oldValue == null) {
                if (log.isDebugEnabled()) {
                    log.debug("sequence:" + seqName + " 未初始化");
                }
                stmt = conn.prepareStatement(insertSql);
                stmt.setString(1, seqName);
                stmt.setLong(2, startValue);
                timeStamp = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(3, timeStamp);
                stmt.setTimestamp(4, timeStamp);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SingleSequenceException(
                            "faild to auto adjust init value at  " + seqName + " insert affectedRow =0");
                }

                if (log.isDebugEnabled()) {
                    log.debug("插入初值:" + seqName + ", value:" + startValue);
                }
                conn.commit();
                return affectedRows;
            }
            if (log.isDebugEnabled()) {
                log.debug("sequence:" + seqName + ", 初始化锁定表后，发现已经被其它线程初始化了，得到的初始化值为：" + oldValue);

            }

            conn.commit();
            return 0;
        } catch (Throwable e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("初始化sequence:" + seqName + "发生SQLException时，rollback又发生异常", e1);
            }
            throw new SingleSequenceException("初始化sequence:" + seqName + "发生异常时", e);
        } finally {
            closeResultSet(rs);
            closeResultSet(rsLock);
            closeStatement(stmt);
            closeStatement(stmtLock);
            closeConnection(conn);
        }
    }

    public static int[] randomIntSequence(int n) throws SingleSequenceException {
        if (n <= 0) {
            throw new SingleSequenceException("产生随机序列范围值小于等于0");
        }
        int[] num = new int[n];
        for (int i = 0; i < n; i++) {
            num[i] = i;
        }
        if (n == 1) {
            return num;
        }
        Random random = new Random();
        if ((n == 2) && (random.nextInt(2) == 1)) {
            int temp = num[0];
            num[0] = num[1];
            num[1] = temp;
        }

        for (int i = 0; i < n + 10; i++) {
            int rindex = random.nextInt(n);
            int mindex = random.nextInt(n);
            int temp = num[mindex];
            num[mindex] = num[rindex];
            num[rindex] = temp;
        }
        return num;
    }
}
