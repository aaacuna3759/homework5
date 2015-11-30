public class Test {

	public static void main(String[] args) {
		// FileProcess fileProcess = new FileProcess("src/train");
		// fileProcess.process();

		NaiveBayes nB = new NaiveBayes("src/test", "src/train");

		nB.classifyAllByFrequency();
		
	}

}
