### 前言

这一篇文章将讲述Redis中的hash类型命令，同样也是通过demo来讲述，其他部分这里就不在赘述了。

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-hash](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-hash)

### 案例

demo功能是通讯录，整个demo的大致页面如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/%E9%A1%B5%E9%9D%A2.png?raw=true)

### 准备工作

首先定义一个key的前缀，已经存储自增id的key

```java
private static final String CONTACTS_KEY_PREFIX = "contacts:";
private static final String CONTACTS_ID_KEY = "contactsID";
```

通讯录相关的key将会以contacts:1、contacts:2、contacts:3的形式存储

redis操作对象

```java
private RedisTemplate redisTemplate;
//string 命令操作对象
private ValueOperations valueOperations;
//hash 命令操作对象
private HashOperations hashOperatio
```

### 疑惑

如果读者和我一样是学Java的，刚听到hash时的第一反应是这个不是一个算法吗？当时我也是这样想的。那么先来看看hash在Redis中的结构，如下图（图片来源于Redis in Action）。

![图片来源于Redis in Action](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/hash%E7%BB%93%E6%9E%84.png?raw=true)

如果图看不懂的，我再来介绍下。其实Redis中的hash结构就和mysql中的表类似，把key当做表名，一张表中有多个列名（sub-key），每个列有自己的值（value），然后这张表只能存放一条数据。不过，这里的hash结构不会像mysql中固定好的，它可以很方便的增加删除列，例如增加sub-key3删除sub-key1.

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/%E8%A1%A8.png?raw=true)

### 新增

#### 命令介绍

先来看看hash中关于新增的一些命令

| 命令   | 用例                                    | 描述                                             |
| ------ | --------------------------------------- | ------------------------------------------------ |
| HSET   | HSET key field value                    | 设置 key 指定的哈希集中指定字段的值。            |
| HSETNX | HSETNX key field value                  | 当field不存在时，才能成功设置值                  |
| HMSET  | HMSET key field value [field value ...] | 设置 `key` 指定的哈希集中指定字段的值（多个） 。 |



接下来看看demo中新增的功能，下图中点击+按钮，然后在弹出框中填入name和phone属性，点击提交后整个新增流程结束。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/%E6%96%B0%E5%A2%9E.gif?raw=true)

来看看后台的方法

```java
@RequestMapping(value = "/add",method = RequestMethod.POST)
public boolean add(@RequestBody JSONObject contacts){
    //获取自增id
    Long contactsId = valueOperations.increment(CONTACTS_ID_KEY, 1);

    contacts.put("id",String.valueOf(contactsId));
    //json转map，然后存入redis
    hashOperations.putAll(CONTACTS_KEY_PREFIX+contactsId,contacts.getInnerMap());

    return true;
}
```
1. 首先是获得自增id

2. 然后将id存入到前端传过来的json对象中

3. 调用hashOperations对象的putAll方法将对象传入到Redis中。（putAll方法其实是调用了hmset命令,源码如下）

```java
public void putAll(K key, Map<? extends HK, ? extends HV> m) {
    if (!m.isEmpty()) {
        byte[] rawKey = this.rawKey(key);
        Map<byte[], byte[]> hashes = new LinkedHashMap(m.size());
        Iterator var5 = m.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<? extends HK, ? extends HV> entry = (Entry)var5.next();
            hashes.put(this.rawHashKey(entry.getKey()), this.rawHashValue(entry.getValue()));
        }
		//调用hMSet
        this.execute((connection) -> {
            connection.hMSet(rawKey, hashes);
            return null;
        }, true);
    }
}
```

### 列表查询

#### 命令介绍

同样先看看相关的获取值命令

| 命令    | 用例                        | 描述                                    |
| ------- | --------------------------- | --------------------------------------- |
| HGET    | HGET key field              | 返回 key 指定的哈希集中该字段所关联的值 |
| HGETALL | HGETALL key                 | 返回 key 指定的哈希集中所有的字段和值。 |
| HKEYS   | HKEYS key                   | 返回 key 指定的哈希集中所有字段的名字。 |
| HMGET   | HMGET key field [field ...] | 返回 `key` 指定的哈希集中指定字段的值。 |
| HVALS   | HVALS key                   | 返回 key 指定的哈希集中所有字段的值。   |
| HSCAN   |                             | 用于迭代Hash类型中的键值对。            |

##### HGET和HGETALL命令

来看看HGET和HGETALL在redis客户端和java中是如何操作的

redis客户端执行的命令如下

