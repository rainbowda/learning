package com.learnConcurrency.executor.cachedThreadPool;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception{
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            int finalI = i + 1;
            pool.submit(() -> {
                try {
                    System.out.println("任务"+ finalI +":开始等待60秒,时间:"+LocalTime.now()+",当前线程名："+Thread.currentThread().getName());
                    Thread.sleep(60000);
                    System.out.println("任务"+ finalI +":结束等待60秒,时间:"+LocalTime.now()+",当前线程名："+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            //睡眠10秒
            Thread.sleep(10000);
        }
        pool.shutdown();
    }
}
