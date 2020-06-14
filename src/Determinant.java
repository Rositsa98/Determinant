
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;


public class Determinant {
	

	class SubmatrixThread extends Thread {
		
		private int start;
		private int end;
		private double[] resultArr;
		private double[][] matrix;
		private int threadNumber;
		private boolean isQuiet;

		public SubmatrixThread(double[][] matrix, int start, int end, double[] resultArr, boolean isQuiet) {
			this.matrix = matrix;
			this.start = start;
			this.end = end;
			this.resultArr = resultArr;
			this.isQuiet = isQuiet;
		}
		
		public SubmatrixThread(double[][] matrix, int start, int end, double[] resultArr, boolean isQuiet, int threadNumber) {
			this(matrix, start, end, resultArr, isQuiet);
			this.threadNumber = threadNumber;
		}
		
		@Override
		public void run() {
			if(!isQuiet) {
				long startTime = System.currentTimeMillis();
				System.out.println("Thread-" + Thread.currentThread().getName() + " started.");
				resultArr[threadNumber] = deepCalculate(matrix, size, start, end);
				long endTime = System.currentTimeMillis();
				System.out.println("Thread-" + Thread.currentThread().getName() + " stopped.");
				System.out.println("Thread-" + Thread.currentThread().getName() + " execution time was (millis) " +  (endTime - startTime));				
			} else {
				resultArr[threadNumber] = deepCalculate(matrix, size, start, end);
			}
		}
		
		private double deepCalculate(double[][] matrix, int size, int start, int end) {
			if (size == 1) {
	    		return matrix[0][0];
	    	} else if (size == 2) {
	    		return matrix[0][0]*matrix[1][1] - matrix[1][0]*matrix[0][1];
	    	} else {
	    		double result = 0;
	    		for(int current = start; current < end; ++current) {
	    			double[][] submatrix = generateSubmatrix(matrix, size, current);
	    			int cofactor = (int)Math.pow(-1, current + 2);
	    			result += cofactor * matrix[0][current] * deepCalculate(submatrix, size-1, 0, submatrix.length - 1);
	    		}
	    		return result;	    			
    		}
		}
		

		private double[][] generateSubmatrix(double[][] matrix, int size, int column) {
			double[][] subMatrix = new double[size-1][size-1];
			
			for(int i = 1; i < size; ++i) {
				int subMatrixColumn = 0;
				for(int j = 0; j < size; ++j) {
					if(j != column) {
						subMatrix[i-1][subMatrixColumn] = matrix[i][j];	
						++subMatrixColumn;
					}
				}
			}
			
			return subMatrix;
		}
	}
	
	
	private int size = 0;
	private int nThreads;
	private String outputFile;
	private boolean isQuiet;
	
	public Determinant(int nThreads, String outputFile, boolean isQuiet) {
		this.nThreads = nThreads;
		this.outputFile = outputFile;
		this.isQuiet = isQuiet;
	}
	
	public double fromFile(String filePath) throws FileNotFoundException, IOException {
		double[][] matrix = null;
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
		    String line;
		    
		    if((line = br.readLine()) != null) {
		    	size = Integer.parseInt(line);
		    }
		    
		    matrix = new double[size][size];
		    int row = 0;
		    String[] rowValues = null;
		    
		    while ((line = br.readLine()) != null) {
		    	rowValues = line.split("\\s+");
		    	
		    	for(int i = 0; i < size; ++i) {
		    		matrix[row][i] = Integer.parseInt(rowValues[i]);
		    	}
		    	++row;
		    }
		}
		
		double result = calculate(matrix, size);
		if(outputFile != null) {
			writeToFile(outputFile, result);
		}
		return result;
	}
	
	private void writeToFile(String outputFile, double result) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
			writer.write("" + result);
		}
	}

	public double randomized(int size) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.size = size;
		double[][] matrix = new double[size][size];
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				matrix[i][j] = ThreadLocalRandom.current().nextDouble(0, 30);
			}
		}
		
		double result = calculate(matrix, size);
		if(outputFile != null) {
			writeToFile(outputFile, result);
		}
		return result;
	}
	
	
    public double calculate(double[][] matrix, int size) {
    		Thread[] threads = new Thread[nThreads <= size ? nThreads : size];
    		double[] resultArr = new double[threads.length];
    		
    		int len = size;
    		int threadCount = nThreads;
    		int chunkSize = len / threadCount;
    		int remainder = len % threadCount;
    		
    		int threadIndex = 0;
    		int end;
    		for (int start = 0; start < len; start = end) {
    			  remainder -= 1;
    			  if(remainder >= 0) {
    				  end = Math.min(start + chunkSize + 1, len);
    			   } else {
    				  end = Math.min(start + chunkSize, len);				   
    			   }

    			 threads[threadIndex] = new SubmatrixThread(matrix, start, end, resultArr, isQuiet, threadIndex); 
    			 threads[threadIndex].start();
    			 threadIndex++;
    		  }
    		
    		for(Thread thread: threads) {
    			try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
        	return DoubleStream.of(resultArr).sum();
    }

}