import java.util.HashMap;
import java.util.Map;

public class NaiveBayes {

	// Flag that will print messages for debugging
	public static final boolean DEBUG = false;

	FileProcess testSet, trainingSet;

	boolean process = false;
	// Map<class, count>
	Map<Integer, Integer> classificationCounts = new HashMap<Integer, Integer>();
	Map<Integer, Double> classificationProbability = new HashMap<Integer, Double>();

	Map<Integer, Map<String, Double>> classWordProbability = new HashMap<Integer, Map<String, Double>>();
	Map<String, Integer> predictedClassifications = new HashMap<String, Integer>();

	public NaiveBayes(String testSetDirectory, String trainingSetDirectory, boolean process, Integer freqThreshold) {
		this.process = process;
		testSet = new FileProcess(testSetDirectory);
		trainingSet = new FileProcess(trainingSetDirectory);

		testSet.process(process, freqThreshold);
		trainingSet.process(process, freqThreshold);

		// building class counts
		for (Integer type : FileProcess.classifications) {
			classificationCounts.put(type, getClassCount(type));
		}

		// Building class probability
		for (Integer type : FileProcess.classifications) {
			classificationProbability.put(type, getClassProbability(type));
		}

		buildClassWordProbability();

		if (DEBUG) {
			System.out.println(classificationCounts);
			System.out.println(classificationProbability);
			System.out.println(trainingSet.getTotalWordCount());
			System.out.println(trainingSet.getClassWordCount());
			System.out.println(classWordProbability.get(FileProcess.SPAM));
			System.out.println(classWordProbability.get(FileProcess.NOT_SPAM));
		}

	}

	public Integer getClassCount(Integer classification) {

		Integer count = 0;

		for (Map<Map<String, Integer>, Integer> values : trainingSet.getDocuments().values()) {
			for (Integer type : values.values()) {
				if (type == classification) {
					count++;
				}
			}
		}
		return count;
	}

	public Double getClassProbability(Integer classification) {

		Integer totalCount = 0;
		for (Integer count : classificationCounts.values()) {
			totalCount += count;
		}

		return classificationCounts.get(classification) / (double) totalCount;
	}

	public void buildClassWordProbability() {

		// For ease of access
		// Integer vocabCount = trainingSet.getTotalWordCount();
		Map<Integer, Integer> totalClassWords = trainingSet.getClassWordCount();
		Map<String, Double> spamProb = new HashMap<String, Double>();
		Map<String, Double> nSpamProb = new HashMap<String, Double>();
		// for(Integer type: FileProcess.classifications){

		// Building Probability Set
		for (String word : trainingSet.getVocabulary().keySet()) {
			// SPAM PROBABILITIES
			if (trainingSet.getSpamVocab().containsKey(word)) {

				// For readability this is longer but with shorter method calls.
				Integer wordSpamCount = trainingSet.getSpamVocab().get(word);

				Double wordProb = (wordSpamCount + 1)
						/ (double) (totalClassWords.get(FileProcess.SPAM) + trainingSet.getVocabulary().size());
				spamProb.put(word, wordProb);

				if (spamProb.get(word) == 0.0) {
					System.out.println(word + " spam is 0");
				}

			} else {
				Double wordProb = 1
						/ (double) (totalClassWords.get(FileProcess.SPAM) + trainingSet.getVocabulary().size());
				spamProb.put(word, wordProb);
			}
			if (trainingSet.getNotSpamVocab().containsKey(word)) {

				// For readability this is longer but with shorter method calls.
				Integer wordNSpamCount = trainingSet.getNotSpamVocab().get(word);

				Double wordProb = (wordNSpamCount + 1)
						/ (double) (totalClassWords.get(FileProcess.NOT_SPAM) + trainingSet.getVocabulary().size());
				nSpamProb.put(word, wordProb);

				if (nSpamProb.get(word) == 0.0) {
					System.out.println(word + " not spam is 0");
				}
			}

		}

		classWordProbability.put(FileProcess.SPAM, spamProb);
		classWordProbability.put(FileProcess.NOT_SPAM, nSpamProb);

	}

