package com.learnDubbo.demo.provider;


import com.learnDubbo.demo.DemoService;

import java.time.LocalDate;

public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        System.out.println("[" + LocalDate.now() + "] Hello " + name );
        return "Hello " + name;
    }

}
