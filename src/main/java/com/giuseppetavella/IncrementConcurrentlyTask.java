package com.giuseppetavella;

import java.util.ArrayList;
import java.util.List;

public class IncrementConcurrentlyTask implements Runnable {
    private final int nThreads;
    private final List<Thread> threads;
    private final Counter counter;
    private final int limit;

    public IncrementConcurrentlyTask(int nThreads, Counter counter, int limit) {
        this.nThreads = nThreads;
        this.threads = new ArrayList<>();
        this.counter = counter;
        this.limit = limit;
    }

    @Override
    public void run() {
        for (int i = 0; i < nThreads; i++) {
            Runnable task = () -> {
                for (int j = 0; j < limit; j++) {
                    counter.increment();
                }
            };
            threads.add(new Thread(task));
        }

        for (int i = 0; i < nThreads; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < nThreads; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
}
