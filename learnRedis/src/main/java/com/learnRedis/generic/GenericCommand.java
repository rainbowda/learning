package com.learnRedis.generic;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GenericCommand extends RedisBaseConnection {

    @Test
    public void del(){
        //根据key移除，忽略不存在的key

        jedis.set("key1","key1");
        jedis.set("key2","key2");

        Long delNum = jedis.del("key1", "key2", "key3");
        System.out.println(delNum);

        //spring
        redisTemplate.opsForValue().set("key1","key1");
        redisTemplate.opsForValue().set("key2","key2");

        String[] strs = {"key1", "key2", "key3"};
        delNum = redisTemplate.delete(Arrays.asList(strs));
        System.out.println(delNum);
    }


    @Test
    public void dump(){
        jedis.set("dump","dumpdump");

        byte[] dumpData = jedis.dump("dump");
        System.out.println(Arrays.toString(dumpData));

        //spring
        redisTemplate.dump("dump");
        System.out.println(Arrays.toString(dumpData));
    }

    @Test
    public void exist(){
        jedis.set("exist","exist");

        Boolean exist = jedis.exists("exist");
        Boolean noExist = jedis.exists("noExist");
        System.out.println(exist);
        System.out.println(noExist);

        //spring
        exist = redisTemplate.hasKey("exist");
        noExist = redisTemplate.hasKey("noExist");
        System.out.println(exist);
        System.out.println(noExist);
    }

    @Test
    public void expire() throws InterruptedException {
        jedis.set("expire","expire");
        jedis.expire("expire",10);

        System.out.println(jedis.ttl("expire"));
        Thread.sleep(2000);//休眠5秒
        System.out.println(jedis.ttl("expire"));

        //spring
        redisTemplate.expire("expire",5, TimeUnit.SECONDS);
        System.out.println(redisTemplate.getExpire("expire"));
        Thread.sleep(2000);//休眠5秒
        System.out.println(redisTemplate.getExpire("expire"));
    }

    @Test
    public void expireAt() throws InterruptedException {
        jedis.set("expireAt","expireAt");
        jedis.expireAt("expireAt",(System.currentTimeMillis()+3000)/1000);

        System.out.println(jedis.ttl("expireAt"));
        Thread.sleep(2000);//休眠5秒
        System.out.println(jedis.ttl("expireAt"));

        //spring
        redisTemplate.expireAt("expireAt",new Date(System.currentTimeMillis()+3000));
        System.out.println(redisTemplate.getExpire("expireAt"));
        Thread.sleep(2000);//休眠5秒
        System.out.println(redisTemplate.getExpire("expireAt"));
    }


}
