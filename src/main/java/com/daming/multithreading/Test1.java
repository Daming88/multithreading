package com.daming.multithreading;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {

    public static void main(String[] args) {
        Thread t=new Thread(){
            public void run(){
                log.info("running");
            }
        };
        t.setName("t1");
        t.start();
        log.info("main");
    }

}
