package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test31 {

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1000, TimeUnit.MILLISECONDS, 10);

        int count=0;
        for (int i = 0; i < 5; i++) {
            int j=i;
            count = j;
            threadPool.execute(() -> {
                log.info("{} ", j);
            });
        }

        System.out.println();
    }

}

// 线程池
@Slf4j
class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet();

    // 核心线程数
    private int coreSize;

    // 获取去任务的超时时间
    private long timeout;

    // 时间单位
    private TimeUnit timeUnit;

    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 当 task不为空，执行任务
            // 如果task为空或者task执行完毕，则判断任务队列中是否还存在任务，如果存在则接着继续执行
            while (task!=null || (task=(taskQueue.take()))!=null){
                try {
                    log.info("正在执行任务...{}",task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    task = null;
                }
            }

            synchronized (workers){
                log.info("worker被移除{}",this);
                workers.remove(this);
            }
        }
    }

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
    }

    // 执行任务
    public void execute(Runnable task) {
        // 当任务数量小于 coreSize 时，直接交给worker对象执行
        // 如果任务数超过了 coreSize 时，将任务放入任务队列暂存起来
        synchronized (workers){
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.info("新增worker{},{}",worker,task);
                worker.start();
                workers.add(worker);
            } else {
                // 如果任务队列没有满，则将任务放入任务队列中
                log.info("加入任务队列{}",task);
                taskQueue.put(task);
            }
        }
    }

}

// 任务队列
class BlockingQueue<T> {

    // 1、任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2、锁
    private ReentrantLock lock = new ReentrantLock();

    // 3、生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4、消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5、容量
    private int capCity;

    public BlockingQueue(int capCity) {
        this.capCity = capCity;
    }

    // 待超时的阻塞获取
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 获取需要等待的时间
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {

                    if (nanos <= 0) {
                        return null;
                    }
                    // 返回的是剩余等待的时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                   e.printStackTrace();
                }
            }

            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T element) {

        lock.lock();
        try {
            while (queue.size() == capCity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.addLast(element);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }

    }

    // 获取队列大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
