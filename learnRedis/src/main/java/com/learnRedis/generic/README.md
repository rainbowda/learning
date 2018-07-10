这篇主要讲述redis基本的命令，[官网地址](https://redis.io/commands#generic)

![](C:\IdeaProjects\learnWay\learnRedis\img\generic  Commands.png)

##### DEL命令

根据key移除，忽略不存在的key

```
set key1 "key1"
set key2 "key2"
del key1 key2 key3
```



```java
@Test
public void del() {

    jedis.set("key1", "key1");
    jedis.set("key2", "key2");

    Long delNum = jedis.del("key1", "key2", "key3");
    System.out.println(delNum);

    //spring
    redisTemplate.opsForValue().set("key1", "key1");
    redisTemplate.opsForValue().set("key2", "key2");

    String[] strs = {"key1", "key2", "key3"};
    delNum = redisTemplate.delete(Arrays.asList(strs));
    System.out.println(delNum);
}
```



##### DUMP命令

序列化给定 key的value值 ，并返回被序列化的值

```
set dump dumpdump
dump dump
```



```java
@Test
public void dump() {
    jedis.set("dump", "dumpdump");

    byte[] dumpData = jedis.dump("dump");
    System.out.println(Arrays.toString(dumpData));

    //spring
    dumpData = redisTemplate.dump("dump");
    System.out.println(Arrays.toString(dumpData));
}
```

##### EXISTS命令

检查key 是否存在

```
set exist exist
exists exist
exists noExist
```



```java
@Test
public void exists() {
    jedis.set("exists", "exists");

    Boolean exists = jedis.exists("exists");
    Boolean noExists = jedis.exists("noExists");
    System.out.println(exists);
    System.out.println(noExists);

    //spring
    exists = redisTemplate.hasKey("exists");
    noExists = redisTemplate.hasKey("noExists");
    System.out.println(exists);
    System.out.println(noExists);
}
```

##### EXPIRE命令

设置 key 的过期时间

```
set expire expire
expire expire 10
ttl expire
```



```java
@Test
public void expire() throws InterruptedException {
    jedis.set("expire", "expire");
    jedis.expire("expire", 10);

    System.out.println(jedis.ttl("expire"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.ttl("expire"));

    //spring
    //三个参数，key，时间，时间单位
    redisTemplate.expire("expire", 5, TimeUnit.SECONDS);
    System.out.println(redisTemplate.getExpire("expire"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(redisTemplate.getExpire("expire"));
}
```

##### EXPIREAT命令

以 UNIX 时间戳(unix timestamp)格式设置 key 的过期时间

```
set expireAt expireAt
expireAt expire 1531187220
```



```java
@Test
public void expireAt() throws InterruptedException {
    jedis.set("expireAt", "expireAt");
    jedis.expireAt("expireAt", (System.currentTimeMillis() + 3000) / 1000);

    System.out.println(jedis.ttl("expireAt"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.ttl("expireAt"));

    //spring
    redisTemplate.expireAt("expireAt", new Date(System.currentTimeMillis() + 3000));
    System.out.println(redisTemplate.getExpire("expireAt"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(redisTemplate.getExpire("expireAt"));
}
```

##### KEYS命令

##### MIGRATE命令

##### MOVE命令

##### OBJECT命令

##### PERSIST命令

##### PEXPIRE命令

##### PEXPIREAT命令

##### PTTL命令

##### RANDOMKEY命令

##### RENAME命令

##### RENAMENX命令

##### RESTORE命令

##### SCAN命令

##### SORT命令

##### TOUCH命令

##### TTL命令

##### TYPE命令

##### UNLINK命令

##### WAIT命令