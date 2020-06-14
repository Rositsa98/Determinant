package com.fmi.determinant.project.rsa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

	/**
	 * Executing parameters
	 */
	private static final String OUTPUT_FILE_PARAM = "-o";
	private static final String INPUT_FILE_PARAM = "-i";
	private static final String TASKS_PARAM_SHORT = "-t";
	private static final String TASKS_PARAM = "-tasks";
	private static final String QUIET_PARAM_SHORT = "-q";
	private static final String QUIET_PARAM = "-quiet";
	private static final String MATRIX_SIZE_PARAM = "-n";

	private static DeterminantCalculator determinantCalculator;

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		String[] argss = { "-n", "4", "-t", "3", "-quiet" };
		List<String> arguments = Arrays.asList(argss);

		String outputFileName = null;
		int threadCount = 1; // initial, default size of threads
		boolean isQuiet = false;

		if (arguments.indexOf(OUTPUT_FILE_PARAM) != -1) { // output file

			int argIdx = arguments.indexOf(OUTPUT_FILE_PARAM) + 1;
			outputFileName = arguments.get(argIdx);
		}

		if (arguments.indexOf(TASKS_PARAM_SHORT) != -1 || arguments.indexOf(TASKS_PARAM) != -1) {

			int argIdx = Math.max(arguments.indexOf(TASKS_PARAM_SHORT), arguments.indexOf(TASKS_PARAM)) + 1;
			threadCount = Integer.parseInt(arguments.get(argIdx));
		}

		if (arguments.indexOf(QUIET_PARAM_SHORT) != -1 || arguments.indexOf(QUIET_PARAM) != -1) {
			isQuiet = true;
		}

		determinantCalculator = new DeterminantCalculator(outputFileName, threadCount, isQuiet);

		if (arguments.indexOf(INPUT_FILE_PARAM) != -1 && arguments.indexOf(MATRIX_SIZE_PARAM) != -1) {
			System.out.println("Both case:");
			int argIdx1 = arguments.indexOf(INPUT_FILE_PARAM) + 1;
			String fileName = arguments.get(argIdx1);

			int argIdx2 = arguments.indexOf(MATRIX_SIZE_PARAM) + 1;
			int size = Integer.parseInt(arguments.get(argIdx2));

			double[][] matrix = createRandomlyFilledMatrix(size);

			long timeCalculateDeterminantWithRandomNumbers = calculateDeterminantThreadLocalRandomNumbers(matrix, size);

			long timeCalculateDeterminantFromFile = calculateDeterminantFromFile(fileName);

			System.out.println("Time to calculate random generated matrix determinant is: "
					+ timeCalculateDeterminantWithRandomNumbers);
			System.out.println(
					"Time to calculate determinant of matrix read from file is: " + timeCalculateDeterminantFromFile);

			System.out.println("Difference of: "
					+ Math.abs(timeCalculateDeterminantWithRandomNumbers - timeCalculateDeterminantFromFile)
					+ " is found!");

		}

		else if (arguments.indexOf(INPUT_FILE_PARAM) != -1) {
			int argIdx = arguments.indexOf(INPUT_FILE_PARAM) + 1;
			String fileName = arguments.get(argIdx);

			calculateDeterminantFromFile(fileName);

		} else if (arguments.indexOf(MATRIX_SIZE_PARAM) != -1) {
			int argIdx = arguments.indexOf(MATRIX_SIZE_PARAM) + 1;
			int size = Integer.parseInt(arguments.get(argIdx));

			double[][] matrix = createRandomlyFilledMatrix(size);
			double[][] matrix2 = createRandomlyFilledMatrix2(size);

			calculateDeterminantThreadLocalRandomNumbers(matrix, size);

			calculateDeterminantRandomNumbers(matrix2, size);

			calculateDeterminantExecutorPool(matrix, size);

			calculateDeterminantWithSingleProcess(matrix, size);
		}
	}

	/*
	 * (Non-java doc) 
	 * Method used to calculate determinant, reading it from file
	 * with syntax:
	 * 
	 * ========= 
	 * 	n
	 *  a11 ... a1n
	 *  ... .... 
	 *  an1 ... ann 
	 *  ========== 
	 * as n is considered
	 * size of matrix
	 * 
	 */
	static long calculateDeterminantFromFile(String fileName)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		long start = System.currentTimeMillis();
		double result = determinantCalculator.calculateDeterminantFromFile(fileName);
		long end = System.currentTimeMillis();
		long timeElapsed = end - start;

		System.out.println("Total execution time for determinanet from file (millis): " + timeElapsed);
		System.out.println("Result of calculation is: " + result);

		return timeElapsed;
	}

	/*
	 * (Non-java doc) 
	 * Method used to calculate determinant filling matrix with
	 * random numbers using thread local number for generating them
	 */
	static long calculateDeterminantThreadLocalRandomNumbers(double[][] matrix, int size)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		long start = System.currentTimeMillis();
		double result = determinantCalculator.calculateDeterminantRandomMatrixFill(matrix, size);
		long end = System.currentTimeMillis();
		long timeElapsed = end - start;

		System.out.println("Total execution time for random generated determinant [with ThreadLocalRandom] (millis): "
				+ timeElapsed);
		System.out.println("Result of calculation is: " + result);

		return timeElapsed;
	}

	/*
	 * (Non-java doc) 
	 * Method used to calculate determinant filling matrix with
	 * random numbers using Math.random() for generating them
	 */
	static long calculateDeterminantRandomNumbers(double[][] matrix, int size)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		long start = System.currentTimeMillis();
		double result = determinantCalculator.calculateDeterminantRandomMatrixFill2(matrix, size);
		long end = System.currentTimeMillis();
		long timeElapsed = end - start;

		System.out.println(
				"Total execution time for random generated determinant [with Math.random] (millis): " + timeElapsed);
		System.out.println("Result of calculation is: " + result);

		return timeElapsed;
	}

	/*
	 * (Non-java doc) 
	 * Method used to calculate determinant filling matrix with
	 * random numbers using thread local number for generating them.
	 * This method is more specific because it shows the way thread pool is making
	 * the speed with fixed size of threads.
	 */
	static long calculateDeterminantExecutorPool(double[][] matrix, int size) {
		long start = System.currentTimeMillis();
		determinantCalculator.calculateDeterminantWithExecutorPool(matrix, size);
		long end = System.currentTimeMillis();
		long timeElapsed = end - start;

		System.out.println(
				"Total execution time for random generated determinant [with Executor pool] (millis): " + timeElapsed);

		return timeElapsed;
	}

	/*
	 * (Non-java doc)
	 * Method ethalon - this method calculated determinant of matrix with single thread/process.
	 * 
	 */
	static double calculateDeterminantWithSingleProcess(double[][] matrix, int size)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		determinantCalculator.setThreadCount(1);
		long start = System.currentTimeMillis();
		double result = determinantCalculator.calculateDeterminantRandomMatrixFill(matrix, size);
		long end = System.currentTimeMillis();
		long timeElapsed = end - start;

		System.out.println(
				"Total execution time for random generated determinant [with SINGLE PROCESS] (millis): " + timeElapsed);
		System.out.println("Result of calculation is: " + result);

		return timeElapsed;
	}

	/*
	 * Helper method generating matrix
	 */
	static double[][] createRandomlyFilledMatrix(int size) {
		double[][] matrix = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = ThreadLocalRandom.current().nextDouble(0, 40);
			}
		}
		return matrix;
	}

	/*
	 * Helper method generating matrix
	 */
	static double[][] createRandomlyFilledMatrix2(int size) {
		double[][] matrix = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = Math.random() * 40;
			}
		}

		return matrix;
	}

}
