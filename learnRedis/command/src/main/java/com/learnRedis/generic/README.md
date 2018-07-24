这篇主要讲述redis基本的命令，[官网地址](https://redis.io/commands#generic)

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic%20%20Commands.png?raw=true)

##### DEL命令

根据key移除，忽略不存在的key

DEL key [key ...]
返回值：被删除 key 的数量

redis客户端执行的命令如下
```
set key1 "key1"
set key2 "key2"
del key1 key2 key3
```
执行结果如下  

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/del%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void del() {

    jedis.set("key1", "key1");
    jedis.set("key2", "key2");

    Long delNum = jedis.del("key1", "key2", "key3");
    System.out.println(delNum);

    //spring
    redisTemplate.opsForValue().set("key1", "key1");
    redisTemplate.opsForValue().set("key2", "key2");

    String[] strs = {"key1", "key2", "key3"};
    delNum = redisTemplate.delete(Arrays.asList(strs));
    System.out.println(delNum);
}
```



##### DUMP命令

序列化给定 key的value值 ，并返回被序列化的值  
 
DUMP key  
返回值：如果 key 不存在，那么返回 nil 。 否则，返回序列化之后的值。  

redis客户端执行的命令如下  
```
set dump dumpdump
dump dump
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/dump%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void dump() {
    jedis.set("dump", "dumpdump");

    byte[] dumpData = jedis.dump("dump");
    System.out.println(new String(dumpData, StandardCharsets.UTF_8));

    //spring
    dumpData = redisTemplate.dump("dump");
    System.out.println(new String(dumpData, StandardCharsets.UTF_8));
}
```

##### EXISTS命令

检查key 是否存在  

EXISTS key [key ...]  
返回值：若 key 存在返回 1 ，否则返回 0 。  

redis客户端执行的命令如下
```
set exists exists
exists exists
exists noExists
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/exists%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void exists() {
    jedis.set("exists", "exists");

    Boolean exists = jedis.exists("exists");
    Boolean noExists = jedis.exists("noExists");
    System.out.println(exists);
    System.out.println(noExists);

    //spring
    exists = redisTemplate.hasKey("exists");
    noExists = redisTemplate.hasKey("noExists");
    System.out.println(exists);
    System.out.println(noExists);
}
```

##### EXPIRE命令

设置 key 的过期时间

EXPIRE key seconds
返回值：设置成功返回 1 。 当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。

redis客户端执行的命令如下
```
set expire expire
expire expire 10
ttl expire
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/expire%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void expire() throws InterruptedException {
    jedis.set("expire", "expire");
    jedis.expire("expire", 10);

    System.out.println(jedis.ttl("expire"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.ttl("expire"));

    //spring
    //三个参数，key，时间，时间单位
    redisTemplate.expire("expire", 5, TimeUnit.SECONDS);
    System.out.println(redisTemplate.getExpire("expire"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(redisTemplate.getExpire("expire"));
}
```

##### EXPIREAT命令

以 UNIX 时间戳(unix timestamp)格式设置 key 的过期时间

EXPIREAT key timestamp
返回值：设置成功返回 1 。 当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。

redis客户端执行的命令如下
```
set expireAt expireAt
expireAt expire 1531187220
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/expireAt%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void expireAt() throws InterruptedException {
    jedis.set("expireAt", "expireAt");
    jedis.expireAt("expireAt", (System.currentTimeMillis() + 3000) / 1000);

    System.out.println(jedis.ttl("expireAt"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.ttl("expireAt"));

    //spring
    redisTemplate.expireAt("expireAt", new Date(System.currentTimeMillis() + 3000));
    System.out.println(redisTemplate.getExpire("expireAt"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(redisTemplate.getExpire("expireAt"));
}
```

##### KEYS命令

查找所有符合给定模式 pattern 的 key

KEYS pattern
返回值：符合给定模式的 key 列表 (Array)。

redis客户端执行的命令如下
```
keys *
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/keys%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void keys() {
    Set<String> keys = jedis.keys("*");

    System.out.println(keys.toString());

    //spring
    keys = redisTemplate.keys("*");
    System.out.println(keys.toString());
}
```

##### MIGRATE命令

将key移动到目标redis服务器上，原来redis服务器上的key会丢失  

（如果目标服务器上有这个key，会出现错误(error) ERR Target instance replied with error: BUSYKEY Target key name already exists.）
  
MIGRATE host port key|"" destination-db timeout [COPY][REPLACE] [KEYS key [key ...]]  
操作：  
COPY 原来的key不会丢失  
REPLACE 替换远程的数据，原来的key会丢失  
KEYS 我试了没效果，望指点。官网原话：If the key argument is an empty string, the command will instead migrate all the keys that follow the KEYS option (see the above section for more info)  
返回值：成功返回OK, key不存在返回NOKEY  

redis客户端执行的命令如下  
```
set migrate migrate
migrate 192.168.17.101 6379 "" 0 5000 keys migrate
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/migrate%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void migrate() {
    // 注释虚拟机上的redis.conf 的 bind 127.0.0.1
    // 直接关闭虚拟机的防火墙 systemctl stop firewalld.service，出现下面问题
    // DENIED Redis is running in protected mode because protected mode is enabled,
    // no bind address was specified, no authentication password is requested to clients.
    // In this mode connections are only accepted from the loopback interface.
    // If you want to connect from external computers to Redis you may adopt one of the following solutions:
    // 1) Just disable protected mode sending the command 'CONFIG SET protected-mode no' from the loopback interface by connecting to Redis from the same host the server is running,
    // however MAKE SURE Redis is not publicly accessible from internet if you do so.
    // Use CONFIG REWRITE to make this change permanent.
    // 2) Alternatively you can just disable the protected mode by editing the Redis configuration file,
    // and setting the protected mode option to 'no', and then restarting the server.
    // 3) If you started the server manually just for testing, restart it with the '--protected-mode no' option.
    // 4) Setup a bind address or an authentication password.
    // NOTE: You only need to do one of the above things in order for the server to start accepting connections from the outside.
    // 将虚拟机上的redis.conf 的 bind 127.0.0.1改为bind 0.0.0.0即所有地址均可连接
    // src/redis-server redis.conf    根据配置文件启动
    jedis.set("migrate", "migrate");
    jedis.migrate("192.168.17.101", 6379, "migrate", 0, 5000);
    Jedis otherJedis = new Jedis("192.168.17.101");
    System.out.println(otherJedis.get("migrate"));

    //spring
    RedisNode.RedisNodeBuilder redisNodeBuilder = new RedisNode.RedisNodeBuilder();
    redisNodeBuilder.listeningAt("192.168.17.101", 6379);
    RedisNode redisNode = redisNodeBuilder.build();

    redisTemplate.getConnectionFactory().getConnection().migrate("migrate".getBytes(), redisNode, 0, RedisServerCommands.MigrateOption.COPY, 5000);
}
```

##### MOVE命令

将当前数据库的 key 移动到给定的数据库 db 当中  

MOVE key db  
返回值：移动成功返回 1 ，失败则返回 0 。  

redis客户端执行的命令如下  
```
select 0
set move move
move move 1
get move
select 1
get move
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/move%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void move() {
    //选择第0个数据库
    jedis.select(0);
    jedis.set("move", "move");
    //移动到第一个数据库中
    jedis.move("move", 1);
    System.out.println(jedis.get("move"));

    //选择第1个数据库
    jedis.select(1);
    System.out.println(jedis.get("move"));

    //spring
    redisTemplate.move("move", 2);

}
```

##### OBJECT命令

用来调试或者用于了解key存储的编码方式  

OBJECT subcommand [arguments [arguments ...]]  
subcommand:  
REFCOUNT 引用次数  
ENCODING 编码格式  
IDLETIME 空闲时间  
FREQ     访问频率  
返回值：不同的subcommand返回不同的值  

redis客户端执行的命令如下  
```
set object object
object encoding object
object idletime object
object refcount object
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/object%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void object() {
    jedis.set("object", "object");
    String encoding = jedis.objectEncoding("object");
    Long idletime = jedis.objectIdletime("object");
    Long refcount = jedis.objectRefcount("object");

    System.out.println("encoding:" + encoding);
    System.out.println("idletime:" + idletime);
    System.out.println("refcount:" + refcount);

}
```

##### PERSIST命令

移除key的生存时间，将这个 key 从volatile(设置了生存时间的key )转换成persistent (一个没有过期时间的 key )。  

PERSIST key  
返回值：过期时间被移除返回1，如果key不存在或者已超时则返回0  

redis客户端执行的命令如下  
```
set persist persist
expire persist 10
ttl persist
persist persist
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/persist%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void persist() {
    jedis.set("persist", "persist");

    jedis.expire("persist", 10);
    System.out.println(jedis.ttl("persist"));

    Long persist = jedis.persist("persist");
    System.out.println("返回值：" + persist + "  时间：" + jedis.ttl("persist"));

    //spring
    redisTemplate.expire("persist", 5, TimeUnit.SECONDS);
    Boolean persistBoolean = redisTemplate.persist("persist");
    System.out.println("返回值：" + persistBoolean + "  时间：" + redisTemplate.getExpire("persist"));
}
```

##### PEXPIRE命令

这个命令和EXPIRE类似，不同的是EXPIRE是以秒为单位，PEXPIRE是以毫秒为单位  

PEXPIRE key milliseconds  
返回值：设置成功返回 1 。当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。  

redis客户端执行的命令如下  
```
set pexpire pexpire
pexpire pexpire 10000
ttl pexpire
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/pexpire%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void pexpire() throws InterruptedException {
    jedis.set("pexpire", "pexpire");
    jedis.pexpire("pexpire", 10000L);

    System.out.println(jedis.ttl("pexpire"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.ttl("pexpire"));

}
```

##### PEXPIREAT命令

 以 UNIX 时间戳(unix milliseconds)格式设置 key 的过期时间  

PEXPIREAT key milliseconds-timestamp  
返回值：设置成功返回 1 。当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。  

redis客户端执行的命令如下  
```
set pexpireAt pexpireAt
pexpireAt pexpireAt 1531187220000
pttl pexpireAt
```

执行结果如下
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/pexpireAt%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void pexpireAt() throws InterruptedException {
    jedis.set("pexpireAt", "pexpireAt");
    jedis.pexpireAt("pexpireAt", System.currentTimeMillis() + 3000);

    System.out.println(jedis.pttl("pexpireAt"));
    Thread.sleep(2000);//休眠2秒
    System.out.println(jedis.pttl("pexpireAt"));
}
```

##### PTTL命令

类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间  

PTTL key  
返回值：当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间。  

redis客户端执行的命令如下  
```
set pttl pttl
pexpireAt pttl 1531187220000
pttl pttl
```

下面是java代码
```java
@Test
public void pttl() {
    jedis.set("pttl", "pttl");
    jedis.pexpireAt("pttl", System.currentTimeMillis() + 3000);

    System.out.println(jedis.pttl("pttl"));
}
```

##### RANDOMKEY命令

返回当前选择数据库的随机key  

RANDOMKEY  
返回值：如果数据库没有任何key，返回nil，否则返回一个随机的key  

redis客户端执行的命令如下  
```
set randomkey randomkey
set randomkey1 randomkey1
set randomkey2 randomkey2
randomkey
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/randomkey%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void randomKey() {
    jedis.set("randomkey", "randomkey");
    jedis.set("randomkey1", "randomkey1");
    jedis.set("randomkey2", "randomkey2");

    System.out.println(jedis.randomKey());

}
```

##### RENAME命令

将key重命名为newkey，如果key不存在，将返回一个错误。如果newkey已经存在，则值将被覆盖。  

RENAME key newkey  

redis客户端执行的命令如下  
```
set rename rename
* rename rename newRename
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/rename%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void rename() {
    jedis.set("rename", "rename");

    jedis.rename("rename", "newRename");

    //spring
    redisTemplate.rename("newRename", "newNewRename");

}
```

##### RENAMENX命令

当newkey不存在时，将key重命名为newkey。如果key不存在，将返回一个错误。  
RENAMENX key newkey  
返回值：修改成功时，返回 1 。如果 newkey 已经存在，返回 0 。  

redis客户端执行的命令如下
```
set renameNx1 renameNx1
set renameNx2 renameNx2
renamenx renameNx1 renameNx2
renamenx renameNx1 renameNx3
```
执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/renamenx%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void renameNx() {
    jedis.set("renameNx1", "renameNx1");
    jedis.set("renameNx2", "renameNx2");

    System.out.println(jedis.renamenx("renameNx1", "renameNx2"));
    System.out.println(jedis.renamenx("renameNx1", "renameNx3"));

    System.out.println(jedis.keys("*"));

    //spring
    System.out.println(redisTemplate.renameIfAbsent("renameNx2", "renameNx3"));
    System.out.println(redisTemplate.renameIfAbsent("renameNx2", "renameNx1"));

    System.out.println(redisTemplate.keys("*"));
}
```

##### RESTORE命令

反序列化给定的序列化值，并将它和给定的 key 关联。  
参数 ttl 以毫秒为单位为 key 设置生存时间；如果 ttl 为 0 ，那么不设置生存时间。  
当key已存在时，会返回(error) BUSYKEY Target key name already exists.  
RESTORE会检查RDB版本和数据校验，如果不匹配择返回错误。  
RESTORE key ttl serialized-value [REPLACE]  

redis客户端执行的命令如下  
```
set restore restore
dump restore
restore restore 0 "\x00\arestore\a\x00\\f*3\xc1Bo\x83"
restore newRestore 2000 "\x00\arestore\a\x00\\f*3\xc1Bo\x83"
get newRestore
等待2-3秒
get newRestore
restore newRestore 0 "\x00\arestore\a\x00\\f*3\xc1Bo\x83"
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/restore%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void restore() throws InterruptedException {
    jedis.set("restore", "restore");
    byte[] restoreBytes = jedis.dump("restore");

    try {
        jedis.restore("restore", 0, restoreBytes);//key已存在时，会返回(error) BUSYKEY Target key name already exists.
    } catch (Exception e) {
        e.printStackTrace();
    }

    jedis.restore("newRestore", 2000, restoreBytes);//2秒后过期
    System.out.println(jedis.get("newRestore"));

    //休眠2秒
    Thread.sleep(3000);
    System.out.println(jedis.get("newRestore"));


    redisTemplate.restore("newRestore", restoreBytes, 0, TimeUnit.SECONDS);
    System.out.println(redisTemplate.opsForValue().get("newRestore"));
}
```

##### SCAN命令

用于迭代当前数据库中的key集合。命令说明：https://redis.io/commands/scan  

redis客户端执行的命令如下  
```
set 1 1
set 2 2
...
scan 0
scan 17
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/scan%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void scan() {
    for (int i = 0; i < 20; i++) {
        jedis.set(String.valueOf(i), String.valueOf(i));
    }

    String cursor = "0";
    //当cursor再次为0时结束循环
    do {
        ScanResult<String> scanResult = jedis.scan(cursor);
        System.out.println(scanResult.getResult());
        cursor = scanResult.getStringCursor();
    } while (!cursor.equals("0"));

    //spring
    List<String> keys = new ArrayList();//存放key
    RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
    Cursor<byte[]> c = redisConnection.scan(ScanOptions.NONE);
    while (c.hasNext()) {
        byte[] key = c.next();
        keys.add(new String(key, StandardCharsets.UTF_8));
    }

    System.out.println(keys);
}
```

##### SORT命令

返回或存储key的list、 set 或sorted set 中的元素。默认是按照数值类型排序的，并且按照两个元素的双精度浮点数类型值进行比较。  
SORT key [BY pattern][LIMIT offset count] [GET pattern [GET pattern ...]][ASC|DESC] [ALPHA][STORE destination]  
返回值：返回或存储key的list、 set 或sorted set 中的元素。  


redis客户端执行的命令如下
```
lpush sortNum "3" "2" "5" "1" "4"
sort sortNum
sort sortNum desc

lpush sortWebsite "www.redis.io" "www.google.com" "www.baidu.com"
sort sortWebsite ALPHA
sort sortWebsite ALPHA LIMIT 0 2

lpush userId 1 2 3 4
set userName:1 admin:1
set userName:2 admin:2
set userName:3 admin:3
set userName:4 admin:4

set userIntegral:1 232
set userIntegral:2 11
set userIntegral:3 666
set userIntegral:4 123
sort userId by userIntegral:* get userName:*

hmset userInfo:1 name admin:1 integral 232
hmset userInfo:2 name admin:2 integral 11
hmset userInfo:3 name admin:3 integral 666
hmset userInfo:4 name admin:4 integral 123
sort userId BY userInfo:*->integral GET userInfo:*->name

sort userId BY userInfo:*->integral GET userInfo:*->name store destResult
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/sort%E5%91%BD%E4%BB%A4%E4%B8%80%E8%88%AC%E7%94%A8%E6%B3%95%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/sort%E5%91%BD%E4%BB%A4%E4%BD%BF%E7%94%A8%E5%A4%96%E9%83%A8key%E8%BF%9B%E8%A1%8C%E6%8E%92%E5%BA%8F%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/sort%E5%91%BD%E4%BB%A4%E4%BF%9D%E5%AD%98%E6%8E%92%E5%BA%8F%E7%BB%93%E6%9E%9C%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/sort%E5%91%BD%E4%BB%A4%E5%8F%82%E6%95%B0ALPHA%E5%92%8CLIMIT%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/sort%E5%91%BD%E4%BB%A4%E5%B0%86%E5%93%88%E5%B8%8C%E8%A1%A8%E4%BD%9C%E4%B8%BAGET%E6%88%96BY%E7%9A%84%E5%8F%82%E6%95%B0%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码  
```java
@Test
public void sort() {
    /**
     * 一般 SORT 用法
     */
    System.out.println("---------一般 SORT 用法---------");
    jedis.lpush("sortNum", "3", "2", "5", "1", "4");

    System.out.println("sortNum:" + jedis.sort("sortNum"));
    System.out.println("sortNum spring输出:" + redisTemplate.sort(SortQueryBuilder.sort("sortNum").build()));//spring

    //逆序
    SortingParams sortingParams = new SortingParams();
    sortingParams.desc();
    System.out.println("逆序输出:" + jedis.sort("sortNum", sortingParams));

    //spring逆序
    System.out.println("spring逆序输出:" + redisTemplate.sort(SortQueryBuilder.sort("sortNum").order(SortParameters.Order.DESC).build()));//spring


    /**
     * 使用 ALPHA 修饰符对字符串进行排序
     * 当需要对字符串进行排序时， 需要显式地在 SORT 命令之后添加 ALPHA 修饰符
     * 未使用ALPHA 修饰符时，会出现错误：ERR One or more scores can't be converted into double
     */
    System.out.println("---------使用 ALPHA 修饰符对字符串进行排序---------");
    jedis.lpush("sortWebsite", "www.redis.io", "www.google.com", "www.baidu.com");

    sortingParams.asc();
    sortingParams.alpha();
    System.out.println("sortWebsite:" + jedis.sort("sortWebsite", sortingParams));
    System.out.println("sortWebsite spring输出:" + redisTemplate.sort(SortQueryBuilder.sort("sortWebsite").alphabetical(true).build()));

    /**
     * 使用 LIMIT 修饰符限制返回结果
     */
    System.out.println("---------使用 LIMIT 修饰符限制返回结果---------");
    sortingParams.limit(0, 2);
    System.out.println("使用 LIMIT 修饰符后的sortWebsite:" + jedis.sort("sortWebsite", sortingParams));
    System.out.println("使用 LIMIT 修饰符后的sortWebsite spring输出:" +
            redisTemplate.sort(SortQueryBuilder.sort("sortWebsite").alphabetical(true).limit(0, 2).build()));

    /**
     * 使用外部 key 进行排序
     */
    System.out.println("---------使用外部 key 进行排序---------");
    //设置
    jedis.lpush("userId", "1", "2", "3", "4");

    jedis.set("userName:1","admin:1");
    jedis.set("userName:2","admin:2");
    jedis.set("userName:3","admin:3");
    jedis.set("userName:4","admin:4");
    jedis.set("userIntegral:1","232");
    jedis.set("userIntegral:2","11");
    jedis.set("userIntegral:3","666");
    jedis.set("userIntegral:4","123");

    sortingParams = new SortingParams();
    sortingParams.by("userIntegral:*");
    sortingParams.get("userName:*");
    System.out.println("使用 get 的userId:" + jedis.sort("userId", sortingParams));
    System.out.println("使用 get 的userId spring输出:" +
            redisTemplate.sort(SortQueryBuilder.sort("userId").by("userIntegral:*").get("userName:*").build()));

    //将哈希表作为 GET 或 BY 的参数
    System.out.println("---------使用哈希表作为 GET 或 BY 的参数---------");
    jedis.hset("userInfo:1","name","admin:1");
    jedis.hset("userInfo:1","integral","232");
    jedis.hset("userInfo:2","name","admin:2");
    jedis.hset("userInfo:2","integral","11");
    jedis.hset("userInfo:3","name","admin:3");
    jedis.hset("userInfo:3","integral","666");
    jedis.hset("userInfo:4","name","admin:4");
    jedis.hset("userInfo:4","integral","123");

    sortingParams = new SortingParams();
    sortingParams.by("userInfo:*->integral");
    sortingParams.get("userInfo:*->name");
    System.out.println("使用 哈希表 的userId:" + jedis.sort("userId", sortingParams));
    System.out.println("使用 哈希表 的userId spring输出:" +
            redisTemplate.sort(SortQueryBuilder.sort("userId").by("userInfo:*->integral").get("userInfo:*->name").build()));

    /**
     * 保存排序结果
     */
    System.out.println("---------保存排序结果---------");
    System.out.println("保存排序结果:" + jedis.sort("userId", sortingParams,"destResult"));
    System.out.println("使用 哈希表 的userId spring输出:" +
            redisTemplate.sort(SortQueryBuilder.sort("userId").by("userInfo:*->integral").get("userInfo:*->name").build(),"destSpringResult"));
}
```

##### TOUCH命令

修改key的最后访问时间，不存在则忽略  
TOUCH key [key ...]  
返回值：修改key的个数  


redis客户端执行的命令如下  
```
set touch touch
touch touch
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/touch%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)



##### TTL命令

返回 key 的剩余生存时间  
TTL key  
返回值：当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间。  

redis客户端执行的命令如下  
```
set ttl ttl
expire ttl 10
ttl ttl
```

下面是java代码
```java
@Test
public void ttl() {
    jedis.set("ttl", "ttl");
    jedis.expire("ttl", 10);

    System.out.println(jedis.ttl("ttl"));
    //spring
    System.out.println(redisTemplate.getExpire("ttl"));
}
```

##### TYPE命令

返回key所存储的value的数据结构类型  
TYPE key  
返回值：返回当前key的数据类型，如果key不存在时返回none  

redis客户端执行的命令如下  
```
set stringType stringValue
lpush listType listValue
sadd setType setValue

type stringType
type listType
type setType
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/type%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void type() {
    jedis.set("stringType", "stringValue");
    jedis.lpush("listType", "listValue");
    jedis.sadd("setType", "setValue");

    System.out.println(jedis.type("stringType"));
    System.out.println(jedis.type("listType"));

    //spring
    System.out.println(redisTemplate.type("setType"));

}
```

##### UNLINK命令

和DEL命令相似，唯一的不同就是UNLINK命令不是阻塞的(4.0.0.后新增)  
UNLINK key [key ...]  
返回值：返回删除的数量  

redis客户端执行的命令如下  
```
set unlink unlink
unlink unlink
```

执行结果如下  
![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/generic/unlink%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

##### WAIT命令

阻塞当前客户端。命令说明：https://redis.io/commands/wait  
WAIT numslaves timeout  

redis客户端执行的命令如下  
```
set wait wait
wait 1 0
wait 2 1000
```


下面是java代码  
```java 
@Test
public void waitCommand() throws InterruptedException {
    jedis.set("wait", "wait");
    jedis.waitReplicas(1, 0);

}
```