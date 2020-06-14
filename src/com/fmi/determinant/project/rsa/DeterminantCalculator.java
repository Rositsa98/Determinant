package com.fmi.determinant.project.rsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.stream.DoubleStream;

/**
 * Class used for calculating determinants by
 * calculating each adjustable amount with proper count of 
 * submatrixthreads.
 *
 */
public class DeterminantCalculator {

	private String outputFileName;
	private int threadCount;
	private boolean isQuiet;

	private int size = 0;

	public DeterminantCalculator(String outputFileName, int threadCount, boolean isQuiet) {
		this.outputFileName = outputFileName;
		this.threadCount = threadCount;
		this.isQuiet = isQuiet;
	}

	double calculateDeterminant(double[][] matrix, int size) {
		Thread[] threads = new Thread[threadCount <= size ? threadCount : size];
		double[] resultArr = new double[threads.length];

		int length = size;
		int threadCount = this.threadCount;
		int chunkSize = length / threadCount;
		int remainder = length % threadCount;

		int threadIndex = 0;
		int end;
		for (int start = 0; start < length; start = end) {
			remainder -= 1;
			if (remainder >= 0) {
				end = Math.min(start + chunkSize + 1, length);
			} else {
				end = Math.min(start + chunkSize, length);
			}

			threads[threadIndex] = new SubmatrixThreadCalculator(matrix, start, end, resultArr, isQuiet, threadIndex,
					size);
			threads[threadIndex].start();
			threadIndex++;
		}

		for (Thread thread : threads) {
			try {
				thread.join(); //waiting for this thread to die
			} catch (InterruptedException e) {
				System.out.println("ERROR: " + e.getMessage() + e.getCause());
			}
		}

		return DoubleStream.of(resultArr).sum();
	}

	public double calculateDeterminantRandomMatrixFill(double[][] matrix, int size)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.size = size;
		double result = calculateDeterminant(matrix, size);

		if (this.outputFileName != null) {
			writeToFile(this.outputFileName, result);
		}

		return result;
	}

	public double calculateDeterminantRandomMatrixFill2(double[][] matrix, int size)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.size = size;
		double result = calculateDeterminant(matrix, size);

		if (this.outputFileName != null) {
			writeToFile(this.outputFileName, result);
		}

		return result;
	}

	public double calculateDeterminantFromFile(String filePath)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		double[][] matrix = null;
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			String line;

			if ((line = br.readLine()) != null) {
				size = Integer.parseInt(line);
			}

			matrix = new double[size][size];
			int row = 0;
			String[] rowValues = null;

			while ((line = br.readLine()) != null) {
				rowValues = line.split("\\s+");

				for (int i = 0; i < size; ++i) {
					matrix[row][i] = Integer.parseInt(rowValues[i]);
				}
				++row;
			}
		}

		double result = calculateDeterminant(matrix, size);
		if (this.outputFileName != null) {
			writeToFile(this.outputFileName, result);
		}
		return result;
	}

	private void writeToFile(String outputFile, double result)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
			writer.write("" + result);
		}
	}

	public void calculateDeterminantWithExecutorPool(double[][] matrix, int size) {
		ExecutorPoolCalculator ex = new ExecutorPoolCalculator();

		ex.calculateDeterminantWithThreadPool(matrix, size);
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public boolean isQuiet() {
		return isQuiet;
	}

	public void setQuiet(boolean isQuiet) {
		this.isQuiet = isQuiet;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
