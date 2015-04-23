package meta.classifier;

import meta.evaluator.Evaluator;
import weka.core.Instances;

public class NaiveClassifier implements IClassifier {
	
	private EvalResource resource;
	
	public NaiveClassifier(EvalResource resource) {	
		this.resource = resource;
	}

	/**
	 * 评估没有添加pattern的分类效果
	 * @throws Exception
	 */
	@Override
	public void evaluate() throws Exception {
		Instances inss = new Instances(resource.getInstances());
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);
		int numFolds = resource.getNumFolds();
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);
			Instances train = inss.testCV(numFolds, fold);
//			Instances[] trainTest = TrainTestGen.genTrainTest(trainRatio, inss, fold);
//			Instances train = trainTest[0];
//			Instances test = trainTest[1];
			
			eval.evalV2(train, test);
			//System.out.println(eval.printEvalRst());
		}
		//System.out.println(eval.printEvalRst());
		//System.out.println(eval.printConfusionMatrix());
		System.out.println(eval.getAvgRstString());
		System.out.println(eval.getMaxRstString());
	}
}
