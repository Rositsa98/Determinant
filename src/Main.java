
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String[] argss = {"-t", "1","-n","8"};
		List<String> listArgs = Arrays.asList(argss);
		String outputFile = null;
		int threadCount = 1;
		boolean quiet = false;
		
		if(listArgs.indexOf("-o") != -1) {
			int index = listArgs.indexOf("-o");
			outputFile = listArgs.get(index + 1);
		}
		
		if(listArgs.indexOf("-t") != -1 || listArgs.indexOf("-tasks") != -1) {
			int index = Math.max(listArgs.indexOf("-t"), listArgs.indexOf("-tasks"));
			threadCount = Integer.parseInt(listArgs.get(index + 1));
		}
		
		if(listArgs.indexOf("-q") != -1 || listArgs.indexOf("-quiet") != -1) {
			quiet = true;
		}
		
		Determinant det = new Determinant(threadCount, outputFile, quiet);
		
		
		if(listArgs.indexOf("-i") != -1 && listArgs.indexOf("-n") != -1) {
			System.out.println("You can't read from a file and generate a random matrix at the same time!");
			return;
		} else if(listArgs.indexOf("-i") != -1) {
			int index = listArgs.indexOf("-i");
			String filename = listArgs.get(index + 1);
			
			try {
				
				long start = System.currentTimeMillis();
				double result = det.fromFile(filename);
				long end = System.currentTimeMillis();
				long timeElapsed = end - start;
				
				
				System.out.println("Total execution time for current run (millis): " + timeElapsed);
				System.out.println("Result: " + result);
							
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if(listArgs.indexOf("-n") != -1) {
			int index = listArgs.indexOf("-n");
			int matrixSize = Integer.parseInt(listArgs.get(index + 1));
			
			
			long start = System.currentTimeMillis();
			double result = det.randomized(matrixSize);
			long end = System.currentTimeMillis();
			long timeElapsed = end - start;
			
			
			System.out.println("Total execution time for current run (millis): " + timeElapsed);
			System.out.println("Result: " + result);
		}
		
	}

}