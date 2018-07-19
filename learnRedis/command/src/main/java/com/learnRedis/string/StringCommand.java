package com.learnRedis.string;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringCommand extends RedisBaseConnection {

    /**
     * 将键key设定为指定的“字符串”值。
     * 如果	key	已经保存了一个值，那么这个操作会直接覆盖原来的值，并且忽略原始类型。
     * 当set命令执行成功之后，之前设置的过期时间都将失效
     * Options
     * EX seconds – 设置键key的过期时间，单位时秒
     * PX milliseconds – 设置键key的过期时间，单位时毫秒
     * NX – 只有键key不存在的时候才会设置key的值
     * XX – 只有键key存在的时候才会设置key的值
     * 注：由于SET命令加上选项已经可以完全取代SETNX, SETEX, PSETEX的功能，所以在将来的版本中，redis可能会不推荐使用并且最终抛弃这几个命令。
     * 返回值：
     * 命令：
     *
     */
    @Test
    public void set() {}

    /**
     * 返回key的value。如果key不存在，返回特殊值nil。如果key的value不是string，就返回错误，因为GET只处理string类型的values。
     * GET key
     * 返回值：
     * 命令：
     * set getKey getValue
     * get getKey
     */
    @Test
    public void get() {
        jedis.set("getKey", "getValue");

        String getValue = jedis.get("getKey");
        System.out.println(getValue);

        Object objectValue = valueOperations.get("getKey");
        System.out.println(objectValue);
    }


    /**
     * 设置或者清空key的value(字符串)在offset处的bit值。
     * SETBIT key offset value
     * 返回值：在offset处原来的bit值
     * 命令：
     *
     */
    @Test
    public void setBit() {}

    /**
     * 返回key对应的string在offset处的bit值
     * GETBIT key offset
     * 返回值：在offset的bit值
     * 命令：
     * setbit bitKey 0 1
     * getbit bitKey 0
     */
    @Test
    public void getbit() {
        jedis.setbit("bitKey",0L,true);
        Boolean bitKey = jedis.getbit("bitKey", 0L);
        System.out.println(bitKey);

        valueOperations.setBit("bitKey",1L,true);
        bitKey = valueOperations.getBit("bitKey", 1L);
        System.out.println(bitKey);
    }
    /**
     * 设置key对应字符串value，并且设置key在给定的seconds时间之后超时过期。
     * SETEX key seconds value
     * 返回值：
     * 命令：
     * setex setExKey 5 setExValue
     * 等同于
     * （set setExKey setExValue ex 5）
     * （set setExKey setExValue
     *   expire setExKey 5 ）
     *
     */
    @Test
    public void setEx() {
        jedis.setex("setExKey", 5, "setExValue");
    }

    /**
     * PSETEX和SETEX一样，唯一的区别是到期时间以毫秒为单位,而不是秒。
     * PSETEX key milliseconds value
     * 返回值：
     * 命令：
     * psetex setExKey 5000 setExValue
     */
    @Test
    public void pSetEx() {

    }

    /**
     * 将key设置值为value，如果key不存在，这种情况下等同SET命令。 当key存在时，什么也不做。SETNX是”SET if Not eXists”的简写。
     * SETNX key value
     * 返回值：1 如果key被设置了,0 如果key没有被设置
     * 命令：
     * setnx setNxKey setNxValue
     * setnx setNxKey setNxValue
     */
    @Test
    public void setNx() {
        Long result = jedis.setnx("setNxKey", "setNxValue");
        Boolean flag = valueOperations.setIfAbsent("setNxKey", "setNxValue");

        System.out.println("jedis执行结果："+result);
        System.out.println("spring执行结果："+flag);
    }

    /**
     * 覆盖key对应的string的一部分，从指定的offset处开始，覆盖value的长度。
     * SETRANGE key offset value
     * 返回值：
     * 命令：
     * set setRangeKey "Hello World"
     * setrange setRangeKey 6 "Redis"
     * get setRangeKey
     */
    @Test
    public void setRange() {
        jedis.set("setRangeKey", "Hello World");

        jedis.setrange("setRangeKey", 6 , "Redis");
        System.out.println(jedis.get("setRangeKey"));

        //spring
        valueOperations.set("setRangeKey", "learyRedis", 6);
        System.out.println(valueOperations.get("setRangeKey"));
    }

    /**
     * 返回key对应的字符串value的子串，这个子串是由start和end位移决定的（两者都在string内）。可以用负的位移来表示从string尾部开始数的下标。所以-1就是最后一个字符，-2就是倒数第二个，以此类推。
     * GETRANGE key start end
     * 返回值：
     * 命令：
     * set getRangeKey "Hello learyRedis"
     * getrange getRangeKey 6 -1
     * getrange getRangeKey 0 -12
     */
    @Test
    public void getRange() {
        jedis.set("getRangeKey", "Hello learyRedis");

        System.out.println(jedis.getrange("getRangeKey", 6, -1));

        //spring
        System.out.println(valueOperations.get("getRangeKey", 0, -12));
    }

    /**
     * 设置新值，返回旧值。如果key存在但是对应的value不是字符串，就返回错误。
     * GETSET key value
     * 返回值：返回之前的旧值，如果之前Key不存在将返回nil。
     * 命令：
     * getset getSetKey newValue
     * set getSetKey value
     * getset getSetKey newValue
     * get getSetKey
     */
    @Test
    public void getSet() {
        jedis.getSet("getSetKey", "newValue");

        jedis.set("getSetKey", "value");

        Object newValue = valueOperations.getAndSet("getSetKey", "newValue");
        System.out.println(newValue);
        System.out.println(valueOperations.get("getSetKey"));
    }

    /**
     * 如果key已经存在，并且是一个字符串，则该命令将value在字符串的末尾附加。如果key不存在，它将被创建并设置为空字符串，因此 APPEND 在这种特殊情况下将与SET类似。
     * APPEND key value
     * 返回值：追加操作后的字符串长度。
     * 命令：
     * append appendKey append
     * append appendKey Value
     * get appendKey
     */
    @Test
    public void append() {
        jedis.append("appendKey","append");

        valueOperations.append("appendKey","Value");

        System.out.println(jedis.get("appendKey"));
    }

    /**
     * 对存储在指定key的数值执行原子的加1操作。没有对应的key则设置为0，再相加
     * INCR key
     * 返回值：增加之后的value值。
     * 命令：
     * incr incrKey
     */
    @Test
    public void incr() {
        System.out.println(jedis.incr("incrKey"));

        System.out.println(valueOperations.increment("incrKey", 1L));
    }

    /**
     * 将key对应的数字加increment。
     * INCRBY key increment
     * 返回值：增加之后的value值。
     * 命令：
     * incyby incyByKey 5
     */
    @Test
    public void incrBy() {
        System.out.println(jedis.incrBy("incyByKey", 5));

        System.out.println(valueOperations.increment("incrByKey", 5L));
    }

    /**
     * 通过指定浮点数key来增长浮点数(存放于string中)的值
     * INCRBYFLOAT key increment
     * 返回值：
     * 命令：
     * incrbyfloat incrByFloatKey 5.11
     * incrbyfloat incrByFloatKey 5.22
     */
    @Test
    public void incrByFloat() {
        System.out.println(jedis.incrByFloat("incrByFloatKey", 5.11));

        System.out.println(valueOperations.increment("incrByFloatKey", 5.22));
    }

    /**
     * 将存储的数字减key1。如果密钥不存在，则0在执行操作之前将其设置为。如果密钥包含错误类型的值或包含无法表示为整数的字符串，则会返回错误。该操作仅限于64位有符号整数。没有对应的key则设置为0，再相减
     * DECR key
     * 返回值：key减量后的值
     * 命令：
     * set decrKey 6
     * decr decrKey
     *
     * set decrKey 6666666666666666666666666666666666
     * decr decrKey
     *
     * set decrKey learyRedis
     * decr decrKey
     */
    @Test
    public void decr() {
        jedis.set("decrKey", "6");

        Long decrValue = jedis.decr("decrKey");
        System.out.println(decrValue);

        decrValue = valueOperations.increment("decrKey", -1L);
        System.out.println(decrValue);

        jedis.set("decrKey", "6666666666666666666666666666666666");
        decrValue = jedis.decr("decrKey");//出现JedisDataException异常：ERR value is not an integer or out of range

    }

    /**
     * 递减存储在数key通过decrement。如果密钥不存在，则0在执行操作之前将其设置为。如果密钥包含错误类型的值或包含无法表示为整数的字符串，则会返回错误。该操作仅限于64位有符号整数。
     * DECRBY key decrement
     * 返回值：key减量后的值
     * 命令：
     * set decrByKey 6
     * decrby decrByKey 2
     */
    @Test
    public void decrBy() {
        jedis.set("decrByKey", "6");

        Long decrByValue = jedis.decrBy("decrByKey", 2);
        System.out.println(decrByValue);

        decrByValue = valueOperations.increment("decrByKey", -2L);
        System.out.println(decrByValue);

    }


    /**
     * 对应给定的keys到他们相应的values上。MSET会用新的value替换已经存在的value，就像普通的SET命令一样。如果你不想覆盖已经存在的values，请参看命令MSETNX。
     * MSET是原子的，所以所有给定的keys是一次性set的。客户端不可能看到这种一部分keys被更新而另外的没有改变的情况。
     * MSET key value [key value ...]
     * 返回值：ok
     * 命令：
     *
     */
    @Test
    public void mSet() {}
    /**
     * 返回所有指定的key的value。对于每个不对应string或者不存在的key，都返回特殊值nil。正因为此，这个操作从来不会失败。
     * MGET key [key ...]
     * 返回值：指定的key对应的values的list
     * 命令：
     * mset mGetKey1 mGetValue1 mGetKey2 mGetValue2 mGetKey3 mGetValue3
     * mget mGetKey1 mGetKey2 mGetKey3 mGetKey4
     */
    @Test
    public void mGet() {
        jedis.mset("mGetKey1", "mGetValue1","mGetKey2", "mGetValue2","mGetKey3", "mGetValue3");
        System.out.println(jedis.mget("mGetKey1", "mGetKey2", "mGetKey3", "mGetKey4"));

        jedis.flushDB();

        //spring
        Map<String, String> map = new HashMap<>(3);
        map.put("mGetKey1", "mGetValue1");
        map.put("mGetKey2", "mGetValue2");
        map.put("mGetKey3", "mGetValue3");
        valueOperations.multiSet(map);
        System.out.println(valueOperations.multiGet(map.keySet()));
    }

    /**
     * 对应给定的keys到他们相应的values上。只要有一个key已经存在，MSETNX一个操作都不会执行。
     * 由于这种特性，MSETNX可以实现要么所有的操作都成功，要么一个都不执行，这样可以用来设置不同的key，来表示一个唯一的对象的不同字段。
     * MSETNX是原子的，所以所有给定的keys是一次性set的。客户端不可能看到这种一部分keys被更新而另外的没有改变的情况。
     * MSETNX key value [key value ...]
     * 返回值：1 如果所有的key被set,0 如果没有key被set(至少其中有一个key是存在的)
     * 命令：
     * msetnx mSetNxKey1 mSetNxValue1 mSetNxKey2 mSetNxValue2
     * msetnx mSetNxKey2 mSetNxValue2 mSetNxKey3 mSetNxValue3
     */
    @Test
    public void mSetNx() {

        System.out.println(jedis.msetnx("mSetNxKey1", "mSetNxValue1","mSetNxKey2", "mSetNxValue2"));

        //spring
        Map<String, String> map = new HashMap<>(3);
        map.put("mSetNxKey2", "mSetNxValue2");
        map.put("mSetNxKey3", "mSetNxValue3");
        System.out.println(valueOperations.multiSetIfAbsent(map));
    }


    /**
     * 返回key的string类型value的长度。如果key对应的非string类型，就返回错误。
     * STRLEN key
     * 返回值：
     * 命令：
     * set strLenKey "hello Redis"
     * strlen strLenKey
     */
    @Test
    public void strLen() {
        jedis.set("strLenKey", "hello Redis");

        System.out.println(jedis.strlen("strLenKey"));

        System.out.println(valueOperations.size("strLenKey"));
    }

    /**
     * 统计字符串被设置为1的bit数.
     * BITCOUNT key [start end]
     * 返回值：
     * 命令：
     * set bitcountKey bitcountValue
     * bitcount bitcountKey
     */
    @Test
    public void bitCount() {
        jedis.set("bitcountKey", "bitcountValue");

        Long bitCount = jedis.bitcount("bitcountKey");
        System.out.println(bitCount);

    }

    /**
     * BITFIELD会把Redis字符串当作位数组，并能对变长位宽和任意未字节对齐的指定整型位域进行寻址。详见：https://redis.io/commands/bitfield
     * BITFIELD key [GET type offset] [SET type offset value] [INCRBY type offset increment] [OVERFLOW WRAP|SAT|FAIL]
     * 返回值：
     * 命令：
     *
     */
    @Test
    public void bitField() {

    }

    /**
     * 对一个或多个保存二进制位的字符串 key 进行位元操作，并将结果保存到 destkey 上。详见：https://redis.io/commands/bitop
     * BITOP operation destkey key [key ...]
     * 返回值：
     * 命令：
     *
     */
    @Test
    public void bitop() {

    }

    /**
     * 返回字符串里面第一个被设置为1或者0的bit位。
     * BITPOS key bit [start] [end]
     * 返回值：
     * 命令：
     *
     */
    @Test
    public void bitpos() {

    }
}
