package com.learnRedis.hash.action;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/contacts")
public class ContactsAction {

    private static final String CONTACTS_KEY_PREFIX = "contacts:";
    private static final String CONTACTS_ID_KEY = "contactsID";

    private RedisTemplate redisTemplate;

    private HashOperations hashOperations;

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
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 列表
     * @return
     */
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public List getList(){
        List list = new ArrayList();

        //获取联系人的keys
        Set<String> keys = redisTemplate.keys(CONTACTS_KEY_PREFIX+"*");

        for (String key: keys) {
            Map entries = hashOperations.entries(key);
            list.add(entries);
        }

        return list;
    }


    /**
     * 列表
     * @return
     */
    @RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
    public Map getById(@PathVariable  String id){
        return hashOperations.entries(CONTACTS_KEY_PREFIX+id);
    }


    /**
     * 新增
     * @param contacts
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public boolean add(@RequestBody JSONObject contacts){
        //获取自增id
        Long contactsId = valueOperations.increment(CONTACTS_ID_KEY, 1);

        contacts.put("id",String.valueOf(contactsId));
        //json转map，然后存入redis
        hashOperations.putAll(CONTACTS_KEY_PREFIX+contactsId,contacts.getInnerMap());

        return true;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
    public boolean del(@PathVariable  String id){

        return redisTemplate.delete(CONTACTS_KEY_PREFIX + id);
    }

    /**
     * 添加属性
     * @param contacts
     * @return
     */
    @RequestMapping(value = "/addAttr", method = RequestMethod.POST)
    public boolean addAttr(@RequestBody JSONObject contacts){
        String id = contacts.getString("id");
        String fieldName = contacts.getString("fieldName");
        String fieldValue = contacts.getString("fieldValue");

        hashOperations.put(CONTACTS_KEY_PREFIX+id, fieldName, fieldValue);

        return true;
    }

    /**
     * 删除属性
     * @param contacts
     * @return
     */
    @RequestMapping(value = "/delAttr", method = RequestMethod.POST)
    public boolean delAttr(@RequestBody JSONObject contacts){

        String id = contacts.getString("id");
        String fieldName = contacts.getString("fieldName");
        hashOperations.delete(CONTACTS_KEY_PREFIX+id, fieldName);

        return true;
    }

}
