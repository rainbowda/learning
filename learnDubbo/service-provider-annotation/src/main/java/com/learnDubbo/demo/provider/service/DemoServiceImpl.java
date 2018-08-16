package com.learnDubbo.demo.provider.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.learnDubbo.demo.DemoService;

import java.time.LocalDate;

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        System.out.println("[" + LocalDate.now() + "] Hello " + name  + "by annotation");
        return "Hello " + name + " by annotation";
    }

}
