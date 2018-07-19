package com.learnRedis.set;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;
import redis.clients.jedis.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SetCommand extends RedisBaseConnection {

    /**
     * 添加一个或多个指定的member元素到集合的 key中.指定的一个或者多个元素member 如果已经在集合key中存在则忽略.如果集合key 不存在，则新建集合key,并添加member元素到集合key中.
     * 如果key 的类型不是集合则返回错误.
     * SADD key member [member ...]
     * 返回值：返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
     * 命令：
     * sadd sAddKey hello
     * sadd sAddKey redis
     * sadd sAddKey redis
     * smembers sAddKey
     */
    @Test
    public void sAdd() {
        jedis.sadd("sAddKey", "hello", "redis");
        System.out.println(jedis.smembers("sAddKey"));

        //spring redisTemplate
        setOperations.add("sAddKey", "hello", "redis");
        System.out.println(setOperations.members("sAddKey"));
    }

    /**
     * 返回key集合所有的元素.
     * SMEMBERS key
     * 返回值： 集合中的所有元素.
     */
    @Test
    public void sMembers() {}

    /**
     * 返回成员 member 是否是存储的集合 key的成员.
     * SISMEMBER key member
     * 返回值：如果member元素是集合key的成员，则返回1。如果member元素不是key的成员，或者集合key不存在，则返回0
     * 命令：
     *  sadd sIsMemberKey hello
     *  sismember sIsMemberKey hello
     *  sismember sIsMemberKey redis
     */
    @Test
    public void sIsMember() {
        jedis.sadd("sIsMember", "hello");

        System.out.println(jedis.sismember("sIsMember", "hello"));

        //spring redisTemplate
        System.out.println(setOperations.isMember("sIsMember", "redis"));
    }

    /**
     * 在key集合中移除指定的元素. 如果指定的元素不是key集合中的元素则忽略 如果key集合不存在则被视为一个空的集合，该命令返回0.
     * 如果key的类型不是一个集合,则返回错误.
     * SREM key member [member ...]
     * 返回值：从集合中移除元素的个数，不包括不存在的成员.
     * 命令：
     * sadd sRemKey 0 1 2 3 4
     * srem sRemKey 1 3
     * smembers sRemKey
     */
    @Test
    public void sRem() {
        jedis.sadd("sRemKey", "0", "1", "2", "3", "4");

        jedis.srem("sRemKey", "1", "3");

        setOperations.remove("sRemKey", "1", "3");

        System.out.println(jedis.smembers("sRemKey"));
    }

    /**
     * 返回集合存储的key的基数 (集合元素的数量).
     * SCARD key
     * 返回值：集合的基数(元素的数量),如果key不存在,则返回 0.
     * 命令：
     * sadd sCardKey 1 2 3
     * scard sCardKey
     */
    @Test
    public void sCard() {
        jedis.sadd("sCardKey", "1", "2", "3");

        System.out.println(jedis.scard("sCardKey"));

        //spring redisTemplate
        System.out.println(setOperations.size("sCardKey"));
    }

    /**
     * 将member从source集合移动到destination集合中. 对于其他的客户端,在特定的时间元素将会作为source或者destination集合的成员出现.
     * 如果source 集合不存在或者不包含指定的元素,这smove命令不执行任何操作并且返回0.
     * 否则对象将会从source集合中移除，并添加到destination集合中去，
     * 如果destination集合已经存在该元素，则smove命令仅将该元素充source集合中移除.
     * 如果source 和destination不是集合类型,则返回错误.
     * SMOVE source destination member
     * 返回值：如果该元素成功移除,返回1。如果该元素不是 source集合成员,无任何操作,则返回0.
     * 命令：
     * sadd sMoveKeySrc 0 1 2 3 4
     * smove sMoveKeySrc sMoveKeyDst 5
     * smove sMoveKeySrc sMoveKeyDst 3
     * smembers sMoveKeyDst
     */
    @Test
    public void sMove() {
        jedis.sadd("sMoveKeySrc", "0", "1", "2", "3", "4");

        System.out.println("移动一个不存在的元素,结果:"+jedis.smove("sMoveKeySrc", "sMoveKeyDst", "5"));

        //spring redisTemplate
        System.out.println("移动一个存在的元素,结果:" + setOperations.move("sMoveKeySrc","3", "sMoveKeyDst"));

        System.out.println(jedis.smembers("sMoveKeyDst"));
    }

    /**
     * 移除且返回一个或多个随机元素
     * SPOP key [count]
     * 返回值：移除的元素，当key不存在时返回nil
     *
     * 命令：
     * sadd sPopKey 0 1 2 3 4
     * spop sPopKey
     * spop sPopKey 2
     * smembers sPopKey
     */
    @Test
    public void sPop() {
        jedis.sadd("sPopKey", "0", "1", "2", "3", "4");

        System.out.println(jedis.spop("sPopKey"));

        //spring redisTemplate
        System.out.println(setOperations.pop("sPopKey", 2));

        System.out.println(jedis.smembers("sPopKey"));

    }

    /**
     * 仅提供key参数,那么随机返回key集合中的一个元素.
     * Redis 2.6开始, 可以接受 count 参数,
     * 如果count是整数且小于元素的个数，返回含有 count 个不同的元素的数组,
     * 如果count是个整数且大于集合中元素的个数时,仅返回整个集合的所有元素,
     * 当count是负数,则会返回一个包含count的绝对值的个数元素的数组，
     * 如果count的绝对值大于元素的个数,则返回的结果集里会出现一个元素出现多次的情况.
     * 仅提供key参数时,该命令作用类似于SPOP命令, 不同的是SPOP命令会将被选择的随机元素从集合中移除, 而SRANDMEMBER仅仅是返回该随记元素,而不做任何操作.
     * SRANDMEMBER key [count]
     * 返回值：不使用count 参数的情况下该命令返回随机的元素,如果key不存在则返回nil.使用count参数,则返回一个随机的元素数组,如果key不存在则返回一个空的数组.
     *
     * 命令：
     * sadd sRandMemberKey 0 1 2 3 4
     * srandmember sRandMemberKey 2
     * srandmember sRandMemberKey 9
     * srandmember sRandMemberKey -2
     * srandmember sRandMemberKey -9
     */
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

    /**
     * 返回第一个key集合与其他key集合的差集.
     * SDIFF key [key ...]
     * 返回值：结果集的元素.
     * 命令：
     * sadd sDiffKey1 0 1 2 3 4
     * sadd sDiffKey2 1
     * sadd sDiffKey3 4 5 6
     * sdiff sDiffKey1 sDiffKey2 sDiffKey3
     */
    @Test
    public void sDiff() {
        jedis.sadd("sDiffKey1", "0", "1", "2", "3", "4");
        jedis.sadd("sDiffKey2", "1");
        jedis.sadd("sDiffKey3", "4", "5", "6");

        System.out.println(jedis.sdiff("sDiffKey1", "sDiffKey2", "sDiffKey3"));

        //spring redisTemplate
        List list = new ArrayList();
        list.add("sDiffKey2");
        list.add("sDiffKey3");
        System.out.println(setOperations.difference("sDiffKey1",list));
    }

    /**
     * 该命令类似于 SDIFF, 不同之处在于该命令不返回结果集，而是将结果存放在destination集合中.如果destination已经存在, 则将其覆盖重写.
     * SDIFFSTORE destination key [key ...]
     * 返回值：结果集元素的个数.
     */
    @Test
    public void sDiffStore() {}

    /**
     * 返回指定所有的集合的成员的交集.
     * SINTER key [key ...]
     * 返回值：结果集成员的列表.
     * 命令：
     * sadd sInterKey1 0 1 2 3 4
     * sadd sInterKey2 1 2 3 5
     * sadd sInterKey3 2 3 4
     * sinter sInterKey1 sInterKey2 sInterKey3
     */
    @Test
    public void sInter() {
        jedis.sadd("sInterKey1", "0", "1", "2", "3", "4");
        jedis.sadd("sInterKey2", "1", "2", "3", "5");
        jedis.sadd("sInterKey3", "2", "3", "4");

        System.out.println(jedis.sinter("sInterKey1", "sInterKey2", "sInterKey3"));

        //spring redisTemplate
        List list = new ArrayList();
        list.add("sInterKey2");
        list.add("sInterKey3");
        System.out.println(setOperations.intersect("sInterKey1",list));
    }

    /**
     * 这个命令与SINTER命令类似, 但是它并不是直接返回结果集,而是将结果保存在 destination集合中.如果destination 集合存在, 则会被重写.
     * SINTERSTORE destination key [key ...]
     * 返回值：结果集中成员的个数.
     */
    @Test
    public void sInterStore() {}

    /**
     * 返回给定的多个集合的并集中的所有成员.
     * SUNION key [key ...]
     * 返回值：并集的成员列表
     * 命令：
     * sadd sUnionKey1 0 1 2 3
     * sadd sUnionKey2 1 3 5 7
     * sadd sUnionKey3 0 2 4 6
     * sunion sUnionKey1 sUnionKey2 sUnionKey3
     */
    @Test
    public void sUnion() {
        jedis.sadd("sUnionKey1", "0", "1", "2", "3");
        jedis.sadd("sUnionKey2", "1", "3", "5", "7");
        jedis.sadd("sUnionKey3", "0", "2", "4", "6");

        System.out.println(jedis.sunion("sUnionKey1", "sUnionKey2", "sUnionKey3"));

        //spring redisTemplate
        List list = new ArrayList();
        list.add("sUnionKey2");
        list.add("sUnionKey3");
        System.out.println(setOperations.union("sUnionKey1", list));
    }

    /**
     * 该命令作用类似于SUNION命令,不同的是它并不返回结果集,而是将结果存储在destination集合中.
     * 如果destination 已经存在,则将其覆盖.
     * SUNIONSTORE destination key [key ...]
     * 返回值：结果集中元素的个数
     */
    @Test
    public void sUnionStore() {}


    /**
     *
     * SSCAN key cursor [MATCH pattern] [COUNT count]
     * 返回值：
     *
     * 命令：
     */
    @Test
    public void sScan() {}

}
