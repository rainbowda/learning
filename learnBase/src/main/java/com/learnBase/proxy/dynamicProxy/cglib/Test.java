package com.learnBase.proxy.dynamicProxy.cglib;

public class Test {

    public static void main(String[] args) {
        UserDaoImpl dao = new UserDaoImpl();

        UserDaoImpl proxy = (UserDaoImpl) new ProxyFactory(dao).getProxyInstaance();

        System.out.println(proxy.save("666","15533445566"));
    }
}