```
hset key field1 "Hi"
hset key field1 "Hello"
hsetnx key field1 "Hello"
hsetnx key field2 " redis"
hget key field1
hgetall key
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hset%E5%92%8Chget%E7%9B%B8%E5%85%B3%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hGetAll() {
    jedis.hset("key", "field1", "Hi");
    redisTemplate.opsForHash().put("key", "field1", "Hello");

    System.out.println(jedis.hsetnx("key", "field1", "Hello"));
    System.out.println(redisTemplate.opsForHash().putIfAbsent("key", "field2", "Hello"));

    System.out.println(jedis.hget("key", "field1"));
    System.out.println(jedis.hgetAll("key"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().get("key", "field1"));
    System.out.println(redisTemplate.opsForHash().entries("key"));
}
```
##### HKEYS

redis客户端执行的命令如下

```
hset hashKey field1 value1
hset hashKey field2 value2
hkeys hashKey
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hkeys%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hKeys() {
    jedis.hset("hashKey", "field1", "value1");
    jedis.hset("hashKey", "field2", "value2");

    System.out.println(jedis.hkeys("hashKey"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().keys("hashKey"));
    /**
     * 注：两次结果返回的顺序是不一样的，
     * 因为jedis.hkeys返回的是HashSet(内部使用HashMap)
     * redisTemplate.opsForHash().keys返回的是LinkHashSet（内部使用LinkHashMap）
     */
}
```


##### HVALS

redis客户端执行的命令如下

```
hmset key field1 value1 field2 value2 field3 value3
hvals key
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hvals%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

下面是java代码
```java
@Test
public void hVals() {
    Map<String, String> map = new HashMap<>(3);
    map.put("field1", "value1");
    map.put("field2", "value2");
    map.put("field3", "value3");

    jedis.hmset("key", map);

    System.out.println(jedis.hvals("key"));

    //spring redisTemplate
    System.out.println(redisTemplate.opsForHash().values("key"));
}
```



#### 查询方法代码


接着写个查询方法，将新增的内容查询出来

```java
@RequestMapping(value = "/getList",method = RequestMethod.GET)
    public List getList(){
        List list = new ArrayList();

        //获取联系人的keys
        Set<String> keys = redisTemplate.keys(CONTACTS_KEY_PREFIX+"*");

        for (String key: keys) {
            Map entries = hashOperations.entries(key);
            list.add(entries);
        }

        return list;
    }
```

这个hash查询多个会不方便些，步骤如下

1. 获取相关的key
2. 循环查找key相关的数据
3. 将查询出来的结果添加到list中，返回

### 添加属性

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/%E6%B7%BB%E5%8A%A0%E5%B1%9E%E6%80%A7.gif?raw=true)



来看看代码

```java
@RequestMapping(value = "/addAttr", method = RequestMethod.POST)
public boolean addAttr(@RequestBody JSONObject contacts){
    String id = contacts.getString("id");
    String fieldName = contacts.getString("fieldName");
    String fieldValue = contacts.getString("fieldValue");

    hashOperations.put(CONTACTS_KEY_PREFIX+id, fieldName, fieldValue);

    return true;
}
```

其实就是用hset命令进行插入

```
hset contacts:1 address 北京9527号
```

### 删除属性

#### 命令介绍

| 命令 | 用例                       | 描述                              |
| ---- | -------------------------- | --------------------------------- |
| HDEL | HDEL key field [field ...] | 从 key 指定的哈希集中移除指定的域 |

redis客户端执行的命令如下

```
hset hDelKey filed1 filedValue1
hdel hDelKey filed1
hdel hDelKey filed1
```
执行结果如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/hash/hdel%E5%91%BD%E4%BB%A4%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

现在来看看demo中的删除属性

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-hash/%E5%88%A0%E9%99%A4%E5%B1%9E%E6%80%A7.gif?raw=true)

代码如下

```java
@RequestMapping(value = "/delAttr", method = RequestMethod.POST)
public boolean delAttr(@RequestBody JSONObject contacts){

    String id = contacts.getString("id");
    String fieldName = contacts.getString("fieldName");
    hashOperations.delete(CONTACTS_KEY_PREFIX+id, fieldName);

    return true;
}
```

### 其他命令

| 命令         | 用例                             | 描述                                      |
| ------------ | -------------------------------- | ----------------------------------------- |
| HEXISTS      | HEXISTS key field                | 返回hash里面field是否存在                 |
| HINCRBY      | HINCRBY key field increment      | 增加 `key` 指定的哈希集中指定字段的数值   |
| HINCRBYFLOAT | HINCRBYFLOAT key field increment | 同上，加的是浮点型                        |
| HLEN         | HLEN key                         | 返回 `key` 指定的哈希集包含的字段的数量。 |
| HSTRLEN      | HSTRLEN key field                | 返回hash指定field的value的字符串长度      |



建议学习的人最好每个命令都去敲下，加深印象。下面诗句送给你们。
> 纸上得来终觉浅，绝知此事要躬行。————出自《冬夜读书示子聿》
