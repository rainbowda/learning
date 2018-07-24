### 前言

这一篇文章将讲述Redis中的list类型命令，同样也是通过demo来讲述，其他部分这里就不在赘述了。

项目Github地址：[https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-list](https://github.com/rainbowda/learnWay/tree/master/learnRedis/case-list)

### 案例

demo功能是队列，整个demo的大致页面如下。左边是存储到Redis中的数据，右边是从Redis中弹出的数据。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E9%A1%B5%E9%9D%A2.png?raw=true)

### 准备工作

首先定义一个存储list的key

```java
private static final String LIST_KEY = "list:1";
```

队列的key就用list:1

redis操作对象

```java
private RedisTemplate redisTemplate;
//string 命令操作对象
private ValueOperations valueOperations;
//list 命令操作对象
private ListOperations listOperations;
```


list在Redis中的结构可以看下图（图片来源于Redis in Action）。

![图片来源于Redis in Action](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E7%BB%93%E6%9E%84.png?raw=true)



### 插入数据

#### 头部插入

##### 命令介绍

| 命令   | 用例                        | 描述                                                         |
| ------ | --------------------------- | ------------------------------------------------------------ |
| LPUSH  | LPUSH key value [value ...] | 将所有指定的值插入到存于 key 的列表的头部。  如果 key 不存在，那么在进行 push 操作前会创建一个空列表。 |
| LPUSHX | LPUSHX key value            | 只有当 key 已经存在并且存着一个 list 的时候，在这个 key 下面的 list 的头部插入 value。 |



接下来看看demo中头部插入的功能，点击下图中头部插入按钮，然后在弹出框中填入数字0，点击提交后整个头部插入流程结束。可以看到左边的队列数据出现了一条{"data":"0"} 数据，在数据{"data":"1"} 上面。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E5%A4%B4%E9%83%A8%E6%8F%92%E5%85%A5.gif?raw=true)

来看看后台的方法

```java
@RequestMapping(value = "/leftPop",method = RequestMethod.GET)
public Object leftPop(){
    return listOperations.leftPop(LIST_KEY);
}
```

如果需要在Redis中操作，可以敲下面的命令

```
lpush list:1 "{\"data\":\"0\"}" 
```
#### 尾部插入

##### 命令介绍

| 命令   | 用例                        | 描述                                                         |
| ------ | --------------------------- | ------------------------------------------------------------ |
| RPUSH  | RPUSH key value [value ...] | 向存于 key 的列表的尾部插入所有指定的值。如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。 |
| RPUSHX | RPUSHX key value            | 将值 value 插入到列表 key 的表尾, 当且仅当 key 存在并且是一个列表。 |



接下来看看demo中尾部插入的功能，点击下图中尾部插入按钮，然后在弹出框中填入数字11，点击提交后整个新增流程结束。可以看到左边的队列数据出现了一条{"data":"11"} 数据，在数据{"data":"10"}下面。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E5%B0%BE%E9%83%A8%E6%8F%92%E5%85%A5.gif?raw=true)

来看看后台的方法

```java
@RequestMapping(value = "/rightPop",method = RequestMethod.GET)
public Object rightPop(){
    return listOperations.rightPop(LIST_KEY);
}
```

如果需要在Redis中操作，可以敲下面的命令

```
rpush list:1 "{\"data\":\"11\"}" 
```


### 列表查询

#### 命令介绍

同样先看看相关的获取值命令

| 命令   | 用例                  | 描述                                           |
| ------ | --------------------- | ---------------------------------------------- |
| LRANGE | LRANGE key start stop | 返回存储在 key 的列表里指定范围内的元素。      |
| LINDEX | LINDEX key index      | 返回列表里的元素的索引 index 存储在 key 里面。 |
| LLEN   | LLEN key              | 返回存储在 key 里的list的长度。                |

#### 


后台查询方法，将新增的内容查询出来

```java
@RequestMapping(value = "/getList",method = RequestMethod.GET)
public List getList(){
    List list = listOperations.range(LIST_KEY, 0, -1);

    //可以用size获取成员长度
    //listOperations.size(LIST_KEY);

    return list;
}
```



### 数据弹出

