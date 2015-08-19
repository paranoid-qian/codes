package meta.classifier;

import meta.evaluator.Evaluator;
import meta.gen.TrainTestGen;
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
			Instances[] tt = TrainTestGen.genTrainTest(resource.getTrainRatio(), inss, fold);
			Instances train = tt[0];
			Instances test = tt[1];
//			Instances train = TrainTestGen.genTrain(inss, numFolds, fold);
//			Instances test = TrainTestGen.genTest(inss, numFolds, fold);
			
			eval.evalV2(train, test);
		}
		System.out.println("------------------------------------------");
		System.out.println("naive:");
		System.out.println("avg\t" + eval.getAvgRstString());
		System.out.println("max\t" + eval.getMaxRstString());
		System.out.println("------------------------------------------");
	}
}
