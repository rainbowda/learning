## 自定义线程池
开篇一张图（图片来自[阿里巴巴Java开发手册（详尽版）](https://github.com/alibaba/p3c/blob/master/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%EF%BC%88%E8%AF%A6%E5%B0%BD%E7%89%88%EF%BC%89.pdf)），后面全靠编

<a href="https://github.com/alibaba/p3c/blob/master/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%EF%BC%88%E8%AF%A6%E5%B0%BD%E7%89%88%EF%BC%89.pdf" target="_blank ">阿里巴巴Java开发手册（详尽版） </a>

![](https://github.com/rainbowda/learnWay/blob/customThreadPool/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%E5%88%9B%E5%BB%BA%E7%BA%BF%E7%A8%8B%E6%B1%A0%E8%A7%84%E7%BA%A6.png?raw=true)

好了，从图片中就可以看到这篇博文的主题了，ThreadPoolExecutor自定义线程池，

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

### ThreadPoolExecutor构造函数介绍

在介绍穿件线程池的方法之前要先介绍一个类ThreadPoolExecutor，应为Executors工厂大部分方法都是返回ThreadPoolExecutor对象，先来看看它的构造函数吧

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

#### 自定义拒绝策略   
实现RejectedExecutionHandle接口  

