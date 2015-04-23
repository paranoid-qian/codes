package meta.classifier;

import java.util.List;

import meta.entity.Pattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instances;

public class FpIgClassifier implements IClassifier {

	private EvalResource resource;
	
	public FpIgClassifier(EvalResource resource) {
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
			
			// load train_x pattern��ֱ������FP������pattern���ɣ�
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// sort patterns according to IG value
			patterns = IgFilter.calculateAndSortByIg(train, patterns);
			
			// ����fpig_deltaѡ��pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fpig_delta);
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
		}
		//System.out.println(eval.printEvalRst());
		
	}
}
