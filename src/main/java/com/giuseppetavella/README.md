In this example, I use the Decorator pattern to make a non-thread-safe class thread-safe. 

What I've realized is that this implementation does not make the original non-thread-safe class magically thread-safe.

It's only by using the decorator that the original class becomes thread-safe. In short, the original class provides the functionality, and the decorator class provides thread safety.

Thus, a mixed access of original class and decorator will still lead to non thread safety.

What I mean by that, the thread safety does not just depend on using the decorator once, but always.

Given an application running on one machine, we can logically represent thread safety as the logical AND of its accesses; If *all* accesses go through the thread safe decorator, then we guarantee thread safety, else thread safety is not guaranteed.

Let A be Access. We try to answer this question: Is the original class thread safe from the app perspective, as a whole?

The answer can be formulated as:

`
isThreadSafe(A_1) AND isThreadSafe(A_2) AND ... isThreadSafe(A_N)
`

Decoupling thread safety from functionality may or may not be a wanted outcome. If the functionality class is legacy and the cost of making it thread-safe is high, having a separate class provide thread safety can be a solution. Another example: the functionality class needs to be developed without the additional development effort of adding and testing synchronization, then this decoupling, which can be achieved with the Decorator pattern, can be a solution.

To be more precise, this is not thread safety in the strict definition. Thread safety requires that the object be internally synchronized and does not need further synchronization or even awareness from the client.  

Thus, this thread safety is derived from the decorator, not from the original class.

It's true that a thread-safe object can still be used in a non-thread-safe or non-atomic context, and that thread safety is also about usage and not just what an object guarantees to the outside world. 

However, the original class is *already* non-thread-safe. Then the Decorator makes the access to it thread-safe. But following the definition, we can still use this thread-safe access in a non-thread-safe context. 

After running a test, the question came: Should I synchronize on the target object or on the decorator object? The answer for me is on the target object.

This took the code from this:

```java
public class CounterImplSafeWrapper implements Counter {
    private final Counter target;

    public CounterImplSafeWrapper(Counter target) {
        this.target = target;
    }


    @Override
    public synchronized void increment() { // <---
        target.increment();
    }

    @Override
    public synchronized int get() {  // <---
        return target.get();
    }
}
```


To this:

```java
public class CounterImplSafeWrapper implements Counter {
    private final Counter target;

    public CounterImplSafeWrapper(Counter target) {
        this.target = target;
    }


    @Override
    public void increment() {   // --
        synchronized (target) { // ++
            target.increment();
        }
    }

    @Override
    public int get() {           // --
        synchronized (target) {  // ++
            return target.get();
        }
    }
}
```

So now the code is thread-safe by going through the wrapper as well as thread-safe *across wrappers*.