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



import com.qiwumind.next.components.sequence.single.core.SingleCoreSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSingleSequenceDao implements SingleCoreSequence {
    private static final Logger   log                              = LoggerFactory
            .getLogger(AbstractSingleSequenceDao.class);
    protected static final long   DELTA                            = 100000000L;
    private static final int      DEFAULT_INNER_STEP               = 1000;
    private static final int      DEFAULT_RETRY_TIMES              = 2;
    private static final int      MIN_STEP                         = 1;
    private static final int      MAX_STEP                         = 100000;
    private static final Boolean  DEFAULT_ADJUST                   = false;
    private static final String   DEFAULT_TABLE_NAME               = "sequence";
    private static final String   DEFAULT_NAME_COLUMN_NAME         = "name";
    protected static final String DEFAULT_VALUE_COLUMN_NAME        = "value";
    protected static final String VALUE_COLUMN_NAME                = "current_value";
    private static final String   DEFAULT_GMT_MODIFIED_COLUMN_NAME = "gmt_modified";
    private static final String   DEFAULT_GMT_CREATE_COLUMN_NAME   = "gmt_created";
    private int                   retryTimes                       = 2;
    private int                   innerStep                        = 1000;
    private boolean               adjust;
    private String                tableName;
    private String                nameColumnName;
    private String                defaultValueColumnName;
    private String                valueColumnName;
    private String                gmtModifiedColumnName;
    private String                gmtCreateColumnName;
    private String                appName;

    public AbstractSingleSequenceDao() {
        this.adjust = DEFAULT_ADJUST;
        this.tableName = DEFAULT_TABLE_NAME;
        this.nameColumnName = DEFAULT_NAME_COLUMN_NAME;
        this.defaultValueColumnName = DEFAULT_VALUE_COLUMN_NAME;
        this.valueColumnName = VALUE_COLUMN_NAME;
        this.gmtModifiedColumnName = DEFAULT_GMT_MODIFIED_COLUMN_NAME;
        this.gmtCreateColumnName = DEFAULT_GMT_CREATE_COLUMN_NAME;
    }

    public int getRetryTimes() {
        return this.retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        if (retryTimes < 0) {
            log.warn("retryTimes is invalid " + retryTimes);
            throw new IllegalArgumentException(
                    "Property retryTimes cannot be less than zero, retryTimes = " + retryTimes);
        } else {
            this.retryTimes = retryTimes;
        }
    }

    public int getInnerStep() {
        return this.innerStep;
    }

    public void setInnerStep(int innerStep) {
        if (innerStep >= 1 && innerStep <= 100000) {
            this.innerStep = innerStep;
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Property step out of range [").append(1);
            message.append(",").append(100000).append("], step = ").append(innerStep);
            throw new IllegalArgumentException(message.toString());
        }
    }

    public boolean isAdjust() {
        return this.adjust;
    }

    public void setAdjust(final boolean adjust) {
        this.adjust = adjust;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getNameColumnName() {
        return this.nameColumnName;
    }

    public void setNameColumnName(final String nameColumnName) {
        this.nameColumnName = nameColumnName;
    }

    public String getDefaultValueColumnName() {
        return this.defaultValueColumnName;
    }

    public void setDefaultValueColumnName(final String defaultValueColumnName) {
        this.defaultValueColumnName = defaultValueColumnName;
    }

    public String getValueColumnName() {
        return this.valueColumnName;
    }

    public void setValueColumnName(final String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    public String getGmtModifiedColumnName() {
        return this.gmtModifiedColumnName;
    }

    public void setGmtModifiedColumnName(final String gmtModifiedColumnName) {
        this.gmtModifiedColumnName = gmtModifiedColumnName;
    }

    public String getGmtCreateColumnName() {
        return this.gmtCreateColumnName;
    }

    public void setGmtCreateColumnName(final String gmtCreateColumnName) {
        this.gmtCreateColumnName = gmtCreateColumnName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(final String appName) {
        this.appName = appName;
    }
}
