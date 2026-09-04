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

package com.qiwumind.next.components.redis.core;



import com.github.rholder.retry.Retryer;
import com.qiwumind.next.components.redis.core.constants.RedisConstant;
import com.qiwumind.next.components.redis.core.retry.RedisRetryerBuilder;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.Callable;

public class JedisPoolManager {
    private final static Logger log = LoggerFactory.getLogger(JedisPoolManager.class);
    /**
     * redis相关配置
     */
    private int maxTotal = RedisConstant.POOL_MAX_TOTAL;
    private int maxIdle = RedisConstant.POOL_MAX_IDLE;
    private int timeOut = RedisConstant.DEFAULT_TIMEOUT;
    private long maxWaitMillis = -1;
    private String host;
    private String password;
    private int port = 6379;
    private int dbNo;
    private JedisPool pool;

    /**
     * 环境 默认test
     */
    private String env = RedisConstant.DEV_ENV;

    public JedisPoolManager() {
        super();
    }

    public JedisPoolManager(String host, String password, int port) {
        super();
        this.host = host;
        this.password = password;
        this.port = port;
    }

    public void init() {
        Validate.notBlank(host, "Redis host cannot be blank");
        Validate.isTrue(port > 0, "Redis port must be greater than 0");
        
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大空闲连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
        config.setMaxIdle(this.maxIdle);
        // 最大连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
        config.setMaxTotal(this.maxTotal);
        config.setTestOnBorrow(true);//向调用者输出“链接”资源时，是否检测是有有效，如果无效则从连接池中移除，并尝试获取继续获取。设为true
        config.setTestOnReturn(true);//向连接池“归还”链接时，是否检测“链接”对象的有效性
        config.setMaxWaitMillis(this.maxWaitMillis);
        try {
            this.pool = new JedisPool(config, this.host, this.port, this.timeOut, this.password);
            log.info(
                    "========> jedis pool is created by JedisPool, host:{},port:{},dbNo:{},maxIdle:{},maxtotal:{},timeout:{} maxWaitMillis:{}",
                    this.host, this.port, this.dbNo, this.maxIdle, this.maxTotal, this.timeOut,
                    this.maxWaitMillis);
        } catch (Exception e) {
            log.error("jedis pool is created fail " + e.getMessage(), e);
            throw new RuntimeException("jedis pool is created fail " + e.getMessage(), e);
        }

    }

    /**
     * 获取jedis
     *
     * @return
     */
    public Jedis getJedis() {
        Validate.validState(pool != null, "Redis pool is not initialized. Please call init() first.");
        try {
            final Retryer<Jedis> retryer = RedisRetryerBuilder.build();
            final Jedis jedis = retryer.call(new Callable<Jedis>() {
                @Override
                public Jedis call() throws Exception {
                    return pool.getResource();
                }
            });
            
            // 如果指定了数据库，切换到对应的数据库
            if (dbNo > 0) {
                jedis.select(dbNo);
            }
            
            return jedis;
        } catch (final Exception e) {
            log.error("多次获取Redis连接失败！", e);
            throw new RuntimeException("Failed to get Redis connection after multiple attempts", e);
        }
    }

    /**
     * 关闭jedis
     *
     * @param jedis
     */
    public void close(Jedis jedis) {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                log.error("关闭Jedis连接失败", e);
            }
        }
    }

    /**
     * 执行Redis操作
     *
     * @param action Redis操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T execute(RedisAction<T> action) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return action.execute(jedis);
        } finally {
            close(jedis);
        }
    }

    /**
     * 销毁链接池
     */
    public void destroy() {
        if (this.pool != null) {
            log.info("====> jedisPool is destroy ");
            try {
                this.pool.destroy();
            } catch (Exception e) {
                log.error("销毁Jedis连接池失败", e);
            }
        }
    }

    /**
     * Redis操作接口
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface RedisAction<T> {
        T execute(Jedis jedis);
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDbNo(int dbNo) {
        this.dbNo = dbNo;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public String getHost() {
        return this.host;
    }

    public String getPassword() {
        return this.password;
    }

    public int getPort() {
        return this.port;
    }

    public int getDbNo() {
        return this.dbNo;
    }

    public JedisPool getPool() {
        return this.pool;
    }

    public long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getEnv() {
        return this.env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public JedisPoolManager(String host, String password, int port, int dbNo) {
        super();
        this.host = host;
        this.password = password;
        this.port = port;
        this.dbNo = dbNo;
    }
}
