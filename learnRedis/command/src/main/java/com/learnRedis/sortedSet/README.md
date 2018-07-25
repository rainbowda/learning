#### ZADD命令
将所有指定成员添加到键为key有序集合（sorted set）里面。  
添加时可以指定多个分数/成员（score/member）对。  
如果指定添加的成员已经是有序集合里面的成员，则会更新改成员的分数（scrore）并更新到正确的排序位置。  
如果key不存在，将会创建一个新的有序集合（sorted set）并将分数/成员（score/member）对添加到有序集合，就像原来存在一个空的有序集合一样。如果key存在，但是类型不是有序集合，将会返回一个错误应答。  
分数值是一个双精度的浮点型数字字符串。+inf和-inf都是有效值。  

ZADD 参数（options） (>= Redis 3.0.2)  
ZADD 命令在key后面分数/成员（score/member）对前面支持一些参数，他们是：  

XX: 仅仅更新存在的成员，不添加新成员。  
NX: 不更新存在的成员。只添加新成员。  
CH: 修改返回值为发生变化的成员总数，原始是返回新添加成员的总数 (CH 是 changed 的意思)。更改的元素是新添加的成员，已经存在的成员更新分数。 所以在命令中指定的成员有相同的分数将不被计算在内。注：在通常情况下，ZADD返回值只计算新添加成员的数量。  
INCR: 当ZADD指定这个选项时，成员的操作就等同ZINCRBY命令，对成员的分数进行递增操作。  

ZADD key [NX|XX] [CH] [INCR] score member [score member ...]  
返回值：添加到有序集合的成员数量，不包括已经存在更新分数的成员。成员的新分数（双精度的浮点型数字）字符串。  

redis客户端执行的命令如下  
```
zadd zAddKey 1 one
zadd zAddKey 2 two
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zadd%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zAdd() {
    jedis.zadd("zAddKey",1, "one");
    jedis.zadd("zAddKey",2, "two");


    //spring redisTemplate
    zSetOperations.add("zAddKey", "one", 1);
    zSetOperations.add("zAddKey", "two", 2);

    System.out.println(zSetOperations.range("zAddKey", 0, -1));
}
```

#### ZSCORE命令
返回有序集key中，成员member的score值。  
ZSCORE key member  
返回值：成员member的score值  

redis客户端执行的命令如下  
```
zadd zScoreKey 1 one
ZSCORE zScoreKey one
```

下面是java代码  
```java
@Test
public void zScore() {
    jedis.zadd("zScoreKey",1, "one");

    System.out.println(jedis.zscore("zScoreKey", "one"));

    System.out.println(zSetOperations.score("zScoreKey", "one"));
}
```

#### ZRANGE命令
返回范围内的集合(从小到大)，加WITHSCORES参数显示分数  
ZRANGE key start stop [WITHSCORES]  

#### ZREVRANGE命令
返回范围内的集合(从大到小)  
ZREVRANGE key start stop [WITHSCORES]  

#### ZRANGEBYLEX命令
返回指定成员区间内的成员，按成员字典正序排序。https://redis.io/commands/zrangebylex  
ZRANGEBYLEX key min max [LIMIT offset count]  
返回值：指定成员范围的元素列表。  

redis客户端执行的命令如下  
```
ZADD zRangeByLexKey 0 ba 0 a 0 ab 0 aa 0 b
ZRANGEBYLEX zRangeByLexKey - +
ZRANGEBYLEX zRangeByLexKey [aa (ba
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zrangebylex%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zRangeByLex() {
    zSetOperations.add("zRangeByLexKey", "ba", 0);
    zSetOperations.add("zRangeByLexKey", "a", 0);
    zSetOperations.add("zRangeByLexKey", "ab", 0);
    zSetOperations.add("zRangeByLexKey", "aa", 0);
    zSetOperations.add("zRangeByLexKey", "b", 0);

    System.out.println(jedis.zrangeByLex("zRangeByLexKey", "-", "+"));

    RedisZSetCommands.Range range = new RedisZSetCommands.Range();
    range.gte("aa");
    range.lt("ba");
    System.out.println(zSetOperations.rangeByLex("zRangeByLexKey",range));
}
```

