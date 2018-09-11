package com.learnSpring.beanLifeCycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Person implements BeanFactoryAware, BeanNameAware,
        InitializingBean, DisposableBean {

    private String name;
    private String address;
    private int phone;

    private BeanFactory beanFactory;
    private String beanName;

    public Person() {
        System.out.println("3.2. 调用Person的构造器");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("【注入属性】注入属性name");
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        System.out.println("【注入属性】注入属性address");
        this.address = address;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        System.out.println("【注入属性】注入属性phone");
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Person [address=" + address + ", name=" + name + ", phone="
                + phone + "]";
    }

    @Override
    public void setBeanName(String arg0) {
        System.out.println("3.5. 调用BeanNameAware.setBeanName方法");
        this.beanName = arg0;
    }

    @Override
    public void setBeanFactory(BeanFactory arg0) throws BeansException {
        System.out.println("3.6. 调用BeanFactoryAware.setBeanFactory方法");
        this.beanFactory = arg0;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("3.9. 调用InitializingBean的afterPropertiesSet方法");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("调用DiposibleBean的destory方法");
    }

    public void myInit() {
        System.out.println("3.10. 调用<bean>的init-method属性指定的初始化方法");
    }

    public void myDestory() {
        System.out.println("调用<bean>的destroy-method属性指定的初始化方法");
    }
}
