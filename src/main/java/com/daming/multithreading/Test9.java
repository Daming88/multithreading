package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test9 {

    static int counter=0;
    static final Object lock =new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1=new Thread("t1"){
            public void run(){
                for(int i=0;i<5000;i++){
                    synchronized (lock){
                        counter++;
                    }
                }
            }
        };

        Thread t2=new Thread("t2"){
            public void run(){
                for(int i=0;i<5000;i++){
                   synchronized (lock){
                       counter--;
                   }
                }
            }
        };

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("counter:{}",counter);

    }

}
