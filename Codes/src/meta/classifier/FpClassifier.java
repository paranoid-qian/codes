package meta.classifier;

import java.util.List;

import meta.entity.Pattern;
import meta.evaluator.Evaluator;
import meta.gen.PatternGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instances;

public class FpClassifier implements IClassifier{

	private EvalResource resource;
	
	public FpClassifier(EvalResource resource) {	
		this.resource = resource;
	}
	
	@Override
	public void evaluate() throws Exception {
		Instances inss = new Instances(resource.getInstances());
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);
		int numFolds = resource.getNumFolds();
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			
			// gen train_x pattern
			PatternGen.genTrain_XFpPatterns(train, fold);
			
			// load train_x pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// 计算cover instance程度
			if (Constant.debug_fp_coverU) {
				for (Pattern pattern : patterns) {
					for (int i = 0; i < test.numInstances(); i++) {
						if (pattern.isFit(test.instance(i))) {
							pattern.incrCoveredU();
						}
					}
					System.out.println("fold-" + fold + ": " + pattern.pId() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
				}
			}
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
		}
//		System.out.println(eval.printEvalRst());
//		System.out.println(eval.printConfusionMatrix());
		System.out.println(eval.getAvgRstString());
		System.out.println(eval.getMaxRstString());
	}

}
