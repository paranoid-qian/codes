package meta.evaluator;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Evaluator {
	
	/* EvalRst(inner class) for store evaluating result */
	public static class EvalRst {
		private double precision;
		private double recall;
		private double fMeasure;
		
		public EvalRst() {
			this.precision = 0;
			this.recall = 0;
			this.fMeasure = 0;
		}
		public double getPrecision() {
			return precision;
		}
		public double getRecall() {
			return recall;
		}
		public double getfMeasure() {
			return fMeasure;
		}
	}
	
	private EvalRst rst;
	private Classifier classifier;
	private int evalTimes;
	private Evaluator(Classifier classifier) {
		this.classifier = classifier;
		this.rst = new EvalRst();
		this.evalTimes = 0;
	}
	
	/**
	 * Factory method for new evaluator
	 * @param classifier
	 * @return
	 */
	public static Evaluator newEvaluator(Classifier classifier) {
		return new Evaluator(classifier);
	}
	
	/**
	 * Evaluate function
	 * @param train
	 * @param test
	 * @throws Exception
	 */
	public void eval(Instances train, Instances test) throws Exception{
		Evaluation eval = new Evaluation(train);
		eval.setPriors(train);
		Classifier cls = Classifier.makeCopy(classifier);
		cls.buildClassifier(train);
		eval.evaluateModel(cls, test);
		rst.precision += eval.weightedPrecision();
		rst.recall += eval.weightedRecall();
		rst.fMeasure += eval.weightedFMeasure();
		evalTimes++;
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
	public String formatEvalRst() {
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.doubleToString(this.precision(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(this.recall(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(this.fMeasure(), 7, 4));
		return sb.toString();
	}
	
	
	// get average precision
	private double precision() {
		return this.rst.precision/this.evalTimes;
	}
	// get average recall
	private double recall() {
		return this.rst.recall/this.evalTimes;
	}
	// get average f-measure
	private double fMeasure() {
		return this.rst.fMeasure/this.evalTimes;
	}
	
}