#### 头部弹出
| 命令  | 用例                        | 描述                                                         |
| ----- | --------------------------- | ------------------------------------------------------------ |
| LPOP  | LPOP key                    | 移除并且返回 key 对应的 list 的第一个元素。                  |
| BLPOP | BLPOP key [key ...] timeout | 它是命令 [LPOP](http://www.redis.cn/commands/lpop.html) 的阻塞版本，这是因为当给定列表内没有任何元素可供弹出的时候， 连接将被 [BLPOP](http://www.redis.cn/commands/blpop.html) 命令阻塞。 |



接下来看看头部弹出的功能，点击下图中头部弹出按钮，可以看到左边的队列顶部数据减少了，在右边弹出的数据出现了左边队列数据消失的数据。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E5%A4%B4%E9%83%A8%E5%BC%B9%E5%87%BA.gif?raw=true)

来看看后台的方法

```java
@RequestMapping(value = "/leftPop",method = RequestMethod.GET)
public Object leftPop(){
    return listOperations.leftPop(LIST_KEY);
}
```

如果需要在Redis中操作，可以敲下面的命令

```
lpop list:1 
```
#### 尾部弹出
| 命令  | 用例                        | 描述                                                         |
| ----- | --------------------------- | ------------------------------------------------------------ |
| RPOP  | RPOP key                    | 移除并返回存于 key 的 list 的最后一个元素。                  |
| BRPOP | BRPOP key [key ...] timeout | 它是 [RPOP](http://www.redis.cn/commands/commands/rpop.html) 的阻塞版本，因为这个命令会在给定list无法弹出任何元素的时候阻塞连接。 |



接下来看看尾部弹出的功能，点击下图中尾部弹出按钮，可以看到左边的队列尾部数据减少了，在右边弹出的数据出现了左边队列数据消失的数据。

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/case-list/%E5%B0%BE%E9%83%A8%E5%BC%B9%E5%87%BA.gif?raw=true)

来看看后台的方法

```java
@RequestMapping(value = "/rightPop",method = RequestMethod.GET)
public Object rightPop(){
    return listOperations.rightPop(LIST_KEY);
}
```

如果需要在Redis中操作，可以敲下面的命令

```
rpop list:1 
```


### 其他命令

