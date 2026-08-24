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

package com.qiwumind.next.components.sequence;



import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.qiwumind.next.components.sequence.handler.Sequence;
import com.qiwumind.next.components.sequence.properties.SingleSequenceConfiguration;
import com.qiwumind.next.components.sequence.single.SingleSequence;
import com.qiwumind.next.components.sequence.single.dao.SingleSequenceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SequenceGenerator {
    private static final Logger               log = LoggerFactory.getLogger(SequenceGenerator.class);
    private final SingleSequenceConfiguration properties;
    private final DataSource                  dataSource;
    private final Set<String>                 sequenceCache;
    private final Map<String, Sequence>       cache;

    public SequenceGenerator(DataSource datasource, SingleSequenceConfiguration properties) {
        this.dataSource = datasource;
        this.properties = properties;
        this.cache = new ConcurrentHashMap<>();
        this.sequenceCache = new HashSet<>();
        if (properties.getSequenceNames() != null) {
            String[] var3 = properties.getSequenceNames();
            int var4 = var3.length;
            for (int var5 = 0; var5 < var4; ++var5) {
                String sequenceName = var3[var5];
                this.sequenceCache.add(sequenceName);
                log.info("Init Sequence {}", sequenceName);
            }
        }

    }

    public synchronized Sequence getSequence(String sequenceName) throws SQLException {
        if (!this.sequenceCache.contains(sequenceName)) {
            throw new IllegalArgumentException(String.format("xdbc.sequence seqName %s 未配置", sequenceName));
        } else {
            Sequence sequence = this.cache.get(sequenceName);
            if (sequence == null) {
                SingleSequenceDao sequenceDao = new SingleSequenceDao();
                sequenceDao.setAdjust(true);
                sequenceDao.setDataSource(this.dataSource);
                sequenceDao.setInnerStep(this.properties.getInnerStep());
                sequenceDao.setRetryTimes(this.properties.getRetryTimes());
                sequenceDao.init(this.properties.getValueName());
                SingleSequence singleSequence = new SingleSequence();
                singleSequence.setName(sequenceName);
                singleSequence.setSequenceDao(sequenceDao);
                this.cache.put(sequenceName, singleSequence);
                log.info("Init Sequence {}", sequenceName);
                sequence = singleSequence;
            }

            return sequence;
        }
    }
}
