package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Test32 {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {

            private AtomicInteger t=new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"my-thread-"+t.getAndIncrement());
            }
        });

        pool.execute(()->{
            log.info("1");
        });

        pool.execute(()->{
            log.info("2");
        });

        pool.execute(()->{
            log.info("3");
        });
    }

}
