package com.learnRedis.base;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application.xml")
public class RedisBaseConnection {
    protected static Jedis jedis = null;


    protected RedisTemplate redisTemplate;

    protected ValueOperations valueOperations;

    protected HashOperations hashOperations;

    protected ListOperations listOperations;

    protected SetOperations setOperations;

    protected ZSetOperations zSetOperations;

    protected GeoOperations geoOperations;

    protected HyperLogLogOperations hyperLogLogOperations;


    @Resource
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.hashOperations = redisTemplate.opsForHash();
        this.listOperations = redisTemplate.opsForList();
        this.setOperations = redisTemplate.opsForSet();
        this.zSetOperations = redisTemplate.opsForZSet();
        this.geoOperations = redisTemplate.opsForGeo();
        this.hyperLogLogOperations = redisTemplate.opsForHyperLogLog();

    }

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
