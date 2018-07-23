package com.learnRedis.list.action;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/list")
public class Action {

    private static final String LIST_KEY = "list:1";

    private RedisTemplate redisTemplate;

    private ListOperations listOperations;

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
        this.listOperations = this.redisTemplate.opsForList();

    }

    /**
     * 列表
     * @return
     */
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public List getList(){
        List list = listOperations.range(LIST_KEY, 0, -1);

        //可以用size获取成员长度
        //listOperations.size(LIST_KEY);

        return list;
    }

    /**
     * 向队列的左边添加值
     * @return
     */
    @RequestMapping(value = "/leftPush",method = RequestMethod.POST)
    public boolean leftPush(@RequestBody JSONObject value){
        listOperations.leftPush(LIST_KEY,value.toString());
        return true;
    }

    /**
     * 向队列的右边添加值
     * @return
     */
    @RequestMapping(value = "/rightPush",method = RequestMethod.POST)
    public boolean rightPush(@RequestBody JSONObject value){
        listOperations.rightPush(LIST_KEY,value.toString());
        return true;
    }

    /**
     * 向队列的左边弹出值
     * @return
     */
    @RequestMapping(value = "/leftPop",method = RequestMethod.GET)
    public Object leftPop(){
        return listOperations.leftPop(LIST_KEY);
    }

    /**
     * 向队列的右边弹出值
     * @return
     */
    @RequestMapping(value = "/rightPop",method = RequestMethod.GET)
    public Object rightPop(){
        return listOperations.rightPop(LIST_KEY);
    }

}
