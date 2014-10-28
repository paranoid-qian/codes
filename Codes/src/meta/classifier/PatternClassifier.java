package meta.classifier;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import meta.entity.Pattern;
import meta.pattern.IgFilter;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;


public class PatternClassifier extends AbstractClassifier {
	
	private Classifier classifier;
	
	public PatternClassifier(Classifier classifier) {
		super();
		this.classifier = classifier;
	}
	
	
	@Override
	public void evaluate() throws Exception {
		int numFolds = 10;
		
		/********************* before use pattern *****************************/
		Instances inss = new Instances(data);
		if (inss.classAttribute().isNominal()) {
			inss.stratify(numFolds);
		}
		// randomize instances and stratify train set / test set
		long s = System.currentTimeMillis();
		System.out.println("random: " + s);
		Random rand = new Random(s);
		inss.randomize(rand);
		
		Evaluation eval = new Evaluation(inss);
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			// evaluate
			try {
				Classifier cls = Classifier.makeCopy(classifier);
				cls.buildClassifier(train);
				eval.evaluateModel(cls, test);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("---不添加pattern的分类结果：");
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toClassDetailsString());
		/*System.out.println("precision: " + eval.precision(0) + "/" + eval.precision(1));
		System.out.println("recall: " + eval.recall(0) + "/" + eval.recall(1));
		System.out.println("recall: " + eval.fMeasure(0) + "/" + eval.fMeasure(1));*/
		
		/********** after use pattern *************/
		inss = new Instances(data);
		if (inss.classAttribute().isNominal()) {
			inss.stratify(numFolds);
		}
		inss.randomize(rand);
		eval = new Evaluation(inss);
		// randomize instances and stratify train set / test set
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			
			// filter pattern
			List<Pattern> augPatterns = null;
			try {
				 augPatterns = IgFilter.filter(inss, train, pats, Constant.TOP_K);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			/*System.out.println();
			for (Pattern pattern : augPatterns) {
				System.out.println(pattern.pName() + ": " + pattern.getIg());
			}*/
			
			// augment pattern to inss
			try {
				train = TransactionAug.augmentDataset(augPatterns, train);
				//System.out.println(train);
				//test = TransactionAug.augmentDataset(augPatterns, test);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			/*try {
				Classifier cls = Classifier.makeCopy(classifier);
				cls.buildClassifier(train);
				eval.evaluateModel(cls, test);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
		System.out.println("---添加pattern的分类结果：");
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toClassDetailsString());
		/*System.out.println("precision: " + eval.precision(0) + "/" + eval.precision(1));
		System.out.println("recall: " + eval.recall(0) + "/" + eval.recall(1));
		System.out.println("recall: " + eval.fMeasure(0) + "/" + eval.fMeasure(1));*/
		
	}
}
