package com.learnBase.proxy.dynamicProxy.cglib;

public class UserDaoImpl {

    public Boolean save(String name,String phone){
        try {
            Thread.sleep((long) (3000 * Math.random()));
            System.out.println("保存姓名："+name+"，号码:"+phone);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
