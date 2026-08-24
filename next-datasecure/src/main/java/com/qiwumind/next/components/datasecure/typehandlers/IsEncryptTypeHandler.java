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

package com.qiwumind.next.components.datasecure.typehandlers;



import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.qiwumind.next.components.datasecure.common.DataSecureConstants;
import com.qiwumind.next.components.datasecure.common.EncryptType;
import com.qiwumind.next.components.datasecure.common.config.EncryptSwitchConfig;

/**
 */
public class IsEncryptTypeHandler extends BaseTypeHandler<EncryptType> {

    @Override
    public EncryptType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return new EncryptType(rs.getString(columnIndex));
    }

    @Override
    public EncryptType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return new EncryptType(rs.getString(columnName));
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, EncryptType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, DataSecureConstants.IS_ENCRYPT_CLOSE);
        if (EncryptSwitchConfig.getEncryptFlag()) {
            ps.setString(i, DataSecureConstants.IS_ENCRYPT_OPEN);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.
     * CallableStatement, int)
     */
    @Override
    public EncryptType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return new EncryptType(cs.getString(columnIndex));
    }

    /*
     * (non-Javadoc)
     * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.
     * PreparedStatement, int, java.lang.Object,
     * org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setNonNullParameter(PreparedStatement arg0, int arg1, EncryptType arg2, JdbcType arg3)
            throws SQLException {
        // TODO Auto-generated method stub

    }
}
