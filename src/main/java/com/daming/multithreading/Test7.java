package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test7 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            public void run() {
                log.info("enter sleep");
                try {
                    Thread.sleep(2000);
                    log.info("睡眠时间到了");
                } catch (InterruptedException e) {
                    log.info("wake up");
                    e.printStackTrace();
                }
            }
        };
        t1.start();

        Thread.sleep(1000);
        log.info("interrupt t1...");
        t1.interrupt();
    }

}
