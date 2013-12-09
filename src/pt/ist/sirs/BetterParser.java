package pt.ist.sirs;

import java.io.IOException;

public class BetterParser {
	
	private static final String K14_FILE_NAME = "file.txt";
	private static final String PRE_PROCESSING_FILE_NAME = "PreProcessing2.txt";
	private static final String ARFF_TRAIN_FILE_NAME = "train1.arff";
	private static final String ARFF_TEST_FILE_NAME = "test1.arff";
	private static final int BYTE_COUNT = 54;
	
	/**
	 * Parses k14 file to a temporary byte-only text file to an arff file.
	 */
	public static void main(String[] args) {
		try {
			PreProcessor preProc = new PreProcessor(BYTE_COUNT);
			preProc.parse(K14_FILE_NAME, PRE_PROCESSING_FILE_NAME);
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
