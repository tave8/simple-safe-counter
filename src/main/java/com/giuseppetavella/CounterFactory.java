package com.giuseppetavella.decorator.example4;

public class CounterFactory {
    public static Counter newCounter() {
        CounterImpl counter = new CounterImpl();
        return new CounterImplSafeWrapper(counter);
    }
}
