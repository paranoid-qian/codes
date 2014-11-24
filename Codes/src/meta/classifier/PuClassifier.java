package meta.classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.pattern.FitJudger;
import meta.pattern.IgFilter;
import meta.pattern.PatternGen;
import meta.pattern.PuFilter;
import meta.transaction.TransactionAug;
import meta.transaction.TransactionGen;
import meta.util.constants.Constant;
import meta.util.loader.ItemLoader;
import meta.util.loader.PatternLoader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import wlsvm.WLSVM;

public class PuClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = Constant.numFolds;
	
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
		
		double[] precision = {0, 0, 0};
		double[] recall = {0, 0, 0};
		double[] fMeasure = {0, 0, 0};
		
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
			precision[2] += eval.weightedPrecision();
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			recall[2] += eval.weightedRecall();
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
			fMeasure[2] += eval.weightedFMeasure();
		}
		
		if (Constant.debug_origin_summary) {
			System.out.println(eval.toClassDetailsString());
		}
		
		System.out.println("\n\nPrecision\t\tRecall\t\tF-Measure");
		System.out.println("===================================================");
		System.out.println("不添加pattern的分类结果:");
		//System.out.println("precision\t recall\t fMeasure");
		//System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
		//System.out.println(Utils.doubleToString(precision[1]/numFolds, 7 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
		System.out.println(Utils.doubleToString(precision[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[2]/numFolds, 7, 4));
		
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

			// load train上生成的pattern
			List<Pattern> patterns = PatternGen.genTrain_XFpPatterns(inss, fold);
			
			//*******************pattern all**********************************//
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			// pat_all分类
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			precision[0] += eval.weightedPrecision();
			recall[0] += eval.weightedRecall();
			fMeasure[0] += eval.weightedFMeasure();
			
		}
		System.out.println("===================================================");
		System.out.println("Fp分类结果:");
		System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
	}
	
	
	/**
	 * 评估fp的分类效果
	 * @throws Exception
	 */
	public void evaluateFPIG() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// load train上生成的pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			//*******************pattern fs**********************************//
			// 根据ig排序patterns
			patterns = IgFilter.calculateAndSortByIg(train, patterns);
			// 根据delta选择pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fp_delta);
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			// pat_fs分类
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			precision[1] += eval.weightedPrecision();
			recall[1] += eval.weightedRecall();
			fMeasure[1] += eval.weightedFMeasure();
			
		}
		
		System.out.println("===================================================");
		System.out.println("Fp_fs分类结果:");
		System.out.println(Utils.doubleToString(precision[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
	}
	
	
	/**
	 * 评估pu的分类效果
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0, 0};
		double[] recall = {0, 0, 0};
		double[] fMeasure = {0, 0, 0};
		
		String[] itemCover = new String[10];
		String[] instanceCover = new String[10];
		
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
			
			/*// 计算pattern的suppL0和suppL1
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
			}*/
			
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
			
			/*for (PuPattern puPattern : patterns1) {
				System.out.println(puPattern.pItems());
			}*/
			
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
	//*********************************************************************************************
			final int itemCoverTimesDelta = 1;
			// 统计item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// 统计item的cover程度
			
			int itemTotal = itemList.size();
			for (PuPattern pattern : patterns1) {
				for (AttrValEntry entry : pattern.entrys()) {
					if (!itemCountMap.containsKey(entry.getId())) {
						itemCountMap.put(entry.getId(), 1);
					} else {
						itemCountMap.put(entry.getId(), itemCountMap.get(entry.getId())+1);
					}
				}
			}
			int coverage = 0;
			for (AttrValEntry entry : itemList) {
				if (!itemCountMap.containsKey(entry.getId())) {
					continue;
				}
				int count = itemCountMap.get(entry.getId());
				if (count >= itemCoverTimesDelta) {
					coverage++;
				}
			}
			itemCover[fold] = "item coverage: " + coverage + " (" + ((double)coverage/itemTotal) + ")";
			
			// 统计instance coverage
			final int insCoverTimesDelta = 1;
			coverage = 0;
			for (int i = 0; i < train.numInstances(); i++) {
				int coverNPattern = 0;
				Instance instance = train.instance(i);
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(instance, pattern)) {
						coverNPattern++;
					}
				}
				if (coverNPattern >= insCoverTimesDelta) {
					coverage++;
				}
				
			}
			instanceCover[fold] = "instance coverage(fold_"+ fold +"): " + coverage + " (" + ((double)coverage/train.numInstances()) + ")";
	//*********************************************************************************************
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns1, train);
			Instances augTest = TransactionAug.augmentDataset(patterns1, test);
			
			// 分类
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			precision[2] += eval.weightedPrecision();
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			recall[2] += eval.weightedRecall();
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
			fMeasure[2] += eval.weightedFMeasure();
		}
		
		// 输出instance和item的coverage
		for (String insC : instanceCover) {
			System.out.println(insC);
		}
		for (String itemC : itemCover) {
			System.out.println(itemC);
		}
		
		System.out.println("===================================================");
		System.out.println("Pu分类结果:");
		//System.out.println("precision\t recall\t fMeasure");
		//System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
		//System.out.println(Utils.doubleToString(precision[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
		System.out.println(Utils.doubleToString(precision[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[2]/numFolds, 7, 4));
	}
	
	
	/**
	 * 计算cp的coverage
	 * @throws Exception
	 */
	public void evaluateCP() throws Exception {
		Instances inss = new Instances(data);
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			
		
			// 生成train_1上的transaction文件
			TransactionGen.genCpTrainTransaction(train, fold);
			// 在train_1上挖closed frequent pattern 
			File transFile = new File(Constant.CP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("第"+ fold +"折的trainsL1 文件生成！");
					break;
				}
			}

			// load L1上生成的patterns
			List<PuPattern> patterns1 = PatternGen.genTrain_XCpPatterns(inss, fold);
			//*********************************************************************************************
			final int itemCoverTimesDelta = 1;
			// 统计item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// 统计item的cover程度
			
			int itemTotal = itemList.size();
			for (PuPattern pattern : patterns1) {
				for (AttrValEntry entry : pattern.entrys()) {
					if (!itemCountMap.containsKey(entry.getId())) {
						itemCountMap.put(entry.getId(), 1);
					} else {
						itemCountMap.put(entry.getId(), itemCountMap.get(entry.getId())+1);
					}
				}
			}
			int coverage = 0;
			for (AttrValEntry entry : itemList) {
				if (!itemCountMap.containsKey(entry.getId())) {
					continue;
				}
				int count = itemCountMap.get(entry.getId());
				if (count >= itemCoverTimesDelta) {
					coverage++;
				}
			}
			System.out.println("item coverage: " + coverage + " (" + ((double)coverage/itemTotal) + ")");
			//*********************************************************************************************
		}
	}
	
	public static void main(String[] args) {
		PuClassifier eval = new PuClassifier(new WLSVM());
		try {
			eval.evaluateOrigin();
			eval.evaluateFP();
			eval.evaluateFPIG();
			eval.evaluatePu();
			//c45.evaluateCP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
