package com.learnSpring.beanLifeCycle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {

        System.out.println("1. 现在开始初始化容器");

        ApplicationContext factory = new ClassPathXmlApplicationContext("beanLifeCycle.xml");
        System.out.println("容器初始化成功");
        //得到Preson，并使用
        Person person = factory.getBean("person",Person.class);
        System.out.println(person);

        System.out.println("现在开始关闭容器！");
        ((ClassPathXmlApplicationContext)factory).registerShutdownHook();
    }
}
