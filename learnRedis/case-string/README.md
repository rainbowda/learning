### 前言

该文章将通过一个小demo将讲述Redis中的string类型命令。demo将以springboot为后台框架快速开发，iview前端框架进行简单的页面设计，为了方便就不使用DB存储数据了，直接采用Redis作为存储。

文中不会讲述springboot用法及项目搭建部分。直接根据功能方面进行讲述，穿插string命令操作说明。

如果需要详细了解该项目的其他部分，请点击下方项目Github地址

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-string](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-string)

### 案例

demo功能是记录日志，整个demo的大致页面如下

![](C:\IdeaProjects\learnWay\learnRedis\img\case-string\大致页面.png)

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

![](C:\IdeaProjects\learnWay\learnRedis\img\case-string\新增.gif)

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

###### INCRBY

###### INCRBYFLOAT

###### DECR

###### DECRBY



valueOperations.set就是对应Redis的SET命令了，相关联的还有SETEX、SETNX和PSETEX。需要注意的是set在Redis版本2.6.12 提供了`EX` 、`PX`  、`NX`  、`XX`参数用于取代SETEX、SETNX和PSETEX，后续版本可能会移除SETEX、SETNX和PSETEX命令。下面是官网的原话

> Since the [SET](https://redis.io/commands/set) command options can replace [SETNX](https://redis.io/commands/setnx), [SETEX](https://redis.io/commands/setex), [PSETEX](https://redis.io/commands/psetex), it is possible that in future versions of Redis these three commands will be deprecated and finally removed. 



###### SET

###### SETEX

###### SETNX

###### SETRANGE

###### PSETEX

###### MSET

######　MSETNX



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

###### GET

###### GETRANGE

###### GETSET

###### MGET



#### 更新

![](C:\IdeaProjects\learnWay\learnRedis\img\case-string\更新.gif)



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

###### STRLEN



#### 删除

![](C:\IdeaProjects\learnWay\learnRedis\img\case-string\删除.gif)

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



### BIT相关命令

bit命令有BITCOUNT、BITFIELD、BITOP、BITPOS这些。

###### 



