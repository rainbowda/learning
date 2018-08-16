package com.learnDubbo.demo.consumer.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfiguration {

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-consumer-annotation");
        return applicationConfig;
    }

//    @Bean
//    public ConsumerConfig consumerConfig() {
//        ConsumerConfig consumerConfig = new ConsumerConfig();
//        consumerConfig.setTimeout(3000);
//        return consumerConfig;
//    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        //registryConfig.setAddress("zookeeper://127.0.0.1:2181");
        //registryConfig.setAddress("redis://localhost:6379");
        registryConfig.setAddress("multicast://224.5.6.7:1234");
        //registryConfig.setClient("curator");
        return registryConfig;
    }
}
