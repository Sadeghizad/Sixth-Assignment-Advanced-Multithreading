# Report.md

## 1. `Atomic Variables`

### Questions & Answers

#### 🔹 What output do you get from the program? Why?

**Example Output:**

```
Atomic Counter: 2000000  
Normal Counter: 1583361
```

**Explanation:**
The `atomicCounter` consistently reaches the expected value `2,000,000` because `AtomicInteger` ensures thread-safe increments using low-level atomic CPU instructions (like CAS – Compare-And-Swap).
In contrast, `normalCounter` is not thread-safe. When multiple threads attempt to increment it simultaneously, race conditions occur, causing some increments to be lost. This leads to an incorrect and inconsistent final value.

---

#### 🔹 What is the purpose of `AtomicInteger` in this code?

`AtomicInteger` ensures **atomic (indivisible) operations** on an integer variable in a **multi-threaded environment**. It allows safe incrementing without using explicit synchronization mechanisms (like `synchronized` blocks or locks), reducing overhead and complexity.

---

#### 🔹 What thread-safety guarantees does `atomicCounter.incrementAndGet()` provide?

The `incrementAndGet()` method:

* Atomically increments the current value by one.
* Ensures **visibility**, **ordering**, and **atomicity** across threads using **volatile** semantics and **CAS operations**.
* Prevents other threads from observing intermediate (inconsistent) states of the variable.

In other words, all threads always see the latest, correct value after each atomic operation.

---

#### 🔹 In which situations would using a lock be a better choice than an atomic variable?

Locks (e.g., `ReentrantLock`, `synchronized`) are more suitable when:

* Multiple shared variables must be modified together atomically (compound actions).
* The critical section includes complex logic (e.g., condition-based updates).
* You need control over thread access (e.g., tryLock, fairness policies, interruptible locks).
* The operation cannot be expressed as a single atomic instruction.

Atomic variables are only appropriate for **simple, isolated** read-modify-write operations.

---

#### 🔹 Besides `AtomicInteger`, what other data types are available in the `java.util.concurrent.atomic` package?

The `java.util.concurrent.atomic` package provides atomic classes for various data types:

* **Primitive types:**

  * `AtomicBoolean`
  * `AtomicInteger`
  * `AtomicLong`
  * `AtomicIntegerArray`
  * `AtomicLongArray`

* **Reference types:**

  * `AtomicReference<V>`
  * `AtomicReferenceArray<E>`
  * `AtomicStampedReference<V>` (prevents ABA problems)
  * `AtomicMarkableReference<V>`

These classes provide atomic operations on their respective types, enabling safe access and modification in concurrent programs.
---
### Was the multi-threaded implementation always faster than the single-threaded one?

#### Observed Output Summary:

| Implementation              | π Approximation | Time Taken (ms) |
| --------------------------- | --------------- | --------------- |
| Single-threaded             | 3.14164248      | 388             |
| Multi-threaded (14 threads) | 3.1417808       | 103             |

---

#### Answer:

In this run, the **multi-threaded implementation was significantly faster** than the single-threaded version, completing in roughly one quarter of the time.

---

#### Why was the multi-threaded version faster?

* **CPU Parallelism:**
  The multi-threaded version utilizes multiple CPU cores (14 logical threads in this case) to split the computation of random points concurrently. This parallelism reduces the total execution time compared to a single thread doing all the work sequentially.

* **Workload Division:**
  The total number of points (`NUM_POINTS`) is divided among threads, allowing each thread to perform a smaller chunk of the work simultaneously.

* **Reduced Wall-clock Time:**
  Since the tasks run in parallel, the time taken is approximately the time for the slowest thread to finish rather than the sum of all computations sequentially.

---

#### Was it *always* faster? What factors might affect this?

Multi-threading is not guaranteed to always be faster. Several factors may reduce or negate its advantage:

1. **Thread Management Overhead:**
   Creating, scheduling, and synchronizing threads adds overhead. For smaller workloads, this overhead can outweigh the benefits of parallelism.

2. **Contention and Locks:**
   In this implementation, threads update a shared counter protected by a lock (`ReentrantLock`). If contention is high, threads may spend time waiting to acquire the lock, reducing parallel efficiency.

3. **CPU Core Limits:**
   If the number of threads exceeds physical CPU cores (hyperthreading counts as logical cores but may not yield linear speedup), performance gains plateau or even degrade due to context switching.

4. **Memory and Cache Effects:**
   Multi-threading can cause cache invalidation or increased memory traffic, which may slow down execution depending on the CPU architecture.

5. **JVM Warm-up and Garbage Collection:**
   JVM optimizations and garbage collection pauses may affect timing measurements.

---

#### How to mitigate these issues?

* **Minimize Lock Contention:**
  Instead of updating a shared counter inside the critical section each time, accumulate a local count in each thread and update the shared counter *once* after finishing the computation (as your code already does).

* **Use Atomic Variables:**
  Consider `AtomicLong` to reduce lock overhead, especially when frequent updates are needed.

* **Tune Number of Threads:**
  Use a thread pool size matching physical CPU cores, not just logical cores, and experiment with different sizes to find the optimal number.

* **Batch Workload Size:**
  Avoid too small workloads per thread, as overhead dominates. Balance granularity so each thread does enough work.

---
