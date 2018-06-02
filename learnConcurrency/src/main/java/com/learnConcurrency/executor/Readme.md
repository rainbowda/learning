这里讲介绍Executor框架的使用  

[TOC]

1. Executor接口介绍
2. ExecutorService接口介绍
3. 创建线程池的一些方法介绍
4. 自定义线程池
   
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
## 创建线程池的一些方法介绍   
为什么要讲ExecutorService接口呢？是因为我们使用Executors的方法时返回的都是ExecutorService。
Executors提供了几个创建线程池方法,接下来我就介绍一下这些方法
```
newFixedThreadPool()

newWorkStealingPool()

newSingleThreadExecutor()

newCachedThreadPool() 

newScheduledThreadPool()

newSingleThreadScheduledExecutor()
```

### newFixedThreadPool()   
创建一个线程的线程池，若空闲则执行，若没有空闲线程则暂缓在任务队列中。

### newWorkStealingPool()  
创建持有足够线程的线程池来支持给定的并行级别，并通过使用多个队列，减少竞争，它需要穿一个并行级别的参数，如果不传，则被设定为默认的CPU数量。

### newSingleThreadExecutor()  
该方法返回一个固定数量的线程池  
该方法的线程始终不变，当有一个任务提交时，若线程池空闲，则立即执行，若没有，则会被暂缓在一个任务队列只能怪等待有空闲的线程去执行。

### newCachedThreadPool()  
返回一个可根据实际情况调整线程个数的线程池，不限制最大线程数量，若有空闲的线程则执行任务，若无任务则不创建线程，并且每一个空闲线程会在60秒后自动回收。

### newScheduledThreadPool()  
返回一个SchededExecutorService对象，但该线程池可以设置线程的数量，支持定时及周期性任务执行。
  
### newSingleThreadScheduledExecutor()
创建一个单例线程池，定期或延时执行任务。  
  
当然除了以上这些，开可以自定义线程池
## 自定义线程池


### 有界队列  

### 无界队列  

### 拒绝策略  
拒绝策略  
#### AbortPolicy   
直接抛出异常信息，系统正常工作  
  
#### CallerRunsPolicy  
只要线程池未关闭，该策略直接在调用者线程中，云枭当前被丢弃的任务。  
  
#### DiscardPolicy  
丢弃无法处理的任务，不给予任何处理。  
  
#### DiscardOldestPolicy  
丢弃最老的一个请求，尝试再次提交当前任务。  
  
#### 自定义拒绝策略   
实现RejectedExecutionHandle接口  

