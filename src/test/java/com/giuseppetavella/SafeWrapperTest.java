package com.giuseppetavella;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SafeWrapperTest {
    @Test
    void twoSafeWrappersGiveExactOutcome() {
        int limit = 1_000_000;
        int nThreads = 2;
        
        int expected = limit * nThreads * 2;
        
        var counterImpl = new CounterImpl();
        var safeCounter1 = new CounterImplSafeWrapper(counterImpl);
        var safeCounter2 = new CounterImplSafeWrapper(counterImpl);

        var taskSafeCounter1 = new IncrementConcurrentlyTask(nThreads, safeCounter1, limit);
        var taskSafeCounter2 = new IncrementConcurrentlyTask(nThreads, safeCounter2, limit);
        
        new ConcurrentTasks(taskSafeCounter1, taskSafeCounter2).run();
        
        assertEquals(expected, counterImpl.get());
        
    }
}
