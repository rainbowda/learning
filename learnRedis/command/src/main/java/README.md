## 前言

这个redis系列的文章将会记录博主学习redis的过程。基本上现在的互联网公司都会用到redis，所以学习这门技术于你于我都是有帮助的。

博主在写这个系列是用的是目前最新版本4.0.10，虚拟机装的是4.0.10，为了方便window也安装了（版本3.2.100）。后续命令会采用命令行，jedis和spring集成jedis这三种方式进行操作。

在这片博文的开始，可以先试着问几个问题，带着问题看博文，或许能更有收获。

1. 什么是redis？
2. 为什么要使用redis？
3. 如何搭建redis环境？

## 什么是redis?

Redis 是开源的（[Github地址](https://github.com/antirez/redis)），采用BSD协议，C语言编写的、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库。

它支持不同类型的value，包括string(字符串)、list(链表)、set(集合)、zset(sorted set --有序集合)和hash（哈希类型）、基数统计的算法 HyperLogLogs, 位图Bitmaps 。

这些数据的操作不仅仅有设置值和获取值方法，还支持更复杂的操作方式，例如交集、并集、差集等等。

## 为什么要使用redis

想要知道为什么要使用redis前，需要先知道为什么要用缓存。

### 为什么要用缓存

当一个应用的数据量或者用户量上来后，如果每一次的查询都去访问数据库，或造成数据库效率变慢甚至崩溃。

而且在大多数应用中都是读多写少的，就可以将这些经常读的数据放到另外一个地方去（也就是缓存），让系统先从这个地方（缓存）获取，获取不到在查询数据库。这样可以大大的减少数据库的压力。

### 有没有其他的缓存

有，经常与redis做比较的memcache，这里就不比较它们的区别了，有兴趣的可以自己搜索。

我们来看看redis的其它方面

### redis特点

- Redis支持数据的持久化，可以将内存中的数据保持在磁盘中，重启的时候可以再次加载进行使用。
- Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储。
- Redis支持数据的备份，即master-slave模式的数据备份。

### Redis 优势

- 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。

- 丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
- 原子 – Redis的所有操作都是原子性的，同时Redis还支持对几个操作全并后的原子性执行。
- 丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性。



## 如何搭建redis环境？



### 下载

window上的64位redis下载地址https://github.com/MicrosoftArchive/redis/releases

linux 官网地址： https://redis.io/download

目前官网首页提供的版本是4.0.10

Redis版本列表 ：http://download.redis.io/releases/



### window

将下载下来的文件解压或者安装，目录如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/window%E4%B8%8B%E7%9A%84redis%E7%9B%AE%E5%BD%95.png?raw=true)

双击redis-server.exe启动redis服务

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/window%E5%90%AF%E5%8A%A8redis%E6%9C%8D%E5%8A%A1.png?raw=true)

双击redis-cli.exe启动redis客户端



### linux

用linux命令wget

```linux
wget http://download.redis.io/releases/redis-4.0.10.tar.gz
```

或者从官网下载，然后上传文件到linux上

#### 解压

```
tar xzf redis-4.0.10.tar.gz
```

进入redis目录，编译

```
cd redis-4.0.10
make
```

#### 启动服务

```
src/redis-server
```
或者进入src目录
```
./redis-server
```
想要后台启动最后加个`&`

```
src/redis-server &
```

这样启动的话，系统已重启又要重新启动redis服务

我们可以加到系统启动里面，让它开机自启动

> 注：出现问题 Could not connect to Redis at 127.0.0.1:6379: Connection refused
>
> 找到redis目录的redis.conf 文件
>
> 修改`daemonize no` 为`daemonize yes `,也就是将no改为yes，目的是开启守护线程模式，可以在后台运行 

#### 开启客户端

```
src/redis-cli
```
或者进入src目录
```
./redis-cli
```

在线测试 redis http://try.redis.io/

## 后续内容

##### 命令介绍

- Cluster
- Connection
- Geo
- Hashes
- HyperLogLog
- Keys
- Lists
- Pub/Sub
- Scripting
- Server
- Sets
- Sorted Sets
- Streams
- Strings
- Transactions

##### redis事务

##### Redis的数据持久化

##### redis主从、哨兵、集群

等等







