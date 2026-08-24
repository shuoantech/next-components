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

package com.qiwumind.next.components.redis.core.filter;



import com.google.common.collect.Lists;
import com.google.common.math.LongMath;
import com.google.common.primitives.Longs;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

/**
 * 类描述：
 *
 * @author KS.Li 2020年9月3日 上午11:25:09
 */
public class RedisBitmaps {

    private final JedisFilter jedisFilter;
    private final String      key;
    private final long        bitSize;

    public RedisBitmaps(String key, long bits) {
        this(null, key, bits);
    }

    public RedisBitmaps(JedisPool jedisPool, String key, long bits) {
        this.jedisFilter = new JedisFilter(jedisPool);
        this.key = key;
        this.bitSize = LongMath.divide(bits, 64, RoundingMode.CEILING) * Long.SIZE;//位数组的长度，相当于n个long的长度
        if (this.bitCount() == 0) {
            this.jedisFilter.execute((jedis -> jedis.setbit(this.currentKey(), this.bitSize - 1, false)));
        }
    }

    boolean get(long[] offsets) {
        for (long i = 0; i < this.cursor() + 1; i++) {
            final long cursor = i;
            //只要有一个cursor对应的bitmap中，offsets全部命中，则表示可能存在
            boolean match = Arrays.stream(offsets).boxed()
                    .map(offset -> this.jedisFilter.execute(jedis -> jedis.getbit(this.genkey(cursor), offset)))
                    .allMatch(b -> (Boolean) b);
            if (match) {
                return true;
            }
        }
        return false;
    }

    boolean get(final long offset) {
        return this.jedisFilter.execute(jedis -> jedis.getbit(this.currentKey(), offset));
    }

    boolean set(long[] offsets) {
        if (this.cursor() > 0 && this.get(offsets)) {
            return false;
        }
        boolean bitsChanged = false;
        for (long offset : offsets) {
            bitsChanged |= this.set(offset);
        }
        return bitsChanged;
    }

    boolean set(long offset) {
        if (!this.get(offset)) {
            this.jedisFilter.execute(jedis -> jedis.setbit(this.currentKey(), offset, true));
            return true;
        }
        return false;
    }

    long bitCount() {
        return this.jedisFilter.execute(jedis -> jedis.bitcount(this.currentKey()));
    }

    long bitSize() {
        return this.bitSize;
    }

    private String currentKey() {
        return this.genkey(this.cursor());
    }

    private String genkey(long cursor) {
        return this.key + "-" + cursor;
    }

    private String genkeyCursor() {
        return this.key + "-cursor";
    }

    private Long cursor() {
        String cursor = this.jedisFilter.execute(jedis -> jedis.get(this.genkeyCursor()));
        return cursor == null ? 0 : Longs.tryParse(cursor);
    }

    void ensureCapacityInternal() {
        if ((this.bitCount() << 1) > this.bitSize()) {
            this.grow();
        }
    }

    void grow() {
        Long cursor = this.jedisFilter.execute(jedis -> jedis.incr(this.genkeyCursor()));
        this.jedisFilter.execute(jedis -> jedis.setbit(this.genkey(cursor), this.bitSize - 1, false));
    }

    void reset() {
        String[] keys = LongStream.range(0, this.cursor() + 1).boxed().map(this::genkey).toArray(String[]::new);
        this.jedisFilter.execute(jedis -> jedis.del(keys));
        this.jedisFilter.execute(jedis -> jedis.set(this.genkeyCursor(), "0"));
        this.jedisFilter.execute(jedis -> jedis.setbit(this.currentKey(), this.bitSize - 1, false));
    }

    void pipelineSet(List<long[]> offsets) {
        List<JedisFilter.PipelineExecutor> pipelineExecutors = Lists.newArrayList();
        offsets.stream().forEach(offset -> {
            pipelineExecutors.add(new JedisFilter.PipelineExecutor() {
                @Override
                public void load(Pipeline pipeline) {
                    for (long off : offset) {
                        pipeline.setbit(RedisBitmaps.this.currentKey(), off, true);
                    }

                }
            });
        });
        this.jedisFilter.pipeline(pipelineExecutors);
    }

}
