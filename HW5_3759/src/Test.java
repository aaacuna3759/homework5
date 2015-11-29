import java.io.IOException;
import java.util.HashMap;

public class Test {

	public static void main(String[] args) throws IOException {
		FileProcess fileProcess = new FileProcess("src/train", "src/test");
		
		HashMap<String, Integer> fullMap = new HashMap<String, Integer>();
		HashMap<String, Integer> processedMap = new HashMap<String, Integer>();

		// Output csv files for use in KNN classification
		fullMap = fileProcess.fullMap(); // Create map of all words from all emails
		fileProcess.knnCSV(fullMap, "train", "knnTrain.csv"); // Output csv file for training data
		fileProcess.knnCSV(fullMap, "test", "knnTest.csv"); // Output csv file for test data
		System.out.println("Data has been formatted into knnTrain.csv and knnTest.csv for KNN classification use in Matlab.");
		
		// Output csv files for use in KNN classification
		processedMap = fileProcess.processedMap(); // Create map of all words from all emails after pre-processing
		fileProcess.knnCSV(processedMap, "train", "knnTrainPP"); // Output csv file for training data
		fileProcess.knnCSV(processedMap, "test", "knnTestPP"); // Output csv file for test data
		System.out.println("Data has been formatted into knnTrainPP.csv and knnTestPP.csv for KNN classification use in Matlab.");
	}

}
