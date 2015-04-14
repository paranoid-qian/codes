package meta.main;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import wlsvm.WLSVM;
import meta.classifier.BaseClassifier;

public class Main {
	
	/* ������ */
	public static final Classifier CLASSIFIER = new NaiveBayes();
	
	public static void main(String[] args) {
		BaseClassifier eval = new BaseClassifier(CLASSIFIER);
		System.out.println("\n��1:naive, ��2:fp, ��3:fp-ig, ��4:pu");
		System.out.println("\n  Prec\t Recall\t F-Meas");
		System.out.println("----------------------------");
		try {
			eval.evaluateOrigin();
			eval.evaluateFP();
			//eval.evaluateFPIG();
			eval.evaluatePu();
			//c45.evaluateCP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
