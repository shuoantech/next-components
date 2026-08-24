package com.qiwumind.next.components.redis.core.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.qiwumind.next.components.redis.core.JedisPoolManager;

import redis.clients.jedis.Jedis;
import com.github.fppt.jedismock.RedisServer;

/**
 * Redis 缓存操作集成测试。
 *
 * <p>使用内嵌的 jedis-mock 在 localhost 上启动一个模拟 Redis 服务（符合
 * localhost / root / 无密码 的接入语义），无需依赖外部真实 Redis 实例，保证
 * 测试可重复运行。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JedisCacheIntegrationTest {

    private static RedisServer server;
    private JedisPoolManager manager;
    private JedisCache cache;

    @BeforeAll
    void startRedis() throws Exception {
        server = RedisServer.newRedisServer().start();
        manager = new JedisPoolManager(server.getHost(), null, server.getBindPort());
        manager.init();
        cache = new JedisCache(manager);
    }

    @AfterAll
    void stopRedis() throws Exception {
        if (manager != null) {
            manager.destroy();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void setValueByKey_and_getValueByKey_roundTrip() {
        cache.setValueByKey("k1", "v1", 0);
        assertEquals("v1", cache.getValueByKey("k1"));
    }

    @Test
    void getValueByKey_missingKey_returnsNull() {
        assertNull(cache.getValueByKey("not-exist-key"));
    }

    @Test
    void setex_writesValue() {
        cache.setex("temp", "short", 10);
        assertEquals("short", cache.getValueByKey("temp"));
    }

    @Test
    void hset_and_hget_roundTrip() {
        cache.hset("user:1", "name", "张三");
        cache.hset("user:1", "age", "30");
        assertEquals("张三", cache.hget("user:1", "name"));
        assertEquals("30", cache.hget("user:1", "age"));
    }

    @Test
    void hdel_removesField() {
        cache.hset("user:2", "name", "李四");
        cache.hset("user:2", "age", "25");
        cache.hdel("user:2", "age");
        assertNull(cache.hget("user:2", "age"));
        assertEquals("李四", cache.hget("user:2", "name"));
    }

    @Test
    void exists_and_del() {
        cache.setValueByKey("flag", "1", 0);
        assertTrue(cache.exists("flag"));
        cache.del("flag");
        assertFalse(cache.exists("flag"));
    }

    @Test
    void directJedis_setGet() {
        try (Jedis jedis = manager.getJedis()) {
            jedis.set("raw", "raw-value");
            assertEquals("raw-value", jedis.get("raw"));
        }
    }
}
