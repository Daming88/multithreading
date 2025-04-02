package com.daming.multithreading;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask=new FutureTask<>(()->{
            System.out.println("running");
            Thread.sleep(1000);
            return 1;
        });
        Thread t=new Thread(futureTask);
        t.start();
        Integer result = futureTask.get();
        System.out.println(result);
    }
}
