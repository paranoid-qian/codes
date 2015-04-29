package meta.classifier;

import java.util.List;

import meta.entity.Pattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instances;

/**
 * This classifier is for HongChen's method
 * @author paranoid.Q
 * @date Apr 29, 2015 2:04:52 PM
 */
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
			Instances train = TrainTestGen.genTrain(inss, numFolds, fold);
			Instances test = TrainTestGen.genTest(inss, numFolds, fold);
			
			// gen train_x pattern
			PatternGen.genTrain_Foldx_FpPatterns(train, fold);
			
			// load train_x pattern（直接利用FP产生的pattern即可）
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// sort patterns according to IG value
			patterns = IgFilter.calculateAndSortByIg(train, patterns);
			
			// 根据fpig_delta选择pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fpig_delta);
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
		}
		//System.out.println(eval.printEvalRst());
		
	}
}
