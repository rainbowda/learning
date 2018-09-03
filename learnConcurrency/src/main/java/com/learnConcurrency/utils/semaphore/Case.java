package com.learnConcurrency.utils.semaphore;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.*;

public class Case {

    public static void main(String[] args) throws IOException {
        Semaphore semaphore = new Semaphore(5);
        //Semaphore semaphore = new Semaphore(5,true);

        ExecutorService service = Executors.newCachedThreadPool();
        //模拟100个玩家排队
        for (int i = 0; i < 100; i++) {
            service.submit(new Player("玩家"+i,semaphore));
        }
        //关闭线程池
        service.shutdown();

        //判断线程池是否中断，没有则循环查看当前排队总人数
        while (!service.isTerminated()){
            System.out.println("当前排队总人数:"+semaphore.getQueueLength());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class Player implements Runnable{


        private String playerName;
        private Semaphore semaphore;

        public Player(String playerName, Semaphore semaphore) {
            this.playerName = playerName;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();

                System.out.println(playerName+"进入，时间:"+LocalTime.now());
                Thread.sleep((long) (3000 * Math.random()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(playerName+"退出");
                semaphore.release();
            }
        }
    }
}
