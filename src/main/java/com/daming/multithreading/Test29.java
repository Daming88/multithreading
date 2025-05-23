package com.daming.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Test29 {

    public static void main(String[] args) {
        demo(()->new AtomicLong(0), (adder)->adder.getAndIncrement());
        demo(()->new LongAdder(), (adder)->adder.increment());
    }

    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action){
        T adder = adderSupplier.get();

        List<Thread> ts=new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            ts.add(new Thread(()->{
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }

        long start = System.nanoTime();

        ts.forEach(Thread::start);
        ts.forEach(t->{
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.nanoTime();

        System.out.println(adder+"耗时："+(end-start)/1000_000);
    }

}
