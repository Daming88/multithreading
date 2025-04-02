package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test10 {

    public static void main(String[] args) throws InterruptedException {

        Room room=new Room();

        Thread t1=new Thread("t1"){
            public void run(){
                for(int i=0;i<5000;i++){
                    room.increment();
                }
            }
        };

        Thread t2=new Thread("t2"){
            public void run(){
                for(int i=0;i<5000;i++){
                    room.decrement();
                }
            }
        };

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("counter:{}",room.getCounter());

    }

}

class Room{
    private int counter=0;

    public void increment(){
        synchronized (this){
            counter++;
        }
    }

    public void decrement(){
        synchronized (this){
            counter--;
        }
    }

    public int getCounter(){
        synchronized (this){
            return counter;
        }
    }
}
