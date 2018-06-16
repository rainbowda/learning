package com.learnConcurrency.executor.customThreadPool.testRejectedExecutionHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
