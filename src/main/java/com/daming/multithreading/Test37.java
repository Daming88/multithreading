package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Test37 {

    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        log.info("start...");
        pool.scheduleAtFixedRate(()->{
            log.info("running...");
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void methodScheduledThreadPool() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

        pool.schedule(()->{
            log.info("task1");
            int i = 1/0;
        }, 1, TimeUnit.SECONDS);

        pool.schedule(()->{
            log.info("task2");
        }, 1, TimeUnit.SECONDS);
    }

    // Timer代码示例
    private static void methodTimer() {
        Timer timer = new Timer();

        TimerTask task1 = new TimerTask() {

            @Override
            public void run() {
                log.info("task1");
                int i = 1/0;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        TimerTask task2 = new TimerTask() {

            @Override
            public void run() {
                log.info("task2");
            }
        };

        log.info("start...");
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }

}
