### 前言

这一篇文章将讲述Redis中的set类型命令，同样也是通过demo来讲述，其他部分这里就不在赘述了。

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-set](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-set)
### 案例

demo功能是共同好友，整个demo的大致页面如下。左边是存储到Redis中的数据，右边是从Redis中弹出的数据。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E9%A1%B5%E9%9D%A2.png?raw=true)

### 准备工作

首先定义一个存储a、b好友的key

```java
private static final String A_FRIEND_KEY = "friend:a";

private static final String B_FRIEND_KEY = "friend:b";
```


redis操作对象

```java
private RedisTemplate redisTemplate;
//string 命令操作对象
private ValueOperations valueOperations;
//set 命令操作对象
private SetOperations setOperations;
```


set在Redis中的结构可以看下图（图片来源于Redis in Action）。

![图片来源于Redis in Action](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E7%BB%93%E6%9E%84.png?raw=true)

### 添加好友
#### 命令介绍

| 命令 | 用例                         | 描述                                         |
| ---- | ---------------------------- | -------------------------------------------- |
| SADD | SADD key member [member ...] | 添加一个或多个指定的member元素到集合的 key中 |

我们来看看demo中的新增功能，点击添加好友，往用户A里面添加一些好友。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E6%B7%BB%E5%8A%A0%E5%A5%BD%E5%8F%8B.gif?raw=true)

添加完毕后，A有好友1、2、3，B有好友2、3、4。

后台java代码如下

```java
@RequestMapping(value = "/addFriend", method = RequestMethod.POST)
public Long addFriend(String user, String friend) {
    String currentKey = A_FRIEND_KEY;
    if ("B".equals(user)) {
        currentKey = B_FRIEND_KEY;
    }
    //返回添加成功的条数
    return setOperations.add(currentKey, friend);
}
```

相同的redis命令如下

```
SADD friend:a 1 2 3
SADD friend:b 2 3 4
```

好友结构如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E5%A5%BD%E5%8F%8B%E7%BB%93%E6%9E%84.png?raw=true)





### 删除好友

#### 命令介绍

| 命令 | 用例                         | 描述                      |
| ---- | ---------------------------- | ------------------------- |
| SREM | SREM key member [member ...] | 在key集合中移除指定的元素 |

删除功能如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E5%88%A0%E9%99%A4%E5%A5%BD%E5%8F%8B.gif?raw=true)

后台java代码如下

```java
@RequestMapping(value = "/delFriend", method = RequestMethod.DELETE)
public Long delFriend(String user, String friend) {
    String currentKey = A_FRIEND_KEY;
    if ("B".equals(user)) {
        currentKey = B_FRIEND_KEY;
    }
    //返回删除成功的条数
    return setOperations.remove(currentKey, friend);
}
```

相同的redis命令如下

```
SREM friend:b 5
```



### 列表查询

#### 命令介绍

| 命令     | 用例         | 描述                   |
| -------- | ------------ | ---------------------- |
| SMEMBERS | SMEMBERS key | 返回key集合所有的元素. |

后台java代码如下，分别查出A和B的好友，然后添加到map里

```java
@RequestMapping(value = "/getList", method = RequestMethod.GET)
public Map getList() {
    Map map = new HashMap();

    Set aFriend = setOperations.members(A_FRIEND_KEY);
    Set bFriend = setOperations.members(B_FRIEND_KEY);

    map.put("aFriend", aFriend);
    map.put("bFriend", bFriend);

    return map;
}
```

相同的redis命令如下

```
SMEMBERS friend:a
SMEMBERS friend:b
```



### 共同好友
#### 命令介绍

| 命令        | 用例                                  | 描述                                                         |
| ----------- | ------------------------------------- | ------------------------------------------------------------ |
| SINTER      | SINTER key [key ...]                  | 返回指定所有的集合的成员的交集.                              |
| SINTERSTORE | SINTERSTORE destination key [key ...] | 这个命令与SINTER命令类似, 但是它并不是直接返回结果集,而是将结果保存在 destination集合中. |

页面如下，点击共同好友按钮，经过后台的数据获取，页面下方显示共同好友2、3。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E5%85%B1%E5%90%8C%E5%A5%BD%E5%8F%8B.gif?raw=true)

共同好友也就是好友A和好友B共有的好友，两个数据做交集即可得到共有的数据，即A好友∩B好友={1,2,3}∩{2,3,4}={2,3} 。红色部分就是交集的结果

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/AB%E5%A5%BD%E5%8F%8B%E4%BA%A4%E9%9B%86.png?raw=true)

后台代码如下

