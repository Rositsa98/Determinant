package com.fmi.determinant.project.rsa;

/**
 * Class calculating determinant of square matrix, which
 * creates a thread for each adjustable amount if manageable to do so.
 * If there are less threads than adjustable amounts, 1 thread takes care for more
 * than one adjustable amounts.
 *
 */
public class SubmatrixThreadCalculator extends Thread {

	private int start;
	private int end;
	private double[] resultArr;
	private double[][] matrix;
	private int threadNumber;
	private boolean isQuiet;

	private int size;

	public SubmatrixThreadCalculator(double[][] matrix, int start, int end, double[] resultArr, boolean isQuiet,
			int size) {
		this.matrix = matrix;
		this.start = start;
		this.end = end;
		this.resultArr = resultArr;
		this.isQuiet = isQuiet;

		this.size = size;
	}

	public SubmatrixThreadCalculator(double[][] matrix, int start, int end, double[] resultArr, boolean isQuiet,
			int threadNumber, int size) {
		this(matrix, start, end, resultArr, isQuiet, size);
		this.threadNumber = threadNumber;
	}

	@Override
	public void run() {
		if (!isQuiet) {
			long startTime = System.currentTimeMillis();
			System.out.println("Thread-" + Thread.currentThread().getName() + " started.");
			resultArr[threadNumber] = calculateDeterminant(matrix, size, start, end);
			long endTime = System.currentTimeMillis();
			System.out.println("Thread-" + Thread.currentThread().getName() + " stopped.");
			System.out.println("Thread-" + Thread.currentThread().getName() + " execution time was (millis) "
					+ (endTime - startTime));
		} else {
			resultArr[threadNumber] = calculateDeterminant(matrix, size, start, end);
		}
	}

	double calculateDeterminant(double[][] matrix, int n, int start, int end) {

		if (n < 1) {
			// could throw error
			return -1;
		}

		if (n == 1) {
			return matrix[0][0];
		} else if (n == 2) {
			return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
		} else if (n == 3) {
			return calculateDeterminentTrianglesMethod(matrix);
		} else {
			double result = 0;
			for (int colIndex = start; colIndex < end; colIndex++) {
				double[][] submatrix = generateSmallerMatrix(matrix, n, colIndex);
				int cofactor = (int) Math.pow(-1, colIndex + 2);
				result += cofactor * matrix[0][colIndex]
						* calculateDeterminant(submatrix, n - 1, 0, submatrix.length - 1);
			}
			return result;
		}
	}

	// calculate 3x3 matrix by the triangle method
	double calculateDeterminentTrianglesMethod(double[][] matrix) {
		return matrix[0][0] * matrix[1][1] * matrix[2][2] + matrix[0][1] * matrix[1][0] * matrix[2][2]
				+ matrix[0][0] * matrix[2][1] * matrix[1][2] - matrix[0][2] * matrix[1][1] * matrix[2][0]
				- matrix[0][1] * matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][1] * matrix[0][2];
	}

	/*
	 * (Non-java doc)
	 * Method generating submatrix by unfolding by the 0 row.
	 */
	double[][] generateSmallerMatrix(double[][] matrix, int n, int colIndex) {
		double[][] subMatrix = new double[n - 1][n - 1];
		for (int i = 1; i < n; ++i) {
			int subMatrixColumn = 0;
			for (int j = 0; j < n; ++j) {
				if (j != colIndex) {
					subMatrix[i - 1][subMatrixColumn] = matrix[i][j];
					++subMatrixColumn;
				}
			}
		}
		return subMatrix;
	}

}
