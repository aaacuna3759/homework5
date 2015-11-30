import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FileProcess {

	public static final boolean DEBUG = false;

	public static final Integer NOT_SPAM = 0, SPAM = 1, ERROR = -1;
	public static final Integer[] classifications = { NOT_SPAM, SPAM, ERROR };

	String folderPath;
	File folder;
	private Integer totalWordCount;
	private Map<Integer, Integer> classWordCount = new HashMap<Integer, Integer>();
	// List<Map<String, Integer>> termVectors = new ArrayList<Map<String,
	// Integer>>();
	Map<String, Integer> vocabulary = new HashMap<String, Integer>();
	Map<String, Integer> spamVocab = new HashMap<String, Integer>();
	Map<String, Integer> notSpamVocab = new HashMap<String, Integer>();

	// Map <filename, Map<Map<word, count>,class>>
	Map<String, Map<Map<String, Integer>, Integer>> documents = new HashMap<String, Map<Map<String, Integer>, Integer>>();

	List<Character> characters = new ArrayList<Character>();

	FileProcess(String folderPath) {

		this.folderPath = folderPath;
		this.folder = new File(folderPath);
		totalWordCount = 0;

		for (Integer type : classifications) {
			classWordCount.put(type, 0);
		}

	}

	/**
	 * This function removes all punctuation and symbols from all files in the
	 * folder
	 * 
	 * @return true is succesful, false otherwise
	 */
	void process(boolean process, Integer frequency) {

		// Folder iterator
		for (File file : folder.listFiles()) {
			String fileName = file.getName();

			if (DEBUG) {
				System.out.println("\t" + fileName);
			}

			Map<String, Integer> wordCount = new HashMap<String, Integer>();
			Map<Map<String, Integer>, Integer> classification = new HashMap<Map<String, Integer>, Integer>();
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					StringTokenizer tokenizer;
					if (process) {
						tokenizer = new StringTokenizer(line, " \t\n\r\f,.:;?![]'->@()/+-\"#\\<*_=&~`{}$%|^0123456789");
					} else {
						tokenizer = new StringTokenizer(line);
					}
					while (tokenizer.hasMoreTokens()) {
						String word = tokenizer.nextToken();
						if (vocabulary.containsKey(word)) {
							vocabulary.put(word, vocabulary.get(word) + 1);
							// System.out.println(fileName + "\tUpdated word: "
							// + word);
						} else {
							vocabulary.put(word, 1);
							// System.out.println(fileName + "\tAdded new word:
							// " + word);
						}

						// for (int i = 0; i < word.length(); i++) {
						// if (!characters.contains(word.charAt(i))) {
						// characters.add(word.charAt(i));
						// }
						// }

						// Add to the spam or not spam vocab;
						if (fileName.substring(0, 2).equals("sp")) {
							if (spamVocab.containsKey(word)) {
								spamVocab.put(word, spamVocab.get(word) + 1);
							} else {
								spamVocab.put(word, 1);
							}
							classWordCount.put(SPAM, classWordCount.get(SPAM) + 1);
						} else {
							if (notSpamVocab.containsKey(word)) {
								notSpamVocab.put(word, notSpamVocab.get(word) + 1);
							} else {
								notSpamVocab.put(word, 1);
							}
							classWordCount.put(NOT_SPAM, classWordCount.get(NOT_SPAM) + 1);
						}

						// Document index
						if (wordCount.containsKey(word)) {
							wordCount.put(word, wordCount.get(word) + 1);
							if (DEBUG) {
								System.out.println(fileName + "\tUpdated word: " + word + " - " + wordCount.get(word));
							}
						} else {
							wordCount.put(word, 1);
							if (DEBUG) {
								System.out
										.println(fileName + "\tAdded new word: " + word + " - " + wordCount.get(word));
							}
						}

						totalWordCount++;

					}

					// load next line
					line = br.readLine();
				}
			} catch (FileNotFoundException fNFE) {
				fNFE.printStackTrace();
			} catch (IOException iOE) {
				iOE.printStackTrace();
			}

			// classify
			if (fileName.substring(0, 2).equals("sp")) {
				classification.put(wordCount, SPAM);
			} else {
				classification.put(wordCount, NOT_SPAM);
			}

			documents.put(fileName, classification);
		}

		if (DEBUG) {
			for (char c : characters) {
				System.out.print(c);
			}
		}

		if (DEBUG) {
			if (process) {
				System.out.println("Eliminated " + eliminateWordByFreq(frequency) + " words");
			} else {
				System.out.println("Eliminated " + eliminateWordByFreq(0) + " words");
			}
			System.out.println("VOCABULARY:\t\t" + vocabulary);
			System.out.println("SPAM VOCABULARY:\t" + spamVocab);
			System.out.println("REGULAR VOCABULARY:\t" + notSpamVocab);
		}
	}

	/**
	 * 
	 * Anything word with a frequency of less than frequency, is removed.
	 * 
	 * @param frequency
	 *            - The threshold for word frequency
	 * @return the number of words eliminated
	 */
	public Integer eliminateWordByFreq(Integer frequency) {

		Integer eliminateCount = 0;

		for (String word : vocabulary.keySet().toArray(new String[vocabulary.keySet().size()])) {
			if (vocabulary.get(word) < frequency) {
				vocabulary.remove(word);
				spamVocab.remove(word);
				notSpamVocab.remove(word);
				eliminateCount++;
			}
		}

		return eliminateCount;
	}

	public Map<String, Integer> getVocabulary() {
		return vocabulary;
	}

	public Map<String, Integer> getSpamVocab() {
		return spamVocab;
	}

	public Map<String, Integer> getNotSpamVocab() {
		return notSpamVocab;
	}

	public Map<String, Map<Map<String, Integer>, Integer>> getDocuments() {
		return documents;
	}

	public Integer getTotalWordCount() {
		return totalWordCount;
	}

	public Map<Integer, Integer> getClassWordCount() {
		return classWordCount;
	}

	/**
	 * This function outputs a map into CSV format for use in KNN
	 * classification.
	 * 
	 * @throws IOException
	 * @param map
	 *            is the map of all words and total counts
	 * @param folderPath
	 *            is the folder where the messages are located
	 * @param output
	 *            is the name of the file that will be created
	 */
	public void knnCSV(Map<String, Integer> map, String output) throws IOException {

		FileWriter fw = null;
		File[] files = null;

		files = folder.listFiles();
		fw = new FileWriter("src/" + output, true);

		// Create a temporary map of all words and counts in each individual
		// file
		for (File file : files) {
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			try {
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
			} catch (FileNotFoundException fNFE) {
				fNFE.printStackTrace();
			} catch (IOException iOE) {
				iOE.printStackTrace();
			} finally {
				br.close(); // Close BufferedReader
			}

			// Print value of words in tempMap for each word in fullMap
			// separated by commas
			Iterator<HashMap.Entry<String, Integer>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				HashMap.Entry<String, Integer> entry = entries.next();
				String word = entry.getKey();
				if (tempMap.containsKey(word)) {
					fw.write(tempMap.get(word).toString());
				} else {
					fw.write("0");
				}
				if (entries.hasNext()) {
					fw.write(",");
				} else {
					fw.write("\r\n");
				}
			}
		}
		fw.close();

	}
}
