package com.learnDubbo.demo.comsumer;

import com.learnDubbo.demo.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo-consumer.xml");
        context.start();
        DemoService demoService = (DemoService) context.getBean("demoService"); // get remote service proxy
        while (true) {
            try {
                Thread.sleep(1000);
                String hello = demoService.sayHello("Dubbo"); // call remote method
                System.out.println(hello); // get result
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
