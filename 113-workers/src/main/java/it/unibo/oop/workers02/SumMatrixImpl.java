package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Multithread matrix sum.
 */
public final class SumMatrixImpl implements SumMatrix {

    private final int nthread;

    /**
     * Constructor.
     * 
     * @param nthread num of thread
     */
    public SumMatrixImpl(final int nthread) {
        this.nthread = nthread;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int threadLines = matrix.length % nthread + matrix.length / nthread;
        final List<Future<Double>> futures = new ArrayList<>();

        double sum = 0;

        try (ExecutorService service = Executors.newFixedThreadPool(nthread)) {
            for (int i = 0; i < nthread; i++) {
                final int start = i * threadLines;
                final int end = Math.min(start + threadLines, matrix.length);
                futures.add(service.submit(() -> {
                    System.out.println("Calcolo somma da riga " + start + " a riga " + end); //NOPMD just an excercise
                    double tempSum = 0;
                    for (int row = start; row < end; row++) {
                        for (int col = 0; col < matrix[row].length; col++) {
                            tempSum += matrix[row][col];
                        }
                    }
                    return tempSum;
                }));
            }

            for (final Future<Double> future : futures) {
                try {
                    sum += future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace(); //NOPMD just an excercise
                }
            }
        }

        return sum;
    }
}
