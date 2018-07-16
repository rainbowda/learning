package com.learnRedis.string.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/myLog")
public class MyLogAction {

    private static final String MY_LOG_REDIS_KEY_PREFIX = "myLog:";

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/getMyLog")
    public List getMyLog(){
        //获取mylog的keys
        Set myLogKeys = redisTemplate.keys("myLog:*");
        System.out.println("myLogKeys:"+myLogKeys);
        return redisTemplate.opsForValue().multiGet(myLogKeys);
    }


    @RequestMapping(value = "/addMyLog",method = RequestMethod.POST)
    public boolean addMyLog(String logContext){
        redisTemplate.opsForValue().set(MY_LOG_REDIS_KEY_PREFIX+LocalDateTime.now(), logContext);

        return true;
    }

    @RequestMapping("/updateMyLog")
    public void updateMyLog(){

    }

    @RequestMapping("/delMyLog")
    public void delMyLog(){

    }
}
