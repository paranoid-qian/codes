package meta.classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.PuPattern;
import meta.pattern.FitJudger;
import meta.pattern.PatternGen;
import meta.pattern.PuFilter;
import meta.transaction.TransactionAug;
import meta.transaction.TransactionGen;
import meta.util.constants.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class PuClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = 10;
	
	public PuClassifier(Classifier classifier) {
		
		super();
		this.classifier = classifier;
		
		// 随机化train和test集
		System.out.println("random: " + Constant.s);
		
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		data.randomize(Constant.rand);
		
		// 输出参数
		System.out.println("min_support: " + Constant.minSupport);
		System.out.println("recall阈值：" + Constant.recall);
		System.out.println("delta: " + Constant.delta);
	}
	
	
	/**
	 * 评估没有添加pattern的分类效果
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		// 评估分类性能
		Evaluation eval = new Evaluation(inss);
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			// evaluate
			try {
				Classifier cls = Classifier.makeCopy(classifier);
				cls.buildClassifier(train);
				eval.evaluateModel(cls, test);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
		}
		
		if (Constant.debug_origin_summary) {
			System.out.println(eval.toClassDetailsString());
		}
		
		System.out.println("\n\nPrecision\tRecall\t\tF-Measure");
		System.out.println("===================================================");
		System.out.println("不添加pattern的分类结果:");
		//System.out.println("precision\t recall\t fMeasure");
		System.out.println(Utils.doubleToString(precision[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/10, 7, 4));
		System.out.println(Utils.doubleToString(precision[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/10, 7, 4));
		
	}
	
	
	/**
	 * 评估fp的分类效果
	 * @throws Exception
	 */
	public void evaluateFP() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// 生成train_1上的transaction文件
			TransactionGen.genFpTrainTransaction(train, fold);
			// 在train_1上挖closed frequent pattern 
			File transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("第"+ fold +"折的trainsL1 文件生成！");
					break;
				}
			}

			// load L1上生成的pattern
			List<PuPattern> patterns = PatternGen.genTrain_XFpPatterns(inss, fold);
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// 分类
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(new J48());
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			//System.out.println(eval.toSummaryString());
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
		}
		
		System.out.println("===================================================");
		System.out.println("Fp分类结果:");
		//System.out.println("precision\t recall\t fMeasure");
		System.out.println(Utils.doubleToString(precision[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/10, 7, 4));
		System.out.println(Utils.doubleToString(precision[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/10, 7, 4));
	}
	
	/**
	 * 评估pu的分类效果
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			
			// 对train区分0/1两类
			List<Instance> trainC_0 = new ArrayList<Instance>();
			List<Instance> trainC_1 = new ArrayList<Instance>();
			for (int j = 0; j < train.numInstances(); j++) {
				Instance ins = train.instance(j);
				if (ins.classValue() == 0.0) {
					trainC_0.add(ins);
				} else {
					trainC_1.add(ins);
				}
				
			}
			
			// 生成train_1和train_0上的分别生成transaction文件
			TransactionGen.genL_X_PuTransactionFile(trainC_1, fold, 1);
			TransactionGen.genL_X_PuTransactionFile(trainC_0, fold, 0);

			// 在train_1上挖closed frequent pattern 
			File transFile = new File(Constant.PU_TRAIN_L1_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("第"+ fold +"折的trainsL1 文件生成！");
					break;
				}
			}
			List<PuPattern> patterns1 = PatternGen.genTrain_XPuPatterns(inss ,fold, 1);
			
			// 在train_1上挖closed frequent pattern 
			transFile = new File(Constant.PU_TRAIN_L0_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("第"+ fold +"折的trainsL1 文件生成！");
					break;
				}
			}
			List<PuPattern> patterns0 = PatternGen.genTrain_XPuPatterns(inss ,fold, 0);
			
			// 计算pattern的suppL0和suppL1
			for (Instance ins : trainC_1) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1的instance满足pattern，suppL1++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1的instance满足pattern，suppL1++
					}
				}
			}
			for (Instance ins : trainC_0) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0的instance满足pattern，suppL0++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0的instance满足pattern，suppL0++
					}
				}
			}
			
			// filter
			patterns1 = PuFilter.filter1(trainC_1, trainC_0, patterns1, test, Constant.recall);
			patterns0 = PuFilter.filter0(trainC_1, trainC_0, patterns0, test, Constant.recall);
			// 求L0pattern和L1pattern的并集
			Map<String, PuPattern> union = new HashMap<String, PuPattern>();
			for (PuPattern pat : patterns1) {
				union.put(pat.pName(), pat);
			}
			for (PuPattern pat : patterns0) {
				if (!union.containsKey(pat.pName())) {
					union.put(pat.pName(), pat);
				}
			}
			patterns1 = new ArrayList<PuPattern>(union.values());
			
			// 根据supportU排序
			patterns1 = PuFilter.CalculateAndSortBySuppU(patterns1, test);
			
			// 根据coverage选取pattern
			patterns1 = PuFilter.filterByCoverage(train, patterns1, Constant.delta);
			
			if (Constant.debug_pu_pattern) {
				System.out.println("-------------------------");
				System.out.println("pattern | suppL1 | suppL0 | suppU");
				for (PuPattern pattern : patterns1) {
					System.out.println(pattern.pItems() + " | " + pattern.getSuppL1() + " | " + pattern.getSuppL0() + " | " + pattern.getSuppU());
				}
			}
			
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns1, train);
			Instances augTest = TransactionAug.augmentDataset(patterns1, test);
			
			// 分类
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(new J48());
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
		}
		
		System.out.println("===================================================");
		System.out.println("Pu分类结果:");
		//System.out.println("precision\t recall\t fMeasure");
		System.out.println(Utils.doubleToString(precision[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/10, 7, 4));
		System.out.println(Utils.doubleToString(precision[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/10, 7, 4));
		
	}
	
	public static void main(String[] args) {
		PuClassifier c45 = new PuClassifier(new J48());
		try {
			c45.evaluateOrigin();
			c45.evaluateFP();
			c45.evaluatePu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
