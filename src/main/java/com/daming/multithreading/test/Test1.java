package com.daming.multithreading.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {

    public static void main(String[] args) {
        Thread t1=new Thread(){
            public void run(){
            }
        };
        System.out.println(t1.getState());
        t1.start();
        System.out.println(t1.getState());

        Thread t2 = new Thread("t2") {
            public void run() {
                log.info("running");
            }
        };

        t2.run();

    }

}
