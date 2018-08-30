package com.learnConcurrency.utils.cyclicBarrier;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class StartGame implements Runnable {
    private String player;
    private CyclicBarrier barrier;

    public StartGame(String player, CyclicBarrier barrier) {
        this.player = player;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.getPlayer()+" 开始匹配玩家...");
            findOtherPlayer();
            barrier.await();

            System.out.println(this.getPlayer()+" 进行选择角色...");
            choiceRole();
            System.out.println(this.getPlayer()+" 角色选择完毕等待其他玩家...");
            barrier.await();

            System.out.println(this.getPlayer()+" 开始游戏,进行游戏加载...");
            loading();
            System.out.println(this.getPlayer()+" 游戏加载完毕等待其他玩家加载完成...");
            barrier.await();
            //barrier.await(2,TimeUnit.SECONDS);

            start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getPlayer() {
        return player;
    }

    /**
     * 匹配其他玩家
     */
    public void findOtherPlayer() throws InterruptedException {
        Thread.sleep((long) (1000*Math.random()));
    }

    /**
     * 选择角色
     */
    public void choiceRole() throws InterruptedException {
        Thread.sleep((long) (3000*Math.random()));
    }

    /**
     * 加载
     */
    public void loading() throws InterruptedException {
        Thread.sleep((long) (10000*Math.random()));
    }

    /**
     * 开始
     */
    public void start(){
        System.out.println(this.getPlayer()+" 开始");
    }

}
