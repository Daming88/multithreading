package com.daming.multithreading;

public class Test2 {

    public static void main(String[] args) {
        Runnable r= () -> System.out.println("running");

        Thread t=new Thread(r,"t2");
        t.start();
    }

}
