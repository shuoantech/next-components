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

package com.qiwumind.next.components.sequence.single.dao;



import java.util.Objects;

import javax.sql.DataSource;

import com.qiwumind.next.components.sequence.single.core.SingleSequenceRange;
import com.qiwumind.next.components.sequence.single.exception.SingleSequenceException;
import com.qiwumind.next.components.sequence.single.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SingleSequenceDao extends AbstractSingleSequenceDao {
    private static final Logger log = LoggerFactory.getLogger(SingleSequenceDao.class);
    private DataSource          dataSource;
    protected volatile String   selectSql;
    protected volatile String   updateSql;
    protected volatile String   insertSql;

    public SingleSequenceDao() {
    }

    public void init(String valueName) throws SingleSequenceException {
        if (this.getDataSource() == null) {
            SingleSequenceException sequenceException = new SingleSequenceException("dataSource is Null ");
            log.error("dataSource", sequenceException);
            throw sequenceException;
        }
        if (DEFAULT_VALUE_COLUMN_NAME.equals(valueName) || Objects.isNull(valueName)) {
            this.insertSql = SeqUtil.getSeqInsertSql(this.getTableName(), this.getNameColumnName(),
                    this.getDefaultValueColumnName(), this.getGmtCreateColumnName(), this.getGmtModifiedColumnName());
            this.selectSql = SeqUtil.getSeqSelectSql(this.getTableName(), this.getNameColumnName(),
                    this.getDefaultValueColumnName());
            this.updateSql = SeqUtil.getSeqUpdateSql(this.getTableName(), this.getNameColumnName(),
                    this.getDefaultValueColumnName(), this.getGmtModifiedColumnName());
        }
        if (VALUE_COLUMN_NAME.equals(valueName)) {
            this.insertSql = SeqUtil.getSeqInsertSql(this.getTableName(), this.getNameColumnName(),
                    this.getValueColumnName(), this.getGmtCreateColumnName(), this.getGmtModifiedColumnName());
            this.selectSql = SeqUtil.getSeqSelectSql(this.getTableName(), this.getNameColumnName(),
                    this.getValueColumnName());
            this.updateSql = SeqUtil.getSeqUpdateSql(this.getTableName(), this.getNameColumnName(),
                    this.getValueColumnName(), this.getGmtModifiedColumnName());
        }

    }

    @Override
    public SingleSequenceRange nextRange(String name) throws SingleSequenceException {
        if (name == null) {
            throw new IllegalArgumentException("序列名称不能为空");
        }
        for (int i = 0; i < this.getRetryTimes() + 1; i++) {
            Long oldValue = SeqUtil.selectSeqValue(this.getDataSource(), this.selectSql, name);
            if (oldValue == null) {
                this.adjust(name);
                oldValue = SeqUtil.selectSeqValue(this.getDataSource(), this.selectSql, name);
                if (oldValue == null) {
                    continue;
                }
            }
            StringBuilder message;

            if (oldValue.longValue() < 0L) {
                message = new StringBuilder();
                message.append("Sequence value cannot be less than zero, value = ").append(oldValue);
                message.append(", please check table ").append(this.getTableName());

                throw new SingleSequenceException(message.toString());
            }

            if (oldValue.longValue() > Long.MAX_VALUE) {
                message = new StringBuilder();
                message.append("Sequence value overflow, value = ").append(oldValue);
                message.append(", please check table ").append(this.getTableName());

                throw new SingleSequenceException(message.toString());
            }

            Long newValue = oldValue.longValue() + this.getInnerStep();

            int affectedRows = SeqUtil.updateSeqValue(this.getDataSource(), this.updateSql, newValue.longValue(), name,
                    oldValue.longValue());
            if (affectedRows != 0) {
                return new SingleSequenceRange(oldValue.longValue() + 1L, newValue.longValue());
            }
        }
        throw new SingleSequenceException("Retried too many times, retryTimes = " + this.getRetryTimes());
    }

    @Override
    public synchronized void adjust(String name) throws SingleSequenceException {
        Long value = SeqUtil.selectSeqValue(this.getDataSource(), this.selectSql, name);

        if (value == null) {
            if (this.isAdjust()) {
                log.info("初始化sequence,seq name:" + name);
                int newValue = this.getInnerStep();
                SeqUtil.insertSeq(this.dataSource, this.insertSql, name, newValue);
                log.info("完成初始化sequence,seq name:" + name);
            } else {
                log.error("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关,seq name:" + name);
                throw new SingleSequenceException("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关,seq name:" + name);
            }
        }

    }

//    private void adjustInsert(String name) throws SequenceException, SQLException {
//        int newValue = this.getInnerStep();
//
//        SeqUtil.insertSeqForLock(this.getDataSource(), this.insertSql, name, newValue, this.selectSql);
//    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
