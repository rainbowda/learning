package com.learnRedis.string.action;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexAction {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/index")
    public String index(){
        System.out.println(redisTemplate.getConnectionFactory().getConnection().ping());
        return "hello";
    }
}