```java
@RequestMapping(value = "/intersectFriend", method = RequestMethod.GET)
public Set intersectFriend() {
    return setOperations.intersect(A_FRIEND_KEY, B_FRIEND_KEY);
}
```

相同的redis命令如下

```
SINTER friend:a friend:b
```

### A独有的好友

#### 命令介绍

| 命令       | 用例                                 | 描述                                                         |
| ---------- | ------------------------------------ | ------------------------------------------------------------ |
| SDIFF      | SDIFF key [key ...]                  | 返回一个集合与给定集合的差集的元素.                          |
| SDIFFSTORE | SDIFFSTORE destination key [key ...] | 该命令类似于 SDIFF命令, 不同之处在于该命令不返回结果集，而是将结果存放在`destination`集合中. |

页面如下，点击A独有的好友按钮，经过后台的数据获取，页面下方显示独有的好友1。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/A%E7%8B%AC%E6%9C%89%E7%9A%84%E5%A5%BD%E5%8F%8B.gif?raw=true)

A独有的好友也就是取出A的好友在B好友中没有出现过的，也就是取差集，即A好友-B好友={1,2,3}-{2,3,4}={1}，下方图片中红色部分就是差集的结果。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/AB%E5%A5%BD%E5%8F%8B%E5%B7%AE%E9%9B%86.png?raw=true)

后台java代码如下

```java
@RequestMapping(value = "/differenceFriend", method = RequestMethod.GET)
public Set differenceFriend(String user) {
    return setOperations.difference(A_FRIEND_KEY, B_FRIEND_KEY);
}
```

相同的redis命令如下

```
SDIFF friend:a friend:b
```

### 所有的好友

#### 命令介绍

| 命令        | 用例                                  | 描述                                                         |
| ----------- | ------------------------------------- | ------------------------------------------------------------ |
| SUNION      | SUNION key [key ...]                  | 返回给定的多个集合的并集中的所有成员.                        |
| SUNIONSTORE | SUNIONSTORE destination key [key ...] | 该命令作用类似于SUNION命令,不同的是它并不返回结果集,而是将结果存储在destination集合中. |

页面如下，点击所有的好友按钮，经过后台的数据获取，页面下方显示共同好友1、2、3、4。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/%E6%89%80%E6%9C%89%E7%9A%84%E5%A5%BD%E5%8F%8B.gif?raw=true)

所有的好友就是A和B的好友,也就是A好友和B好友的并集，即A好友∪ B好友={1,2,3}∪ {2,3,4}={1,2,3,4}，图片如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-set/AB%E5%A5%BD%E5%8F%8B%E5%B9%B6%E9%9B%86.png?raw=true)

后台java代码如下

```java
@RequestMapping(value = "/unionFriend", method = RequestMethod.GET)
public Set unionFriend() {
    return setOperations.union(A_FRIEND_KEY, B_FRIEND_KEY);
}
```

相同的redis命令如下

```
SUNION friend:a friend:b
```



### 其他命令

| 命令        | 用例                                          | 描述                                        |
| ----------- | --------------------------------------------- | ------------------------------------------- |
| SCARD       | SCARD key                                     | 返回集合存储的key的基数 (集合元素的数量).   |
| SISMEMBER   | SISMEMBER key member                          | 返回成员 member 是否是存储的集合 key的成员. |
| SMOVE       | SMOVE source destination member               | 将member从source集合移动到destination集合中 |
| SPOP        | SPOP key [count]                              | 返回移除的一个或者多个key中的元素           |
| SRANDMEMBER | SRANDMEMBER key [count]                       | 随机返回key集合中的一个或者多个元素         |
| SSCAN       | SSCAN key cursor [MATCH pattern][COUNT count] | 和scan类似                                  |

#### SCARD命令
返回集合存储的key的基数 (集合元素的数量).  
SCARD key  
返回值：集合的基数(元素的数量),如果key不存在,则返回 0.  

redis客户端执行的命令如下
```
sadd sCardKey 1 2 3
scard sCardKey
```

下面是java代码
```java
@Test
public void sCard() {
    jedis.sadd("sCardKey", "1", "2", "3");

    System.out.println(jedis.scard("sCardKey"));

    //spring redisTemplate
    System.out.println(setOperations.size("sCardKey"));
}
```
#### SISMEMBER命令
返回成员 member 是否是存储的集合 key的成员.  
SISMEMBER key member  
返回值：如果member元素是集合key的成员，则返回1。如果member元素不是key的成员，或者集合key不存在，则返回0  

redis客户端执行的命令如下
```
 sadd sIsMemberKey hello
 sismember sIsMemberKey hello
 sismember sIsMemberKey redis
```

