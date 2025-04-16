package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 采用不类型的任务通过不同线程池来处理，解决饥饿问题
 */
@Slf4j
public class Test36 {

   static final List<String> MENU= Arrays.asList("地三鲜","宫保鸡丁","辣子鸡丁","烤鸡翅");
   static Random RANDOM=new Random();
   static String cooking(){
       return MENU.get(RANDOM.nextInt(MENU.size()));
   }

    public static void main(String[] args) {
        ExecutorService waiterPool = Executors.newFixedThreadPool(1);
        ExecutorService cookPool = Executors.newFixedThreadPool(1);

        waiterPool.execute(()->{
            log.info("处理点餐...");
            Future<String> f = cookPool.submit(() -> {
                log.info("做菜...");
                return cooking();
            });
            try {
                log.info("上菜:{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        waiterPool.execute(()->{
            log.info("处理点餐...");
            Future<String> f = cookPool.submit(() -> {
                log.info("做菜...");
                return cooking();
            });
            try {
                log.info("上菜:{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
