package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test22 {

    private static ReentrantLock lock= new ReentrantLock();

    public static void main(String[] args) {
        lock.lock();

        try {
            log.info("enter main");
            m1();
        }finally {
            log.info("exit main");
            lock.unlock();
        }
    }

    public static void m1(){
        lock.lock();
        try {
            log.info("enter m1");
            m2();
        }finally {
            log.info("exit m1");
            lock.unlock();
        }
    }

    public static void m2(){
        lock.lock();
        try {
            log.info("enter m2");
        }finally {
            log.info("exit m2");
            lock.unlock();
        }
    }
}
