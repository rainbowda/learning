## 自定义线程池
开篇一张图（图片来自[阿里巴巴Java开发手册（详尽版）](https://github.com/alibaba/p3c/blob/master/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%EF%BC%88%E8%AF%A6%E5%B0%BD%E7%89%88%EF%BC%89.pdf)），后面全靠编

![](https://github.com/rainbowda/learnWay/blob/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%E5%88%9B%E5%BB%BA%E7%BA%BF%E7%A8%8B%E6%B1%A0%E8%A7%84%E7%BA%A6.png?raw=true)

好了要开始编了，从图片中就可以看到这篇博文的主题了，ThreadPoolExecutor自定义线程池。

## 目录

1. [ThreadPoolExecutor构造函数介绍](#threadpoolexecutor构造函数介绍)
2. [核心线程数corePoolSize](#核心线程数corepoolsize)
3. [最大线程数maximumPoolSize](#最大线程数maximumpoolsize)
4. [线程存活时间keepAliveTime](#线程存活时间keepalivetime)
5. [线程存活时间单位unit](#线程存活时间单位unit)
6. [创建线程的工厂threadFactory](#创建线程的工厂threadfactory)
7. [队列](#队列)
    + 7.1 [有界队列](#有界队列)
    + 7.2 [无界队列](#无界队列)
8. [拒绝策略](#拒绝策略)
    + 8.1 [AbortPolicy](#abortpolicy)
    + 8.2 [CallerRunsPolicy](#callerrunspolicy)
    + 8.3 [DiscardPolicy](#discardpolicy)
    + 8.4 [DiscardOldestPolicy](#discardoldestpolicy)
    + 8.5 [自定义拒绝策略](#自定义拒绝策略)
9. [线程池扩展](#线程池扩展) 

### ThreadPoolExecutor构造函数介绍

在介绍穿件线程池的方法之前要先介绍一个类ThreadPoolExecutor，因为Executors工厂大部分方法都是返回ThreadPoolExecutor对象，先来看看它的构造函数吧

```java
public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {...}
```

参数介绍

| 参数            | 类型                     | 含义                           |
| :-------------- | ------------------------ | ------------------------------ |
| corePoolSize    | int                      | 核心线程数                     |
| maximumPoolSize | int                      | 最大线程数                     |
| keepAliveTime   | long                     | 存活时间                       |
| unit            | TimeUnit                 | 时间单位                       |
| workQueue       | BlockingQueue<Runnable>  | 存放线程的队列                 |
| threadFactory   | ThreadFactory            | 创建线程的工厂                 |
| handler         | RejectedExecutionHandler | 多余的的线程处理器（拒绝策略） |
### 核心线程数corePoolSize

这个参数表示线程池中的基本线程数量也就是核心线程数量。

### 最大线程数maximumPoolSize

这个参数是线程池中允许创建的最大线程数量，当使用有界队列时，且队列存放的任务满了，那么线程池会创建新的线程（最大不会超过这个参数所设置的值）。需要注意的是，**当使用无界队列时，这个参数是无效的。**

### 线程存活时间keepAliveTime

这个就是线程空闲时可以存活的时间，一旦超过这个时间，线程就会被销毁。

### 线程存活时间单位unit

线程存活的时间单位，有NANOSECONDS（纳秒）、MICROSECONDS（微秒）、MILLISECONDS（毫秒）、SECONDS（秒）、MINUTES（分钟）、HOURS（小时）、DAYS（天）。TimeUnit代码如下

```java
public enum TimeUnit {
    NANOSECONDS {...},

    MICROSECONDS {...},

    MILLISECONDS {...},

    SECONDS {...},

    MINUTES {...},

    HOURS {...},

    DAYS {...};
}
```

### 创建线程的工厂threadFactory

创建线程的工厂，一般都是采用Executors.defaultThreadFactory()方法返回的DefaultThreadFactory，当然也可以用其他的来设置更有意义的名称。

DefaultThreadFactory类如下

```java
/**
 * The default thread factory
 */
static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                      poolNumber.getAndIncrement() +
                     "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
```

### 队列

分为有界队列和无界队列，用于存放等待执行的任务的阻塞队列。有SynchronousQueue、ArrayBlockingQueue、LinkedBlockingQueue、DelayQueue、PriorityBlockingQueue、LinkedTransferQueue、DelayedWorkQueue、LinkedBlockingDeque。下面将介绍有界和无界两种常用的队列。BlockingQueue类图如下

![](https://github.com/rainbowda/learnWay/blob/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/BlockingQueue%E7%B1%BB%E5%9B%BE.png?raw=true)

#### 有界队列  

当使用有界队列时，如果有新的任务需要添加进来时，如果线程池实际线程数小于corePoolSize（核心线程数），则优先创建线程，如果线程池实际线程数大于corePoolSize（核心线程数）,则会将任务加入队列，若队列已满，则在中现场数不大于maximumPoolSize（最大线程数）的前提下，创建新的线程，若线程数大于maximumPoolSize（最大线程数），则执行拒绝策略。



#### 无界队列  

当使用无界队列时，maximumPoolSize（最大线程数）和拒绝策略便会失效，因为队列是没有限制的，所以就不存在队列满的情况。和有界队列相比，当有新的任务添加进来时，都会进入队列等待。但是这也会出现一些问题，例如线程的执行速度比任务提交速度慢，会导致无界队列快速增长，直到系统资源耗尽。

### 拒绝策略  
当使用有界队列时，且队列任务被填满后，线程数也达到最大值时，拒绝策略开始发挥作用。ThreadPoolExecutor默认使用AbortPolicy拒绝策略。RejectedExecutionHandler类图如下

![](https://github.com/rainbowda/learnWay/blob/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/RejectedExecutionHandler%E7%B1%BB%E5%9B%BE.png?raw=true)

我们来看看ThreadPoolExecutor是如何调用RejectedExecutionHandler的，可以直接查看execute方法

```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    
    public void execute(Runnable command) {
            if (command == null)
                throw new NullPointerException();

            int c = ctl.get();
            if (workerCountOf(c) < corePoolSize) {
                if (addWorker(command, true))
                    return;
                c = ctl.get();
            }
            if (isRunning(c) && workQueue.offer(command)) {
                int recheck = ctl.get();
                if (! isRunning(recheck) && remove(command))
                    reject(command);
                else if (workerCountOf(recheck) == 0)
                    addWorker(null, false);
            }else if (!addWorker(command, false))
                //拒绝线程
                reject(command);
        }
}
```



可以看到经过一系列的操作，不符合条件的会调用reject方法,那我么接着来看看reject方法

```java
final void reject(Runnable command) {
    handler.rejectedExecution(command, this);
}
```

可以看到调用了RejectedExecutionHandler接口的rejectedExecution方法。好了，现在来看看jdk提供的几个拒绝策略。

[拒绝策略的测试代码在这](https://github.com/rainbowda/learnWay/tree/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/testRejectedExecutionHandler)

> 注：后续会写一篇ThreadPoolExecutor源码解析，专门介绍ThreadPoolExecutor各个流程

#### AbortPolicy   

从下面代码可以看到直接抛出异常信息，但是线程池还是可以正常工作的。  

```java
public static class AbortPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        throw new RejectedExecutionException("Task " + r.toString() +
                                             " rejected from " +
                                             e.toString());
    }
}
```

示例代码

线程类

```java
public class Task implements Runnable{

   private int id ;

   public Task(int id){
      this.id = id;
   }

   public int getId() {
      return id;
   }
   public void setId(int id) {
      this.id = id;
   }

   @Override
   public void run() {
      //
      System.out.println(LocalTime.now()+" 当前线程id和名称为:" + this.id);
      try {
         Thread.sleep(1000);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }


   public String toString(){
      return "当前线程的内容为:{ id : " + this.id + "}";
   }

}
```
测试代码
```java
public class TestAbortPolicy {

    public static void main(String[] args) {
        //定义了1个核心线程数，最大线程数1个，队列长度2个
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2),
                new ThreadPoolExecutor.AbortPolicy());


        //直接提交4个线程
        executor.submit(new Task(1));
        executor.submit(new Task(2));
        executor.submit(new Task(3));
        //提交第四个抛异常
        executor.submit(new Task(4));

    }
}
```

执行结果

```java
当前线程id和名称为:1
Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@1540e19d rejected from java.util.concurrent.ThreadPoolExecutor@677327b6[Running, pool size = 1, active threads = 1, queued tasks = 2, completed tasks = 0]
	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2047)
	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:823)
	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1369)
	at java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:112)
	at com.learnConcurrency.executor.customThreadPool.testRejectedExecutionHandler.TestAbortPolicy.main(TestAbortPolicy.java:25)
当前线程id和名称为:2
当前线程id和名称为:3
```

可以看到添加第四个线程是抛出异常

#### CallerRunsPolicy  

首先判断线程池是否关闭，如果未关闭，则直接执行该线程。关闭则不做任何事情。

```java
public static class CallerRunsPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            r.run();
        }
    }
}
```

代码和上面的差不多就不贴了，想要查看的可以到github上查看TestCallerRunsPolicy，执行结果如下

```
14:58:19.462 当前线程id和名称为:4
14:58:19.462 当前线程id和名称为:1
14:58:20.464 当前线程id和名称为:5
14:58:20.464 当前线程id和名称为:2
14:58:21.464 当前线程id和名称为:3
14:58:22.464 当前线程id和名称为:6
```

#### DiscardPolicy  

可以看到里面没有任何代码，也就是这个被拒绝的线程任务被丢弃了，不作任何处理。  

```java
public static class DiscardPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    }
}
```

#### DiscardOldestPolicy  

首先判断线程池是否关闭，如果未关闭，丢弃最老的一个请求，尝试再次提交当前任务。  关闭则不做任何事情。

```java
public static class DiscardOldestPolicy implements RejectedExecutionHandler {
   
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            e.getQueue().poll();
            e.execute(r);
        }
    }
}
```

代码和上面的差不多就不贴了，想要查看的可以到github上查看TestDiscardOldestPolicy，执行结果如下

```
15:02:28.484 当前线程id和名称为:1
15:02:29.486 当前线程id和名称为:5
15:02:30.487 当前线程id和名称为:6
```

可以看到线程2、3、4都被替换了

#### 自定义拒绝策略   

实现RejectedExecutionHandle接口即可,如下MyRejected

```java
public class MyRejected implements RejectedExecutionHandler{

   @Override
   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      System.out.println("自定义处理：开始记录日志");
      System.out.println(r.toString());
      System.out.println("自定义处理：记录日志完成");
   }

}
```

测试代码

```java
public class TestCustomeRejectedPolicy {

    public static void main(String[] args) {
        //定义了1个核心线程数，最大线程数1个，队列长度2个
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2),
                new MyRejected());


        executor.execute(new Task(1));
        executor.execute(new Task(2));
        executor.execute(new Task(3));
        executor.execute(new Task(4));
        executor.execute(new Task(5));
        executor.execute(new Task(6));


        executor.shutdown();
    }
}
```

输出结果

```
自定义处理：开始记录日志
当前线程的内容为:{ id : 4}
自定义处理：记录日志完成
自定义处理：开始记录日志
当前线程的内容为:{ id : 5}
自定义处理：记录日志完成
自定义处理：开始记录日志
当前线程的内容为:{ id : 6}
自定义处理：记录日志完成
15:12:39.267 当前线程id和名称为:1
15:12:40.268 当前线程id和名称为:2
15:12:41.268 当前线程id和名称为:3

Process finished with exit code 0
```

这里如果有仔细观察的你可能会有所好奇，为什么这里用execute方法而不是用submit？

这时因为用submit方法后，传入的线程会被封装成RunnableFuture，而我写的MyRejected有调用到toString方法，Task类有重写toString方法，但是被封装成RunnableFuture会输入如下内容

```
自定义处理：开始记录日志
java.util.concurrent.FutureTask@1540e19d
自定义处理：记录日志完成
自定义处理：开始记录日志
java.util.concurrent.FutureTask@677327b6
自定义处理：记录日志完成
自定义处理：开始记录日志
java.util.concurrent.FutureTask@14ae5a5
自定义处理：记录日志完成
15:18:17.262 当前线程id和名称为:1
15:18:18.263 当前线程id和名称为:2
15:18:19.264 当前线程id和名称为:3

Process finished with exit code 0
```

### 线程池扩展

ThreadPoolExecutor类中有三个方法是空方法，可以通过继承来重写这三个方法对线程进行监控。通过重写beforeExecute和afterExecute方法，可以添加日志、计时、监控等等功能。terminated方法是在线程关闭时调用的，可以在这里面进行通知、日志等操作。

```java
//任务执行前
protected void beforeExecute(Thread t, Runnable r) { }
//任务执行后
protected void afterExecute(Runnable r, Throwable t) { }
//线程池关闭
protected void terminated() { }
```

示例代码

```java
public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new MyThreadPoolExecutor(
                2,              //coreSize
                4,              //MaxSize
                60,          //60
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));

        for (int i = 0; i < 8; i++) {
            int finalI = i + 1;
            pool.submit(() -> {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();
    }

    static class MyThreadPoolExecutor extends ThreadPoolExecutor{
        private final AtomicInteger tastNum = new AtomicInteger();
        private final ThreadLocal<Long> startTime = new ThreadLocal<>();

        public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            startTime.set(System.nanoTime());
            System.out.println(LocalTime.now()+" 执行之前-任务："+r.toString());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            long endTime = System.nanoTime();
            long time = endTime - startTime.get();
            tastNum.incrementAndGet();
            System.out.println(LocalTime.now()+" 执行之后-任务："+r.toString()+",花费时间(纳秒):"+time);
            super.afterExecute(r, t);
        }

        @Override
        protected void terminated() {
            System.out.println("线程关闭，总共执行线程数:"+tastNum.get());
            super.terminated();
        }
    }

}
```

执行结果

```
15:43:23.329 执行之前-任务：java.util.concurrent.FutureTask@469dad33
15:43:23.329 执行之前-任务：java.util.concurrent.FutureTask@1446b68c
15:43:23.329 执行之前-任务：java.util.concurrent.FutureTask@5eefc31e
15:43:23.329 执行之前-任务：java.util.concurrent.FutureTask@33606b2
15:43:23.513 执行之后-任务：java.util.concurrent.FutureTask@33606b2,花费时间(纳秒):216399556
15:43:23.513 执行之前-任务：java.util.concurrent.FutureTask@236e71ad
15:43:23.601 执行之后-任务：java.util.concurrent.FutureTask@1446b68c,花费时间(纳秒):304505594
15:43:23.601 执行之前-任务：java.util.concurrent.FutureTask@107920dc
15:43:23.733 执行之后-任务：java.util.concurrent.FutureTask@5eefc31e,花费时间(纳秒):436283680
15:43:23.733 执行之前-任务：java.util.concurrent.FutureTask@502826b3
15:43:23.808 执行之后-任务：java.util.concurrent.FutureTask@469dad33,花费时间(纳秒):512242583
15:43:23.808 执行之前-任务：java.util.concurrent.FutureTask@96741ab
15:43:23.924 执行之后-任务：java.util.concurrent.FutureTask@107920dc,花费时间(纳秒):322900976
15:43:24.059 执行之后-任务：java.util.concurrent.FutureTask@236e71ad,花费时间(纳秒):546324680
15:43:24.498 执行之后-任务：java.util.concurrent.FutureTask@502826b3,花费时间(纳秒):765309335
15:43:24.594 执行之后-任务：java.util.concurrent.FutureTask@96741ab,花费时间(纳秒):785868205
线程关闭，总共执行线程数:8
```

[代码位置](https://github.com/rainbowda/learnWay/blob/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/Main.java)

## GitHub地址

[地址在这](https://github.com/rainbowda/learnWay/tree/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool)

觉得不错的点个star

## 参考资料

[1] Java 并发编程的艺术 

[2] Java 并发编程实战

