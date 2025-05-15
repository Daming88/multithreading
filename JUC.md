## AQS原理
> AQS是AbstractQueuedSynchronizer的简称，是用于构建锁和同步器的框架，它通过一个被volatile修饰的int 类型变量 state 来表示同步状态，并且维护一个FIFO的等待队列，用于管理等待获取锁的线程。
>
> **_核心功能_**：     
>> 1、支持独占锁模式：例如：ReentrantLock
> 
>> 2、支持共享锁模式：例如：Semaphore、CountDownLatch

### AQS 核心结构解析
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {

    // 同步队列的头节点
    private transient volatile Node head;

    // 同步队列的尾节点
    private transient volatile Node tail;

    // 同步状态，0 表示未加锁，>0 表示已加锁（支持重入）
    private volatile int state;

    // 当前持有锁的线程（继承自 AbstractOwnableSynchronizer）
    private Thread exclusiveOwnerThread;
    
    ...
}

```

> **_1、state：同步状态_**：
> 
>> 1、通过volatile修饰保证可见性
> 
>> 2、在ReentrantLock中，state==0标识无锁，state>0表示有锁，且值为重入次数。
> 
>> 3、所有对锁的操作本质上都是对state的CAS操作。
> 
> **_2、head和tail：这两个是构成先进先出队列的头尾指针：_**
> 
> 使用Node节点 构成一个双向链表，用于保存等待获取锁的线程。
> 
>> 1、head：指向当前正在尝试获取锁或者即将被唤醒的线程节点。
> 
>> 2、tail：指向当前正在等待获取锁的线程的最后一个节点。
> 
> **_3、exclusiveOwnerThread：当前持有锁的线程_**：
> 
>> 1、继承AbstractOwnableSynchronizer，用于保存当前持有锁的线程。
> 
>> 2、用于记录当前哪个线程尺有所，主要用于锁的可重入操作的时候的判断。

### Node节点的详解
```java
static final class Node {
    static final Node SHARED = new Node();      // 共享模式
    static final Node EXCLUSIVE = null;         // 独占模式

    static final int CANCELLED = 1;             // 节点取消
    static final int SIGNAL = -1;               // 后继节点需要被唤醒
    static final int CONDITION = -2;            // 节点在条件队列中
    static final int PROPAGATE = -3;            // 共享模式下传播唤醒

    volatile int waitStatus;                    // 节点等待状态
    volatile Node prev;                         // 前驱节点
    volatile Node next;                         // 后继节点
    volatile Thread thread;                     // 当前线程
    Node nextWaiter;                            // 下一个等待节点（用于 Condition）

    final boolean isShared() { return nextWaiter == SHARED; }
    final Node predecessor() throws NullPointerException { ... }
}

```
**_waitStatus：_** 节点的等待状态，通常是下面的几种值，作用是判断当前线程的节点处于什么状态，后续是否需要被唤醒，或者以什么样的形式唤醒。
> **CANCELLED_**：1，表示节点被取消，不会再阻塞
>> 1、表示该节点所代表的线程已经取消获取锁。
> 
>> 2、可能由于线程中断或超时等原因导致。
> 
>> 3、一旦进入 CANCELLED 状态，节点不会再参与锁的竞争。
> 
>> AQS 会跳过这些节点，不会尝试唤醒它们。
> 
> **_SIGNAL_**：-1，表示后继节点需要被唤醒
>> 1、表示当前节点的线程即将阻塞，希望在释放锁后唤醒它的后继节点。
>
>> 2、在加锁失败后，节点加入队列时，其前驱节点可能会被设置为 SIGNAL。
> 
>> 3、如果前驱节点是 SIGNAL，那么当前线程可以安全地进入阻塞状态（park()）。
> 
> **_CONDITION_**：-2，表示节点在条件队列中
> 
>> 1、仅用于 ConditionObject.await() 相关操作。
> 
>> 2、表示当前节点正在某个 Condition 上等待，尚未转移到主同步队列。
> 
>> 3、当调用 signal() 或 signalAll() 时，这些节点会被移动到主同步队列中，并更新状态为 0 或 SIGNAL。
> 
> **_PROPAGATE_**：-3，表示共享模式下唤醒应继续传播
> 
>> 1、仅用于共享模式（如 CountDownLatch, Semaphore）。
> 
>> 2、表示当前节点被唤醒后，应该继续唤醒后续的共享节点，以保证多个线程能够并发获取资源。
> 
> **_默认值也就是0_**：0，会触发 PROPAGATE 状态，确保所有等待线程都能被唤醒。

### AQS 普通加锁流程详解(以ReentrantLock为例子)

**_第一步：_** 通过ReentrantLock加锁，源码调用的流程如下：
```java
// 通过lock方法加锁，实际调用的源码如下
public void lock() {
    sync.lock();
}

// 非公平锁的加锁方式
final void lock() {
    if (compareAndSetState(0, 1))
        setExclusiveOwnerThread(Thread.currentThread());
    else
        acquire(1);
}

