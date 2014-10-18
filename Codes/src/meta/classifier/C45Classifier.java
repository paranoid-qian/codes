package meta.classifier;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import meta.entity.Pattern;
import meta.pattern.IgFilter;
import meta.transaction.TransactionAug;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;


public class C45Classifier extends AbstractClassifier {
	
	private Classifier classifier = new J48();
	
	public C45Classifier() {
		super();
	}
	
	
	@Override
	public void evaluate() {
		// randomize instances and stratify train set / test set
		Random rand = inss.getRandomNumberGenerator(System.currentTimeMillis());
		inss.randomize(rand);
		int split = (int)(inss.numInstances()*0.9);
		Instances train = new Instances(inss, 0, split);
		Instances test = new Instances(inss, split, inss.numInstances()-split);
		
		
		// filter pattern
		List<Pattern> augPatterns = null;
		try {
			 augPatterns = IgFilter.filter(inss, train, pats, 0.05);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		for (Pattern pattern : augPatterns) {
			System.out.println(pattern.getIg());
		}
		
		// augment pattern to inss
		TransactionAug.augmentDataset(augPatterns, train);
		TransactionAug.augmentDataset(augPatterns, test);
		
		
		// evaluate
		try {
			String sum = eval(this.classifier, train, test);
			System.out.println(sum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		AbstractClassifier classifier = new C45Classifier();
		classifier.evaluate();
	}

}
