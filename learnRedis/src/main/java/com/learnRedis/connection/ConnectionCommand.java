package com.learnRedis.connection;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConnectionCommand extends RedisBaseConnection {

    /**
     * 请求在受密码保护的 Redis 服务器中进行身份验证。
     * AUTH password
     * 返回值：RESP Simple Strings 详见：https://redis.io/topics/protocol#simple-string-reply
     * 命令：
     * auth 123456
     */
    @Test
    public void auth() {
        jedis.keys("*");//在需要密码的redis中会返回错误：(error) NOAUTH Authentication required.
        jedis.auth("123456");
    }

    /**
     * 回显消息
     * ECHO message
     * 返回值：message
     * 命令：
     * echo message
     */
    @Test
    public void echo() {
        String message = jedis.echo("message");
        System.out.println(message);
    }

    /**
     * 此命令通常用于测试连接是否仍然存在，或测量延迟。
     * PING [message]
     * 返回值：返回PONG如果没有提供参数，否则返回参数的副本作为一个主体。
     * 命令：
     * ping
     * ping hello
     */
    @Test
    public void ping() {
        String pingResult = jedis.ping();
        System.out.println("jedis执行结果：" + pingResult);

        //spring
        pingResult = redisTemplate.getConnectionFactory().getConnection().ping();
        System.out.println("redisTemplate执行结果：" + pingResult);

    }

    /**
     * 请求服务器关闭连接。
     * OK
     * 返回值：OK
     * 命令：
     * quit
     */
    @Test
    public void quit() {
        jedis.quit();
    }

    /**
     * 选择Redis 逻辑数据库。新连接始终使用数据库0。
     * SELECT index
     * 返回值：
     * 命令：
     * select 1
     * select 0
     */
    @Test
    public void select() {
        jedis.select(1);
        jedis.select(0);

        redisTemplate.getConnectionFactory().getConnection().select(1);
    }

    /**
     * 交换两个 Redis 数据库
     * SWAPDB index index
     * 返回值：成功返回OK
     * 命令：
     * swapdb 0 1
     * swapdb 1 0
     */
    @Test
    public void swapdb() {

    }

}
