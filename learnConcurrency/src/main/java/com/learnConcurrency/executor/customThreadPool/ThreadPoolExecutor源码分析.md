## 前言

这篇主要讲述ThreadPoolExecutor的源码分析，贯穿类的创建、任务的添加到线程池的关闭整个流程，让你知其然所以然。希望你可以通过本篇博文知道ThreadPoolExecutor是怎么添加任务、执行任务的，以及延伸的知识点。那么先来看看ThreadPoolExecutor的继承关系吧。

## 继承关系

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/ThreadPoolExecutor%E7%B1%BB%E5%9B%BE.png?raw=true)

### Executor接口

```java
public interface Executor {
    void execute(Runnable command);
}
```

Executor接口只有一个方法execute,传入线程任务参数

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

先介绍下ThreadPoolExecutor线程池的状态吧

### 线程池状态

int 是4个字节，也就是32位（`注：一个字节等于8位`）

```java
//记录线程池状态和线程数量（总共32位，前三位表示线程池状态，后29位表示线程数量）
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
//线程数量统计位数29  Integer.SIZE=32 
private static final int COUNT_BITS = Integer.SIZE - 3;
//容量 000 11111111111111111111111111111
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

//运行中 111 00000000000000000000000000000
private static final int RUNNING    = -1 << COUNT_BITS;
//关闭 000 00000000000000000000000000000
private static final int SHUTDOWN   =  0 << COUNT_BITS;
//停止 001 00000000000000000000000000000
private static final int STOP       =  1 << COUNT_BITS;
//整理 010 00000000000000000000000000000
private static final int TIDYING    =  2 << COUNT_BITS;
//终止 011 00000000000000000000000000000
private static final int TERMINATED =  3 << COUNT_BITS;

//获取运行状态（获取前3位）
private static int runStateOf(int c)     { return c & ~CAPACITY; }
//获取线程个数（获取后29位）
private static int workerCountOf(int c)  { return c & CAPACITY; }
private static int ctlOf(int rs, int wc) { return rs | wc; }
```

- RUNNING：接受新任务并且处理阻塞队列里的任务
- SHUTDOWN：拒绝新任务但是处理阻塞队列里的任务
- STOP：拒绝新任务并且抛弃阻塞队列里的任务同时会中断正在处理的任务
- TIDYING：所有任务都执行完（包含阻塞队列里面任务）当前线程池活动线程为0，将要调用terminated方法
- TERMINATED：终止状态。terminated方法调用完成以后的状态

线程池状态转换

```
RUNNING -> SHUTDOWN
   显式调用shutdown()方法, 或者隐式调用了finalize()方法
(RUNNING or SHUTDOWN) -> STOP
   显式调用shutdownNow()方法
SHUTDOWN -> TIDYING
   当线程池和任务队列都为空的时候
STOP -> TIDYING
   当线程池为空的时候
TIDYING -> TERMINATED
   当 terminated() hook 方法执行完成时候
```

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

流程步骤如下

1. 调用submit方法，传入Runnable或者Callable对象
2. 判断传入的对象是否为null，为null则抛出异常，不为null继续流程
3. 将传入的对象转换为RunnableFuture对象
4. 执行execute方法，传入RunnableFuture对象
5. 返回RunnableFuture对象

流程图如下

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/sibmit%E6%96%B9%E6%B3%95%E6%B5%81%E7%A8%8B%E5%9B%BE.png?raw=true)



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
其实从上面代码注释中可以看出就三个判断，

1. 核心线程数是否已满
2. 队列是否已满
3. 线程池是否已满

然后根据这三个条件进行不同的操作，下图是Java并发编程的艺术书中的线程池的主要处理流程，或许会比较容易理解些

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/%E7%BA%BF%E7%A8%8B%E6%B1%A0%E7%9A%84%E4%B8%BB%E8%A6%81%E5%A4%84%E7%90%86%E6%B5%81%E7%A8%8B.png?raw=true)

下面是整个流程的详细步骤

