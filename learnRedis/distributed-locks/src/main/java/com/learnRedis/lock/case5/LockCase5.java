package com.learnRedis.lock.case5;

import com.learnRedis.lock.RedisLock;
import redis.clients.jedis.Jedis;

import java.time.LocalTime;
import java.util.Timer;

import static com.learnRedis.lock.LockConstants.*;


public class LockCase5 extends RedisLock {

    public LockCase5(Jedis jedis, String lockKey) {
        super(jedis, lockKey);

    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, lockValue, NOT_EXIST, SECONDS, 30);
            if (OK.equals(result)) {
                System.out.println("线程id:"+Thread.currentThread().getId() + "加锁成功!时间:"+LocalTime.now());

                //开启定时刷新过期时间
                isOpenExpirationRenewal = true;
                scheduleExpirationRenewal();
                break;
            }
            System.out.println("线程id:"+Thread.currentThread().getId() + "获取锁失败，休眠10秒!时间:"+LocalTime.now());
            //休眠10秒
            sleepBySencond(10);
        }
    }

    @Override
    public void unlock() {
        System.out.println("线程id:"+Thread.currentThread().getId() + "解锁!时间:"+LocalTime.now());

        String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                                    "return redis.call('del', KEYS[1]) " +
                                    "else " +
                                    "return 0 " +
                                    "end";
        jedis.eval(checkAndDelScript, 1, lockKey, lockValue);
        isOpenExpirationRenewal = false;

    }
}
