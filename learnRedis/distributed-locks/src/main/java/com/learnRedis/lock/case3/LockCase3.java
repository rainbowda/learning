package com.learnRedis.lock.case3;

import com.learnRedis.lock.RedisLock;
import redis.clients.jedis.Jedis;

import java.util.UUID;

import static com.learnRedis.lock.LockConstants.*;

/**
 * 存在问题：解锁不具备原子性
 */
public class LockCase3 extends RedisLock {

    public LockCase3(Jedis jedis, String name) {
        super(jedis, name);

    }

    @Override
    public void lock() {
        while(true){
            /**
             * 设置value为当前线程特有的值
             */
            String result = jedis.set(lockKey, lockValue, NOT_EXIST,SECONDS,30);
            if(OK.equals(result)){
                System.out.println(Thread.currentThread().getId()+"加锁成功!");
                break;
            }
        }
    }

    @Override
    public void unlock() {
        /**
         * 此处不具备原子性,可以分为三个步骤
         * 1.获取锁对应的value值
         * 2.检查是否与requestId相等
         * 3.如果相等则删除锁（解锁）
         */
        String lockValue = jedis.get(lockKey);
        if (lockValue.equals(lockValue)){
            jedis.del(lockKey);
        }

    }
}
