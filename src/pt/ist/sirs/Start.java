package pt.ist.sirs;

public class Start {
	private static final String resourceDirectory = "res/";
	
	public static void main(String[] args) throws Exception {
		NIDS dark = new NIDS();
		dark.train(resourceDirectory + "train.arff");
		dark.label(resourceDirectory + "test.arff");
	}
}
