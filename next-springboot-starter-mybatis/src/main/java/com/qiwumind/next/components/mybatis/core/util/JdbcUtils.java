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

package com.qiwumind.next.components.mybatis.core.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.qiwumind.next.components.common.util.object.ObjectUtils;
import com.qiwumind.next.components.common.util.spring.SpringUtils;
import com.qiwumind.next.components.mybatis.core.enums.DbTypeEnum;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC 工具类
 */
public class JdbcUtils {

    /**
     * 判断连接是否正确
     *
     * @param url      数据源连接
     * @param username 账号
     * @param password 密码
     * @return 是否正确
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获得 URL 对应的 DB 类型
     *
     * @param url URL
     * @return DB 类型
     */
    public static DbType getDbType(String url) {
        return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
    }

    /**
     * 通过当前数据库连接获得对应的 DB 类型
     *
     * @return DB 类型 ,这里默认mysql
     */
    public static DbType getDbType() {
//        DataSource dataSource;
//        try {
//            DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
//            dataSource = dynamicRoutingDataSource.determineDataSource();
//        } catch (NoSuchBeanDefinitionException e) {
//            dataSource = SpringUtils.getBean(DataSource.class);
//        }
//        try (Connection conn = dataSource.getConnection()) {
//            return DbTypeEnum.find(conn.getMetaData().getDatabaseProductName());
//        } catch (SQLException e) {
//            throw new IllegalArgumentException(e.getMessage());
//        }
        return DbType.MYSQL;
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     *
     * @param url JDBC 连接
     * @return 是否为 SQLServer 数据库
     */
    public static boolean isSQLServer(String url) {
        DbType dbType = getDbType(url);
        return isSQLServer(dbType);
    }

    /**
     * 判断 JDBC 连接是否为 SQLServer 数据库
     *
     * @param dbType DB 类型
     * @return 是否为 SQLServer 数据库
     */
    public static boolean isSQLServer(DbType dbType) {
        return ObjectUtils.equalsAny(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
    }

}
