## 前言



## 继承关系

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/ThreadPoolExecutor%E7%B1%BB%E5%9B%BE.png?raw=true)

### Executor接口

```java
public interface Executor {
    void execute(Runnable command);
}
```

Executor接口只有一个方法execute

### ExecutorService接口

```java
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

ExecutorService接口继承Executor接口，并增加了submit、shutdown、invokeAll等等一系列方法。

### AbstractExecutorService抽象类

```java
public abstract class AbstractExecutorService implements ExecutorService {

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                              boolean timed, long nanos)
        throws InterruptedException, ExecutionException, TimeoutException {...}

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {... }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                           long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {...}

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {...}

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                         long timeout, TimeUnit unit)
        throws InterruptedException {...}

}
```

AbstractExecutorService抽象类实现ExecutorService接口，并且提供了一些方法的默认实现，例如submit方法、invokeAny方法、invokeAll方法。

像execute方法、线程池的关闭方法（shutdown、shutdownNow等等）就没有提供默认的实现。

## ThreadPoolExecutor

### 线程池状态

- RUNNING：接受新任务并且处理阻塞队列里的任务
- SHUTDOWN：拒绝新任务但是处理阻塞队列里的任务
- STOP：拒绝新任务并且抛弃阻塞队列里的任务同时会中断正在处理的任务
- TIDYING：所有任务都执行完（包含阻塞队列里面任务）当前线程池活动线程为0，将要调用terminated方法
- TERMINATED：终止状态。terminated方法调用完成以后的状态

### 构造函数

有四个构造函数，其他三个都是调用下面代码中的这个构造函数

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
}
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



### 提交任务

#### submit

```java
public Future<?> submit(Runnable task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<Void> ftask = newTaskFor(task, null);
    execute(ftask);
    return ftask;
}

public <T> Future<T> submit(Runnable task, T result) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<T> ftask = newTaskFor(task, result);
    execute(ftask);
    return ftask;
}

public <T> Future<T> submit(Callable<T> task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<T> ftask = newTaskFor(task);
    execute(ftask);
    return ftask;
}
```

#### execute

```java
public void execute(Runnable command) {
   //传进来的线程为null，则抛出空指针异常
   if (command == null)
       throw new NullPointerException();
  
   //获取当前线程池的状态+线程个数变量
   int c = ctl.get();
   /**
    * 3个步骤
    */
   //1.判断当前线程池线程个数是否小于corePoolSize,小于则调用addWorker方法创建新线程运行,且传进来的Runnable当做第一个任务执行。
   //如果调用addWorker方法返回false，则直接返回
   if (workerCountOf(c) < corePoolSize) {
       if (addWorker(command, true))
           return;
       c = ctl.get();
   }

   //2.如果线程池处于RUNNING状态，则添加任务到阻塞队列
   if (isRunning(c) && workQueue.offer(command)) {

       //二次检查
       int recheck = ctl.get();
       //如果当前线程池状态不是RUNNING则从队列删除任务，并执行拒绝策略
       if (! isRunning(recheck) && remove(command))
           reject(command);

       //否者如果当前线程池线程空，则添加一个线程
       else if (workerCountOf(recheck) == 0)
           addWorker(null, false);
   }
   //3.新增线程，新增失败则执行拒绝策略
   else if (!addWorker(command, false))
       reject(command);
}
```



#####　addWorker

```java
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            int wc = workerCountOf(c);
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            if (compareAndIncrementWorkerCount(c))
                break retry;
            c = ctl.get();  // Re-read ctl
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        w = new Worker(firstTask);
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // Recheck while holding lock.
                // Back out on ThreadFactory failure or if
                // shut down before lock acquired.
                int rs = runStateOf(ctl.get());

                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive()) // precheck that t is startable
                        throw new IllegalThreadStateException();
                    workers.add(w);
                    int s = workers.size();
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)
            addWorkerFailed(w);
    }
    return workerStarted;
}
```



### 关闭线程池

#### shutdown

当调用shutdown方法时，线程池将不会再接收新的任务，然后将先前放在队列中的任务执行完成。

下面是shutdown方法的源码

```java
public void shutdown() {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        interruptIdleWorkers();
        onShutdown(); // hook for ScheduledThreadPoolExecutor
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
}
```

#### shutdownNow

 立即停止所有的执行任务，并将队列中的任务返回

```java
public List<Runnable> shutdownNow() {
    List<Runnable> tasks;
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(STOP);
        interruptWorkers();
        tasks = drainQueue();
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
    return tasks;
}
```





### 扩展方法

#### beforeExecute

线程执行前

beforeExecute方法是在runWorker方法中调用的

#### afterExecute

线程执行后

beforeExecute方法是在runWorker方法中调用的

#### terminated

线程池关闭

terminated方法是在tryTerminate方法中调用的