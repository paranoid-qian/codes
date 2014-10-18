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
	public abstract void evaluate();
	
	
	
	protected Instances inss = null;
	protected List<Pattern> pats = null;	
	
	
	public AbstractClassifier() {
		try {
			inss = InstanceLoader.loadInstances(Constant.DATASET_ARFF);
			pats = PatternLoader.loadPattern();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * eval util
	 */
	protected String eval(Classifier classifier, Instances train, Instances test) throws Exception {
		classifier.buildClassifier(train);	// build classifier according to train set
	
		Evaluation eval = new Evaluation(train);	// build evaluator
		eval.evaluateModel(classifier, test);
		
		return eval.toSummaryString();
	}
	
}
