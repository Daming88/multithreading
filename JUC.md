## AQS原理
全称 AbstractQueueSynchronizer，是阻塞式锁和相关的同步器工具的框架  
特点：   
1、用 state 属性来表示资源的状态(分独占模式和共享模式)， 子类需要定义如何维护这个状态，控制如何获取和释放锁。     
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getState()：获取当前状态   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;setState()：设置当前状态   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;compareAndSetState()：cas机制设置state状态   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;独占模式是只有一个线程能够访问共享资源，而共享模式可以允许多个线程访问共享资源。   
2、提供了基于FIFO的等待队列，类似于Monitor的EntryList
3、条件变量来实现等待、唤醒机制，支持多个条件变量，类似 Monitor 的 WaitSet   
[自定义不可重入锁的示例代码实现](src/main/java/com/daming/multithreading/test/TestAqs.java)

## ReentrantLock原理

### 非公平锁