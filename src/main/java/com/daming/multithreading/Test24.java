package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test24 {

    private static ReentrantLock lock=new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            public void run() {
                log.info("尝试获取锁");
                if (!lock.tryLock()){
                    log.info("t1 没有获取不到锁，返回");
                    return;
                }
                try{
                    log.info("获取到锁");
                }finally {
                    lock.unlock();
                }
            }
        };

        lock.lock();
        log.info("main 获取到锁");
        t1.start();
    }

}

