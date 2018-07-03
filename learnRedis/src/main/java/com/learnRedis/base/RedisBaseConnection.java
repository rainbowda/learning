package com.learnRedis.base;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisBaseConnection {
    private Jedis jedis = null;

    @Before
    public void init(){
        //连接本地的 Redis 服务
        jedis = new Jedis("localhost");
    }

    @Test
    public void ping(){
        System.out.println("服务正在运行: "+jedis.ping());
    }
}
