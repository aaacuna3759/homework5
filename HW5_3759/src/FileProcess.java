import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FileProcess {

	public static final Integer NOT_SPAM = 0, SPAM = 1, ERROR = -1;

	String folderPath;
	File folder;
	List<Map<String, Integer>> termVectors = new ArrayList<Map<String, Integer>>();
	Map<String, Integer> vocabulary = new HashMap<String, Integer>();
	Map<String, Integer> spamVocab = new HashMap<String, Integer>();
	Map<String, Integer> notSpamVocab = new HashMap<String, Integer>();

	// Map <filename, Map<Map<word, count>,class>>
	Map<String, Map<Map<String, Integer>, Integer>> documents = new HashMap<String, Map<Map<String, Integer>, Integer>>();

	List<Character> characters = new ArrayList<Character>();

	FileProcess(String folderPath) {

		this.folderPath = folderPath;
		this.folder = new File(folderPath);

	}

	/**
	 * This function removes all punctuation and symbols from all files in the
	 * folder
	 * 
	 * @return true is succesful, false otherwise
	 */
	void process() {

		// Folder iterator
		for (File file : folder.listFiles()) {
			String fileName = file.getName();
			System.out.println(fileName);

			Map<String, Integer> wordCount = new HashMap<String, Integer>();
			Map<Map<String, Integer>, Integer> classification = new HashMap<Map<String, Integer>, Integer>();
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {

					StringTokenizer tokenizer = new StringTokenizer(line,
							" \t\n\r\f,.:;?![]'->@()/+-\"#\\<*_=&~`{}$%|^");

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
						} else {
							if (notSpamVocab.containsKey(word)) {
								notSpamVocab.put(word, spamVocab.get(word) + 1);
							} else {
								notSpamVocab.put(word, 1);
							}
						}

						// Document index
						if (wordCount.containsKey(word)) {
							wordCount.put(word, wordCount.get(word) + 1);
							System.out.println(fileName + "\tUpdated word: " + word + " - " + wordCount.get(word));
						} else {
							wordCount.put(word, 1);
							System.out.println(fileName + "\tAdded new word: " + word + " - " + wordCount.get(word));
						}
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

		/*
		 * for (char c : characters) { System.out.print(c); }
		 */

		System.out.println(vocabulary);
	}

}
