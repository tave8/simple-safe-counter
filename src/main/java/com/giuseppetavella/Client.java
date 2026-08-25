package com.giuseppetavella;

public class Client {
    static void main(String[] args) {
        Counter counter = CounterFactory.newCounter();

        int limit = 1_000_000;
        int nThreads = 2;
        int expected = limit * nThreads;

        var unsafeCounter = new CounterImpl();
        var safeCounter = new CounterImplSafeWrapper(new CounterImpl());

        var taskUnsafeCounter = new IncrementConcurrentlyTask(nThreads, unsafeCounter, limit);
        var taskSafeCounter = new IncrementConcurrentlyTask(nThreads, safeCounter, limit);
        
        System.out.println("Expected: %d".formatted(expected));

        long start = System.currentTimeMillis();

        taskUnsafeCounter.run();
        System.out.println("[%d ms] Unsafe counter: %s".formatted(System.currentTimeMillis()-start, unsafeCounter.get()));
        
        start = System.currentTimeMillis();

        taskSafeCounter.run();
        System.out.println("[%d ms] Safe counter: %s".formatted(System.currentTimeMillis()-start, safeCounter.get()));

        
    }
}
