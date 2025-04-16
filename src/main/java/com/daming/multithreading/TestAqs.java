package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestAqs {

    public static void main(String[] args) {

        ReentrantLock reentrantLock = new ReentrantLock();

        MyLocks lock = new MyLocks();

        new Thread(() -> {
            lock.lock();
            log.info("locking。。。。");
            lock.lock();
            try{
                log.info("locking。。。。");
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }finally {
                log.info("unlocking。。。。");
                lock.unlock();
            }
        },"t1").start();

        new Thread(() -> {
            lock.lock();
            try{
                log.info("locking。。。。");
            }finally {
                log.info("unlocking。。。。");
                lock.unlock();
            }
        },"t2").start();

    }

}

/**
 * 自定义锁(不可重入锁)
 */
@Slf4j
class MyLocks implements Lock {

    // 独占锁
    class MySync extends AbstractQueuedSynchronizer{

        @Override   // 尝试获得锁
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)){
                // 加上了锁,并且设置 owner 为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override   // 是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    @Override   // 加锁（不成功会进入等待队列
    public void lock() {
        sync.acquire(1);
    }

    @Override  // 加锁，可打断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override   // 尝试加锁（-次）
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override   // 尝试加锁，带超时时间
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override   // 解锁
    public void unlock() {
        sync.release(1);
    }

    @Override   // 创建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }
}
