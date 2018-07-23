package com.learnRedis.list;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

public class JosephusProblem extends RedisBaseConnection {

    @Test
    public void test() {
        //构造数据
        for (int i = 1; i <= 41; i++) {
            listOperations.leftPush("josephus", String.valueOf(i));
        }

        int index = 1;
        while (listOperations.size("josephus") > 0) {
            //当数到3时，弹出
            if (index == 3) {
                System.out.println(listOperations.range("josephus", 0, -1));
                System.out.println("当前被杀的人是：" + listOperations.rightPop("josephus"));
                index = 0;
            } else {
                listOperations.rightPopAndLeftPush("josephus", "josephus");
            }
            index++;
        }
    }
}