1. 调用execute方法，传入Runable对象
2. 判断传入的对象是否为null，为null则抛出异常，不为null继续流程
3. 获取当前线程池的状态和线程个数变量
4. 判断当前线程数是否小于核心线程数，是走流程5，否则走流程6
5. 添加线程数，添加成功则结束，失败则重新获取当前线程池的状态和线程个数变量,
6. 判断线程池是否处于RUNNING状态，是则添加任务到阻塞队列，否则走流程10，添加任务成功则继续流程7
7. 重新获取当前线程池的状态和线程个数变量
8. 重新检查线程池状态，不是运行状态则移除之前添加的任务，有一个false走流程9，都为true则走流程11
9. 检查线程池线程数量是否为0，否则结束流程，是调用addWorker(null, false)，然后结束
10. 调用!addWorker(command, false)，为true走流程11，false则结束
11. 调用拒绝策略reject(command)，结束

可能看上面会有点绕，不清楚的可以看下面的流程图

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/execute%E6%96%B9%E6%B3%95%E6%B5%81%E7%A8%8B%E5%9B%BE.png?raw=true)


#####　addWorker

```java
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // 检查当前线程池状态是否是SHUTDOWN、STOP、TIDYING或者TERMINATED
        // 且！（当前状态为SHUTDOWN、且传入的任务为null，且队列不为null）
        // 条件都成立则返回false
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;
		//循环
        for (;;) {
            int wc = workerCountOf(c);
            //如果当前的线程数量超过最大容量或者大于（根据传入的core决定是核心线程数还是最大线程数）核心线程数 || 最大线程数，则返回false
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            //CAS增加c，成功则跳出retry
            if (compareAndIncrementWorkerCount(c))
                break retry;
            //CAS失败执行下面方法，查看当前线程数是否变化，变化则继续retry循环，没变化则继续内部循环
            c = ctl.get();  // Re-read ctl
            if (runStateOf(c) != rs)
                continue retry;
        }
    }
	//CAS成功
    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        //新建一个线程
        w = new Worker(firstTask);
        final Thread t = w.thread;
        if (t != null) {
            //加锁
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                
                //重新检查线程池状态
                //避免ThreadFactory退出故障或者在锁获取前线程池被关闭
                int rs = runStateOf(ctl.get());

                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive()) // 先检查线程是否是可启动的
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
            //判断worker是否添加成功，成功则启动线程，然后将workerStarted设置为true
            if (workerAdded) {
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        //判断线程有没有启动成功，没有则调用addWorkerFailed方法
        if (! workerStarted)
            addWorkerFailed(w);
    }
    return workerStarted;
}
```

这里可以将addWorker分为两部分，第一部分增加线程池个数，第二部分是将任务添加到workder里面并执行。

第一部分主要是两个循环，外层循环主要是判断线程池状态，下面描述来自[Java中线程池ThreadPoolExecutor原理探究 ](http://ifeve.com/java%E4%B8%AD%E7%BA%BF%E7%A8%8B%E6%B1%A0threadpoolexecutor%E5%8E%9F%E7%90%86%E6%8E%A2%E7%A9%B6/)

> ```
> rs >= SHUTDOWN &&
>               ! (rs == SHUTDOWN &&
>                   firstTask == null &&
>                   ! workQueue.isEmpty())
> ```
>
> 展开！运算后等价于
>
> ```
> s >= SHUTDOWN &&
>                (rs != SHUTDOWN ||
>              firstTask != null ||
>              workQueue.isEmpty())
> ```
>
> 也就是说下面几种情况下会返回false：
>
> - 当前线程池状态为STOP，TIDYING，TERMINATED
> - 当前线程池状态为SHUTDOWN并且已经有了第一个任务
> - 当前线程池状态为SHUTDOWN并且任务队列为空
>
> 内层循环作用是使用cas增加线程个数，如果线程个数超限则返回false，否者进行cas，cas成功则退出双循环，否者cas失败了，要看当前线程池的状态是否变化了，如果变了，则重新进入外层循环重新获取线程池状态，否者进入内层循环继续进行cas尝试。
>
> 到了第二部分说明CAS成功了，也就是说线程个数加一了，但是现在任务还没开始执行，这里使用全局的独占锁来控制workers里面添加任务，其实也可以使用并发安全的set，但是性能没有独占锁好（这个从注释中知道的）。这里需要注意的是要在获取锁后重新检查线程池的状态，这是因为其他线程可可能在本方法获取锁前改变了线程池的状态，比如调用了shutdown方法。添加成功则启动任务执行。 

所以这里也将流程图分为两部分来描述

第一部分流程图

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/addWorkers%E7%AC%AC%E4%B8%80%E9%83%A8%E5%88%86.png?raw=true)





