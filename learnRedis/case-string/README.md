### 前言

该文章将通过一个小demo将讲述Redis中的string类型命令。demo将以springboot为后台框架快速开发，iview前端框架进行简单的页面设计，为了方便就不使用DB存储数据了，直接采用Redis作为存储。

文中不会讲述springboot用法及项目搭建部分。直接根据功能方面进行讲述，穿插string命令操作说明。

如果需要详细了解该项目的其他部分，请点击下方项目Github地址

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-string](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-string)

> 纸上得来终觉浅，绝知此事要躬行————出自《冬夜读书示子聿》

### 案例

demo功能是记录日志，整个demo的大致页面如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-string/%E5%A4%A7%E8%87%B4%E9%A1%B5%E9%9D%A2.png?raw=true)

#### 准备工作

首先定义一个key的前缀，已经存储自增id的key

```java
private static final String MY_LOG_REDIS_KEY_PREFIX = "myLog:";
private static final String MY_LOG_REDIS_ID_KEY = "myLogID";
```

日志相关的key将会以myLog:1、myLog:2、myLog:3的形式存储

redis操作对象

```java
private RedisTemplate redisTemplate;
//string 命令操作对象
private ValueOperations valueOperations;
```

#### 新增

先来看看gif图吧

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-string/%E6%96%B0%E5%A2%9E.gif?raw=true)

来看看后台的方法

```java
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
```
从上面代码可以看出有两个操作redis的地方

> valueOperations.increment(MY_LOG_REDIS_ID_KEY, 1);
>
> valueOperations.set(MY_LOG_REDIS_KEY_PREFIX+myLogId, myLog.toString());



##### 命令介绍

valueOperations.increment其实就相当于Redis中的INCR、INCRBY、INCRBYFLOAT、DECR、DECRBY

###### INCR
> INCR key
对存储在指定key的数值执行原子的加1操作。没有对应的key则设置为0，再相加
###### INCRBY
> INCRBY key increment
其实和INCR类似，不同的是这个命令可以指定具体加多少
###### INCRBYFLOAT
> INCRBYFLOAT key increment
也是类似的，不同的是加的数值是浮点数
```
incrbyfloat incrByFloatKey 5.11
incrbyfloat incrByFloatKey 5.22
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/incrbyfloat%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void incrByFloat() {
    System.out.println(jedis.incrByFloat("incrByFloatKey", 5.11));

    System.out.println(redisTemplate.opsForValue().increment("incrByFloatKey", 5.22));
}
```
###### DECR
> DECR key
将存储的数字减key1。如果密钥不存在，则0在执行操作之前将其设置为。如果密钥包含错误类型的值或包含无法表示为整数的字符串，则会返回错误。该操作仅限于64位有符号整数。没有对应的key则设置为0，再相减.
其实通过INCRBY命令也可以是实现该命令。
###### DECRBY
> DECRBY key decrement
与INCRBY命令相似，指定减多少



valueOperations.set就是对应Redis的SET命令了，相关联的还有SETEX、SETNX和PSETEX。需要注意的是set在Redis版本2.6.12 提供了`EX` 、`PX`  、`NX`  、`XX`参数用于取代SETEX、SETNX和PSETEX，后续版本可能会移除SETEX、SETNX和PSETEX命令。下面是官网的原话