| 命令       | 用例                                  | 描述                                                         |
| ---------- | ------------------------------------- | ------------------------------------------------------------ |
| LINSERT    | LINSERT key BEFORE\|AFTER pivot value | 把 value 插入存于 key 的列表中在基准值 pivot 的前面或后面。  |
| LREM       | LREM key count value                  | 从存于 key 的列表里移除前 count 次出现的值为 value 的元素。  |
| LSET       | LSET key index value                  | 设置 index 位置的list元素的值为 value。                      |
| LTRIM      | LTRIM key start stop                  | 修剪(trim)一个已存在的 list，这样 list 就会只包含指定范围的指定元素。 |
| RPOPLPUSH  | RPOPLPUSH source destination          | 原子性地返回并移除存储在 source 的列表的最后一个元素（列表尾部元素）， 并把该元素放入存储在 destination 的列表的第一个元素位置（列表头部）。 |
| BRPOPLPUSH | BRPOPLPUSH source destination timeout | `BRPOPLPUSH` 是 [RPOPLPUSH](http://www.redis.cn/commands/rpoplpush.html) 的阻塞版本。 |



### RPOPLPUSH和BRPOPLPUSH

这两个命令作用其实是相同的，只不过BRPOPLPUSH是阻塞的，当没有数据时，会一直阻塞，直到有数据。

在Redis官方文档中，用RPOPLPUSH命令举了两个例子，一个是Reliable queue（安全的队列 ），另一个是Circular list（循环列表）。

#### Reliable queue（安全的队列 ）

> Redis通常都被用做一个处理各种后台工作或消息任务的消息服务器。 一个简单的队列模式就是：生产者把消息放入一个列表中，等待消息的消费者用 [RPOP](http://www.redis.cn/commands/rpop.html) 命令（用轮询方式）， 或者用 BRPOP 命令（如果客户端使用阻塞操作会更好）来得到这个消息。
>
> 然而，因为消息有可能会丢失，所以这种队列并是不安全的。例如，当接收到消息后，出现了网络问题或者消费者端崩溃了， 那么这个消息就丢失了。
>
> RPOPLPUSH (或者其阻塞版本的 [BRPOPLPUSH](http://www.redis.cn/commands/brpoplpush.html)） 提供了一种方法来避免这个问题：消费者端取到消息的同时把该消息放入一个正在处理中的列表。 当消息被处理了之后，该命令会使用 LREM 命令来移除正在处理中列表中的对应消息。
>
> 另外，可以添加一个客户端来监控这个正在处理中列表，如果有某些消息已经在这个列表中存在很长时间了（即超过一定的处理时限）， 那么这个客户端会把这些超时消息重新加入到队列中。
>
> 翻译来自 http://www.redis.cn/commands/rpoplpush.html



#### Circular list（循环列表）

> RPOPLPUSH 命令的 source 和 destination 是相同的话， 那么客户端在访问一个拥有n个元素的列表时，可以在 O(N) 时间里一个接一个获取列表元素， 而不用像 [LRANGE](http://www.redis.cn/commands/lrange.html) 那样需要把整个列表从服务器端传送到客户端。
>
> 上面这种模式即使在以下两种情况下照样能很好地工作： * 有多个客户端同时对同一个列表进行旋转（rotating）：它们会取得不同的元素，直到列表里所有元素都被访问过，又从头开始这个操作。 * 有其他客户端在往列表末端加入新的元素。
>
> 这个模式让我们可以很容易地实现这样一个系统：有 N 个客户端，需要连续不断地对一批元素进行处理，而且处理的过程必须尽可能地快。 一个典型的例子就是服务器上的监控程序：它们需要在尽可能短的时间内，并行地检查一批网站，确保它们的可访问性。
>
> 值得注意的是，使用这个模式的客户端是易于扩展（scalable）且安全的（reliable），因为即使客户端把接收到的消息丢失了， 这个消息依然存在于队列中，等下次迭代到它的时候，由其他客户端进行处理。
>
> 翻译来自 http://www.redis.cn/commands/rpoplpush.html



#### 案例-约瑟夫问题

> **约瑟夫问题**（有时也称为**约瑟夫斯置换**），是一个出现在[计算机科学](https://zh.wikipedia.org/wiki/%E8%AE%A1%E7%AE%97%E6%9C%BA%E7%A7%91%E5%AD%A6)和[数学](https://zh.wikipedia.org/wiki/%E6%95%B0%E5%AD%A6)中的问题。在计算机[编程](https://zh.wikipedia.org/wiki/%E7%BC%96%E7%A8%8B)的算法中，类似问题又称为**约瑟夫环**。
>
> 人们站在一个等待被处决的圈子里。 计数从圆圈中的指定点开始，并沿指定方向围绕圆圈进行。 在跳过指定数量的人之后，执行下一个人。 对剩下的人重复该过程，从下一个人开始，朝同一方向跳过相同数量的人，直到只剩下一个人，并被释放。
>
> 问题即，给定人数、起点、方向和要跳过的数字，选择初始圆圈中的位置以避免被处决。
>
> 来自维基百科 https://zh.wikipedia.org/wiki/%E7%BA%A6%E7%91%9F%E5%A4%AB%E6%96%AF%E9%97%AE%E9%A2%98



![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/list/josephus.png?raw=true)

##### 思路

定义一个list key为josephus，利用

```
RPOPLPUSH  josephus josephus
```

命令来构造循环链表，每当数到3时，使用rpop

```
rpop josephus
```

命令弹出

##### 代码实现

```java
public class JosephusProblem extends RedisBaseConnection {

    @Test
    public void test() {
        //构造数据
        for (int i = 1; i <= 41; i++) {
            listOperations.leftPush("josephus", String.valueOf(i));
        }

        int index = 1;
        while (listOperations.size("josephus") > 0) {
            //当数到3时，弹出
            if (index == 3) {
                System.out.println(listOperations.range("josephus", 0, -1));
                System.out.println("当前被杀的人是：" + listOperations.rightPop("josephus"));
                index = 0;
            } else {
                listOperations.rightPopAndLeftPush("josephus", "josephus");
            }
            index++;
        }
    }
}
```
整个代码步骤如下
1. 先是模拟有41个人（向redis中key为josephus的list添加41个数据）
2. 定义索引index
3. 循环判断key为josephus的数据长度是否大于0
4. 当索引index为3时，调用Redis的rpop命令弹出对应的数据。索引index不为3时，调用RPOPLPUSH命令，将对应的数据放到队列头部
5. 索引index加1

运行结果有点长，这里只截图最后一部分的结果，如下

![](https://github.com/rainbowda/learnWay/blob/master/learnRedis/img/list/%E7%BA%A6%E7%91%9F%E5%A4%AB%E9%97%AE%E9%A2%98%E8%BF%90%E8%A1%8C%E7%BB%93%E6%9E%9C.png?raw=true)

约瑟夫问题代码请点击[**JosephusProblem.java** ](https://github.com/rainbowda/learnWay/blob/master/learnRedis/command/src/main/java/com/learnRedis/list/JosephusProblem.java)

------

建议学习的人最好每个命令都去敲下，加深印象。下面诗句送给你们。

> 纸上得来终觉浅，绝知此事要躬行。————出自《冬夜读书示子聿》