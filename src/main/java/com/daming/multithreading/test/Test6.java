package com.daming.multithreading.test;

public class Test6 {

    public static void main(String[] args) {
        Thread t1=new Thread("t1"){
            public void run(){
                System.out.println("t1--running");
            }
        };

        System.out.println("t1执行start之前的线程状态："+t1.getState());
        t1.start();
        System.out.println("t1执行start之后的线程状态："+t1.getState());

        Thread t2=new Thread("t2"){
            public void run(){
                System.out.println("t2--running");
            }
        };

        System.out.println("t2执行run之前的线程状态："+t2.getState());
        t2.run();
        System.out.println("t2执行run之后的线程状态："+t2.getState());

    }

}