// 公平锁
final void lock() {
    acquire(1);
}
```
> 从源码可以看出，非公平锁加锁的时候，是直接先判断能不能获取锁，如果不能则进入AQS的锁竞争流程，而公平锁是直接进入AQS的锁竞争流程，也就是acquire(1)方法。

**_第二步：_** AQS的锁竞争流程,acquire(1)，该方法的实现逻辑就是在AQS内实现，源码如下

```java
// AQS的锁竞争流程
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}

//其中的tryAcquire(arg)方法，sync对AQS实现了重写，按照公平锁和非公平锁的两种实现形式。
// 非公平所：具体实现在ReentrantLock中的NonfairSync的tryAcquire(int acquires)
protected final boolean tryAcquire(int acquires) {
    return nonfairTryAcquire(acquires);
}

final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState(); // 获取当前Lock对象的状态
    if (c == 0) {  // 说明此时处于无锁状态
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);  // 直接进行加锁操作
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {  // 判断当前线程是否是锁的持有者
        int nextc = c + acquires; // 计算锁的次数,也就是state的累加
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc); // 设置锁的次数
        return true;
    }
    return false; // 加锁失败
}

// 公平锁：具体实现在ReentrantLock中的FairSync的tryAcquire(int acquires)
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();  // 获取当前Lock对象的状态
    if (c == 0) {  // 如果等于0，说明处于无锁状态下
        if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {  // 判断，队列中是否存在阻塞的线程，如果没有则就行CAS操作更新state的值，如果CAS成功，则设置当前线程为锁的持有者
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {  // 如果处于有现成持有锁的状态，也就是state的值为0，这是判断当前线程是否是锁的持有者
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);  // 如果是，则直接能获取锁，并且设置锁的次数，也就是state的值+1
        return true;
    }
    return false;
}

// 如果tryAcquire(arg)为真，那么说明线程已经成功获取到锁，此时就直接返回了，如果返回false，这是说明线程获取锁失败，此时则进入阻塞的流程，执行acquireQueued(addWaiter(Node.EXCLUSIVE), arg)

// addWaiter(Node.EXCLUSIVE) 此方法的作用是根据当前线程创建一个Node节点，由于Node.EXCLUSIVE默认是null，所以创建的节点是独占模式的。

private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    enq(node);
    return node;
}

// 然后通过acquireQueued(addWaiter(Node.EXCLUSIVE), arg)实现入队列
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

// 入队成功后，执行selfInterrupt();该方法的作用是线程中断自己，其作用是调用 Thread.currentThread().interrupt()，向当前执行的线程发送中断信号，用于在线程阻塞或等待时提前唤醒它。
static void selfInterrupt() {
    Thread.currentThread().interrupt();
}
```

> 以上就是ReentrantLock通过直接调用或者重写AQS功能，实现普通加锁的原理，至于其他方式的加锁操作也是基于这个原理的基础上实现重试的功能而已。


### AQS 普通解锁流程(以 ReentrantLock 为例子)

**_第一步：_** 通过ReentrantLock调用unlock方法，源码如下：

```java

// 在ReentrantLock中，unlock方法调用的是sync.release(1)方法
public void unlock() {
    sync.release(1);
}

// sync.release(1)方法，该方法在AQS中实现，源码如下：
public final boolean release(int arg) {
    if (tryRelease(arg)) {  // 尝试释放一个锁，
        Node h = head;
        if (h != null && h.waitStatus != 0)  // 如果锁释放成功，则判断当前lock对象中的队列，是否存在阻塞等待获取锁的节点，如果存在则进行唤醒
            unparkSuccessor(h);
        return true;
    }
    return false;
}

// 所释放的源码实现，在Sync中重写了tryRelease(int releases)方法，该方法在ReentrantLock中实现，源码如下：
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;  // 获取锁的次数，减去释放的次数，这里锁的次数，也就是state的值
    if (Thread.currentThread() != getExclusiveOwnerThread())  // 如果当前线程不是锁的持有者，则抛出异常
        throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) {  // 如果扣减后的state为0，则释放锁，并且设置锁的持有者线程为null
        free = true;
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}

// 锁成功释放后，还需要判断是否存在阻塞的线程需要我们去唤醒
private void unparkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;  // 获取当前节点的等待状态
    if (ws < 0)  // 如果小于0，
        compareAndSetWaitStatus(node, ws, 0); // 
    /*
     * 要解除停放的线程保存在后继节点中，后继节点通常是下一个节点。但如果被取消或明显为null，则从尾部向后遍历以找到实际未被取消的后继者。
     */
    Node s = node.next; // 取出下一个等待的节点
    if (s == null || s.waitStatus > 0) {  // 判断下一个节点是否为null或者节点的等待状态大于0，如果大于0，则说明该节点被取消了，那么需要从尾部开始遍历，找到一个非取消的节点，然后进行唤醒
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)  // 如果队列中存在需要被唤醒的线程，则进行唤醒
        LockSupport.unpark(s.thread);
}
```

> 以上就是ReentrantLock通过直接调用或者重写AQS功能，实现普通解锁的原理，至于其他方式的解锁操作也是基于这个原理的基础上实现重试的功能而已。