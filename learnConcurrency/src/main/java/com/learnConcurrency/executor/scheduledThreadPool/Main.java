package com.learnConcurrency.executor.scheduledThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
        //延迟5秒后调用
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("调用schedule方法");
            }
        },5,TimeUnit.SECONDS);

        //
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("调用scheduleAtFixedRate方法");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5,2,TimeUnit.SECONDS);

        pool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("调用scheduleWithFixedDelay方法");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5,2,TimeUnit.SECONDS);


    }
}
