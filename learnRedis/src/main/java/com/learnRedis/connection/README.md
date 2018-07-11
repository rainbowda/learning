这篇主要讲述redis连接（connection）的命令，[官网地址](https://redis.io/commands#connection)

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection%20commands.png?raw=true)

##### AUTH命令
请求在受密码保护的 Redis 服务器中进行身份验证。
AUTH password
返回值：RESP Simple Strings 详见：https://redis.io/topics/protocol#simple-string-reply


redis客户端执行的命令如下
```
auth 123456
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection/auth%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void auth() {
    jedis.keys("*");//在需要密码的redis中会返回错误：(error) NOAUTH Authentication required.
    jedis.auth("123456");
}
```

##### ECHO命令

回显消息
ECHO message
返回值：message

redis客户端执行的命令如下
```
echo message
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection/echo%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void echo() {
    String message = jedis.echo("message");
    System.out.println(message);
}
```
##### PING命令
此命令通常用于测试连接是否仍然存在，或测量延迟。
PING [message]
返回值：返回PONG如果没有提供参数，否则返回参数的副本作为一个主体。


redis客户端执行的命令如下
```
ping
ping hello
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection/ping%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void ping() {
    String pingResult = jedis.ping();
    System.out.println("jedis执行结果：" + pingResult);

    //spring
    pingResult = redisTemplate.getConnectionFactory().getConnection().ping();
    System.out.println("redisTemplate执行结果：" + pingResult);

}
```
##### QUIT命令
请求服务器关闭连接。
返回值：OK


redis客户端执行的命令如下
```
quit
```

##### SELECT命令
选择Redis 逻辑数据库。新连接始终使用数据库0。
SELECT index


redis客户端执行的命令如下
```
select 1
select 0
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection/select%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void select() {
    jedis.select(1);
    jedis.select(0);

    redisTemplate.getConnectionFactory().getConnection().select(1);
}
```
##### AUTH命令
交换两个 Redis 数据库
SWAPDB index index
返回值：成功返回OK


redis客户端执行的命令如下
```
swapdb 0 1
swapdb 1 0
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/connection/swapdb%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)


