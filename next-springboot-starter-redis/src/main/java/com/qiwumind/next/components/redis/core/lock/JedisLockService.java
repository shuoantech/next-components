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

package com.qiwumind.next.components.redis.core.lock;



import com.google.common.base.Preconditions;
import com.qiwumind.next.components.redis.core.JedisPoolManager;
import com.qiwumind.next.components.redis.core.constants.RedisConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class JedisLockService implements LockService {
    private static Logger       logger               = LoggerFactory.getLogger(JedisLockService.class);
    private static final String LOCK_SUCCESS         = "OK";
    private static final String SET_IF_NOT_EXIST     = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long   RELEASE_SUCCESS      = 1L;

    private JedisPoolManager poolManager;

    public JedisLockService(JedisPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    /**
     * 关闭jedis
     *
     * @param jedis
     */
    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取环境key
     *
     * @param key
     * @return
     */
    public String getKey(final String key) {
        final StringBuilder builder = new StringBuilder();
        final String env = StringUtils.isBlank(this.poolManager.getEnv()) ? "PRD" : this.poolManager.getEnv();
        builder.append(env).append(RedisConstant.COLON).append(key);
        return builder.toString();
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey 锁
     * @param value 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(final String lockKey, final String value, final long expireTime) {
        final String tmpKey = this.getKey(lockKey);
        final Jedis jedis = this.poolManager.getJedis();
        Preconditions.checkNotNull(jedis, "jedis不能为空");

        SetParams setParams = SetParams.setParams();
        setParams.nx(); // SET_IF_NOT_EXIST
        setParams.px(expireTime);//SET_WITH_EXPIRE_TIME

        String result = jedis.set(tmpKey, value, setParams);

        /*
         * 第一个为key，我们使用key来当锁，因为key是唯一的。 第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗
         * ，为什么还要用到value？原因就是我们在上面讲到可靠性时 ，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId
         * ，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据
         * 。requestId可以使用UUID.randomUUID().toString()方法生成。 第三个为nxxx，这个参数我们填的是NX，意思是SET
         * IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
         * 第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
         * 第五个为time，与第四个参数相呼应，代表key的过期时间。
         */
        this.close(jedis);
        if (StringUtils.isBlank(result)) {
            return false;
        }
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    /**
     * 释放分布式锁 lua脚本必须在redis3版本以上才可
     *
     * @param lockKey 锁
     * @param value 请求标识
     * @return 是否释放成功
     */
    @Override
    public boolean releaseLock(final String lockKey, final String value) {
        final String tmpKey = this.getKey(lockKey);
        final Jedis jedis = this.poolManager.getJedis();
        Preconditions.checkNotNull(jedis, "jedis不能为空");
        final String script = "if redis.call('exists', KEYS[1]) == 0 then return -1 else if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return -2 end end";
        final Object result = jedis.eval(script, Collections.singletonList(tmpKey), Collections.singletonList(value));
        this.close(jedis);
        if (result == null) {
            return false;
        }
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

}
