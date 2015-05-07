package meta.main;

import meta.classifier.EvalResource;
import meta.classifier.FpIgClassifier;
import meta.classifier.IClassifier;
import meta.classifier.NaiveClassifier;
import meta.classifier.PuClassifier;

public class Main {
	
	public static void main(String[] args) {
		EvalResource resource = EvalResource.initResource();
		System.out.println("\n行1:naive, 行2:fp, 行3:fp-ig, 行4:pu");
		System.out.println("\n  Prec\t Recall\t F-Meas");
		System.out.println("------------------------------------------");
		
		IClassifier cls1 = new NaiveClassifier(resource);
		IClassifier cls2 = new FpIgClassifier(resource);
		IClassifier cls3 = new PuClassifier(resource);
		
		try {
			cls1.evaluate();
			cls2.evaluate();
			cls3.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
