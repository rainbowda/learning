package com.learnRedis.string.action;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/myLog")
public class MyLogAction {

    private static final String MY_LOG_REDIS_KEY_PREFIX = "myLog:";
    private static final String MY_LOG_REDIS_ID_KEY = "myLogID";

    private RedisTemplate redisTemplate;

    private ValueOperations valueOperations;

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }


    /**
     * 列表
     * @return
     */
    @RequestMapping(value = "/getMyLog",method = RequestMethod.GET)
    public List getMyLog(){
        //获取mylog的keys
        Set myLogKeys = redisTemplate.keys("myLog:*");
        return  valueOperations.multiGet(myLogKeys);
    }

    /**
     * 新增
     * @param myLog
     * @return
     */
    @RequestMapping(value = "/addMyLog",method = RequestMethod.POST)
    public boolean addMyLog(@RequestBody JSONObject myLog){
        //获取自增id
        Long myLogId = valueOperations.increment(MY_LOG_REDIS_ID_KEY, 1);

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        myLog.put("id",myLogId);
        myLog.put("createDate", date);
        myLog.put("updateDate", date);
        //将数据写到redis中
        valueOperations.set(MY_LOG_REDIS_KEY_PREFIX+myLogId, myLog.toString());

        return true;
    }

    /**
     * 修改
     * @param myLog
     * @return
     */
    @RequestMapping(value = "/updateMyLog",method = RequestMethod.POST)
    public boolean updateMyLog(@RequestBody JSONObject myLog){
        String myLogId = myLog.getString("id");
        myLog.put("updateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        valueOperations.set(MY_LOG_REDIS_KEY_PREFIX+myLogId, myLog.toString());
        return true;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delMyLog/{id}", method = RequestMethod.DELETE)
    public boolean delMyLog(@PathVariable  String id){
        return redisTemplate.delete(MY_LOG_REDIS_KEY_PREFIX + id);
    }
}
