public class Test {

	public static final Integer FREQUENCY_THRESHOLD = 50;

	public static void main(String[] args) {

		// Naive Bayes

		// Do not pre-process

		NaiveBayes nBProcessed = new NaiveBayes("src/test", "src/train", true, FREQUENCY_THRESHOLD);
		NaiveBayes nBRaw = new NaiveBayes("src/test", "src/train", false, null);

		// Pre-process

		nBProcessed.classifyAllByFrequency();
		nBRaw.classifyAllByFrequency();

		// kNN is done in Matlab, this generates the CSV

		// Do not pre-process
		// FileProcess trainingProcess = new FileProcess("src/train");
		// FileProcess testProcess = new FileProcess("src/test");
		// trainingProcess.process(false, null);
		// testProcess.process(false, null);

		// try {
		// trainingProcess.knnCSV(trainingProcess.getVocabulary(),
		// "knnTrain.csv");
		// testProcess.knnCSV(trainingProcess.getVocabulary(), "knnTest.csv");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// System.out.println(
		// "Data has been formatted into knnTrain.csv and knnTest.csv for KNN
		// classification use in Matlab.");
		//
		// // Pre-Process
		// FileProcess trainingPreProcess = new FileProcess("src/train");
		// FileProcess testPreProcess = new FileProcess("src/test");
		// trainingPreProcess.process(true, FREQUENCY_THRESHOLD);
		// testPreProcess.process(true, FREQUENCY_THRESHOLD);
		//
		// try {
		// trainingPreProcess.knnCSV(trainingProcess.getVocabularyd(),
		// "knnTrainPP.csv");
		// testPreProcess.knnCSV(trainingProcess.getVocabulary(), "knnTestPP.csv");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// System.out.println(
		// "Data has been formatted into knnTrainPP.csv and knnTestPP.csv for
		// KNN classification use in Matlab.");
		//
	}

}
