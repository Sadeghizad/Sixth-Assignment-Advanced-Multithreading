package MonteCarloPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single thread): " + (endTime - startTime) / 1_000_000 + " ms\n");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n", NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (multi‑threaded): " + piWithThreads);
        System.out.println("Time taken (multi‑threaded): " + (endTime - startTime) / 1_000_000 + " ms\n");

        // TODO: After completing the implementation, reflect on the questions in the description of this task in the README file
        //       and include your answers in your report file.
    }

    /**
     * Monte Carlo π approximation using a single thread.
     *
     * @param numPoints number of random (x, y) points to generate
     * @return approximation of π
     */
    public static double estimatePiWithoutThreads(long numPoints) {
        long insideCircle = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (long i = 0; i < numPoints; i++) {
            // generate x and y in the range [‑1, 1]
            double x = random.nextDouble(-1.0, 1.0);
            double y = random.nextDouble(-1.0, 1.0);

            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }

        return 4.0 * insideCircle / numPoints;
    }

    /**
     * Monte Carlo π approximation using multiple threads.
     *
     * @param numPoints  total number of random (x, y) points to generate
     * @param numThreads number of threads to utilise
     * @return approximation of π
     */
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<Long>> tasks = new ArrayList<>(numThreads);

        // Determine how many points each thread should process
        long pointsPerThread = numPoints / numThreads;
        long remainder = numPoints % numThreads; // handle the leftover points

        for (int i = 0; i < numThreads; i++) {
            long pointsForThisThread = pointsPerThread + (i < remainder ? 1 : 0);
            tasks.add(new MonteCarloTask(pointsForThisThread));
        }

        long totalInsideCircle = 0;
        List<Future<Long>> results = executor.invokeAll(tasks);
        for (Future<Long> f : results) {
            totalInsideCircle += f.get();
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);

        return 4.0 * totalInsideCircle / numPoints;
    }

    /**
     * Worker task that counts how many of its assigned points fall inside the unit circle.
     */
    private static class MonteCarloTask implements Callable<Long> {
        private final long iterations;

        MonteCarloTask(long iterations) {
            this.iterations = iterations;
        }

        @Override
        public Long call() {
            long inside = 0;
            ThreadLocalRandom random = ThreadLocalRandom.current();

            for (long i = 0; i < iterations; i++) {
                double x = random.nextDouble(-1.0, 1.0);
                double y = random.nextDouble(-1.0, 1.0);
                if (x * x + y * y <= 1.0) {
                    inside++;
                }
            }
            return inside;
        }
    }
}