第二部分流程图

![](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/img/addWorkers%E7%AC%AC%E4%BA%8C%E9%83%A8%E5%88%86.png?raw=true)

#### Worker对象

Worker是定义在ThreadPoolExecutor中的finnal类，其中继承了AbstractQueuedSynchronizer类和实现Runnable接口，其中的run方法如下

```
public void run() {
    runWorker(this);
}
```

线程启动时调用了runWorker方法，关于类的其他方面这里就不在叙述。

#### runWorker

```java
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock();
    boolean completedAbruptly = true;
    try {
        //循环获取任务
        while (task != null || (task = getTask()) != null) {
            w.lock();
            // 当线程池是处于STOP状态或者TIDYING、TERMINATED状态时，设置当前线程处于中断状态
            // 如果不是，当前线程就处于RUNNING或者SHUTDOWN状态，确保当前线程不处于中断状态
            // 重新检查当前线程池的状态是否大于等于STOP状态
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            try {
                //提供给继承类使用做一些统计之类的事情，在线程运行前调用
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    //提供给继承类使用做一些统计之类的事情，在线程运行之后调用
                    afterExecute(task, thrown);
                }
            } finally {
                task = null;
                //统计当前worker完成了多少个任务
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        //整个线程结束时调用，线程退出操作。统计整个线程池完成的任务个数之类的工作
        processWorkerExit(w, completedAbruptly);
    }
}
```



#### getTask

getTask方法的主要作用其实从方法名就可以看出来了，就是获取任务

```java
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?
	//循环
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //线程线程池状态和队列是否为空
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }
		//线程数量
        int wc = workerCountOf(c);

        
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

        //（当前线程数是否大于最大线程数或者）
        //且（线程数大于1或者任务队列为空）
        //这里有个问题(timed && timedOut)timedOut = false，好像(timed && timedOut)一直都是false吧
        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }

        try {
            //获取任务
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            timedOut = true;
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
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

#### shutdown和shutdownNow区别

shutdown和shutdownNow这两个方法的作用都是关闭线程池，流程大致相同，只有几个步骤不同，如下

1. 加锁
2. 检查关闭权限
3. CAS改变线程池状态
4. **设置中断标志(线程池不在接收任务，队列任务会完成)/中断当前执行的线程**
5. **调用onShutdown方法（给子类提供的方法）/获取队列中的任务**
6. 解锁
7. 尝试将线程池状态变成终止状态TERMINATED
8. **结束/返回队列中的任务**



## 总结

线程池可以给我们多线程编码上提供极大便利，就好像数据库连接池一样，减少了线程的开销，提供了线程的复用。而且ThreadPoolExecutor也提供了一些未实现的方法，供我们来使用，像beforeExecute、afterExecute等方法，我们可以通过这些方法来对线程进行进一步的管理和统计。

在使用线程池上好需要注意，提交的线程任务可以分为`CPU 密集型任务`和` IO 密集型任务`，然后根据任务的不同进行分配不同的线程数量。

- CPU密集型任务：
  - 应当分配较少的线程，比如 `CPU`个数相当的大小
- IO 密集型任务：
    - 由于线程并不是一直在运行，所以可以尽可能的多配置线程，比如 CPU 个数 * 2
- 混合型任务：
  - 可以将其拆分为 `CPU` 密集型任务以及 `IO` 密集型任务，这样来分别配置。

好了，这篇博文到这里就结束了，文中可能会有些纰漏，欢迎留言指正。

如果本文对你有所帮助，给个star呗，谢谢。本文GitHub地址：[点这里点这里](https://github.com/rainbowda/learnWay/blob/master/learnConcurrency/src/main/java/com/learnConcurrency/executor/customThreadPool/ThreadPoolExecutor%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90.md)

## 参考资料

1. [并发编程网-Java中线程池ThreadPoolExecutor原理探究](http://ifeve.com/java%E4%B8%AD%E7%BA%BF%E7%A8%8B%E6%B1%A0threadpoolexecutor%E5%8E%9F%E7%90%86%E6%8E%A2%E7%A9%B6/) 
2. Java并发编程的艺术