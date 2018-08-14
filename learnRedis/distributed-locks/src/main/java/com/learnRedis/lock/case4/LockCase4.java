package com.learnRedis.lock.case4;

import com.learnRedis.lock.RedisLock;
import redis.clients.jedis.Jedis;

import java.util.Collections;

import static com.learnRedis.lock.LockConstants.*;

/**
 * 存在问题：过期时间如何保证大于执行时间?
 */
public class LockCase4 extends RedisLock {

    public LockCase4(Jedis jedis, String lockKey) {
        super(jedis, lockKey);

    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, lockValue, NOT_EXIST, SECONDS, 30);
            if (OK.equals(result)) {
                System.out.println(Thread.currentThread().getId() + "加锁成功!");
                break;
            }
        }
    }

    @Override
    public void unlock() {
        // 使用lua脚本进行原子删除操作
        String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                                    "return redis.call('del', KEYS[1]) " +
                                    "else " +
                                    "return 0 " +
                                    "end";
        jedis.eval(checkAndDelScript, 1, lockKey, lockValue);
    }
}