#### ZREVRANGEBYLEX命令
返回指定成员区间内的成员，按成员字典倒序排序  
ZREVRANGEBYLEX key max min [LIMIT offset count]  

#### ZRANGEBYSCORE命令
获取score在范围之内的数据。min和max可以是-inf和+inf  
ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]  

redis客户端执行的命令如下  
```
ZADD zRangeByScoreKey 1 ba 2 a 3 ab 4 aa 5 b
ZRANGEBYSCORE zRangeByScoreKey -inf +inf
ZRANGEBYSCORE zRangeByScoreKey 2 4
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zrangebyscore%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zRangeByScore() {
    zSetOperations.add("zRangeByScoreKey", "ba", 1);
    zSetOperations.add("zRangeByScoreKey", "a", 2);
    zSetOperations.add("zRangeByScoreKey", "ab", 3);
    zSetOperations.add("zRangeByScoreKey", "aa", 4);
    zSetOperations.add("zRangeByScoreKey", "b", 5);

    System.out.println(jedis.zrangeByScore("zRangeByScoreKey", "-inf", "+inf"));

    RedisZSetCommands.Range range = new RedisZSetCommands.Range();
    System.out.println(zSetOperations.rangeByScore("zRangeByScoreKey", 2, 4));
}
```

#### ZREVRANGEBYSCORE命令
返回有序集合中指定分数区间内的成员，分数由高到低排序  
ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]  

#### ZCARD命令
返回key的有序集元素个数。  
ZCARD key  
返回值：key存在的时候，返回有序集的元素个数，否则返回0。  

redis客户端执行的命令如下  
```
zadd zCardKey 1 one
zcard zCardKey
```

下面是java代码  
```java
@Test
public void zCard() {
    jedis.zadd("zCardKey",1, "one");
    jedis.zadd("zCardKey",2, "two");

    System.out.println(jedis.zcard("zCardKey"));

    System.out.println(zSetOperations.size("zCardKey"));
}
```

#### ZCOUNT命令
返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员数量。  
ZCOUNT key min max  
返回值：指定分数范围的元素个数。  

redis客户端执行的命令如下  
```
zadd zCountKey 1 one 2 two 3 three 4 four
zcount zCountKey 2 3
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zcount%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zCount() {
    jedis.zadd("zCountKey",1, "one");
    jedis.zadd("zCountKey",2, "two");
    jedis.zadd("zCountKey",3, "three");
    jedis.zadd("zCountKey",4, "four");

    System.out.println(jedis.zcount("zCountKey",2, 3));

    System.out.println(zSetOperations.count("zCountKey",2, 3));
}
```

#### ZINCRBY命令
为有序集key的成员member的score值加上增量increment。  
如果key中不存在member，就在key中添加一个member，score是increment（就好像它之前的score是0.0）。  
如果key不存在，就创建一个只含有指定member成员的有序集合。  
当key不是有序集类型时，返回一个错误。  
score值必须是字符串表示的整数值或双精度浮点数，并且能接受double精度的浮点数。也有可能给一个负数来减少score的值。  

ZINCRBY key increment member  

redis客户端执行的命令如下  
```
zincrby zIncrByKey 2 score
zincrby zIncrByKey 5 score
zrange zIncrByKey 0 -1 WITHSCORES
```

下面是java代码  
```java
@Test
public void zIncrBy() {
    jedis.zincrby("zIncrByKey", 2, "score");

    zSetOperations.incrementScore("zIncrByKey", "score", 5);

    Set<ZSetOperations.TypedTuple> set = zSetOperations.rangeWithScores("zIncrByKey", 0, -1);
    for (ZSetOperations.TypedTuple typedTuple : set){
        System.out.println(typedTuple.getValue()+":"+typedTuple.getScore());
    }
}
```

#### ZUNIONSTORE命令
计算给定的numkeys个有序集合的并集，并且把结果放到destination中。  
WEIGHTS参数相当于权重，默认就是1，可以给不同的key设置不同的权重  
AGGREGATE参数默认使用的参数SUM，还可以选择MIN或者MAX。这个参数决定结果集的score是取给定集合中的相加值、最小值还是最大值  
ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]  

