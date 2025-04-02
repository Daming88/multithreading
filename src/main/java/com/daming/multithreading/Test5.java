package com.daming.multithreading;

public class Test5 {

    public static void main(String[] args) {
        new Thread(()->{
           while (true){
               try{
                   Thread.sleep(1000);
                   System.out.println("t1--running");
               }catch (InterruptedException e){
                   e.printStackTrace();
               }
           }
        },"t1").start();

        new Thread(()->{
            while (true){
                try{
                    Thread.sleep(1000);
                    System.out.println("t2--running");
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"t2").start();
    }

}
