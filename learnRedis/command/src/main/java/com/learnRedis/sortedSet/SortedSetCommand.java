package com.learnRedis.sortedSet;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;
import org.springframework.data.redis.core.ZSetOperations;
import redis.clients.jedis.params.sortedset.ZAddParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SortedSetCommand extends RedisBaseConnection {

    /**
     * 将所有指定成员添加到键为key有序集合（sorted set）里面。
     * 添加时可以指定多个分数/成员（score/member）对。
     * 如果指定添加的成员已经是有序集合里面的成员，则会更新改成员的分数（scrore）并更新到正确的排序位置。
     * 如果key不存在，将会创建一个新的有序集合（sorted set）并将分数/成员（score/member）对添加到有序集合，就像原来存在一个空的有序集合一样。如果key存在，但是类型不是有序集合，将会返回一个错误应答。
     * 分数值是一个双精度的浮点型数字字符串。+inf和-inf都是有效值。
     *
     * ZADD 参数（options） (>= Redis 3.0.2)
     * ZADD 命令在key后面分数/成员（score/member）对前面支持一些参数，他们是：
     *
     * XX: 仅仅更新存在的成员，不添加新成员。
     * NX: 不更新存在的成员。只添加新成员。
     * CH: 修改返回值为发生变化的成员总数，原始是返回新添加成员的总数 (CH 是 changed 的意思)。更改的元素是新添加的成员，已经存在的成员更新分数。 所以在命令中指定的成员有相同的分数将不被计算在内。注：在通常情况下，ZADD返回值只计算新添加成员的数量。
     * INCR: 当ZADD指定这个选项时，成员的操作就等同ZINCRBY命令，对成员的分数进行递增操作。
     *
     * ZADD key [NX|XX] [CH] [INCR] score member [score member ...]
     * 返回值：添加到有序集合的成员数量，不包括已经存在更新分数的成员。成员的新分数（双精度的浮点型数字）字符串。
     * 命令：
     * zadd zAddKey 1 one
     * zadd zAddKey 2 two
     */
    @Test
    public void zAdd() {
        jedis.zadd("zAddKey",1, "one");
        jedis.zadd("zAddKey",2, "two");


        //spring redisTemplate
        redisTemplate.opsForZSet().add("zAddKey", "one", 1);
        redisTemplate.opsForZSet().add("zAddKey", "two", 2);

        System.out.println(redisTemplate.opsForZSet().range("zAddKey", 0, -1));
    }

    /**
     * 返回范围内的集合，加WITHSCORES参数显示分数
     * ZRANGE key start stop [WITHSCORES]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRange() {}

    /**
     * 返回key的有序集元素个数。
     * ZCARD key
     * 返回值：key存在的时候，返回有序集的元素个数，否则返回0。
     * 命令：
     * zadd zCardKey 1 one
     * zcard zCardKey
     */
    @Test
    public void zCard() {
        jedis.zadd("zCardKey",1, "one");
        jedis.zadd("zCardKey",2, "two");

        System.out.println(jedis.zcard("zCardKey"));

        System.out.println(redisTemplate.opsForZSet().size("zCardKey"));
    }

    /**
     * 返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员数量。
     * ZCOUNT key min max
     * 返回值：指定分数范围的元素个数。
     * 命令：
     * zadd zCountKey 1 one 2 two 3 three 4 four
     * zcount zCountKey 2 3
     */
    @Test
    public void zCount() {
        jedis.zadd("zCountKey",1, "one");
        jedis.zadd("zCountKey",2, "two");
        jedis.zadd("zCountKey",3, "three");
        jedis.zadd("zCountKey",4, "four");

        System.out.println(jedis.zcount("zCountKey",2, 3));

        System.out.println(redisTemplate.opsForZSet().count("zCountKey",2, 3));
    }

    /**
     * 为有序集key的成员member的score值加上增量increment。
     * 如果key中不存在member，就在key中添加一个member，score是increment（就好像它之前的score是0.0）。
     * 如果key不存在，就创建一个只含有指定member成员的有序集合。
     * 当key不是有序集类型时，返回一个错误。
     * score值必须是字符串表示的整数值或双精度浮点数，并且能接受double精度的浮点数。也有可能给一个负数来减少score的值。
     *
     * ZINCRBY key increment member
     * 返回值：
     * 命令：
     * zincrby zIncrByKey 2 score
     * zincrby zIncrByKey 5 score
     * zrange zIncrByKey 0 -1 WITHSCORES
     */
    @Test
    public void zIncrBy() {
        jedis.zincrby("zIncrByKey", 2, "score");

        redisTemplate.opsForZSet().incrementScore("zIncrByKey", "score", 5);

        Set<ZSetOperations.TypedTuple> set = redisTemplate.opsForZSet().rangeWithScores("zIncrByKey", 0, -1);
        for (ZSetOperations.TypedTuple typedTuple : set){
            System.out.println(typedTuple.getValue()+":"+typedTuple.getScore());
        }
    }

    /**
     * 计算给定的numkeys个有序集合的交集，并且把结果放到destination中。
     * 在给定要计算的key和其它参数之前，必须先给定key个数(numberkeys)。
     * 默认情况下，结果中一个元素的分数是有序集合中该元素分数之和，前提是该元素在这些有序集合中都存在。因为交集要求其成员必须是给定的每个有序集合中的成员，结果集中的每个元素的分数和输入的有序集合个数相等。
     * 对于WEIGHTS和AGGREGATE参数的描述，参见命令ZUNIONSTORE。
     * 如果destination存在，就把它覆盖。
     *
     * ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 返回值：结果有序集合destination中元素个数。
     * 命令：
     */
    @Test
    public void zInterStore() {
    }

    /**
     *
     * ZLEXCOUNT key min max
     * 返回值：
     * 命令：
     */
    @Test
    public void zLexCount() {
    }

    /**
     *
     * ZPOPMAX key [count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zPopMax() {
    }

    /**
     *
     * ZPOPMIN key [count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zPopMin() {
    }


    /**
     *
     * ZRANGEBYLEX key min max [LIMIT offset count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRangeByLex() {
    }

    /**
     *
     * ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRangeByScore() {
    }

    /**
     *
     * ZRANK key member
     * 返回值：
     * 命令：
     */
    @Test
    public void zRank() {
    }

    /**
     *
     * ZREM key member [member ...]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRem() {
    }

    /**
     *
     * ZREMRANGEBYLEX key min max
     * 返回值：
     * 命令：
     */
    @Test
    public void zRemRangeByLex() {
    }

    /**
     *
     * ZREMRANGEBYRANK key start stop
     * 返回值：
     * 命令：
     */
    @Test
    public void zRemRangeByRank() {
    }

    /**
     *
     * ZREMRANGEBYSCORE key min max
     * 返回值：
     * 命令：
     */
    @Test
    public void zRemRangeByScore() {
    }

    /**
     *
     * ZREVRANGE key start stop [WITHSCORES]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRevRange() {
    }

    /**
     *
     * ZREVRANGEBYLEX key max min [LIMIT offset count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRevRangeByLex() {
    }

    /**
     *
     * ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zRevRangeByScore() {
    }

    /**
     *
     * ZREVRANK key member
     * 返回值：
     * 命令：
     */
    @Test
    public void zRevRank() {
    }

    /**
     *
     * ZSCORE key member
     * 返回值：
     * 命令：
     */
    @Test
    public void zScore() {
    }

    /**
     *
     * ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 返回值：
     * 命令：
     */
    @Test
    public void zUnionStore() {
    }


    /**
     *
     * ZSCAN key cursor [MATCH pattern] [COUNT count]
     * 返回值：
     * 命令：
     */
    @Test
    public void zScan() {
    }

    /**
     *
     * BZPOPMIN key [key ...] timeout
     * 返回值：
     * 命令：
     */
    @Test
    public void bzPopMin() {
    }

    /**
     *
     * BZPOPMAX key [key ...] timeout
     * 返回值：
     * 命令：
     */
    @Test
    public void bzPopMax() {
    }

}
