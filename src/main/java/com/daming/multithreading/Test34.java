package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Test34 {

    public static void main(String[] args) {
        //test2();

        test1();
    }

    public static void test2() {

        ExecutorService pool = Executors.newSingleThreadExecutor();

        pool.execute(()->{
            log.info("1");
            int i=1/0;
        });

        pool.execute(()->{
            log.info("2");
        });

        pool.execute(()->{
            log.info("3");
        });

    }

    public static void test1() {

        ExecutorService pool = Executors.newFixedThreadPool(1);

        pool.execute(()->{
            log.info("1");
            int i=1/0;
        });

        pool.execute(()->{
            log.info("2");
        });

        pool.execute(()->{
            log.info("3");
        });

    }

}
