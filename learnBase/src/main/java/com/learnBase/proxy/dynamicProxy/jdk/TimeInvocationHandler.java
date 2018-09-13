package com.learnBase.proxy.dynamicProxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TimeInvocationHandler implements InvocationHandler {
    private Object target;

    public TimeInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = method.invoke(target, args);

        long endTime = System.currentTimeMillis();
        System.out.println("使用jdk代理，执行"+method.getName()+"方法,耗时"+(endTime - startTime)+"毫秒");

        return result;
    }
}
