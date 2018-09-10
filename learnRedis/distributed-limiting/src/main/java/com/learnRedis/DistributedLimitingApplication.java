package com.learnRedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedLimitingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedLimitingApplication.class, args);
    }
}
