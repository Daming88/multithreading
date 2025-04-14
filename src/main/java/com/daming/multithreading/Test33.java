package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;

@Slf4j
public class Test33 {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> integers=new SynchronousQueue<>();

        new Thread(()->{

            try {
                log.info("puting 1");
                integers.put(1);
                log.info("{} putted...",1);

                log.info("puting 2");
                integers.put(2);
                log.info("{} putted...",2);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        },"t1").start();

        Thread.sleep(1000L);

        new Thread(()->{
            try {
                log.info("taking {}",1);
                integers.take();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        },"t2").start();

        Thread.sleep(1000L);

        new Thread(()->{
            try {
                log.info("taking {}",2);
                integers.take();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        },"t3").start();
    }

}
