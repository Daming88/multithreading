package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Test26 {


    //volatile static boolean run=true;
    static boolean run=true;

    final static Object lock=new Object();

//    public static void main(String[] args) throws InterruptedException {
//        new Thread(()->{
//            while(run){
//                int i=0;
//                if (!run){
//                    break;
//                }
//            }
//        }).start();
//        Thread.sleep(1000);
//        run=false;
//    }


    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while(run){
                synchronized (lock){
                    if (!run){
                        break;
                    }
                }
            }
        }).start();
        Thread.sleep(1000);
        synchronized (lock){
            run=false;
        }
    }
}
