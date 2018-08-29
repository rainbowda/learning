package com.learnConcurrency.utils.countDownLatch.loadingData.impl;

import com.learnConcurrency.utils.countDownLatch.loadingData.AbstractDataRunnable;

import java.util.concurrent.CountDownLatch;

/**
 * 背景
 */
public class BackGroundData extends AbstractDataRunnable {

    public BackGroundData(String name, CountDownLatch count) {
        super(name, count);
    }

    @Override
    public void handle() throws InterruptedException {
        //模拟加载时间，2秒
        Thread.sleep(2000);
    }
}
