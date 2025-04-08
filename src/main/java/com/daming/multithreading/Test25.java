package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test25 {

    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    static ReentrantLock ROOM = new ReentrantLock();
    static Condition waitTakeoutSet = ROOM.newCondition();
    static Condition waitCigaretteSet = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {
        // 创建新的条件变量(类似休息室)
        new Thread(() -> {
            ROOM.lock();
            try {
                log.info("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.info("没烟，先歇会！");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("拿到烟了,抽完烟了，可以开始干活了");
            } finally {
                ROOM.unlock();
            }
        }, "小南").start();


        new Thread(() -> {
            ROOM.lock();
            try {
                log.info("外卖没到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.info("没外卖，先歇会！");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("拿到外卖了，吃完外卖，可以开始干活了");
            } finally {
                ROOM.unlock();
            }
        }, "小女").start();

        Thread.sleep(1000);

        new Thread(() -> {
            ROOM.lock();
            try{
                log.info("外卖到了噢！");
                hasTakeout = true;
                waitTakeoutSet.signal();
            }finally {
                ROOM.unlock();
            }
        }, "送外卖的").start();

        Thread.sleep(1000);
        new Thread(() -> {
            ROOM.lock();
            try{
                log.info("烟到了噢！");
                hasCigarette = true;
                waitCigaretteSet.signal();
            }finally {
                ROOM.unlock();
            }
        }, "送烟的").start();


    }

}

