package com.daming.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Test27 {
    public static void main(String[] args) {
        Account accountCas = new AccountCas(100000);
        Account.demo(accountCas);

        Account account = new AccountUnsafe(100000);
        Account.demo(account);
    }
}

class AccountCas implements Account {

    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public int getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(int amount) {
        while (true){
            // 获取余额的最新值
            int prev = balance.get();
            // 要修改的余额
            int next=prev-amount;
            // 真正修改
            if (balance.compareAndSet(prev,next)){
                break;
            }
        }
    }
}

class AccountUnsafe implements Account {

    private int balance;

    public AccountUnsafe(int balance) {
        synchronized (this){
            this.balance= balance;
        }
    }

    @Override
    public int getBalance() {
        return this.balance;
    }

    @Override
    public void withdraw(int amount) {
        synchronized (this){
            this.balance -= amount;
        }
    }
}

interface Account {
    // 获取余额
    int getBalance();

    // 取款
    void withdraw(int amount);

    static void demo(Account account){
        List<Thread> ts = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }

        long start = System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " + (end - start)/1000_000 + " ms");
    }
}
