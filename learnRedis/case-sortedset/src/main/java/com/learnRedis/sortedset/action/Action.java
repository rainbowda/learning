package com.learnRedis.sortedset.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/zset")
public class Action {

    private static final String ZSET_KEY = "articleList";

    private RedisTemplate redisTemplate;

    private ZSetOperations zSetOperations;

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();

    }

    /**
     * 列表
     *
     * @return
     */
    @RequestMapping(value = "/getList/{sortType}", method = RequestMethod.GET)
    public Set getList(@PathVariable String sortType) {
        //如果没有数据，则添加10条数据
        if (zSetOperations.size(ZSET_KEY) == 0){
            for (int i = 1; i <= 10; i++) {
                zSetOperations.add(ZSET_KEY,"文章:"+i, (int)(Math.random()*10+i));
            }
        }

        //ASC根据分数从小到大排序,DESC反之
        if ("ASC".equals(sortType)){
            return zSetOperations.rangeWithScores(ZSET_KEY, 0, -1);
        } else {
            return zSetOperations.reverseRangeWithScores(ZSET_KEY, 0, -1);
        }
    }


    /**
     * 赞或踩
     * @param member
     * @param type
     * @return
     */
    @RequestMapping(value = "/star", method = RequestMethod.POST)
    public boolean starOrUnStar(String member, String type) {
        if ("UP".equals(type)){
            zSetOperations.incrementScore(ZSET_KEY, member, 1);
        } else {
            zSetOperations.incrementScore(ZSET_KEY, member, -1);
        }
        return true;
    }

    /**
     * 获取排名
     * @param member
     * @param type
     * @return
     */
    @RequestMapping(value = "/rank/{type}/{member}", method = RequestMethod.GET)
    public Long rank(@PathVariable String member, @PathVariable String type) {
        Long rank = null;
        if ("ASC".equals(type)){
            rank = zSetOperations.rank(ZSET_KEY, member);
        } else {
            rank = zSetOperations.reverseRank(ZSET_KEY, member);
        }

        return rank;
    }


}