> Since the [SET](https://redis.io/commands/set) command options can replace [SETNX](https://redis.io/commands/setnx), [SETEX](https://redis.io/commands/setex), [PSETEX](https://redis.io/commands/psetex), it is possible that in future versions of Redis these three commands will be deprecated and finally removed. 



###### SET
> SET key value [expiration EX seconds|PX milliseconds] [NX|XX]
设置键key对应value
参数选项
EX seconds – 设置键key的过期时间，单位时秒
PX milliseconds – 设置键key的过期时间，单位时毫秒
NX – 只有键key不存在的时候才会设置key的值
XX – 只有键key存在的时候才会设置key的值

###### SETEX
> SETEX key seconds value
与SET key value EX seconds类似
###### SETNX
> SETNX key value
与SET key value NX类似,可以利用该命令实现分布式锁
```
setnx setNxKey setNxValue
setnx setNxKey setNxValue
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/setNx%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

###### PSETEX
> PSETEX key milliseconds value
与SET key value PX milliseconds类似
###### SETRANGE
> SETRANGE key offset value
替换从指定长度开始的字符
```
set setRangeKey "Hello World"
setrange setRangeKey 6 "Redis"
get setRangeKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/setrange%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void setRange() {
    jedis.set("setRangeKey", "Hello World");

    jedis.setrange("setRangeKey", 6 , "Redis");
    System.out.println(jedis.get("setRangeKey"));

    //spring
    redisTemplate.opsForValue().set("setRangeKey", "learyRedis", 6);
    System.out.println(redisTemplate.opsForValue().get("setRangeKey"));
}
```
###### MSET
> MSET key value [key value ...]
同时设置多个key、value
######　MSETNX
> MSETNX key value [key value ...]
同时设置多个key、value，key存在则忽略


#### 查询

接着写个查询方法，将新增的内容查询出来

```java
@RequestMapping(value = "/getMyLog",method = RequestMethod.GET)
public List getMyLog(){
    //获取mylog的keys
    Set myLogKeys = redisTemplate.keys("myLog:*");
    return  valueOperations.multiGet(myLogKeys);
}
```

方法中的两行都涉及到了Redis操作，先是通过keys命令获取`myLog:*`相关的key集合，然后通过multiGet方法（也就是mget命令）获取记录。

##### 命令介绍

###### KEYS
> KEYS pattern
查找所有符合给定模式pattern（正则表达式）的 key 
###### GET
> GET key
获取key对应的value
```
set getKey getValue
get getKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/set%E5%92%8Cget%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

###### GETRANGE
> GETRANGE key start end
获取start到end之间的字符
```
set getRangeKey "Hello learyRedis"
getrange getRangeKey 6 -1
getrange getRangeKey 0 -12
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/getrange%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

###### GETSET
> GETSET key value
设置key对应的新value且返回原来key对应的value
```
getset getSetKey newValue
set getSetKey value
getset getSetKey newValue
get getSetKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/getset%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

###### MGET
> MGET key [key ...]
返回所有指定的key的value
```
mset mGetKey1 mGetValue1 mGetKey2 mGetValue2 mGetKey3 mGetValue3
mget mGetKey1 mGetKey2 mGetKey3 mGetKey4
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/mset%E5%92%8Cmget%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)


#### 更新

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-string/%E6%9B%B4%E6%96%B0.gif?raw=true)



来看看代码

```java
@RequestMapping(value = "/updateMyLog",method = RequestMethod.POST)
public boolean updateMyLog(@RequestBody JSONObject myLog){
    String myLogId = myLog.getString("id");
    myLog.put("updateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    valueOperations.set(MY_LOG_REDIS_KEY_PREFIX+myLogId, myLog.toString());
    return true;
}
```

这里的set在新增方法里面讲述过,那么来看看APPEND、STRLEN命令吧

#####  命令介绍

###### APPEND
> APPEND key value
在value的尾部追加新值

redis客户端执行的命令如下
```
append appendKey append
append appendKey Value
get appendKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/string/append%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

###### STRLEN
> STRLEN key
返回value的长度


#### 删除

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-string/%E5%88%A0%E9%99%A4.gif?raw=true)

代码如下

```java
@RequestMapping(value = "/delMyLog/{id}", method = RequestMethod.DELETE)
public boolean delMyLog(@PathVariable  String id){
    return redisTemplate.delete(MY_LOG_REDIS_KEY_PREFIX + id);
}
```

可以看到代码中只用了delete方法，对应着Redis的DEL命令（属于基本命令）

##### 命令介绍

###### DEL
> DEL key [key ...]
删除key


### BIT相关命令

bit命令有SETBIT、GETBIT、BITCOUNT、BITFIELD、BITOP、BITPOS这些。
命令这里就不做介绍了，直接讲述bit相关的案例：位图。
> Pattern: real time metrics using bitmaps
> BITOP is a good complement to the pattern documented in the BITCOUNT command documentation. Different bitmaps can be combined in order to obtain a target bitmap where the population counting operation is performed.
> 
> See the article called "Fast easy realtime metrics using Redis bitmaps" for a interesting use cases.

案例地址[Fast easy realtime metrics using Redis bitmaps](http://blog.getspool.com/2011/11/29/fast-easy-realtime-metrics-using-redis-bitmaps)
网上译文也有许多，有需要的百度或者google即可
这里大概讲述下使用位图法统计日登入用户数、周连续登入用户数和月连续登入用户数

