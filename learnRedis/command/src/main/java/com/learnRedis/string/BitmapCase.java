package com.learnRedis.string;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.*;

public class BitmapCase extends RedisBaseConnection {

    //存储的key前缀
    private static final String ONLINE_KEY_PREFIX = "online:";
    //天数
    private static final int DAY_NUM = 30;
    //用户数量
    private static final int PEOPLE_NUM = 10000;

    @Test
    public void daliyActive() {
        /**
         *模拟数据
         */
        createData();

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
     * 生成模拟数据
     */
    public void createData() {
        //用来保证线程执行完在进行后面的操作
        CountDownLatch countDownLatch = new CountDownLatch(DAY_NUM);

        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(DAY_NUM-poolSize));
        //DAY_NUM天
        for (int i = 1; i <= DAY_NUM; i++) {
            int finalI = i;
            executor.execute(() -> {
                //假设有PEOPLE_NUM个用户
                for (int j = 1; j <= PEOPLE_NUM; j++) {
                    redisTemplate.opsForValue().setBit(ONLINE_KEY_PREFIX + finalI, j, Math.random() > 0.1);
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
    }

    /**
     * 根据传入的天数统计
     *
     * @param day
     */
    public void calActive(int day) {
        if (day < 0 || day > DAY_NUM){
            throw new IllegalArgumentException("传入的天数不能小于0或者大于30天!");
        }

        long calStart = System.currentTimeMillis();
        BitSet active = new BitSet();
        active.set(0, PEOPLE_NUM);
        for (int i = 1; i <= day; i++) {
            BitSet bitSet = BitSet.valueOf(jedis.get((ONLINE_KEY_PREFIX + i).getBytes()));
            active.and(bitSet);
        }
        long calEnd = System.currentTimeMillis();
        System.out.println(day + "天的上线用户" + active.cardinality() + ",花费时长:" + (calEnd - calStart));
    }

}
