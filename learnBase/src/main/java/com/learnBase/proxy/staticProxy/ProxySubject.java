package com.learnBase.proxy.staticProxy;

public class ProxySubject implements Subject {
    private Subject target;

    public ProxySubject(Subject target) {
        this.target = target;
    }

    @Override
    public void doSomething() {
        long startTime = System.currentTimeMillis();
        target.doSomething();
        long endTime = System.currentTimeMillis();

        System.out.println("耗时"+(endTime - startTime)+"毫秒");
    }
}
