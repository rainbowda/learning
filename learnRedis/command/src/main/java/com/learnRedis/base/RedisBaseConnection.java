package com.learnRedis.base;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application.xml")
public class RedisBaseConnection {
    protected static Jedis jedis = null;

    @Resource
    protected RedisTemplate redisTemplate;

    /**
     * 连接
     */
    @BeforeClass
    public static void init(){
        jedis = new Jedis("localhost");
    }

    /**
     * 关闭连接
     */
    @AfterClass
    public static void destroy(){
        jedis.flushDB();
        jedis.close();
        jedis = null;
    }
}
