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

package com.qiwumind.next.components.sequence.single;



import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.qiwumind.next.components.sequence.handler.Sequence;
import com.qiwumind.next.components.sequence.single.core.SingleSequenceRange;
import com.qiwumind.next.components.sequence.single.dao.SingleSequenceDao;
import com.qiwumind.next.components.sequence.single.exception.SingleSequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SingleSequence implements Sequence {
    private static final Logger          log  = LoggerFactory.getLogger(SingleSequence.class);
    private String                       name;
    private SingleSequenceDao sequenceDao;
    private volatile SingleSequenceRange currentRange;
    private final Lock                   lock = new ReentrantLock();

    public SingleSequence() {
    }

    public void init() throws SingleSequenceException, SQLException {
        this.sequenceDao.adjust(this.name);
    }

    @Override
    public long nextValue() {
        if (this.currentRange == null) {
            this.lock.lock();
            try {
                if (this.currentRange == null) {
                    this.currentRange = this.sequenceDao.nextRange(this.name);
                }
            } finally {
                this.lock.unlock();
            }
        }

        long value;
        do {
            if (this.currentRange.isOver()) {
                this.lock.lock();

                try {
                    if (this.currentRange.isOver()) {
                        this.currentRange = this.sequenceDao.nextRange(this.name);
                    }
                } finally {
                    this.lock.unlock();
                }
            }
            value = this.currentRange.getAndIncrement();
        } while (value == -1L);

        if (value < 0L) {
            throw new SingleSequenceException("Sequence value overflow, value = " + value);
        } else {
            return value;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SingleSequenceDao getSequenceDao() {
        return this.sequenceDao;
    }

    public void setSequenceDao(final SingleSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
}
