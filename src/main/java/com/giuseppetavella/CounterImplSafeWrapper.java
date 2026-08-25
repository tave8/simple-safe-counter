package com.giuseppetavella.decorator.example4;

public class CounterImplSafeWrapper implements Counter {
    private final Counter target;

    public CounterImplSafeWrapper(Counter target) {
        this.target = target;
    }


    @Override
    public void increment() {
        synchronized (target) {
            target.increment();
        }
    }

    @Override
    public int get() {
        synchronized (target) {
            return target.get();
        }
    }
}
