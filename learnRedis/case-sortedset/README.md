### 前言

这一篇文章将讲述Redis中的sortedset类型命令，同样也是通过demo来讲述，其他部分这里就不在赘述了。

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-sortedset](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-sortedset)

### 案例

demo功能是文章点赞排名等等，整个demo的大致页面如下。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-sortedset/%E9%A1%B5%E9%9D%A2.png?raw=true)

### 准备工作

首先定义一个存储文章的key

```java
private static final String ZSET_KEY = "articleList";
```

redis操作对象

```java
private RedisTemplate redisTemplate;
//string 命令操作对象
private ValueOperations valueOperations;
//zset 命令操作对象
private ZSetOperations zSetOperations;
```

sortedset在Redis中的结构可以看下图（图片来源于Redis in Action）。  

![图片来源于Redis in Action](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-sortedset/%E7%BB%93%E6%9E%84.png?raw=true)

### 列表查询

```java
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
```
这里为了省去一个个添加数据的麻烦，就在获取列表数据中加了个判断。当文章数据为0时，默认添加10条数据，设置随机score加上所在的索引。
然后根据url中的参数sortType来决定返回的数据是按照分数升序还是降序排序。功能效果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-sortedset/%E5%8D%87%E9%99%8D%E5%BA%8F%E6%98%BE%E7%A4%BA.gif?raw=true)

#### 命令介绍
| 命令      | 用例                                                         | 描述                                                         |
| --------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ZADD      | ZADD key [NX\|XX][CH] [INCR] score member [score member ...] | 将所有指定成员添加到键为`key`有序集合（sorted set）里面      |
| ZRANGE    | ZRANGE key start stop [WITHSCORES]                           | 返回有序集key中，指定区间内的成员。其中成员的位置按score值递减(从小到大)来排列。 |
| ZREVRANGE | ZREVRANGE key start stop [WITHSCORES]                        | 返回有序集key中，指定区间内的成员。其中成员的位置按score值递减(从大到小)来排列。 |

### 赞或踩
java代码如下
```java
@RequestMapping(value = "/star", method = RequestMethod.POST)
public boolean starOrUnStar(String member, String type) {
    if ("UP".equals(type)){
        zSetOperations.incrementScore(ZSET_KEY, member, 1);
    } else {
        zSetOperations.incrementScore(ZSET_KEY, member, -1);
    }
    return true;
}
```
根据type决定是否加减分数，当type为UP时表示赞，为其他（DOWN）时表示踩。功能效果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-sortedset/%E5%8D%87%E9%99%8D%E5%BA%8F%E6%98%BE%E7%A4%BA.gif?raw=true)
#### 命令介绍

| 命令    | 用例                         | 描述                                              |
| ------- | ---------------------------- | ------------------------------------------------- |
| ZINCRBY | ZINCRBY key increment member | 为有序集key的成员member的score值加上增量increment |

### 升降序排名
java代码如下   
```java
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
```
根据type决定是升序排名还是降序排名，如果是ASC则调用rank方法获取升序排名，其他则调用reverseRank获取降序排名。与下方redis命令类似
```
ZRANK articleList "文章1"
ZREVRANK articleList "文章1"
```
页面效果图如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-sortedset/%E5%8D%87%E9%99%8D%E5%BA%8F%E6%8E%92%E5%90%8D.gif?raw=true)
#### 命令介绍

| 命令     | 用例                | 描述                                                         |
| -------- | ------------------- | ------------------------------------------------------------ |
| ZRANK    | ZRANK key member    | 返回有序集key中成员member的排名。其中有序集成员按score值递增(从小到大)顺序排列。排名以0为底，也就是说，score值最小的成员排名为0。 |
| ZREVRANK | ZREVRANK key member | 返回有序集key中成员member的排名，其中有序集成员按score值从大到小排列。 |



### 其他命令

#### 获取属性

| 命令      | 用例                  | 描述                                                         |
| --------- | --------------------- | ------------------------------------------------------------ |
| ZCARD     | ZCARD key             | 返回key的有序集元素个数。                                    |
| ZCOUNT    | ZCOUNT key min max    | 返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员个数。 |
| ZLEXCOUNT | ZLEXCOUNT key min max | 用于计算有序集合中指定成员之间的成员数量。                   |
| ZSCORE    | ZSCORE key member     | 返回有序集key中，成员member的score值。                       |

#### 获取成员

| 命令             | 用例                                                         | 描述                                                         |
| ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ZRANGEBYLEX      | ZRANGEBYLEX key min max [LIMIT offset count]                 | 返回指定成员区间内的成员，按成员字典正序排序, 分数必须相同。 |
| ZRANGEBYSCORE    | ZRANGEBYSCORE key min max [WITHSCORES][LIMIT offset count]   | 返回所有符合score条件的成员                                  |
| ZREVRANGEBYLEX   | ZREVRANGEBYLEX key max min [LIMIT offset count]              | 返回指定成员区间内的成员，按成员字典倒序排序, 分数必须相同。 |
| ZREVRANGEBYSCORE | ZREVRANGEBYSCORE key max min [WITHSCORES][LIMIT offset count] | 返回有序集合中指定分数区间内的成员，分数由高到低排序。       |
| ZSCAN            | ZSCAN key cursor [MATCH pattern][COUNT count]                | 请参考 SCAN                                                  |



#### 移除相关命令

| 命令             | 用例                           | 描述                                                         |
| ---------------- | ------------------------------ | ------------------------------------------------------------ |
| ZREM             | ZREM key member [member ...]   | 删除有序集合中的成员                                         |
| ZREMRANGEBYLEX   | ZREMRANGEBYLEX key min max     | 删除名称按字典由低到高排序成员之间所有成员                   |
| ZREMRANGEBYRANK  | ZREMRANGEBYRANK key start stop | 移除有序集key中，指定排名(rank)区间内的所有成员。            |
| ZREMRANGEBYSCORE | ZREMRANGEBYSCORE key min max   | 移除有序集key中，所有score值介于min和max之间(包括等于min或max)的成员 |

#### 交并集

| 命令        | 用例                                                         | 描述                                                         |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ZINTERSTORE | ZINTERSTORE destination numkeys key [key ...][WEIGHTS weight] [SUM\|MIN\|MAX] | 计算给定的numkeys个有序集合的交集，并且把结果放到destination中 |
| ZUNIONSTORE | ZUNIONSTORE destination numkeys key [key ...][WEIGHTS weight] [SUM\|MIN\|MAX] | 计算给定的numkeys个有序集合的并集，并且把结果放到destination中 |

