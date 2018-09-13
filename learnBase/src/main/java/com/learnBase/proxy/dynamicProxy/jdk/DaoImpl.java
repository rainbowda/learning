package com.learnBase.proxy.dynamicProxy.jdk;

public class DaoImpl implements Dao {
    @Override
    public Boolean save(String name) {
        try {
            Thread.sleep((long) (3000 * Math.random()));
            System.out.println("保存:"+name);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
