package com.learnConcurrency.executor.customThreadPool.testRejectedExecutionHandler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class MyRejected implements RejectedExecutionHandler{

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		System.out.println("自定义处理：开始记录日志");
		System.out.println(r.toString());
		System.out.println("自定义处理：记录日志完成");
	}

}
