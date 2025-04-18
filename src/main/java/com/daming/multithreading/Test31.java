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
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1, (queue,task)->{
            // 1) 死等
            // queue.put(task);
            // 2）带超时等待
            //queue.offer(task,1500,TimeUnit.MILLISECONDS);
            // 3）放弃任务执行
            //log.info("放弃任务执行{}",task);
            // 4）抛出异常
            //throw new RuntimeException("任务执行失败"+task);
            // 5）让调用者自己执行任务
            task.run();

        });

        for (int i = 0; i < 4; i++) {
            int j=i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    private RejectPolicy<Runnable> rejectPolicy;

    // 执行任务
    public void execute(Runnable task) {
        // 当任务数量小于 coreSize 时，直接交给worker对象执行
        // 如果任务数超过了 coreSize 时，将任务放入任务队列暂存起来
        synchronized (workers){
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.info("新增worker {}, {}",worker,task);
                workers.add(worker);
                worker.start();
            } else {
                taskQueue.tryPut(rejectPolicy,task);
                // 1) 死等
                // taskQueue.put(task);
                // 2）带超时等待
                // 3）放弃任务执行
                // 4）抛出异常
                // 5）让调用者自己执行任务
            }
        }
    }

    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 当 task不为空，执行任务
            // 如果task执行完毕，则判断任务队列中是否还存在任务，如果存在则接着继续执行
            //while (task!=null || (task=taskQueue.take())!=null){
            while (task!=null || (task=taskQueue.poll(timeout,timeUnit))!=null){
                try {
                    log.info("正在执行...{}",task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    task = null;
                }
            }
            synchronized (workers){
                log.info("worker 被移除{}",this);
                workers.remove(this);
            }
        }
    }

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapcity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }



}

@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue,T task);
}

// 任务队列
@Slf4j
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

    // 阻塞添加
    public void put(T task) {

        lock.lock();
        try {
            while (queue.size() == capCity) {
                try {
                    log.info("等待加入任务队列{}...",task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info("加入任务队列 {}",task);
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }

    }

    // 带超时时间阻塞添加
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {

        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capCity) {
                try {
                    log.info("等待加入任务队列{}...",task);
                    if (nanos<=0){
                        log.info("放弃等待加入任务队列{}...",task);
                        return false;
                    }
                    nanos=fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("加入任务队列 {}",task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
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

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否已经满了
            if (queue.size() == capCity){
                rejectPolicy.reject(this,task);
            }else { // 队列还有空闲
                log.info("加入任务队列 {}",task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}
