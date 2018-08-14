package com.learnRedis.lock.case2;

import com.learnRedis.lock.RedisLock;
import redis.clients.jedis.Jedis;

import static com.learnRedis.lock.LockConstants.*;

/**
 * 存在问题：
 * 1.如何保证锁不会被误删除?
 * 2.过期时间如何保证大于执行时间?
 */
public class LockCase2 extends RedisLock {

    public LockCase2(Jedis jedis, String name) {
        super(jedis, name);
    }

    @Override
    public void lock() {
        while(true){
            /**
             * 这里设置key和设置过期时间需要保持原子性,
             * 设置存活时间30秒,解决LockCase1存在的问题.
             * @see com.learnRedis.lock.case1.LockCase1
             */
            String result = jedis.set(lockKey, "value", NOT_EXIST,SECONDS,30);
            if(OK.equals(result)){
                System.out.println(Thread.currentThread().getId()+"加锁成功!");
                break;
            }
        }
    }

    @Override
    public void unlock() {
        jedis.del(lockKey);
    }
}
