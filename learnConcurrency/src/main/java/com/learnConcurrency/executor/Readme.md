## 目录  

1. [Executor接口介绍](#executor接口介绍)
2. [ExecutorService接口介绍](#executorservice接口介绍)
3. [创建线程池的一些方法介绍](#创建线程池的一些方法介绍)
    + 3. 1 [newFixedThreadPool方法 ](#newfixedthreadpool方法)
    + 3. 2 [newCachedThreadPool方法 ](#newcachedthreadpool方法)
    + 3. 3 [newScheduledThreadPool方法 ](#newscheduledthreadpool方法)
    + 3. 4 [newWorkStealingPool方法 ](#newworkstealingpool方法)


## Executor接口介绍 
Executor是一个接口，里面提供了一个execute方法，该方法接收一个Runable参数，如下
```Java
public interface Executor {
    void execute(Runnable command);
}
```
## ExecutorService接口介绍
接下来我们来看看继承了Executor接口的ExecutorService  
```Java
public interface ExecutorService extends Executor {
    void shutdown();

    List<Runnable> shutdownNow();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Runnable task, T result);

    Future<?> submit(Runnable task);

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```
------

## ThreadPoolExecutor构造函数介绍

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





## 创建线程池的一些方法介绍   

为什么要讲ExecutorService接口呢？是因为我们使用Executors的方法时返回的都是ExecutorService。
Executors提供了几个创建线程池方法,接下来我就介绍一下这些方法
```
newFixedThreadPool()
创建一个线程的线程池，若空闲则执行，若没有空闲线程则暂缓在任务队列中。

newWorkStealingPool()
创建持有足够线程的线程池来支持给定的并行级别，并通过使用多个队列，减少竞争，它需要穿一个并行级别的参数，如果不传，则被设定为默认的CPU数量。

newSingleThreadExecutor()
该方法返回一个固定数量的线程池  
该方法的线程始终不变，当有一个任务提交时，若线程池空闲，则立即执行，若没有，则会被暂缓在一个任务队列只能怪等待有空闲的线程去执行。

newCachedThreadPool() 
返回一个可根据实际情况调整线程个数的线程池，不限制最大线程数量，若有空闲的线程则执行任务，若无任务则不创建线程，并且每一个空闲线程会在60秒后自动回收。

newScheduledThreadPool()
返回一个SchededExecutorService对象，但该线程池可以设置线程的数量，支持定时及周期性任务执行。
 
newSingleThreadScheduledExecutor()
创建一个单例线程池，定期或延时执行任务。  
 
```



### newFixedThreadPool方法  

代码如下

```java
public class Main {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
```

接下来看看Executors工厂内部是如何实现的

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
```

可以看到返回的是一个ThreadPoolExecutor对象，核心线程数和是最大线程数都是传入的参数，存活时间是0，时间单位是毫秒，阻塞队列LinkedBlockingQueue。



### newCachedThreadPool方法  

```java
public class Main {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
    }
}
```

接下来看看Executors工厂内部是如何实现的

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```

newCachedThreadPool方法也是返回ThreadPoolExecutor对象，核心线程是0，最大线程数是Integer的最MAX_VALUE，存活时间是60，时间单位是秒，SynchronousQueue队列。

### newScheduledThreadPool方法  

```java
public class Main {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newScheduledThreadPool(10);
    }
}
```

接下来看看Executors工厂内部是如何实现的

```java
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize);
}
```

这里返回的是ScheduledThreadPoolExecutor对象，我们继续深入进去看看

```java
public ScheduledThreadPoolExecutor(int corePoolSize) {
    super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
          new DelayedWorkQueue());
}
```

这里调用的是父类的构造函数，ScheduledThreadPoolExecutor的父类是ThreadPoolExecutor，所以返回的也是ThreadPoolExecutor对象。核心线程数是传入的参数corePoolSize，线程最大值是Integer的MAX_VALUE，存活时间时0，时间单位是纳秒，队列是DelayedWorkQueue。



### newWorkStealingPool方法

```java
public class Main {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newWorkStealingPool();
    }
}
```

接下来看看Executors工厂内部是如何实现的

```java
public static ExecutorService newWorkStealingPool() {
    return new ForkJoinPool
        (Runtime.getRuntime().availableProcessors(),
         ForkJoinPool.defaultForkJoinWorkerThreadFactory,
         null, true);
}
```

这里返回的是ForkJoinPool对象。