package meta.classifier;

import java.io.IOException;
import java.util.List;

import meta.entity.Pattern;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import meta.util.loader.PatternLoader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public abstract class AbstractClassifier {
	
	/**
	 * abstract method for evaluation classifier
	 */
	public abstract void evaluate() throws Exception;
	
	
	
	protected Instances data = null;
	protected List<Pattern> pats = null;	
	
	
	public AbstractClassifier() {
		try {
			data = InstanceLoader.loadInstances(Constant.DATASET_ARFF);
			pats = PatternLoader.loadPattern();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * eval util
	 */
	protected double[] eval(Classifier classifier, Instances train, Instances test) throws Exception {
		Classifier copiedClassifier = Classifier.makeCopy(classifier);
		copiedClassifier.buildClassifier(train);
		Evaluation eval = new Evaluation(train);	// build evaluator
		eval.evaluateModel(copiedClassifier, test);
		
		double[] rst = {eval.pctCorrect(), eval.correct(), eval.pctIncorrect(), eval.incorrect()};
		return rst;
	}
	
}
