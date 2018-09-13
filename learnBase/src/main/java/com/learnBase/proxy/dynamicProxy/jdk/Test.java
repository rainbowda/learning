package com.learnBase.proxy.dynamicProxy.jdk;

import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        Dao dao = new DaoImpl();

        Dao proxy = (Dao) Proxy.newProxyInstance(Dao.class.getClassLoader(), dao.getClass().getInterfaces(), new TimeInvocationHandler(dao));
        System.out.println(proxy.save("555"));
    }
}
