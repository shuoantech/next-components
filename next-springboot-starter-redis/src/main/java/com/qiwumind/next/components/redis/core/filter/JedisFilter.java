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



import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class JedisFilter {

    private static final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    private JedisPool                            jedisPool;

    public JedisFilter(JedisPool jedisPool) {
        if (jedisPool == null) {
            jedisPool = new JedisPool(poolConfig, "localhost", 6379);
        }
        this.jedisPool = jedisPool;
    }

    @FunctionalInterface
    public interface JedisExecutor<T> {
        T execute(Jedis jedis);
    }

    public <T> T execute(JedisExecutor<T> jedisExecutor) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedisExecutor.execute(jedis);
        }
    }

    @FunctionalInterface
    public interface PipelineExecutor {
        void load(Pipeline pipeline);
    }

    public List<Object> pipeline(List<PipelineExecutor> pipelineExecutors) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (PipelineExecutor executor : pipelineExecutors) {
                executor.load(pipeline);
            }
            return pipeline.syncAndReturnAll();
        }
    }
}
