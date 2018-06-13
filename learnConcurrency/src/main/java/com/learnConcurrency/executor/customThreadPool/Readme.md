1. [自定义线程池](#自定义线程池)
    + 1.1 [有界队列](#有界队列)
    + 1.2 [无界队列](#无界队列)
    + 1.3 [拒绝策略](#拒绝策略)
        + 1.3.1 [AbortPolicy](#abortpolicy)
        + 1.3.2 [CallerRunsPolicy](#callerrunspolicy)
        + 1.3.3 [DiscardPolicy](#discardpolicy)
        + 1.3.4 [DiscardOldestPolicy](#discardoldestpolicy)
        + 1.3.5 [自定义拒绝策略](#自定义拒绝策略)
当然除了以上这些，开可以自定义线程池
## 自定义线程池
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