redis客户端执行的命令如下  
```
ZADD zUnionStoreKey1 1 "one" 2 "two"
ZADD zUnionStoreKey2 1 "one" 2 "two" 3 "three"
ZUNIONSTORE zUnionStoreSumResult 2 zUnionStoreKey1 zUnionStoreKey2 WEIGHTS 2 3
ZUNIONSTORE zUnionStoreMinResult 2 zUnionStoreKey1 zUnionStoreKey2 WEIGHTS 2 3 AGGREGATE MIN
ZUNIONSTORE zUnionStoreMaxResult 2 zUnionStoreKey1 zUnionStoreKey2 WEIGHTS 2 3 AGGREGATE MAX
     *
ZRANGE zUnionStoreSumResult 0 -1 WITHSCORES
ZRANGE zUnionStoreMinResult 0 -1 WITHSCORES
ZRANGE zUnionStoreMaxResult 0 -1 WITHSCORES
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zunionstore%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zUnionStore() {
    zSetOperations.add("zUnionStoreKey1", "one", 1);
    zSetOperations.add("zUnionStoreKey1", "two", 2);

    zSetOperations.add("zUnionStoreKey2", "one", 1);
    zSetOperations.add("zUnionStoreKey2", "two", 2);
    zSetOperations.add("zUnionStoreKey2", "three", 3);


    ZParams zParams = new ZParams();
    zParams.weightsByDouble(2, 3);
    zParams.aggregate(ZParams.Aggregate.SUM);
    jedis.zunionstore("zUnionStoreSumResult", zParams, "zUnionStoreKey1", "zUnionStoreKey2");

    //求最小值
    zParams.aggregate(ZParams.Aggregate.MIN);
    jedis.zunionstore("zUnionStoreMinResult", zParams, "zUnionStoreKey1", "zUnionStoreKey2");

    //求最大值
    zParams.aggregate(ZParams.Aggregate.MAX);
    jedis.zunionstore("zUnionStoreMaxResult", zParams, "zUnionStoreKey1", "zUnionStoreKey2");

    //spring
    zSetOperations.unionAndStore("zUnionStoreKey1", "zUnionStoreKey2", "zUnionStoreResult");


    printTuple("zUnionStoreSumResult", jedis.zrangeWithScores("zUnionStoreSumResult", 0, -1));
    printTuple("zUnionStoreMinResult", jedis.zrangeWithScores("zUnionStoreMinResult", 0, -1));
    printTuple("zUnionStoreMaxResult", jedis.zrangeWithScores("zUnionStoreMaxResult", 0, -1));
    printTuple("zUnionStoreResult", jedis.zrangeWithScores("zUnionStoreResult", 0, -1));
}
```

#### ZINTERSTORE命令
计算给定的numkeys个有序集合的交集，并且把结果放到destination中。  
在给定要计算的key和其它参数之前，必须先给定key个数(numberkeys)。  
默认情况下，结果中一个元素的分数是有序集合中该元素分数之和，前提是该元素在这些有序集合中都存在。因为交集要求其成员必须是给定的每个有序集合中的成员，结果集中的每个元素的分数和输入的有序集合个数相等。  
对于WEIGHTS和AGGREGATE参数的描述，参见命令ZUNIONSTORE。  
如果destination存在，就把它覆盖。  

ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]  
返回值：结果有序集合destination中元素个数。  
redis客户端执行的命令如下  
```
ZADD zInterStoreKey1 1 "one" 2 "two"
ZADD zInterStoreKey2 1 "one" 2 "two" 3 "three"
ZINTERSTORE zInterStoreSumResult 2 zInterStoreKey1 zInterStoreKey2 WEIGHTS 2 3

ZRANGE zInterStoreSumResult 0 -1 WITHSCORES
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zinterstore%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zInterStore() {
    zSetOperations.add("zInterStoreKey1", "one", 1);
    zSetOperations.add("zInterStoreKey1", "two", 2);

    zSetOperations.add("zInterStoreKey2", "one", 1);
    zSetOperations.add("zInterStoreKey2", "two", 2);
    zSetOperations.add("zInterStoreKey2", "three", 3);


    ZParams zParams = new ZParams();
    zParams.weightsByDouble(2, 3);
    zParams.aggregate(ZParams.Aggregate.SUM);
    jedis.zinterstore("zInterStoreSumResult", zParams, "zInterStoreKey1", "zInterStoreKey2");

    printTuple("zInterStoreSumResult", jedis.zrangeWithScores("zInterStoreSumResult", 0, -1));
}
```

