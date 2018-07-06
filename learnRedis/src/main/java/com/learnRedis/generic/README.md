redis是什么?

Redis是一个开源的使用ANSI C语言编写、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。和Memcached类似，它支持存储的value类型相对更多，包括string(字符串)、list(链表)、set(集合)、zset(sorted set --有序集合)和hash（哈希类型）。这些数据类型都支持push/pop、add/remove及取交集并集和差集及更丰富的操作，而且这些操作都是原子性的。在此基础上，redis支持各种不同方式的排序。与memcached一样，为了保证效率，数据都是缓存在内存中。区别的是redis会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件，并且在此基础上实现了master-slave(主从)同步。

redis的出现，很大程度补偿了memcached这类key/value存储的不足，在部分场合可以对关系数据库起到很好的补充作用。它提供了Java，C/C++，C#，PHP，JavaScript，Perl，Object-C，Python，Ruby，Erlang等客户端，使用很方便。

 

Redis支持主从同步。数据可以从主服务器向任意数量的从服务器上同步，从服务器可以是关联其他从服务器的主服务器。这使得Redis可执行单层树复制。存盘可以有意无意的对数据进行写操作。由于完全实现了发布/订阅机制，使得从数据库在任何地方同步树时，可订阅一个频道并接收主服务器完整的消息发布记录。同步对读取操作的可扩展性和数据冗余很有帮助。

redis性能
下面是官方的bench-mark数据：

测试完成了50个并发执行100000个请求。
设置和获取的值是一个256字节字符串。
Linux box是运行Linux 2.6,这是X3320 Xeon 2.5 ghz。
文本执行使用loopback接口(127.0.0.1)。
结果:读的速度是110000次/s,写的速度是81000次/s 。


redis存储
redis使用了两种文件格式：全量数据和增量请求。
全量数据格式是把内存中的数据写入磁盘，便于下次读取文件进行加载；
增量请求文件则是把内存中的数据序列化为操作请求，用于读取文件进行replay得到数据，序列化的操作包括SET、RPUSH、SADD、ZADD。
redis的存储分为内存存储、磁盘存储和log文件三部分，配置文件中有三个参数对其进行配置。
save seconds updates，save配置，指出在多长时间内，有多少次更新操作，就将数据同步到数据文件。这个可以多个条件配合，比如默认配置文件中的设置，就设置了三个条件。
appendonly yes/no ，appendonly配置，指出是否在每次更新操作后进行日志记录，如果不开启，可能会在断电时导致一段时间内的数据丢失。因为redis本身同步数据文件是按上面的save条件来同步的，所以有的数据会在一段时间内只存在于内存中。
appendfsync no/always/everysec ，appendfsync配置，no表示等操作系统进行数据缓存同步到磁盘，always表示每次更新操作后手动调用fsync()将数据写到磁盘，everysec表示每秒同步一次。

Redis 简介
Redis 是完全开源免费的，遵守BSD协议，是一个高性能的key-value数据库。

Redis 与其他 key - value 缓存产品有以下三个特点：

Redis支持数据的持久化，可以将内存中的数据保持在磁盘中，重启的时候可以再次加载进行使用。
Redis不仅仅支持简单的key-value类型的数据，同时还提供list，set，zset，hash等数据结构的存储。
Redis支持数据的备份，即master-slave模式的数据备份。
Redis 优势
性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。
丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
原子 – Redis的所有操作都是原子性的，同时Redis还支持对几个操作全并后的原子性执行。
丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性。
Redis与其他key-value存储有什么不同？
Redis有着更为复杂的数据结构并且提供对他们的原子性操作，这是一个不同于其他数据库的进化路径。Redis的数据类型都是基于基本数据结构的同时对程序员透明，无需进行额外的抽象。
Redis运行在内存中但是可以持久化到磁盘，所以在对不同数据集进行高速读写时需要权衡内存，应为数据量不能大于硬件内存。在内存数据库方面的另一个优点是， 相比在磁盘上相同的复杂的数据结构，在内存中操作起来非常简单，这样Redis可以做很多内部复杂性很强的事情。 同时，在磁盘格式方面他们是紧凑的以追加的方式产生的，因为他们并不需要进行随机访问。

安装

window上的64位redis下载地址https://github.com/MicrosoftArchive/redis/releases





linux 官网地址： https://redis.io/download

目前官网首页提供的版本是4.0.10

Redis版本列表 ：http://download.redis.io/releases/

-------------------------

安装

window

将下载下来的文件解压或者安装，目录如下



双击redis-server.exe启动redis服务



双击redis-cli.exe启动redis客户端





linux

用linux命令wget

```linux
wget http://download.redis.io/releases/redis-4.0.10.tar.gz
```

或者从官网下载，然后上传文件到linux上

解压

```
tar xzf redis-4.0.10.tar.gz
```

进入redis目录，编译

```
cd redis-4.0.10
make
```

启动服务

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


开启客户端
```
src/redis-cli
```
或者进入src目录
```
./redis-cli
```

配置
在客户端输入 config get *
redis.conf
配置参数 ： http://www.runoob.com/redis/redis-conf.html




----------------
遇到的问题

Could not connect to Redis at 127.0.0.1:6379: Connection refused

找到redis目录的redis.conf 文件

修改`daemonize no` 为`daemonize yes `,也就是将no改为yes，目的是开启守护线程模式，可以在后台运行 

