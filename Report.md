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
