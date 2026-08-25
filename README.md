These are my reasonings about thread safety. This is a simple demo whose goal is to turn a non-thread-safe counter into a thread-safe one, by using the Decorator design pattern. 

The code is very simple and it's not that important, my goal was to *sharpen reasoning*. The profound realization was that, in determining whether a particular class is thread-safe or not, *the client participates in this definition and it cannot simply be discarded as "the client must be unaware"*.

This surprised me because it seems to go against one of the subtle assumptions about software, which is *client is unaware*, which is what abstractions are all about: The client does not have to know nor care how something works, and only gets a simplified, goal or task-oriented interface of that subsystem. Client imports that subsystem and the usage of that subsystem is decoupled from its outcome; For the same input, you get the same output. 

This subtle assumption was pouring into reasonings about thread safety, namely that the client must be unaware. However, after analyzing this example and pushing myself to ask what thread safety is all about, I cannot simply have the client not participate in the definition of thread safety.

Concretely, a client can choose to bypass a thread-safe wrapper class, and work directly with a non-thread-safe class. Or the client can use an already thread-safe class in a context such that, in this context, the class is used in a non-thread-safe way.

This brings me to question whether thread safety is a property of an object or of its access. And when we say access, of course we're talking about a client accessing the object. 

From what I gather, thread safety is a property of the object as well as a property of its access or usage. To reiterate: 
- A client can choose to bypass a thread safe wrapper and work directly with the non-thread-safe object, making the entire target object non-thread-safe as a whole, from the app perspective. 
- A class can be thread-safe and the client can use it in a non-thread-safe context. We're talking about non-atomic compound actions. The class is thread-safe, if you ask it directly. But if you ask the app if the class is thread-safe, for the app the class is non-thread-safe.

It's this participation and thus dependency on the client for the definition of thread safety, that has surprised me. Practically, it means that we can't just use a thread-safe class and all our problems are solved. What I mean by that, if a component guarantees thread safety and you integrate in your system, its correct usage is not just about "correctly using its API" but also includes "correctly using it in thread-safe contexts". 

And it's in this last requirement that the client is fully responsible for making sure that this happens. This means that introducing concurrency has a ripple effect. Introducing a thread-safe component has the effect, or I should say, the hidden requirement, that the client *use* this component in a thread-safe context. Client cannot just drop it wherever and hope that its only job is correctly using its API; Client must actively use the component in a *thread-safe context* as well.

Thus, for each *thread-safe component*, we must have an equally *thread-safe context*. The first is a guarantee of the component; The latter is a hidden requirement placed on the client, that the client must fulfill. 


# Reasoning (original)

In this example, I use the Decorator pattern to make a non-thread-safe class thread-safe. 

What I've realized is that this implementation does not make the original non-thread-safe class magically thread-safe.

It's only by using the decorator that the original class becomes thread-safe. In short, the original class provides the functionality, and the decorator class provides thread safety. Thus, a mixed access of original class and decorator will still lead to non thread safety. What I mean by that, the thread safety does not just depend on using the decorator once, but always.

Given an application running on one machine, we can logically represent thread safety as the logical AND of its accesses; If *all* accesses go through the thread safe decorator, then we guarantee thread safety, else thread safety is not guaranteed.

Let A be Access. We try to answer this question: Is the original class thread safe from the app perspective, as a whole?

The answer can be formulated as:

`
isThreadSafe(A_1) AND isThreadSafe(A_2) AND ... isThreadSafe(A_N)
`

Decoupling thread safety from functionality may or may not be a wanted outcome. If the functionality class is legacy and the cost of making it thread-safe is high, having a separate class provide thread safety can be a solution. Another example: the functionality class needs to be developed without the additional development effort of adding and testing synchronization, then this decoupling, which can be achieved with the Decorator pattern, can be a solution.

To be more precise, this is not thread safety in the strictest definition. Thread safety requires that the object be internally synchronized and does not need further synchronization from the client.  

Thus, this thread safety is derived from the decorator, not from the original class. It's true that a thread-safe object can still be used in a non-thread-safe or non-atomic context, and thus that thread safety is also about usage and not just what an object guarantees to the outside world. 

However, the original class is *already* non-thread-safe. Then the Decorator makes the access to it thread-safe. But following the definition, we can still use this thread-safe access in a non-thread-safe context. After running a test, the question came: Should I synchronize on the target object or on the decorator object? The answer for me is on the target object, for now.

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