package com.learnRedis.string;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

import java.time.LocalDate;
import java.util.BitSet;

public class BitmapCase extends RedisBaseConnection {

    @Test
    public void daliyActive(){
        String now = LocalDate.now().toString();
        for (int i = 0;i<10000;i++){
            redisTemplate.opsForValue().setBit("active:"+now,i,Math.random() > 0.5);
        }

        BitSet bitSet = BitSet.valueOf(jedis.get(("active:" + now).getBytes()));
        System.out.println(bitSet.cardinality());


    }

}
