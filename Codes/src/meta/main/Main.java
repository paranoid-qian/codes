package meta.main;

import meta.classifier.BaseClassifier;
import meta.util.constants.Constant;

public class Main {
	
	public static void main(String[] args) {
		BaseClassifier eval = new BaseClassifier(Constant.CLASSIFIER);
		System.out.println("\n\nPrecision\t\tRecall\t\tF-Measure");
		try {
			eval.evaluateOrigin();
			eval.evaluateFP();
			eval.evaluateFPIG();
			eval.evaluatePu();
			//c45.evaluateCP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
