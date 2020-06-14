package com.fmi.determinant.project.rsa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class which calculates determinant of square matrix
 * which uses thread pool executor with fixed size of threads
 * equal to the size of the matrix.
 *
 */
public class ExecutorPoolCalculator {

	class DeterminantCalculateThread implements Runnable {

		private double[][] matrix;
		private int size;

		public DeterminantCalculateThread(double[][] matrix, int size) {
			this.matrix = matrix;
			this.size = size;
		}

		@Override
		public void run() {
			SubmatrixThreadCalculator subM = new SubmatrixThreadCalculator(matrix, 0, size, null, true, size);
			System.out.println("Thread pool execution result: " + subM.calculateDeterminant(matrix, size, 0, size));
		}
	}

	public void calculateDeterminantWithThreadPool(double[][] matrix, int size) {
		DeterminantCalculateThread th = new DeterminantCalculateThread(matrix, size);

		ExecutorService pool = Executors.newFixedThreadPool(size);
		pool.execute(th);
		pool.shutdown();
	}

}
