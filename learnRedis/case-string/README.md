该案例采用redis当做
定义一个key前缀
```java
private static final String MY_LOG_REDIS_KEY_PREFIX = "myLog:";
private static final String MY_LOG_REDIS_ID_KEY = "myLogID";
```

先弄个新增方法
```java
@RequestMapping(value = "/addMyLog",method = RequestMethod.POST)
    public boolean addMyLog(@RequestBody JSONObject myLog){
        Long myLogId = redisTemplate.opsForValue().increment(MY_LOG_REDIS_ID_KEY, 1);

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        myLog.put("id",myLogId);
        myLog.put("createDate", date);
        myLog.put("updateDate", date);

        redisTemplate.opsForValue().set(MY_LOG_REDIS_KEY_PREFIX+myLogId, myLog.toString());

        return true;
    }
```
介绍下incr、incrBy、INCRBYFLOAT、DECR、DECRBy、
set、setex、setnx

接着写个查询方法，将新增的内容查询出来

```java
@RequestMapping("/getMyLog")
    public List getMyLog(){
        //获取mylog的keys
        Set myLogKeys = redisTemplate.keys("myLog:*");

        return  redisTemplate.opsForValue().multiGet(myLogKeys);
    }
```

keys、get、mget、GETRANGE、GETSET




