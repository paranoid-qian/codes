package meta.classifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import meta.entity.Pattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instance;
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
			
			// initialization
			IgFilter.calRelevance(train, patterns);
			IgFilter.sortByGain(patterns);
//			System.out.println("=====");
//			for (Pattern pattern : patterns) {
//				System.out.println(pattern.pId() + "|" + pattern.getGain() + "|" + pattern.getRevelance());
//			}
//			System.out.println("=====");
			
			// 根据fpig_delta选择pattern
			//System.out.println("***");
			patterns  = filter(train, patterns, Constant.fpig_delta);
			//System.out.println("***");
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
		}
		System.out.println("-------------------------------------------");
		System.out.println("PAT_FS patterns:");
		System.out.println("avg\t" + eval.getAvgRstString());
		System.out.println("max\t" + eval.getMaxRstString());
		System.out.println("-------------------------------------------");
		
	}
	
	private List<Pattern> filter(Instances train, List<Pattern> list, int coverage_delta) {
		List<Pattern> result = new ArrayList<>();
		Set<Instance> coveredSet = new HashSet<>();
		
		int cover = 0;			// train中所有instance被cover的最少次数
		int numSelected = 0;	// 已选择的pattern个数
		
		int numInstances = train.numInstances();
		int numPatterns = list.size();
		while(true) {
			if (numSelected >= numPatterns) {
				//System.out.println("selected all");
				break;	// selected all patterns
			}
			if (cover >= coverage_delta) {
				break;
			}
			Pattern selected = list.remove(list.size()-1);
			//System.out.println(selected.pId() + "|" + selected.getGain());
			result.add(selected);
			for (Pattern pattern : list) {
				IgFilter.updateGain(pattern, selected, train);
			}
			IgFilter.sortByGain(list);
			
			numSelected++;
			for (Instance instance : selected.getCoveredSet()) {
				if (coveredSet.size() == numInstances) {
					coveredSet.clear();
					cover++;
				}
				coveredSet.add(instance);
			}
		}
		return result;
	}
}
