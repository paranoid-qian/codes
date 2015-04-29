package meta.evaluator;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;

public class Evaluator {
	
	private Classifier classifier;
	private EvalRecord record;
	
	// 用于eval, weka自带的统计功能，全局fold适用
	// NOTICE: Preserved.
	private Evaluation eval;
	
	private Evaluator(Classifier classifier, Instances inss) throws Exception {
		this.classifier = classifier;
		this.eval = new Evaluation(inss);
		this.record = new EvalRecord();
	}
	
	/**
	 * factory method for new evaluator
	 * @param classifier
	 * @return
	 * @throws Exception 
	 */
	public static Evaluator newEvaluator(Classifier classifier, Instances inss) throws Exception {
		return new Evaluator(classifier, inss);
	}
	
	/**
	 * evaluate for all folders
	 * will give average P-R-F
	 * @param train
	 * @param test
	 * @throws Exception
	 */
	
//	public void eval(Instances train, Instances test) throws Exception{
//		// evaluation for all folders
//		eval.setPriors(train);
//		Classifier cls = Classifier.makeCopy(classifier);
//		cls.buildClassifier(train);
//		eval.evaluateModel(cls, test);
//	}
	
	/**
	 * evaluate for avg and max
	 * will give max P-R-F
	 * @param train
	 * @param test
	 * @throws Exception
	 */
	public void evalV2(Instances train, Instances test) throws Exception {
		Evaluation evaluation = new Evaluation(train);
		evaluation.setPriors(train);
		Classifier cls = Classifier.makeCopy(classifier);
		cls.buildClassifier(train);
		evaluation.evaluateModel(cls, test);
		record.addEval(evaluation.weightedPrecision(), evaluation.weightedRecall());
	}
	
	/**
	 * get average result string for all folds
	 * @return
	 */
	public String getAvgRstString() {
		EvalTriple avg = record.getAvgTriple();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.doubleToString(avg.getPrecision(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(avg.getRecall(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(avg.getfMeasure(), 7, 4));
		return sb.toString();
	}
	
	/**
	 * get max result string for all folds
	 * @return
	 */
	public String getMaxRstString() {
		EvalTriple max = record.getMaxTriple();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.doubleToString(max.getPrecision(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(max.getRecall(), 7, 4)).append("\t") 
				.append(Utils.doubleToString(max.getfMeasure(), 7, 4));
		return sb.toString();
	}
	
	
	
	/**
	 * Preserved for pu1 and pu3 in BaseClassifier
	 * @return
	 */
	public String printWeightedEvalRst() {
		double precision = eval.weightedPrecision();
		double recall = eval.weightedRecall();
		double fMeasure = 2 * precision * recall / (precision + recall);
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.doubleToString(precision, 7, 4)).append("\t") 
				.append(Utils.doubleToString(recall, 7, 4)).append("\t") 
				.append(Utils.doubleToString(fMeasure, 7, 4));
		return sb.toString();
	}
	/**
	 * print confusion matrix for class details
	 * @return
	 * @throws Exception
	 *//*
	public String printConfusionMatrix() throws Exception {
		return this.eval.toMatrixString();
	}*/
	
}
