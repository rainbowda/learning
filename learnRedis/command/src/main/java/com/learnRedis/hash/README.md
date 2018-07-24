##### HSET命令
设置 key 指定的哈希集中指定字段的值。  
如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。  
如果字段在哈希集中存在，它将被重写。  
HSET key field value  
返回值：如果field是一个新的字段返回1,如果field原来在map里面已经存在返回0  


##### HSETNX命令
只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。如果字段已存在，该操作无效果。  
HSETNX key field value  
返回值：1：如果字段是个新的字段，并成功赋值。0：如果哈希集中已存在该字段，没有操作被执行  

##### HGET命令
返回 key 指定的哈希集中该字段所关联的值  
HGET key field  
返回值：该字段所关联的值。当字段不存在或者 key 不存在时返回nil。  

##### HGETALL命令
返回 key 指定的哈希集中所有的字段和值。返回值中，每个字段名的下一个是它的值，所以返回值的长度是哈希集大小的两倍  
HGETALL key  

redis客户端执行的命令如下
```
hset key field1 "Hi"
hset key field1 "Hello"
hsetnx key field1 "Hello"
hsetnx key field2 " redis"
hget key field1
hgetall key
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hset%E5%92%8Chget%E7%9B%B8%E5%85%B3%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void hGetAll() {
    jedis.hset("key", "field1", "Hi");
    redisTemplate.opsForHash().put("key", "field1", "Hello");

    System.out.println(jedis.hsetnx("key", "field1", "Hello"));
    System.out.println(redisTemplate.opsForHash().putIfAbsent("key", "field2", "Hello"));

    System.out.println(jedis.hget("key", "field1"));
    System.out.println(jedis.hgetAll("key"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().get("key", "field1"));
    System.out.println(redisTemplate.opsForHash().entries("key"));
}
```
##### HDEL命令
从 key 指定的哈希集中移除指定的field。在哈希集中不存在的field将被忽略。如果 key 指定的哈希集不存在，它将被认为是一个空的哈希集，该命令将返回0。  
HDEL key field [field ...]  
返回值： 返回从哈希集中成功移除的域的数量，不包括指出但不存在的那些域  

redis客户端执行的命令如下
```
hset hDelKey filed1 filedValue1
hdel hDelKey filed1
hdel hDelKey filed1
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hdel%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void HDel() {
    jedis.hset("hDelKey", "filed1", "filedValue1");

    System.out.println(jedis.hdel("hDelKey", "filed1"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().delete("hDelKey","filed1"));
}
```
##### HEXISTS命令
返回hash里面field是否存在  
HEXISTS key field  
返回值：1 hash里面包含该field。0 hash里面不包含该field或者key不存在。  

redis客户端执行的命令如下
```
hset hExistsKey filed1 filedValue1
hexists hExistsKey filed1
hexists hExistsKey filed2
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hexists%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hExists() {
    jedis.hset("hExistsKey", "filed1", "filedValue1");

    System.out.println(jedis.hexists("hExistsKey", "filed1"));
    System.out.println(jedis.hexists("hExistsKey", "filed2"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().hasKey("hExistsKey", "filed1"));
    System.out.println(redisTemplate.opsForHash().hasKey("hExistsKey", "filed2"));
}
```
##### HINCRBY命令
增加 key 指定的哈希集中指定字段的数值。如果 key 不存在，会创建一个新的哈希集并与 key 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0  
HINCRBY key field increment  
返回值：增值操作执行后的该字段的值。  

redis客户端执行的命令如下  
```
hincrby hIncrByKey field 2
```

下面是java代码   
```java
@Test
public void hIncrBy() {
    System.out.println(jedis.hincrBy("hIncrByKey", "field", 2));

    System.out.println(redisTemplate.opsForHash().increment("hIncrByKey", "field", 3));
}
```
##### HINCRBYFLOAT命令
为指定key的hash的field字段值执行float类型的increment加。  
HINCRBYFLOAT key field increment  
返回值：field执行increment加后的值 
  
redis客户端执行的命令如下  
```
hincrbyfloat hIncrByFloatKey field 2.22
```

下面是java代码
```java
@Test
public void hIncrByFloat() {
    System.out.println(jedis.hincrByFloat("hIncrByFloatKey", "field", 2.22));

    System.out.println(redisTemplate.opsForHash().increment("hIncrByFloatKey", "field", 3.33));
}
```
##### HKEYS命令
返回 key 指定的哈希集中所有字段的名字。  
HKEYS key  
返回值：哈希集中的字段列表，当 key 指定的哈希集不存在时返回空列表。  

redis客户端执行的命令如下  
```
hset hashKey field1 value1
hset hashKey field2 value2
hkeys hashKey
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hkeys%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hKeys() {
    jedis.hset("hashKey", "field1", "value1");
    jedis.hset("hashKey", "field2", "value2");

    System.out.println(jedis.hkeys("hashKey"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().keys("hashKey"));
    /**
     * 注：两次结果返回的顺序是不一样的，
     * 因为jedis.hkeys返回的是HashSet(内部使用HashMap)
     * redisTemplate.opsForHash().keys返回的是LinkHashSet（内部使用LinkHashMap）
     */
}
```
##### HVALS命令
返回 key 指定的哈希集中所有字段的值。  
HVALS key  
返回值：哈希集中的值的列表，当 key 指定的哈希集不存在时返回空列表。  

redis客户端执行的命令如下
```
hmset key field1 value1 field2 value2 field3 value3
hvals key
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hvals%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hVals() {
    Map<String, String> map = new HashMap<>(3);
    map.put("field1", "value1");
    map.put("field2", "value2");
    map.put("field3", "value3");

    jedis.hmset("key", map);

    System.out.println(jedis.hvals("key"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().values("key"));
}
```
##### HLEN命令
返回 key 指定的哈希集包含的字段的数量。  
HLEN key  
返回值：哈希集中字段的数量，当 key 指定的哈希集不存在时返回 0  

redis客户端执行的命令如下  
```
hset hLenKey field1 value1
hlen hLenKey
hlen hLenKey1
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hlen%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hLen() {
    jedis.hset("hLenKey", "field1", "value1");

    System.out.println(jedis.hlen("hLenKey"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().size("hLenKey"));
}
```
##### HSTRLEN命令
获取hash指定field的value的字符串长度  
HSTRLEN key field  
返回值：返回hash指定field的value的字符串长度，如果hash或者field不存在，返回0.  

redis客户端执行的命令如下
```
hmset key field1 value1 num 123
hstrlen key field1
hstrlen key num
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hstrlen%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

##### HMSET命令
和HSET类似，唯一不同的是HMSET可以设置多个field  
HMSET key field value [field value ...]  

##### HMGET命令
和HGET类似，唯一不同的是HMSET可以获取多个field  
HMGET key field [field ...]  

redis客户端执行的命令如下  
```
hmset key field1 value1 field2 value2 field3 value3
hmget key field1 field2 field3
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hmset%E5%92%8Chmget%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hMGet() {
    Map<String, String> map = new HashMap<>(3);
    map.put("field1", "value1");
    map.put("field2", "value2");
    map.put("field3", "value3");

    jedis.hmset("key", map);
    System.out.println(jedis.hmget("key", "field1", "field3"));

    //清空
    jedis.flushDB();

    //spring redisTemplate
    redisTemplate.opsForHash().putAll("key", map);
    System.out.println(redisTemplate.opsForHash().multiGet("key", map.keySet()));
}
```
