package MonteCarloPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws InterruptedException {
        // Single‑threaded version
        System.out.println("Single threaded calculation started:");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single thread): " + (endTime - startTime) / 1_000_000 + " ms\n");

        // Multi‑threaded version using Runnable + explicit lock
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)%n", NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (multi-threaded): " + piWithThreads);
        System.out.println("Time taken (multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms\n");
    }

    /**
     * Monte Carlo π approximation using a single thread.
     */
    public static double estimatePiWithoutThreads(long numPoints) {
        long insideCircle = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (long i = 0; i < numPoints; i++) {
            double x = random.nextDouble(-1.0, 1.0);
            double y = random.nextDouble(-1.0, 1.0);
            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }
        return 4.0 * insideCircle / numPoints;
    }

    /**
     * Monte Carlo π approximation using multiple threads with <strong>Runnable</strong> tasks and a {@link ReentrantLock}
     * to protect a shared counter.
     */

    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Shared mutable state protected by the lock
        final long[] insideCircle = new long[1];
        ReentrantLock lock = new ReentrantLock();

        long pointsPerThread = numPoints / numThreads;
        long remainder = numPoints % numThreads;

        for (int i = 0; i < numThreads; i++) {
            long iterations = pointsPerThread + (i < remainder ? 1 : 0);
            executor.execute(new PiTask(iterations, insideCircle, lock));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return 4.0 * insideCircle[0] / numPoints;
    }
}
class PiTask implements Runnable {
    private final long iterations;
    private final long[] insideCircle;
    private final ReentrantLock lock;

    public PiTask(long iterations, long[] insideCircle, ReentrantLock lock) {
        this.iterations = iterations;
        this.insideCircle = insideCircle;
        this.lock = lock;
    }

    public void run() {
        long localCount = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (long j = 0; j < iterations; j++) {
            double x = random.nextDouble(-1.0, 1.0);
            double y = random.nextDouble(-1.0, 1.0);
            if (x * x + y * y <= 1.0) {
                localCount++;
            }
        }
        lock.lock();
        try {
            insideCircle[0] += localCount;
        } finally {
            lock.unlock();
        }
    }
}
