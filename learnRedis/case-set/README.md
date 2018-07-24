### 前言

这一篇文章将讲述Redis中的set类型命令，同样也是通过demo来讲述，其他部分这里就不在赘述了。

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-list](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-set)
### 案例

demo功能是共同好友，整个demo的大致页面如下。左边是存储到Redis中的数据，右边是从Redis中弹出的数据。

![]()

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

![图片来源于Redis in Action]()

### 添加好友
#### 命令介绍

| 命令 | 用例                         | 描述                                         |
| ---- | ---------------------------- | -------------------------------------------- |
| SADD | SADD key member [member ...] | 添加一个或多个指定的member元素到集合的 key中 |
|      |                              |                                              |
|      |                              |                                              |



### 删除好友
#### 命令介绍

| 命令 | 用例                         | 描述                         |
| ---- | ---------------------------- | ---------------------------- |
| SREM | SREM key member [member ...] | SREM key member [member ...] |
|      |                              |                              |
|      |                              |                              |

### 列表查询

#### 命令介绍

| 命令     | 用例         | 描述                   |
| -------- | ------------ | ---------------------- |
| SMEMBERS | SMEMBERS key | 返回key集合所有的元素. |
|          |              |                        |
|          |              |                        |



### 共同好友
#### 命令介绍

| 命令        | 用例                                  | 描述                                                         |
| ----------- | ------------------------------------- | ------------------------------------------------------------ |
| SINTER      | SINTER key [key ...]                  | 返回指定所有的集合的成员的交集.                              |
| SINTERSTORE | SINTERSTORE destination key [key ...] | 这个命令与SINTER命令类似, 但是它并不是直接返回结果集,而是将结果保存在 destination集合中. |
|             |                                       |                                                              |



### A独有的好友
#### 命令介绍

| 命令       | 用例                                 | 描述                                                         |
| ---------- | ------------------------------------ | ------------------------------------------------------------ |
| SDIFF      | SDIFF key [key ...]                  | 返回一个集合与给定集合的差集的元素.                          |
| SDIFFSTORE | SDIFFSTORE destination key [key ...] | 该命令类似于 SDIFF命令, 不同之处在于该命令不返回结果集，而是将结果存放在`destination`集合中. |
|            |                                      |                                                              |



### 所有的好友
#### 命令介绍

| 命令        | 用例                                  | 描述                                                         |
| ----------- | ------------------------------------- | ------------------------------------------------------------ |
| SUNION      | SUNION key [key ...]                  | 返回给定的多个集合的并集中的所有成员.                        |
| SUNIONSTORE | SUNIONSTORE destination key [key ...] | 该命令作用类似于SUNION命令,不同的是它并不返回结果集,而是将结果存储在destination集合中. |
|             |                                       |                                                              |

### 其他命令

| 命令        | 用例                                          | 描述                                        |
| ----------- | --------------------------------------------- | ------------------------------------------- |
| SCARD       | SCARD key                                     | 返回集合存储的key的基数 (集合元素的数量).   |
| SISMEMBER   | SISMEMBER key member                          | 返回成员 member 是否是存储的集合 key的成员. |
| SMOVE       | SMOVE source destination member               | 将member从source集合移动到destination集合中 |
| SPOP        | SPOP key [count]                              | 返回移除的一个或者多个key中的元素           |
| SRANDMEMBER | SRANDMEMBER key [count]                       | 随机返回key集合中的一个或者多个元素         |
| SSCAN       | SSCAN key cursor [MATCH pattern][COUNT count] | 和scan类似                                  |

