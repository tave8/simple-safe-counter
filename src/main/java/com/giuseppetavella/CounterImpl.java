package com.giuseppetavella;

public class CounterImpl implements Counter {
    private int count;

    public CounterImpl() {
        this.count = 0;
    }
    
    @Override
    public void increment() {
        this.count++;
    }

    @Override
    public int get() {
        return this.count;
    }
}
