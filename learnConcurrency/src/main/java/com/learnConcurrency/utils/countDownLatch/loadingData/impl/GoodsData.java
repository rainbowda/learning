package com.learnConcurrency.utils.countDownLatch.loadingData.impl;

import com.learnConcurrency.utils.countDownLatch.loadingData.AbstractDataRunnable;

import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class GoodsData extends AbstractDataRunnable {

    public GoodsData(String name, CountDownLatch count) {
        super(name, count);
    }

    @Override
    public void handle() throws InterruptedException {
        //模拟加载时间，2.5秒
        Thread.sleep(2500);
    }
}
