package com.learnConcurrency.executor.customThreadPool.testRejectedExecutionHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestAbortPolicy {

    public static void main(String[] args){
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

        //提交抛异常
        executor.submit(new Task(4));
    }
}
