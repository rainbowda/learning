package com.learnDubbo.demo.provider.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfiguration {

    /**
     * 对应xml配置:<dubbo:application name="demo-provider"/>
     * @return
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-provider--annotation");
        return applicationConfig;
    }

    /**
     * 对应xml配置:<dubbo:registry address="zookeeper://localhost:2181"/>
     * @return
     */
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
