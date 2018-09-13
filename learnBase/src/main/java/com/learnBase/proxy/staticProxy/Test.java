package com.learnBase.proxy.staticProxy;

public class Test {

    public static void main(String[] args) {
        Subject subject = new ProxySubject(new RealSubject());

        subject.doSomething();
    }
}
