package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Test28 {

    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(0);

        System.out.println(i.incrementAndGet()); // 等价于  ++1  输出是 1  i最后的结果是1
        System.out.println(i.getAndIncrement()); // 等价于  1++  输出是 1  i最后的结果是2
        System.out.println(i.get());             //             输出是 2  i最后的结果是2

        System.out.println(i.getAndAdd(2)); //  先获取再计算     输出是 2  i最后的结果是4
        System.out.println(i.addAndGet(2)); //  先计算再获取     输出是 6  i最后的结果是6
        System.out.println(i.get());             //        输出是 6  i最后的结果是6

        System.out.println("----------");
        int a=0;

        System.out.println(a=+2);
        System.out.println(a=0);
        System.out.println(a+2);
        System.out.println(a);
    }

}
