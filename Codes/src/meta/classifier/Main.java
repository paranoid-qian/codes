package meta.classifier;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import wlsvm.WLSVM;


public class Main {
	
	public static void main(String[] args) {
		AbstractClassifier c45 = new PatternClassifier(new J48());
		AbstractClassifier svm = new PatternClassifier(new WLSVM());
		AbstractClassifier naiveBayes = new PatternClassifier(new NaiveBayes());
		
		try {
			c45.evaluate();
			System.out.println("=================");
			svm.evaluate();
			System.out.println("=================");
			naiveBayes.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
