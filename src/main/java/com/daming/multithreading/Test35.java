package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class Test35 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);

        methodInvokeAny(pool);
    }

    private static void methodInvokeAny(ExecutorService pool) throws InterruptedException, ExecutionException {
        String result = pool.invokeAny(Arrays.asList(
                () -> {
                    log.info("begin");
                    Thread.sleep(1000);
                    log.info("1 end");
                    return "1";
                },
                () -> {
                    log.info("begin");
                    Thread.sleep(500);
                    log.info("2 end");
                    return "2";
                },
                () -> {
                    log.info("begin");
                    Thread.sleep(2000);
                    log.info("3 end");
                    return "3";
                }
        ));

        log.info("{}", result);
    }

    private static void methodInvokeAll(ExecutorService pool) throws InterruptedException {
        List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.info("begin");
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    log.info("begin");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.info("begin");
                    Thread.sleep(2000);
                    return "3";
                }
        ));

        futures.forEach(future -> {
            try {
                log.info("{}", future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // submit
    private static void methodSubmit(ExecutorService pool) throws InterruptedException, ExecutionException {
        Future<String> future = pool.submit(() -> {
            log.info("running");
            Thread.sleep(1000);
            return "ok";

        });

        log.info("{}", future.get());
    }

}
