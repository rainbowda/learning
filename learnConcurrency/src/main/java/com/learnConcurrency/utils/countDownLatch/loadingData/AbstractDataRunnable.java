package com.learnConcurrency.utils.countDownLatch.loadingData;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractDataRunnable implements Runnable {
    private String name;
    private CountDownLatch count;

    public AbstractDataRunnable(String name, CountDownLatch count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.getName()+" 开始加载...");
            Long l1 = System.currentTimeMillis();
            handle();
            Long l2 = System.currentTimeMillis();
            System.out.println(this.getName()+" 加载完成,花费时间:"+(l2-l1));
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            count.countDown();
        }
        afterCountDown();
    }

    public String getName() {
        return name;
    }

    public abstract void handle() throws InterruptedException;

    public void afterCountDown(){
        System.out.println(this.getName() + ":CountDownLatch计数减一之后,继续加载其他数据...");
    };
}
