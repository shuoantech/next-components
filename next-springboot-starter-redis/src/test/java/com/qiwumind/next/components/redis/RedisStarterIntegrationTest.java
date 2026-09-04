package com.qiwumind.next.components.redis;

import com.qiwumind.next.components.redis.autoconfigure.RedisConfiguration;
import com.qiwumind.next.components.redis.core.JedisPoolManager;
import com.qiwumind.next.components.redis.core.bloomfilter.RedisBloomFilter;
import com.qiwumind.next.components.redis.core.bloomfilter.RedisConnectionManager;
import com.qiwumind.next.components.redis.core.cache.JedisCache;
import com.qiwumind.next.components.redis.core.lock.LockService;
import com.qiwumind.next.components.redis.core.lock.RedissonLockService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * redis starter 集成自测（直连本地 Redis，默认 127.0.0.1:6379 无认证）。
 * <p>
 * 本地 Redis 若开启了 ACL 认证，可通过系统属性覆盖：
 * {@code mvn test -Dredis.username=root -Dredis.password=xxx}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisStarterIntegrationTest {

    static final String HOST = System.getProperty("redis.host", "127.0.0.1");
    static final int PORT = Integer.getInteger("redis.port", 6379);
    static final String USERNAME = System.getProperty("redis.username", "");
    static final String PASSWORD = System.getProperty("redis.password", "");

    static RedissonClient redissonClient;
    static LockService lockService;
    static JedisPoolManager poolManager;
    static JedisCache jedisCache;

    @BeforeAll
    static void setUp() {
        // RedissonClient（与 RedissonAutoConfiguration 同款单机配置）
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + HOST + ":" + PORT)
                .setDatabase(0);
        if (!USERNAME.isEmpty()) {
            config.useSingleServer().setUsername(USERNAME);
        }
        if (!PASSWORD.isEmpty()) {
            config.useSingleServer().setPassword(PASSWORD);
        }
        redissonClient = Redisson.create(config);
        lockService = new RedissonLockService(redissonClient);

        // JedisPoolManager + JedisCache
        poolManager = new JedisPoolManager(HOST, PASSWORD.isEmpty() ? null : PASSWORD, PORT);
        poolManager.init();
        jedisCache = new JedisCache(poolManager);

        // 布隆过滤器的连接管理器
        RedisConfiguration redisConfiguration = new RedisConfiguration();
        redisConfiguration.setHost(HOST);
        redisConfiguration.setPort(PORT);
        redisConfiguration.setDatabase(0);
        if (!USERNAME.isEmpty()) {
            redisConfiguration.setUsername(USERNAME);
        }
        if (!PASSWORD.isEmpty()) {
            redisConfiguration.setPassword(PASSWORD);
        }
        RedisConnectionManager.getInstance().setRedisConfiguration(redisConfiguration);
    }

    @AfterAll
    static void tearDown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
        if (poolManager != null) {
            poolManager.destroy();
        }
        RedisConnectionManager.getInstance().closeAll();
    }

    // ==================== 连接管理 ====================

    @Test
    @Order(1)
    void connectionManager_ping() {
        assertTrue(RedisConnectionManager.getInstance().testConnection(), "本地 Redis 连接失败");
    }

    // ==================== 分布式锁（RedissonLockService） ====================

    @Test
    @Order(2)
    void lock_tryLock_and_release() {
        String key = "next-redis-it:lock:a";
        assertTrue(lockService.tryLock(key, "v1", 10_000), "首次加锁应成功");
        assertTrue(lockService.tryLock(key, "v1", 10_000), "同线程可重入");
        assertTrue(lockService.releaseLock(key, "v1"), "持有者释放应成功");
        assertTrue(lockService.releaseLock(key, "v1"), "可重入锁需释放同等次数");
        assertFalse(lockService.releaseLock(key, "v1"), "全部释放后再次释放应返回 false");
    }

    @Test
    @Order(3)
    void lock_otherThread_cannot_acquire_or_release() throws Exception {
        String key = "next-redis-it:lock:b";
        assertTrue(lockService.tryLock(key, "main", 10_000));

        AtomicBoolean otherAcquired = new AtomicBoolean(false);
        AtomicBoolean otherReleased = new AtomicBoolean(false);
        CountDownLatch done = new CountDownLatch(1);
        new Thread(() -> {
            otherAcquired.set(lockService.tryLock(key, "other", 1_000));
            otherReleased.set(lockService.releaseLock(key, "other"));
            done.countDown();
        }).start();
        done.await();

        assertFalse(otherAcquired.get(), "持有期间其他线程加锁应失败");
        assertFalse(otherReleased.get(), "非持有线程释放应失败（不误删他人锁）");
        assertTrue(lockService.releaseLock(key, "main"));
    }

    @Test
    @Order(4)
    void lock_auto_expire() throws Exception {
        String key = "next-redis-it:lock:c";
        assertTrue(lockService.tryLock(key, "v1", 300), "短租期加锁应成功");
        Thread.sleep(600); // 等锁过期
        AtomicBoolean reacquired = new AtomicBoolean(false);
        CountDownLatch done = new CountDownLatch(1);
        new Thread(() -> {
            reacquired.set(lockService.tryLock(key, "v2", 1_000));
            done.countDown();
        }).start();
        done.await();
        assertTrue(reacquired.get(), "锁过期后其他线程应能获取");
    }

    // ==================== JedisCache ====================

    @Test
    @Order(5)
    void jedisCache_string_ops() {
        jedisCache.setValueByKey("it:str", "hello-redis", 0);
        assertEquals("hello-redis", jedisCache.getValueByKey("it:str"));
        assertTrue(jedisCache.exists("it:str"));
        assertEquals(1, jedisCache.del("it:str"));
        assertFalse(jedisCache.exists("it:str"));
    }

    @Test
    @Order(6)
    void jedisCache_hash_ops() {
        jedisCache.hset("it:hash", "field1", "value1", 60);
        assertEquals("value1", jedisCache.hget("it:hash", "field1"));
        jedisCache.hdel("it:hash", "field1");
        assertNull(jedisCache.hget("it:hash", "field1"), "hdel 后字段应不存在");
    }

    // ==================== 分布式布隆过滤器 ====================

    @Test
    @Order(7)
    void bloomFilter_put_mightContain_clear() {
        RedisBloomFilter<String> filter = new RedisBloomFilter<>("it-bf", 1_000, 0.01, "test", "it-local");
        filter.clear();

        for (int i = 0; i < 500; i++) {
            filter.put("element-" + i);
        }
        for (int i = 0; i < 500; i++) {
            assertTrue(filter.mightContain("element-" + i), "已插入元素不应误判为不存在: " + i);
        }
        // 未插入元素应基本判定为不存在（1% 误判率下抽查 200 个几乎全 false）
        long falsePositives = 0;
        for (int i = 500; i < 700; i++) {
            if (filter.mightContain("element-" + i)) {
                falsePositives++;
            }
        }
        assertTrue(falsePositives <= 10, "200 个抽查中误判不应超过 10 个，实际: " + falsePositives);

        filter.clear();
        assertFalse(filter.mightContain("element-0"), "clear 后不应再命中");
    }
}
