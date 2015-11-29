import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class FileProcess {

	String trainFolderPath;
	String testFolderPath;
	File trainFolder;
	File testFolder;

	public FileProcess(String trainFolderPath, String testFolderPath) {

		this.trainFolderPath = trainFolderPath;
		this.trainFolder = new File(trainFolderPath);
		this.testFolderPath = testFolderPath;
		this.testFolder = new File(testFolderPath);

	}

	
	/**
	 * This function creates a map of all words in all email messages without
	 * any pre-processing.
	 * 
	 * @return HashMap<String, Integer> fullMap
	 */
	public HashMap<String, Integer> fullMap() {
		
		HashMap<String, Integer> fullMap = new HashMap<String, Integer>();
		for (File file : trainFolder.listFiles()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					while (tokenizer.hasMoreTokens()) {
						String word = tokenizer.nextToken();
						if (fullMap.containsKey(word)) {
							fullMap.put(word, fullMap.get(word) + 1);
						} else {
							fullMap.put(word, 1);
						}
					}
					line = br.readLine();
				}
				br.close(); // Close BufferedReader
			} catch (FileNotFoundException fNFE) {
				fNFE.printStackTrace();
			} catch (IOException iOE) {
				iOE.printStackTrace();
			}
		}
		return fullMap;
		
	}
	
	
	/**
	 * This function removes all punctuation and rare words from all files in the
	 * folder and creates map of all words and how many times they occur in all files
	 * for use with task 3 in homework 5.
	 * 
	 * @return HashMap<String, Integer> processedMap
	 */
	public HashMap<String, Integer> processedMap() {
		
		HashMap<String, Integer> processedMap = new HashMap<String, Integer>(); // Map of all words in all emails with processing
		for (File file : trainFolder.listFiles()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					StringTokenizer tokenizer = new StringTokenizer(line,
							" \t\n\r\f,.:;?![]'->@()/+-\"#\\<*_=&~`{}$%|^");
					while (tokenizer.hasMoreTokens()) {
						String word = tokenizer.nextToken();
						if (processedMap.containsKey(word)) {
							processedMap.put(word, processedMap.get(word) + 1);
						} else {
							processedMap.put(word, 1);
						}
					}
					line = br.readLine();
				}
				br.close(); // Close BufferedReader
			} catch (FileNotFoundException fNFE) {
					fNFE.printStackTrace();
			} catch (IOException iOE) {
					iOE.printStackTrace();
			}
		}
		
		// Remove rare words that occur less than 50 times
		Iterator<HashMap.Entry<String, Integer>> entries = processedMap.entrySet().iterator();
		while (entries.hasNext()) {
			  HashMap.Entry<String, Integer> entry = entries.next();
			  int value = entry.getValue();
			  if(value < 50) {
				  entries.remove();
			  }
		}
		
		return processedMap;
		
	}

	
	/**
	 * This function outputs a map into CSV format for use in KNN classification.
	 * 
	 * @throws IOException 
	 * @param map is the map of all words and total counts
	 * @param folderPath is the folder where the messages are located
	 * @param output is the name of the file that will be created
	 */
	public void knnCSV(HashMap<String, Integer> map, String folderPath, String output) throws IOException {
		
		FileWriter fw = null;
		File[] files = null;
		
		// Create array of files to use and file to write to
		if(folderPath.equals("train")) {
			files = trainFolder.listFiles();
			fw = new FileWriter("src/" + output, true);
		}
		else if(folderPath.equals("test")) {
			files = testFolder.listFiles();
			fw = new FileWriter("src/" + output, true);
		}
		
		// Create a temporary map of all words and counts in each individual file
		for (File file : files) {
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					while (tokenizer.hasMoreTokens()) {
						String word = tokenizer.nextToken();
						if (tempMap.containsKey(word)) {
							tempMap.put(word, tempMap.get(word) + 1);
						} else {
							tempMap.put(word, 1);
						}
					}
					line = br.readLine();
				}
				br.close(); // Close BufferedReader
			} catch (FileNotFoundException fNFE) {
					fNFE.printStackTrace();
			} catch (IOException iOE) {
					iOE.printStackTrace();
			}
			
			// Print value of words in tempMap for each word in fullMap separated by commas
			Iterator<HashMap.Entry<String, Integer>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				  HashMap.Entry<String, Integer> entry = entries.next();
				  String word = entry.getKey();
				  if(tempMap.containsKey(word)) {
					  fw.write(tempMap.get(word).toString());
				  }
				  else {
					  fw.write("0");
				  }
				  if(entries.hasNext()) {
					  fw.write(",");
				  }
				  else {
					  fw.write("\r\n");
				  }
			}
		}
		fw.close();
		
	}
}
