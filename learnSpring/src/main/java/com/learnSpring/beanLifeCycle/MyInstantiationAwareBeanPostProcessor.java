package com.learnSpring.beanLifeCycle;


import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class MyInstantiationAwareBeanPostProcessor extends
        InstantiationAwareBeanPostProcessorAdapter {

    public MyInstantiationAwareBeanPostProcessor() {
        super();
        System.out.println("2.3. 调用InstantiationAwareBeanPostProcessorAdapter构造器");
    }

    @Override
    public Object postProcessBeforeInstantiation(Class beanClass,String beanName) throws BeansException {
        System.out.println("3. 开始进行实例化bean");
        System.out.println("3.1. 调用InstantiationAwareBeanPostProcessor的postProcessBeforeInstantiation方法");
        return null;
    }


    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("3.3. 调用InstantiationAwareBeanPostProcessor的postProcessAfterInstantiation方法");

        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs,PropertyDescriptor[] pds, Object bean, String beanName)throws BeansException {
        System.out.println("3.4. 调用InstantiationAwareBeanPostProcessor的postProcessPropertyValues方法");
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("3.8. 调用InstantiationAwareBeanPostProcessor调用postProcessBeforeInitialization方法");

        return super.postProcessBeforeInitialization(bean, beanName);
    }

    // 接口方法、实例化Bean之后调用
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)throws BeansException {
        System.out.println("3.11.  InstantiationAwareBeanPostProcessor调用postProcessAfterInitialization方法");
        return bean;
    }


}