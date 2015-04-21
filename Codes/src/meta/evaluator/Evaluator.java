package meta.evaluator;

import java.util.List;

import meta.util.constants.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Evaluator {
	
	private Classifier classifier;
	private Evaluation eval;
	private Evaluator(Classifier classifier, Instances inss) throws Exception {
		this.classifier = classifier;
		this.eval = new Evaluation(inss);
	}
	
	/**
	 * Factory method for new evaluator
	 * @param classifier
	 * @return
	 * @throws Exception 
	 */
	public static Evaluator newEvaluator(Classifier classifier, Instances inss) throws Exception {
		return new Evaluator(classifier, inss);
	}
	
	/**
	 * Evaluate function
	 * @param train
	 * @param test
	 * @throws Exception
	 */
	
	public void eval(Instances train, Instances test) throws Exception{
		eval.setPriors(train);
		Classifier cls = Classifier.makeCopy(classifier);
		cls.buildClassifier(train);
		eval.evaluateModel(cls, test);
		// Í³¼Æconfusion matrix
		if (Constant.debug_class_matrix) {
			System.out.println(eval.toMatrixString());
			System.out.println(eval.weightedFMeasure());
		}
	}
	
	public void eval4Instances(Instances train, Instances test, List<Integer> errorList) throws Exception {
		Classifier cls = Classifier.makeCopy(classifier);
		cls.buildClassifier(train);
		for (int i = 0; i < test.numInstances(); i++) {
			Instance instance = test.instance(i);
			double pred = cls.classifyInstance(instance);
			if (pred != instance.classValue()) {
				errorList.add(i);
			}
		}
	}
	
	/**
	 * print evaluating result
	 * @return
	 */
	public String printEvalRst() {
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.doubleToString(this.precision(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(this.recall(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(this.fMeasure(), 7, 4));
		return sb.toString();
	}
	
	/**
	 * print confusion matrix for class details
	 * @return
	 * @throws Exception
	 */
	public String printConfusionMatrix() throws Exception {
		return this.eval.toMatrixString();
	}
	
	
	// get average precision
	private double precision() {
		return this.eval.weightedPrecision();
	}
	// get average recall
	private double recall() {
		return this.eval.weightedRecall();
	}
	// get average f-measure
	private double fMeasure() {
		return this.eval.weightedFMeasure();
	}
	
}
