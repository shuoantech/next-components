package com.qiwumind.next.components.redis.core.lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.qiwumind.next.components.redis.core.JedisPoolManager;

import com.github.fppt.jedismock.RedisServer;

/**
 * 基于 Redis 的分布式锁集成测试。
 *
 * <p>使用内嵌 jedis-mock 模拟 localhost 上的 Redis 服务（无密码），
 * 验证锁的获取/释放语义及「解铃还须系铃人」的归属校验。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JedisLockServiceIntegrationTest {

    private static RedisServer server;
    private JedisLockService lockService;

    @BeforeAll
    void startRedis() throws Exception {
        server = RedisServer.newRedisServer().start();
        JedisPoolManager manager = new JedisPoolManager(server.getHost(), null, server.getBindPort());
        manager.init();
        lockService = new JedisLockService(manager);
    }

    @AfterAll
    void stopRedis() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void tryLock_acquiresLock_then_release() {
        String key = "order:lock:1";
        String owner = "request-A";
        assertTrue(lockService.tryLock(key, owner, 5000));
        // 同一持有者再次获取（key 已存在，NX 失败）
        assertFalse(lockService.tryLock(key, owner, 5000));
        // 释放锁
        assertTrue(lockService.releaseLock(key, owner));
        // 释放后其他持有者可重新获取
        assertTrue(lockService.tryLock(key, "request-B", 5000));
    }

    @Test
    void releaseLock_wrongOwner_fails() {
        String key = "order:lock:2";
        String owner = "owner-1";
        assertTrue(lockService.tryLock(key, owner, 5000));
        // 非持有者释放应失败（lua 校验 value 不一致）
        assertFalse(lockService.releaseLock(key, "intruder"));
        // 持有者释放成功
        assertTrue(lockService.releaseLock(key, owner));
    }

    @Test
    void lockManager_delegatesToService() {
        LockManager manager = new LockManager(lockService);
        String key = "order:lock:3";
        String owner = "mgr-owner";
        assertTrue(manager.tryLock(key, owner, 5000));
        assertTrue(manager.releaseLock(key, owner));
    }
}
