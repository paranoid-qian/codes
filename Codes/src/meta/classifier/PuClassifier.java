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
		
		// �����train��test��
		System.out.println("random: " + Constant.s);
		
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		data.randomize(Constant.rand);
		
		// �������
		System.out.println("min_support: " + Constant.minSupport);
		System.out.println("recall��ֵ��" + Constant.recall);
		System.out.println("delta: " + Constant.delta);
	}
	
	
	/**
	 * ����û�����pattern�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		// ������������
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
		System.out.println("�����pattern�ķ�����:");
		//System.out.println("precision\t recall\t fMeasure");
		System.out.println(Utils.doubleToString(precision[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/10, 7, 4));
		System.out.println(Utils.doubleToString(precision[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/10, 7, 4));
		
	}
	
	
	/**
	 * ����fp�ķ���Ч��
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
		
			// ����train_1�ϵ�transaction�ļ�
			TransactionGen.genFpTrainTransaction(train, fold);
			// ��train_1����closed frequent pattern 
			File transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}

			// load L1�����ɵ�pattern
			List<PuPattern> patterns = PatternGen.genTrain_XFpPatterns(inss, fold);
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// ����
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
		System.out.println("Fp������:");
		//System.out.println("precision\t recall\t fMeasure");
		System.out.println(Utils.doubleToString(precision[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/10, 7, 4));
		System.out.println(Utils.doubleToString(precision[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/10, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/10, 7, 4));
	}
	
	/**
	 * ����pu�ķ���Ч��
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
			
			// ��train����0/1����
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
			
			// ����train_1��train_0�ϵķֱ�����transaction�ļ�
			TransactionGen.genL_X_PuTransactionFile(trainC_1, fold, 1);
			TransactionGen.genL_X_PuTransactionFile(trainC_0, fold, 0);

			// ��train_1����closed frequent pattern 
			File transFile = new File(Constant.PU_TRAIN_L1_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}
			List<PuPattern> patterns1 = PatternGen.genTrain_XPuPatterns(inss ,fold, 1);
			
			// ��train_1����closed frequent pattern 
			transFile = new File(Constant.PU_TRAIN_L0_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}
			List<PuPattern> patterns0 = PatternGen.genTrain_XPuPatterns(inss ,fold, 0);
			
			// ����pattern��suppL0��suppL1
			for (Instance ins : trainC_1) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1��instance����pattern��suppL1++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1��instance����pattern��suppL1++
					}
				}
			}
			for (Instance ins : trainC_0) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0��instance����pattern��suppL0++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0��instance����pattern��suppL0++
					}
				}
			}
			
			// filter
			patterns1 = PuFilter.filter1(trainC_1, trainC_0, patterns1, test, Constant.recall);
			patterns0 = PuFilter.filter0(trainC_1, trainC_0, patterns0, test, Constant.recall);
			// ��L0pattern��L1pattern�Ĳ���
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
			
			// ����supportU����
			patterns1 = PuFilter.CalculateAndSortBySuppU(patterns1, test);
			
			// ����coverageѡȡpattern
			patterns1 = PuFilter.filterByCoverage(train, patterns1, Constant.delta);
			
			if (Constant.debug_pu_pattern) {
				System.out.println("-------------------------");
				System.out.println("pattern | suppL1 | suppL0 | suppU");
				for (PuPattern pattern : patterns1) {
					System.out.println(pattern.pItems() + " | " + pattern.getSuppL1() + " | " + pattern.getSuppL0() + " | " + pattern.getSuppU());
				}
			}
			
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns1, train);
			Instances augTest = TransactionAug.augmentDataset(patterns1, test);
			
			// ����
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
		System.out.println("Pu������:");
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
