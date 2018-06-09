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

