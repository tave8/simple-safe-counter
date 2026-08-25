package com.giuseppetavella.decorator.example4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConcurrentTasks implements Runnable {

    private final List<Runnable> tasks;
    private final int nThreads;
    private final List<Thread> threads;

    /**
     * 1 thread per task.
     * 
     * @param tasks
     */
    public ConcurrentTasks(Runnable... tasks) {
        this.tasks = Arrays.stream(tasks).toList();
        this.nThreads = tasks.length;
        this.threads = new ArrayList<>();
    }


    @Override
    public void run() {
        for (int i = 0; i < nThreads; i++) {
            threads.add(new Thread(tasks.get(i)));
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