	public Integer logClassify(Map<String, Integer> testPoint) {

		Integer predictedClass = FileProcess.ERROR;

		// Probability of email being SPAM, at the end greatest wins
		// Double spamProb =
		// Math.log(classificationProbability.get(FileProcess.SPAM));
		Double spamProb = 0.0;
		// Double nSpamProb =
		// Math.log(classificationProbability.get(FileProcess.NOT_SPAM));
		Double nSpamProb = 0.0;

		for (String word : testPoint.keySet()) {
			// SPAM Probability building
			if (classWordProbability.get(FileProcess.SPAM).containsKey(word)) {
				spamProb += Math.log(Math.pow(classWordProbability.get(FileProcess.SPAM).get(word)
						* classificationProbability.get(FileProcess.SPAM), testPoint.get(word)));
			}

			// NOT_SPAM Probability building
			if (classWordProbability.get(FileProcess.NOT_SPAM).containsKey(word)) {
				nSpamProb += Math.log(Math.pow(classWordProbability.get(FileProcess.NOT_SPAM).get(word)
						* classificationProbability.get(FileProcess.NOT_SPAM), testPoint.get(word)));
			}
		}

		if (spamProb > nSpamProb) {
			predictedClass = FileProcess.SPAM;
		} else {
			predictedClass = FileProcess.NOT_SPAM;
		}

		return predictedClass;
	}

	public Integer classify(Map<String, Integer> testPoint) {

		Integer predictedClass = FileProcess.ERROR;

		// Probability of email being SPAM, at the end greatest wins
		Double spamProb = classificationProbability.get(FileProcess.SPAM);
		Double nSpamProb = classificationProbability.get(FileProcess.NOT_SPAM);

		for (String word : testPoint.keySet()) {
			// SPAM Probability building
			if (classWordProbability.get(FileProcess.SPAM).containsKey(word)) {
				if (Math.pow(classWordProbability.get(FileProcess.SPAM).get(word), testPoint.get(word)) == 0.0) {
					System.out.print("SPAM " + word + " is 0.0 ");
				} else {
					spamProb *= Math.pow(classWordProbability.get(FileProcess.SPAM).get(word), testPoint.get(word));
					System.out.println("SPAM: " + spamProb);
				}
			}
			// NOT_SPAM Probability building
			if (classWordProbability.get(FileProcess.NOT_SPAM).containsKey(word)) {
				if (Math.pow(classWordProbability.get(FileProcess.NOT_SPAM).get(word), testPoint.get(word)) == 0.0) {
					System.out.print("NOT SPAM " + word + " is 0.0 ");
				} else {
					nSpamProb *= Math.pow(classWordProbability.get(FileProcess.NOT_SPAM).get(word),
							testPoint.get(word));
					System.out.println("NOT SPAM" + nSpamProb);
				}
			}
		}

		if (DEBUG) {
			System.out.println(spamProb + ", " + nSpamProb);
		}

		if (spamProb >= nSpamProb) {
			predictedClass = FileProcess.SPAM;
		} else {
			predictedClass = FileProcess.NOT_SPAM;
		}

		return predictedClass;
	}

	public double probabilityClass(Integer classification) {
		for (Map<Map<String, Integer>, Integer> wordFreqClass : trainingSet.getDocuments().values()) {
			// if(wordFreqClass.values())
		}

		return 0.0;
	}

	public double probability(Map<String, Integer> testPoint, Integer classification) {

		return 0.0;
	}

	public void classifyAllByFrequency() {

		// Iterate through the keyset and go file by file to classify
		for (String fileName : testSet.getDocuments().keySet()) {

			Map<Map<String, Integer>, Integer> wordFreqClass = testSet.getDocuments().get(fileName);

			if (DEBUG) {
				System.out.print(fileName + ": ");
			}

			// there is only one Map inside and this accesses it.
			for (Map<String, Integer> wordFreq : wordFreqClass.keySet()) {
				// predictedClassifications.put(fileName, classify(wordFreq));
				predictedClassifications.put(fileName, logClassify(wordFreq));
			}
		}

		// accuracy
		Integer correct = 0;

		for (String fileName : predictedClassifications.keySet()) {
			if (predictedClassifications.get(fileName).equals(FileProcess.SPAM)) {

				if (DEBUG) {
					System.out.println(fileName + "\tSPAM");
				}

				if (fileName.substring(0, 2).equals("sp")) {
					correct++;
				}
			} else {

				if (DEBUG) {
					System.out.println(fileName + "\tNOT SPAM");
				}

				if (!fileName.substring(0, 2).equals("sp")) {
					correct++;
				}
			}
		}

		if (process) {
			System.out.println("***********************************\nWITH PRE-PROCESSING");
		} else {
			System.out.println("***********************************\nWITHOUT PRE-PROCESSING");
		}

		System.out.println("\nCorrectly predicted " + correct + " out of " + predictedClassifications.size());

		System.out.println("Accuracy: " + correct / (double) predictedClassifications.size() * 100 + "%");

		System.out.println("***********************************");

	}

	public void classifyAllAppearance() {

	}

}