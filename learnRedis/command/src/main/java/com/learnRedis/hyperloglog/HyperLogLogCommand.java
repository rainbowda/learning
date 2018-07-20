package com.learnRedis.hyperloglog;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

public class HyperLogLogCommand extends RedisBaseConnection {


    /**
     * 将除了第一个参数以外的参数存储到以第一个参数为变量名的HyperLogLog结构中.
     * 如果一个HyperLogLog的估计的近似基数在执行命令过程中发了变化， PFADD 返回1，否则返回0，如果指定的key不存在，这个命令会自动创建一个空的HyperLogLog结构（指定长度和编码的字符串）.
     * PFADD key element [element ...]
     * 返回值：如果 HyperLogLog 的内部被修改了,那么返回 1,否则返回 0 .
     */
    @Test
    public void pfAdd() {}

    /**
     * 当参数为一个key时,返回存储在HyperLogLog结构体的该变量的近似基数，如果该变量不存在,则返回0.
     *
     * https://redis.io/commands/pfcount
     * PFCOUNT key [key ...]
     * 返回值：
     * 命令：
     * PFADD hll foo bar zap
     * PFADD hll zap zap zap
     * PFADD hll foo bar
     * PFCOUNT hll
     * PFADD some-other-hll 1 2 3
     * PFCOUNT hll some-other-hll
     */
    @Test
    public void pfCount() {

    }

    /**
     * 将多个 HyperLogLog 合并（merge）为一个 HyperLogLog ， 合并后的 HyperLogLog 的基数接近于所有输入 HyperLogLog 的可见集合（observed set）的并集.
     * PFMERGE destkey sourcekey [sourcekey ...]
     * 返回值：
     * 命令：
     */
    @Test
    public void pfMerge() {

    }
}
