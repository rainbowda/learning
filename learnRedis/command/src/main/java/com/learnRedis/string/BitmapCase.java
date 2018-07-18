package com.learnRedis.string;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.*;

public class BitmapCase extends RedisBaseConnection {

    //天数
    private static final int DAY_NUM = 30;
    //用户数量
    private static final int PEOPLE_NUM = 10000;

    private CountDownLatch countDownLatch = new CountDownLatch(DAY_NUM);

    @Test
    public void daliyActive() {
        /**
         *模拟数据
         */

        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(30));
        //DAY_NUM天
        for (int i = 1; i <= DAY_NUM; i++) {
            int finalI = i;
            executor.execute(() -> {
                //假设有PEOPLE_NUM个用户
                for (int j = 0; j < PEOPLE_NUM; j++) {
                    redisTemplate.opsForValue().setBit("active:" + finalI, j, Math.random() > 0.1);
                }
                countDownLatch.countDown();
            });
        }

        //等待线程全部执行完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * 开始统计
         */
        //1
        calActive(1);

        //7
        calActive(7);

        //15
        calActive(15);

        //30
        calActive(30);

    }

    /**
     * 根据传入的天数统计
     * @param day
     */
    public void calActive(int day) {
        long calStart = System.currentTimeMillis();
        BitSet active = new BitSet();
        active.set(0, PEOPLE_NUM);
        for (int i = 1; i <= day; i++) {
            BitSet bitSet = BitSet.valueOf(jedis.get(("active:" + i).getBytes()));
            active.and(bitSet);
        }
        long calEnd = System.currentTimeMillis();
        System.out.println(day + "天的上线用户" + active.cardinality() + ",花费时长:" + (calEnd - calStart));
    }

}
