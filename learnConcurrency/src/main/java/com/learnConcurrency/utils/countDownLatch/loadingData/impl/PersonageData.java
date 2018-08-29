package com.learnConcurrency.utils.countDownLatch.loadingData.impl;

import com.learnConcurrency.utils.countDownLatch.loadingData.AbstractDataRunnable;

import java.util.concurrent.CountDownLatch;

/**
 * 人物模型
 */
public class PersonageData extends AbstractDataRunnable {
    public PersonageData(String name, CountDownLatch count) {
        super(name, count);
    }

    @Override
    public void handle() throws InterruptedException {
        //模拟加载时间，1秒
        Thread.sleep(1000);
    }
}
