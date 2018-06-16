package com.learnConcurrency.executor.customThreadPool;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new MyThreadPoolExecutor(
                2, 				//coreSize
                4, 				//MaxSize
                60, 			//60
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));

        for (int i = 0; i < 8; i++) {
            int finalI = i + 1;
            pool.submit(() -> {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();
    }

    static class MyThreadPoolExecutor extends ThreadPoolExecutor{
        private final AtomicInteger tastNum = new AtomicInteger();
        private final ThreadLocal<Long> startTime = new ThreadLocal<>();

        public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            startTime.set(System.nanoTime());
            System.out.println(LocalTime.now()+" 执行之前-任务："+r.toString());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            long endTime = System.nanoTime();
            long time = endTime - startTime.get();
            tastNum.incrementAndGet();
            System.out.println(LocalTime.now()+" 执行之后-任务："+r.toString()+",花费时间(纳秒):"+time);
            super.afterExecute(r, t);
        }

        @Override
        protected void terminated() {
            System.out.println("线程关闭，总共执行线程数:"+tastNum.get());
            super.terminated();
        }
    }

}
