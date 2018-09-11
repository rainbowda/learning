package com.learnSpring.beanLifeCycle;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    public MyBeanPostProcessor() {
        super();
        System.out.println("2.2. 调用BeanPostProcessor构造器");
    }


    @Override
    public Object postProcessBeforeInitialization(Object arg0, String arg1)throws BeansException {
        System.out.println("3.7. 调用BeanPostProcessor的postProcessBeforeInitialization方法");
        return arg0;
    }

    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1)throws BeansException {
        System.out.println("3.10.  BeanPostProcessor接口方法postProcessAfterInitialization对属性进行更改！");
        return arg0;
    }
}