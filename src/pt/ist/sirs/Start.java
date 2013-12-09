package pt.ist.sirs;

public class Start {
	public static void main(String[] args) throws Exception {
		NIDS dark = new NIDS();
		dark.train("train.arff");
		dark.label("test.arff");
	}
}
