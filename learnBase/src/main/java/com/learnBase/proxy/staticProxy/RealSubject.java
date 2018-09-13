package com.learnBase.proxy.staticProxy;

public class RealSubject implements Subject {
    @Override
    public void doSomething() {
        System.out.println("doSomething...");
        try {
            Thread.sleep((long) (3000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
