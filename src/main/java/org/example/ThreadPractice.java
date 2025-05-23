package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPractice {

  public static void main(String[] args) throws Exception { // because Thread.sleep() throws InterruptedException
    // 1. Demonstrate three ways to create threads
    threadCreationDemo(); // directly call static methods

    // 2. Show thread control methods
    threadControlDemo();

    // 3. Demonstrate thread safety
    threadSafetyDemo();

    // 4. Show CAS operation
    casDemo();

    // 5. Demonstrate volatile keyword
    volatileDemo();
  }

  // ===== 1. Three Ways to Create Threads =====
  private static void threadCreationDemo() {
    System.out.println("\n=== Thread Creation Demo ===");

    // Method 1: Extend Thread class
    class MyThread extends Thread {
      public void run() {
        System.out.println("Thread running (extends Thread)");
      }
    }
    new MyThread().start();

    // Method 2: Implement Runnable
    Runnable myRunnable = () -> System.out.println("Thread running (implements Runnable)"); // lambda expression
    new Thread(myRunnable).start();

    // Method 3: Implement Callable (needs ExecutorService)
    Callable<String> myCallable = () -> { // a thread with a returned String
      System.out.println("Thread running (implements Callable)");
      return "Callable result";
    };
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> future = executor.submit(myCallable);
    executor.shutdown();
  }

  // ===== 2. Thread Control Methods =====
  private static void threadControlDemo() throws InterruptedException {
    System.out.println("\n=== Thread Control Demo ===");
    Object lock = new Object();

    // Thread using wait()/notify()
    new Thread(() -> {
      synchronized (lock) { // get the lock
        try {
          System.out.println("Thread 1: Waiting...");
          lock.wait(); // release lock and wait
          System.out.println("Thread 1: Resumed!"); // if it is awaked, get the lock and execute
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    Thread.sleep(1000); // Ensure Thread 1 waits first

    new Thread(() -> {
      synchronized (lock) {
        System.out.println("Thread 2: Notifying");
        lock.notify(); // wake up thread 1
      }
    }).start();

    // sleep() example
    new Thread(() -> { // this is thread 2
      System.out.println("Thread 3: Sleeping for 2 seconds");
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Thread 3: Awake!");
    }).start();
  }

  // ===== 3. Thread Safety Mechanisms =====
  private static void threadSafetyDemo() throws InterruptedException {
    System.out.println("\n=== Thread Safety Demo ===");
    Counter counter = new Counter();
    Lock lock = new ReentrantLock();

    // Unsafe counter
    Runnable unsafeTask = () -> {
      for (int i = 0; i < 1000; i++) {
        counter.incrementUnsafe();
      }
    };

    // Safe counter with synchronized
    Runnable safeTask = () -> {
      for (int i = 0; i < 1000; i++) {
        counter.incrementSafe();
      }
    };

    // Safe counter with Lock
    Runnable lockTask = () -> {
      for (int i = 0; i < 1000; i++) {
        counter.incrementWithLock(lock);
      }
    };

    Thread t1 = new Thread(unsafeTask);
    Thread t2 = new Thread(unsafeTask);
    t1.start(); t2.start();
    t1.join(); t2.join();
    System.out.println("Unsafe counter result: " + counter.getUnsafeCount());

    t1 = new Thread(safeTask);
    t2 = new Thread(safeTask);
    t1.start(); t2.start();
    t1.join(); t2.join();
    System.out.println("Safe counter (synchronized) result: " + counter.getSafeCount());

    t1 = new Thread(lockTask);
    t2 = new Thread(lockTask);
    t1.start(); t2.start();
    t1.join(); t2.join();
    System.out.println("Safe counter (Lock) result: " + counter.getLockCount());
  }

  static class Counter {
    private int unsafeCount = 0;
    private int safeCount = 0;
    private int lockCount = 0;

    public void incrementUnsafe() {
      unsafeCount++;
    }

    public synchronized void incrementSafe() { // synchronized make it safe
      safeCount++;
    }

    public void incrementWithLock(Lock lock) {
      lock.lock();
      try {
        lockCount++;
      } finally {
        lock.unlock();
      }
    }

    public int getUnsafeCount() { return unsafeCount; }
    public int getSafeCount() { return safeCount; }
    public int getLockCount() { return lockCount; }
  }

  // ===== 4. CAS (Compare-And-Swap) Demo =====
  private static void casDemo() {
    System.out.println("\n=== CAS Demo ===");
    AtomicInteger atomicCounter = new AtomicInteger(0);

    Runnable casTask = () -> {
      for (int i = 0; i < 1000; i++) {
        atomicCounter.incrementAndGet();
      }
    };

    Thread t1 = new Thread(casTask);
    Thread t2 = new Thread(casTask);
    t1.start(); t2.start();
    try {
      t1.join(); t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Atomic counter result: " + atomicCounter.get());
  }

  // ===== 5. Volatile Keyword Demo =====
  private static volatile boolean running = true;

  private static void volatileDemo() throws InterruptedException {
    System.out.println("\n=== Volatile Demo ===");

    Thread readerThread = new Thread(() -> {
      while (running) {
        // Keep reading the volatile variable
      }
      System.out.println("Reader thread: Flag changed, exiting");
    });

    Thread writerThread = new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Writer thread: Changing flag to false");
      running = false;
    });

    readerThread.start();
    writerThread.start();

    readerThread.join();
    writerThread.join();
  }
}