package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test23 {

    private static ReentrantLock lock= new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            public void run() {
                try {
                    // 如果没有竞争那么此方法就会获取lock 对象锁
                    // 如果有竞争就进入阻塞队列，可以被其他线程使用interrupt 方法打断
                    log.info("尝试获取锁");
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.info("t1 没有获取到锁，返回");
                    return;
                }

                try{
                    log.info("t1 get lock");
                }finally {
                    lock.unlock();
                }
            }
        };

        lock.lock();
        t1.start();

        Thread.sleep(1000);
        log.info("打断t1线程");
        t1.interrupt();

    }

}
