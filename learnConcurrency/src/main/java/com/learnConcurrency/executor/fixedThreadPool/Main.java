package com.learnConcurrency.executor.fixedThreadPool;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args){
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 8; i++) {
            int finalI = i + 1;
            pool.submit(() -> {
                try {
                    System.out.println("任务"+ finalI +":开始等待2秒,时间:"+LocalTime.now()+",当前线程名："+Thread.currentThread().getName());
                    Thread.sleep(2000);
                    System.out.println("任务"+ finalI +":结束等待2秒,时间:"+LocalTime.now()+",当前线程名："+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
        pool.shutdown();
    }
}
