### 分布式锁

展示了实现分布式锁过程中会出现的问题，以及解决思路。项目中实现的分布式锁代码不适用于生产环境。

### 文件结构
```
com
\---learnRedis
    |   LockTest.java # 测试类
    |
    \---lock
        |   LockConstants.java # 常量类
        |   RedisLock.java # 锁抽象类
        |
        +---case1
        |       LockCase1.java # 分布式锁第1个版本
        |
        +---case2
        |       LockCase2.java # 分布式锁第2个版本。
        |
        +---case3
        |       LockCase3.java # 分布式锁第3个版本。
        |
        +---case4
        |       LockCase4.java # 分布式锁第4个版本。
        |
        \---case5
                LockCase5.java # 分布式锁第5个版本。
```
### 锁版本

1. [分布式锁第1个版本](https://github.com/rainbowda/learnWay/blob/master/learnRedis/distributed-locks/src/main/java/com/learnRedis/lock/case1/LockCase1.java)：最原始版本
2. [分布式锁第2个版本](https://github.com/rainbowda/learnWay/blob/master/learnRedis/distributed-locks/src/main/java/com/learnRedis/lock/case2/LockCase2.java)：解决第1个版本死锁的问题-设置锁的过期时间
3. [分布式锁第3个版本](https://github.com/rainbowda/learnWay/blob/master/learnRedis/distributed-locks/src/main/java/com/learnRedis/lock/case3/LockCase3.java)：解决第2个版本锁误删除的问题-设置锁的value
4. [分布式锁第4个版本](https://github.com/rainbowda/learnWay/blob/master/learnRedis/distributed-locks/src/main/java/com/learnRedis/lock/case4/LockCase4.java)：解决第3个版本解锁不具备原子性的问题-使用lua脚本删除锁
5. [分布式锁第5个版本](https://github.com/rainbowda/learnWay/blob/master/learnRedis/distributed-locks/src/main/java/com/learnRedis/lock/case5/LockCase5.java)：解决第4个版本过期时间小于业务执行时间的问题-新开一个线程定时刷新过期时间
