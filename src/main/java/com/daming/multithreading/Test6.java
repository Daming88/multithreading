package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test6 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        log.info("t1 state:{}", t1.getState());
        t1.start();
        log.info("t1 state:{}", t1.getState());

        Thread.sleep(500);

        log.info("t1 state:{}", t1.getState());
    }
}
