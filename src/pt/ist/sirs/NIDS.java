package pt.ist.sirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.trees.J48;
import weka.core.Instances;

public class NIDS {
	
	private Instances train;
	
	public void train(String filename) throws Exception{
		BufferedReader breader = new BufferedReader(new FileReader(filename));
		
		train = new Instances(breader);
		train.setClassIndex(train.numAttributes()-1);
		
		breader.close();		
	}
	
	public void label(String filename) throws Exception {
		BufferedReader breader = new BufferedReader(new FileReader(filename));
		
		Instances test = new Instances(breader);
		test.setClassIndex(test.numAttributes()-1);
		
		breader.close();
		
		J48 tree = new J48();
		tree.buildClassifier(train);
		Instances labeled = new Instances(test);
		for(int i=0;i<test.numInstances();i++){
			double classLabel=tree.classifyInstance(test.instance(i));
			labeled.instance(i).setClassValue(classLabel);
		}
		
		System.out.println(labeled.toString());
		BufferedWriter writer = new BufferedWriter(new FileWriter("labeled.arff"));
		writer.write(labeled.toString());
		writer.close();
		
		//System.out.println(eval.toSummaryString("\nResluts\n=========\n", true));
		//System.out.println(eval.fMeasure(1) + " " + eval.precision(1) + " " +eval.recall(1));
		//System.out.println(train.numAttributes());
	}
}
