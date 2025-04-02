## 创建和运行线程

1. 直接使用Thread

```java
// 创建线程对象
Thread t = new Thread() {
    public void run() {
        // 要执行的任务
    }
};
// 启动线程
t.

start();
```

[对应演示代码](src/main/java/com/daming/multithreading/Test1.java)

2. 使用Runnable接口

```java
Runnable runnable = new Runnable() {
    public void run() {
        // 要执行的任务
    }
};
// 创建线程对象
Thread t = new Thread(runnable);
// 启动线程
t.

start();
```

[对应演示代码](src/main/java/com/daming/multithreading/Test2.java)

>
>> 方法1是把线程和任务合并在一起，方法2是把线程和任务分开  
> > 用Runnable 更容易与线程池等高级API结合  
> > Runnable 让任务脱离了Thread继承体系，更灵活

3. 使用FutureTask配合Thread  
   FutureTask能够接收Callable类型的参数，用来处理有返回结果的情况

```java
// 创建任务对象

import java.util.concurrent.FutureTask;

FutureTask<Integer> task = new FutureTask<Integer>(() -> {
    System.out.println("hello");
    return 100;
});

// 参数1是任务对象，参数2是线程名字
new

Thread(task, "t1").

start();

Integer result = task.get();
System.out.

println("result="+result);
```

[对应演示代码](src/main/java/com/daming/multithreading/Test3.java)

## 查看进程和线程的方法

linux

```bash
# 查看所有进程
ps -fe 
# 查看指定进程的线程
ps -ft -p <PID> 
```

jconsole来查看某个Java进程中线程的运行情况(图形界面)

```bash
java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1888 -Dcom.sun.management.jmxremote.rmi.port=1888  -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=192.168.1.146 Test5
```

## start和run方法

>
>> start方法会创建一个新线程，并调用线程的run方法

## sleep与yield

> sleep
>> 1、调用sleep会让当前线程从Running进入Timed Waiting状态  
> > 2、其他线程可以使用 interrupt方法进行打断正在睡眠的线程，这时sleep方法会抛出
> > InterruptedException,不会继续执行睡眠时间结束后的代码  
> > 3、睡眠结束后的线程未必会立刻执行  
> > 4、建议用TimeUnit 的 sleep 代替Thread 的sleep 来获得更好的特性  
[对应sleep演示代码](src/main/java/com/daming/multithreading/Test6.java)  
[对应sleep被中断的代码演示](src/main/java/com/daming/multithreading/Test7.java)

> yield
>>
1、调用yield会让当前线程从Running进入Runnable，然后调度执行其他同优先级的线程。如果这时没有同优先级的线程，那么不能保证当前线程的暂停效果  
> > 2、具体的实现依赖于操作系统的任务调度器

## 共享模型之管程

1. 共享问题  
   [共享问题的代码演示](src/main/java/com/daming/multithreading/Test8.java)
    1. 一个程序运行多个线程本身是没问题的
    2. 问题出在多个线程访问共享资源
        1. 多个线程读共享资源其实也没问题
        2. 在多个线程对共享资源读写操作发生指令交错，就会出现问题
    3. 一段代码块内如果存在对共享资源的多线程读写操作，称这段代码块为临界区
    4. 多个线程在临界区内执行，由于代码的执行序列不同而导致结果无法预测，称之为发生了竞态条件
2. synchronized    
   为了避免临界区的竞态条件发生，有多种手段可以达到目的
    1. 阻塞式的解决方案：synchronized，Lock
    2. 非阻塞式的解决方案：原子变量(CAS)

```java
synchronized (对象){
临界区
}
```
[上述共享问题的解决方法实例代码](src/main/java/com/daming/multithreading/Test9.java)
> 思考：
>
>如果把synchronized(obj)放在for循环外面，如何理解，会发生什么效果？  
>> 结果不会发生变化，只不过执行的流程，t2需要等到t1的循环执行完后释放锁，再执行t2  
> 
>如果t1 synchronized(obj1)而t2 synchronized(obj2)，如何理解，会发生什么效果？  
>> 结果出现线程安全问题，结果不可预估，因为t1和t2获取的锁对象不一样，无法保证临界区的代码的原子性
> 
>如果t1 synchronized(obj)而t2没有会怎样？如何理解？  
>> 结果出现线程安全问题，结果不可预估，t1需要获取的锁对象而t2是不需要获取锁对象的，同样无法保证临界区的代码的原子性

[Test8面向对象改造实例代码](src/main/java/com/daming/multithreading/Test10.java)  

```java
// synchronized加在类的成员方法上，实际上锁的是当前实例对象
class Test{
    public synchronized void test(){}
}
// 等价于
class Test{
    public void test(){
        synchronized(this){}
    }
}


// synchronized加在类的静态方法上，实际上锁的是当前类的class对象
class Test{
    public synchronized static void test(){}
}
// 等价于
class Test{
    public static void test(){
        synchronized(Test.class){}
    }
}
```
3. 线程安全分析

4. Monitor  
> Java对象头
5. wait/notify
6. 线程状态转换
7. 活跃性
8. Lock