下面是java代码
```java
@Test
public void sIsMember() {
    jedis.sadd("sIsMember", "hello");

    System.out.println(jedis.sismember("sIsMember", "hello"));

    //spring redisTemplate
    System.out.println(setOperations.isMember("sIsMember", "redis"));
}
```
#### SMOVE命令
将member从source集合移动到destination集合中. 对于其他的客户端,在特定的时间元素将会作为source或者destination集合的成员出现.  
如果source 集合不存在或者不包含指定的元素,这smove命令不执行任何操作并且返回0.  
否则对象将会从source集合中移除，并添加到destination集合中去，  
如果destination集合已经存在该元素，则smove命令仅将该元素充source集合中移除.  
如果source 和destination不是集合类型,则返回错误.  
SMOVE source destination member  
返回值：如果该元素成功移除,返回1。如果该元素不是 source集合成员,无任何操作,则返回0.  

redis客户端执行的命令如下  
```
sadd sMoveKeySrc 0 1 2 3 4
smove sMoveKeySrc sMoveKeyDst 5
smove sMoveKeySrc sMoveKeyDst 3
smembers sMoveKeyDst
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/set/smove%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void sMove() {
    jedis.sadd("sMoveKeySrc", "0", "1", "2", "3", "4");

    System.out.println("移动一个不存在的元素,结果:"+jedis.smove("sMoveKeySrc", "sMoveKeyDst", "5"));

    //spring redisTemplate
    System.out.println("移动一个存在的元素,结果:" + setOperations.move("sMoveKeySrc","3", "sMoveKeyDst"));

    System.out.println(jedis.smembers("sMoveKeyDst"));
}
```
#### SPOP命令
移除且返回一个或多个随机元素  
SPOP key [count]  
返回值：移除的元素，当key不存在时返回nil  

redis客户端执行的命令如下  
```
sadd sPopKey 0 1 2 3 4
spop sPopKey
spop sPopKey 2
smembers sPopKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/set/spop%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void sPop() {
    jedis.sadd("sPopKey", "0", "1", "2", "3", "4");

    System.out.println(jedis.spop("sPopKey"));

    //spring redisTemplate
    System.out.println(setOperations.pop("sPopKey", 2));

    System.out.println(jedis.smembers("sPopKey"));

}
```
#### SRANDMEMBER命令
仅提供key参数,那么随机返回key集合中的一个元素.  
Redis 2.6开始, 可以接受 count 参数,  
如果count是整数且小于元素的个数，返回含有 count 个不同的元素的数组,  
如果count是个整数且大于集合中元素的个数时,仅返回整个集合的所有元素,  
当count是负数,则会返回一个包含count的绝对值的个数元素的数组，  
如果count的绝对值大于元素的个数,则返回的结果集里会出现一个元素出现多次的情况.  
仅提供key参数时,该命令作用类似于SPOP命令, 不同的是SPOP命令会将被选择的随机元素从集合中移除, 而SRANDMEMBER仅仅是返回该随记元素,而不做任何操作.  

SRANDMEMBER key [count]  
返回值：不使用count 参数的情况下该命令返回随机的元素,如果key不存在则返回nil.使用count参数,则返回一个随机的元素数组,如果key不存在则返回一个空的数组.    

redis客户端执行的命令如下    
```
sadd sRandMemberKey 0 1 2 3 4
srandmember sRandMemberKey 2
srandmember sRandMemberKey 9
srandmember sRandMemberKey -2
srandmember sRandMemberKey -9
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/set/srandmember%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void sRandMember() {
    jedis.sadd("sRandMemberKey", "0", "1", "2", "3", "4");

    System.out.println("未加count参数:" + jedis.srandmember("sRandMemberKey"));
    System.out.println("count是整数且小于元素的个数:" + jedis.srandmember("sRandMemberKey", 2));
    System.out.println("count是个整数且大于集合中元素的个数时:" + jedis.srandmember("sRandMemberKey", 9));
    System.out.println("count是整数且小于元素的个数:" + jedis.srandmember("sRandMemberKey", -2));
    System.out.println("count是个整数且大于集合中元素的个数时:" + jedis.srandmember("sRandMemberKey", -9));

    //spring redisTemplate默认支持重复的元素
    System.out.println("count是负数,且绝对值大于元素的个数:" + setOperations.randomMembers("sRandMemberKey", 9));
}
```
还是那句话建议学习的人最好每个命令都去敲下，加深印象。

> 纸上得来终觉浅，绝知此事要躬行。————出自《冬夜读书示子聿》