#### ZLEXCOUNT命令
计算有序集合中指定成员之间的成员数量(按成员字典正序排序),可以使用 - 和 + 表示score最小值和最大值  
ZLEXCOUNT key min max  

redis客户端执行的命令如下  
```
ZADD zLexCountKey 2 "b" 1 "a" 3 "c" 5 "e" 4 "d"
ZLEXCOUNT zLexCountKey - +
ZLEXCOUNT zLexCountKey [b [d
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zlexcount%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zLexCount() {
    zSetOperations.add("zLexCountKey", "b", 2);
    zSetOperations.add("zLexCountKey", "a", 1);
    zSetOperations.add("zLexCountKey", "c", 3);
    zSetOperations.add("zLexCountKey", "e", 5);
    zSetOperations.add("zLexCountKey", "d", 4);

    System.out.println(jedis.zlexcount("zLexCountKey", "-", "+"));

    System.out.println(jedis.zlexcount("zLexCountKey", "[b", "[d"));
}
```

#### ZRANK命令
返回有序集key中成员member的排名。其中有序集成员按score值递增(从小到大)顺序排列。  
ZRANK key member  
#### ZREVRANK命令
返回有序集key中成员member的排名。其中有序集成员按score值递增(从大到小)顺序排列。  
ZREVRANK key member  
返回值：如果member是有序集key的成员，返回的排名。否则返回nil  

redis客户端执行的命令如下  
```
ZADD zRankKey 1 "one" 2 "two" 3 "three"
ZRANK zRankKey three
ZREVRANK zRankKey three
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zrank%E5%92%8Czrevrank%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zRevRank() {
    zSetOperations.add("zRankKey", "one", 1);
    zSetOperations.add("zRankKey", "two", 2);
    zSetOperations.add("zRankKey", "three", 3);

    System.out.println(jedis.zrank("zRankKey", "three"));

    System.out.println(zSetOperations.reverseRank("zRankKey", "three"));
}
```

#### ZREM命令
ZREM key member [member ...]  
返回值：有序集合中删除的成员个数  

redis客户端执行的命令如下  
```
ZADD zRemKey 1 "one" 2 "two" 3 "three"
ZREM zRemKey one
ZRANGE zRemKey 0 -1
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zrem%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void zRem() {
    zSetOperations.add("zRemKey", "one", 1);
    zSetOperations.add("zRemKey", "two", 2);
    zSetOperations.add("zRemKey", "three", 3);

    //jedis.zrem("zRemKey", "one");
    zSetOperations.remove("zRemKey", "one");

    System.out.println(zSetOperations.range("zRemKey", 0 , -1));
}
```

#### ZREMRANGEBYLEX命令
删除名称按字典由低到高排序成员之间所有成员。  
ZREMRANGEBYLEX key min max
返回值：有序集合中删除的成员个数  
#### ZREMRANGEBYRANK命令
移除有序集key中，指定排名(rank)区间内的所有成员。  
ZREMRANGEBYRANK key start stop
返回值：有序集合中删除的成员个数  
#### ZREMRANGEBYSCORE命令
移除有序集key中，所有score值介于min和max之间(包括等于min或max)的成员。  
ZREMRANGEBYSCORE key min max
返回值：有序集合中删除的成员个数  
#### ZSCAN命令
ZSCAN key cursor [MATCH pattern] [COUNT count]  

注：下方命令的redis版本5.0  


#### ZPOPMAX命令
移除且返回score最大的值  
ZPOPMAX key [count]
返回值：被移除的score和值  
#### BZPOPMAX命令
移除且返回score最大的值，没有值时会阻塞  
BZPOPMAX key [key ...] timeout  
#### ZPOPMIN命令
移除且返回score最小的值  
ZPOPMIN key [count]  
返回值：被移除的score和值  

redis客户端执行的命令如下
```
ZADD zPopKey 2 "b" 1 "a" 3 "c" 5 "e" 4 "d"
ZPOPMIN zPopKey
ZPOPMAX zPopKey
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/sortedSet/zpopmin%E5%92%8Czpopmax%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

#### BZPOPMIN命令
移除且返回score最小的值，没有值时会阻塞  
BZPOPMIN key [key ...] timeout  