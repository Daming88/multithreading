package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义使用cas实现锁
 */
@Slf4j
public class Test30 {

    private static int count = 0;

//    public static void main(String[] args) throws InterruptedException {
//        LockCas lockCas = new LockCas();
//        for (int i = 0; i < 100; i++) {
//            new Thread(() -> {
//                lockCas.lock();
//                try{
//                    count++;
//                }finally {
//                    lockCas.unlock();
//                }
//            }, "t+" + i).start();
//        }
//
//        for (int i = 0; i < 100; i++) {
//            new Thread(() -> {
//                lockCas.lock();
//                try{
//                    count--;
//                }finally {
//                    lockCas.unlock();
//                }
//            },"t-"+i).start();
//        }
//
//        Thread.sleep(10000);
//        System.out.println("count=" + count);
//
//    }


    public static void main(String[] args) {
        LockCas lockCas = new LockCas();

        new Thread(() -> {
            log.info("begin");
            lockCas.lock();
            try{
                log.info("lock");
                Thread.sleep(1000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lockCas.unlock();
            }
        },"t1").start();

        new Thread(() -> {
            log.info("begin");
            lockCas.lock();
            try{
                log.info("lock");
            }finally {
                lockCas.unlock();
            }
        },"t2").start();

    }

}


class LockCas {

    private AtomicInteger state = new AtomicInteger(0);

    public LockCas() {
    }

    public void lock() {
        while (true) {
            if (state.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public void unlock() {
        state.set(0);
    }

}