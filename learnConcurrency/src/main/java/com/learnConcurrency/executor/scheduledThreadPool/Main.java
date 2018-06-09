package com.learnConcurrency.executor.scheduledThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newScheduledThreadPool(10);
    }
}
