package com.learnConcurrency.utils.exchanger;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Case {

    private static final Exchanger exchanger = new Exchanger();

    private static ExecutorService service = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        service.submit(new ExchangerRunnable("1", exchanger, "A"));
        service.submit(new ExchangerRunnable("2", exchanger, "B"));

        service.shutdown();

    }


    static class ExchangerRunnable implements Runnable {
        private Object data;
        private String name;
        private Exchanger exchanger;

        public ExchangerRunnable(String name, Exchanger exchanger, Object data) {
            this.exchanger = exchanger;
            this.name = name;
            this.data = data;
        }

        public void run() {
            try {
                Object previous = this.data;

                this.data = this.exchanger.exchange(previous);

                System.out.println("名称:" + name + " 之前数据：" + previous + " ,交换之后数据：" + this.data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
