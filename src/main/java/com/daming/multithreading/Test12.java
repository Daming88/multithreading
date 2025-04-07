package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

@Slf4j
public class Test12 {

    public static void main(String[] args) throws InterruptedException {
        ClassLayout classLayout = ClassLayout.parseInstance(new Dog());
        log.info("{}",classLayout.toPrintable());
        System.out.println("+++++++++++++++++++");
        Thread.sleep(10000);
        ClassLayout classLayout1 = ClassLayout.parseInstance(new Dog());
        log.info("{}",classLayout1.toPrintable());
        System.out.println("+++++++++++++++++++");
    }

}

class Dog{

}
