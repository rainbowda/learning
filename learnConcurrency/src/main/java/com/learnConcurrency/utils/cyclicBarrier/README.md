# CyclicBarrier案例代码   
## 目录结构
```
+---cyclicBarrier
|   |   Case.java  案例测试代码
|   |   README.md  说明文档
|   |   StartGame.java  实现Runnable的类
```

## 需求
继上一篇`CountDownLatch`模拟游戏加载后，现在用户点击开始按钮后，需要匹配包括自己在内的五个玩家才能开始游戏，匹配玩家成功后进入到选择角色阶段。当5位玩家角色都选择完毕后，开始进入游戏。进入游戏时需要加载相关的数据，待全部玩家都加载完毕后正式开始游戏。  
## 解决方案
从需求中可以知道，想要开始游戏需要经过三个阶段，分别是  
1. 匹配玩家
2. 选择角色
3. 加载数据

在这三个阶段中，都需要互相等待对方完成才能继续进入下个阶段。   
这时可以采用`CyclicBarrier`来作为各个阶段的节点，等待其他玩家到达，在进入下个阶段。   

## 定义继承Runnable的类
这里名称就叫做`StartGame`，包含两个属性  
```java
private String player;
private CyclicBarrier barrier;
```
通过构造函数初始化两个属性    
```java
public StartGame(String player, CyclicBarrier barrier) {
    this.player = player;
    this.barrier = barrier;
}
```
run方法如下
```java
public void run() {
    try {
        System.out.println(this.getPlayer()+" 开始匹配玩家...");
        findOtherPlayer();
        barrier.await();

        System.out.println(this.getPlayer()+" 进行选择角色...");
        choiceRole();
        System.out.println(this.getPlayer()+" 角色选择完毕等待其他玩家...");
        barrier.await();

        System.out.println(this.getPlayer()+" 开始游戏,进行游戏加载...");
        loading();
        System.out.println(this.getPlayer()+" 游戏加载完毕等待其他玩家加载完成...");
        barrier.await();


        start();
    } catch (Exception e){
        e.printStackTrace();
    }
}
```
其他的方法findOtherPlayer()、choiceRole()等待使用  
```java
Thread.sleep()
```
来模拟花费时间  

## 编写测试代码
CyclicBarrier有两个构造函数,如下
```java
public CyclicBarrier(int parties) {}
public CyclicBarrier(int parties, Runnable barrierAction) {}
```
先来看看一个参数的构造函数
### CyclicBarrier(int parties)

```java
public static void main(String[] args) throws IOException {
    CyclicBarrier barrier = new CyclicBarrier(5);

    Thread player1 = new Thread(new StartGame("1",barrier));
    Thread player2 = new Thread(new StartGame("2",barrier));
    Thread player3 = new Thread(new StartGame("3",barrier));
    Thread player4 = new Thread(new StartGame("4",barrier));
    Thread player5 = new Thread(new StartGame("5",barrier));

    player1.start();
    player2.start();
    player3.start();
    player4.start();
    player5.start();

    System.in.read();
}
```
测试结果如下

![](F:\gitee\blog\concurrency\img\CyclicBarrier测试图.gif)

### CyclicBarrier(int parties, Runnable barrierAction)
```java
CyclicBarrier barrier = new CyclicBarrier(5);
```

替换为
```java
CyclicBarrier barrier = new CyclicBarrier(5, () -> {
    try {
        System.out.println("阶段完成，等待2秒...");
        Thread.sleep(2000);
        System.out.println("进入下个阶段...");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

});
```

再来看看效果

![](F:\gitee\blog\concurrency\img\CyclicBarrier测试图2.gif)

可以看到在到达某个节点时，会执行实例化CyclicBarrier时传入的Runnable对象。而且每一次到达都会执行一次。示意图如下


## CyclicBarrier和CountDownLatch的区别

| CountDownLatch                                               | CyclicBarrier                                            |
| ------------------------------------------------------------ | -------------------------------------------------------- |
| 计数为0时，无法重置                                          | 计数达到0时，计数置为传入的值重新开始                    |
| 调用countDown()方法计数减一，调用await()方法只进行阻塞，对计数没任何影响 | 调用await()方法计数减一，若减一后的值不等于0，则线程阻塞 |
| 不可重复使用                                                 | 可重复使用                                               |

## await方法
```java
public int await(){}
public int await(long timeout, TimeUnit unit){}
```

无参的await方法这里就不做介绍了，主要介绍下有参的await方法。   
有参的await方法传入两个参数，一个是时间、另一个是时间单位   
当调用有参的await方法时会出现下方两个异常   
```
java.util.concurrent.TimeoutException
java.util.concurrent.BrokenBarrierException
```
TimeoutException异常是指调用`await`方法后等待时间超过传入的时间，此时会将`CyclicBarrier`的状态变成broken，其他调用`await`方法将会抛出BrokenBarrierException异常，这时的`CyclicBarrier`将变得不可用，需要调用`reset()`方法重置`CyclicBarrier`的状态。   

为什么这么说？   
源码分析一波就可以看出来了    
不管是有参还是无参的await方法都是调用`CyclicBarrier`的`dowait(boolean timed, long nanos)`方法，这个方法代码太长了，截取部分贴出来   
```java
private int dowait(boolean timed, long nanos){
    //加锁、try catch代码
    final Generation g = generation;
    //判断栅栏的状态
    if (g.broken)
        throw new BrokenBarrierException();
    //...省略

    int index = --count;
    //(index == 0) 时的代码，省略

    for (;;) {
        try {
            if (!timed)
                trip.await();
            else if (nanos > 0L)
                nanos = trip.awaitNanos(nanos);
        } catch (InterruptedException ie) {}

        //判断栅栏的状态
        if (g.broken)
            throw new BrokenBarrierException();

        if (g != generation)
            return index;
        //判断是否是定时的，且已经超时了
        if (timed && nanos <= 0L) {
            //打破栅栏的状态
            breakBarrier();
            throw new TimeoutException();
        }
    }
    //解锁
}
```

在代码的尾部进行判断当前等待是否已经超时，如果是会调用`breakBarrier()`方法，且抛出TimeoutException异常，下面是`breakBarrier()`的代码
```java
private void breakBarrier() {
    generation.broken = true;
    count = parties;
    trip.signalAll();
}
```
代码中将broken状态置为true，表示当前栅栏移除损坏状态，且重置栅栏数量，然后唤醒其他等待的线程。此时被唤醒的线程或者其他线程进入dowait方法时，都会抛出BrokenBarrierException异常
```
if (g.broken)
    throw new BrokenBarrierException();
```

