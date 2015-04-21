package meta.classifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javafx.binding.StringFormatter;

import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.filter.PuFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class BaseClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = Constant.numFolds;
	private double trainRatio = Constant.trainRatio;
	
	
	public BaseClassifier(Classifier classifier) {
		
		super();
		this.classifier = classifier;
		data.randomize(Constant.rand);
		// �����train��test��
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		
		// �������
		System.out.println("random\t" + Constant.s);
		System.out.println("numFolds\t" + Constant.numFolds);
		System.out.println("itemMaxCount\t" + Constant.itemMaxCount);
		System.out.println("puItemMaxCount\t" + Constant.puItemMaxCount);
		System.out.println("min_support\t" + Constant.minSupport);
		System.out.println("puMin_support\t" + Constant.puMinSupport);
		System.out.println("instance coverage\t" + Constant.instance_coverage);
	}
	
	/**
	 * ����û�����pattern�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		// ������������
		Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);
			Instances train = inss.testCV(numFolds, fold);
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			eval.eval(train, test);
			//System.out.println(eval.printEvalRst());
		}
		System.out.println(eval.printEvalRst());
		System.out.println(eval.printConfusionMatrix());
	}
	
	
	/**
	 * ����fp�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateFP() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			
			// gen train_x pattern
			PatternGen.genTrain_XFpPatterns(train, fold);
			
			// load train_x pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// ����cover instance�̶�
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
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		System.out.println(eval.printEvalRst());
		System.out.println(eval.printConfusionMatrix());
	}
	
	
	/**
	 * ����fp-ig�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateFPIG() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
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
			eval.eval(augTrain, augTest);
		}
		System.out.println(eval.printEvalRst());
	}
	
	
	/**
	 * ����pu�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			
			// map train instances to l_x
			Map<Double, List<Instance>> map = mapInstancesByClass(train);
			
			// union of patterns for storage
			Map<String, PuPattern> union = new HashMap<>();
			
			for (Double classVal : map.keySet()) {
				// get L_1 instances
				List<Instance> instanceListL_x = map.get(classVal);
				
				// gen L_1 patterns
				PatternGen.genPuPatterns4Fold4L_x(instanceListL_x, fold, classVal);
				List<PuPattern> patterns4Fold4L_x = PatternLoader.loadPuPatterns4Fold4L_x(inss, fold, classVal);
				
				// calculate D(x)
				calDx(patterns4Fold4L_x, instanceListL_x, inss);
				
				// union
				for (PuPattern puPattern : patterns4Fold4L_x) {
					if (!union.containsKey(puPattern.pName())) {
						union.put(puPattern.pName(), puPattern);
					}
				}
			}
			
			// sort by D(x) value
			List<PuPattern> patterns = new ArrayList<>(union.values());
			patterns.sort(new Comparator<PuPattern>() {
				@Override // �Ӹߵ�������
				public int compare(PuPattern p1, PuPattern p2) {
					if (p1.getDx() > p2.getDx()) { 
						return -1;
					} else if (p1.getDx() < p2.getDx()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			
			if (Constant.debug_pattern_dx) {
				// print patterns
				for (PuPattern pattern : patterns) {
					System.out.println(pattern.getFromClass() + "|" + pattern.pId() + "|" + pattern.getDx() + "|" + pattern.getSuppL1() + "|" + pattern.getIdf());
				}
			}
			
			int numPatterns = patterns.size();
			
			// filter by instance coverage
			patterns = PuFilter.filterByInstanceCoverage(test, patterns, Constant.instance_coverage);
			
			if (Constant.deubg_pattern_filterd) {
				System.out.println("pattern��������" + numPatterns);
				System.out.println("���˺�pattern��" + patterns.size());
				// print patterns
				System.out.println("fold-" + fold + ":");
				for (PuPattern pattern : patterns) {
					//System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
					System.out.println(pattern.getFromClass() + "|" + pattern.pId() + "|" + pattern.getDx() + "|" + pattern.getSuppL1() + "|" + pattern.getIdf());
				}
			}
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
			
		}
		System.out.println(eval.printEvalRst());
		System.out.println(eval.printConfusionMatrix());
		System.out.println("\n----------------------------");
	}
	
	public void evaluatePu1() throws Exception {
		Instances inss = new Instances(data);
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			
			// map train instances to l_x
			Map<Double, List<Instance>> map = mapInstancesByClass(train);
			
			// union of patterns for storage
			Map<String, PuPattern> union = new HashMap<>();
			
			for (Double classVal : map.keySet()) {
				// get L_1 instances
				List<Instance> instanceListL_x = map.get(classVal);
				
				// gen L_1 patterns
				PatternGen.genPuPatterns4Fold4L_x(instanceListL_x, fold, classVal);
				List<PuPattern> patterns4Fold4L_x = PatternLoader.loadPuPatterns4Fold4L_x(inss, fold, classVal);
				
				// calculate D(x)
				calDx(patterns4Fold4L_x, instanceListL_x, inss);
				
				// union
				for (PuPattern puPattern : patterns4Fold4L_x) {
					if (!union.containsKey(puPattern.pName())) {
						union.put(puPattern.pName(), puPattern);
					}
				}
			}
			
			// sort by D(x) value
			List<PuPattern> patterns = new ArrayList<>(union.values());
			patterns.sort(new Comparator<PuPattern>() {
				@Override // �Ӹߵ�������
				public int compare(PuPattern p1, PuPattern p2) {
					if (p1.getDx() > p2.getDx()) { 
						return -1;
					} else if (p1.getDx() < p2.getDx()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			
			if (Constant.debug_pattern_dx) {
				// print patterns
				for (PuPattern pattern : patterns) {
					System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx());
				}
			}
			
			int numPatterns = patterns.size();
			
			// filter by instance coverage
//			patterns = PuFilter.filterByInstanceCoverage(test, patterns, Constant.instance_coverage);
			
			/*
			 * ���ԣ�10��patternһ�ӿ�����Ч����coverage���
			 */
			System.out.println("Fold-" + fold + ":");
			
			int numU = test.numInstances();
			int numIncr = 10;
			int numSelected = numIncr;
			boolean stop = false;
			List<PuPattern> selectedPatterns = patterns;
			while(!stop) {
				if (numSelected < numPatterns) {
					selectedPatterns = patterns.subList(0, numSelected);
				} else {
					stop = true;
				}
				
				// ����coverInstance����
				int coveredInstances = 0;
				for (PuPattern pattern : selectedPatterns) {
					for (int i = 0; i < test.numInstances(); i++) {
						Instance ins = test.instance(i);
						if (pattern.isFit(ins)) {
							coveredInstances++;
						}
					}
				}
				Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
				Instances augTrain = TransactionAug.augmentDataset(selectedPatterns, train);
				Instances augTest = TransactionAug.augmentDataset(selectedPatterns, test);
				eval.eval(augTrain, augTest);
				System.out.println(eval.printEvalRst() + "\t" + coveredInstances );
				numSelected += numIncr;
			}
			
			
			
//			if (Constant.deubg_pattern_filterd) {
//				System.out.println("pattern��������" + allCount);
//				System.out.println("���˺�pattern��" + patterns.size());
//				// print patterns
//				for (PuPattern pattern : patterns) {
//					System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
//				}
//			}
//			
//			// ����instance
//			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
//			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
//			eval.eval(augTrain, augTest);
			
		}
//		System.out.println(eval.printEvalRst());
//		System.out.println(eval.printConfusionMatrix());
//		System.out.println("\n----------------------------");
	}
	
	/**
	 * ����pu�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluatePu2() throws Exception {
		Instances inss = new Instances(data);
		
		for (int fold = 2; fold < 3; fold++) {
			
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			//Instances train = TrainTestGen.genTrain(trainRatio, inss, fold);
			//Instances test = TrainTestGen.genTest(trainRatio, inss, fold);
			
			// map train instances to l_x
			Map<Double, List<Instance>> map = mapInstancesByClass(train);
			
			// union of patterns for storage
			Map<String, PuPattern> union = new HashMap<>();
			
			/*
			 * ���ԣ�10��patternһ�ӿ�����Ч����coverage���
			 */
			System.out.println("Fold-" + fold + ":");
			for (Double classVal : map.keySet()) {
				System.out.println("class-" + classVal + ":");
				
				// get L_1 instances
				List<Instance> instanceListL_x = map.get(classVal);
				
				// gen L_1 patterns
				PatternGen.genPuPatterns4Fold4L_x(instanceListL_x, fold, classVal);
				List<PuPattern> patterns = PatternLoader.loadPuPatterns4Fold4L_x(inss, fold, classVal);
				
				// calculate D(x)
				calDx(patterns, instanceListL_x, inss);
				
				patterns.sort(new Comparator<PuPattern>() {
					@Override // �Ӹߵ�������
					public int compare(PuPattern p1, PuPattern p2) {
						if (p1.getDx() > p2.getDx()) { 
							return -1;
						} else if (p1.getDx() < p2.getDx()) {
							return 1;
						} else {
							return 0;
						}
					}
				});
				
				int numPatterns = patterns.size();
				int numU = test.numInstances();
				int numIncr = 1;
				int numSelected = 0;
				boolean stop = false;
				List<PuPattern> selectedPatterns = patterns;
				while(!stop) {
					if (numSelected < numPatterns) {
						selectedPatterns = patterns.subList(0, numSelected);
					} else {
						stop = true;
					}
					
					// ����coverInstance����
					int coveredInstances = 0;
					for (PuPattern pattern : selectedPatterns) {
						for (int i = 0; i < numU; i++) {
							Instance ins = test.instance(i);
							if (pattern.isFit(ins)) {
								coveredInstances++;
							}
						}
					}
					Evaluator eval = Evaluator.newEvaluator(this.classifier, inss);
					Instances augTrain = TransactionAug.augmentDataset(selectedPatterns, train);
					Instances augTest = TransactionAug.augmentDataset(selectedPatterns, test);
					eval.eval(augTrain, augTest);
					System.out.println(eval.printEvalRst() + "\t" + coveredInstances );
					numSelected += numIncr;
				}
				
			}
			
		}			
	}
	
	
	private Map<Double, List<Instance>> mapInstancesByClass(Instances train) {
		Map<Double, List<Instance>> map = new HashMap<>();
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
			if (map.containsKey(ins.classValue())) {
				map.get(ins.classValue()).add(ins);
			} else {
				List<Instance> l_x = new ArrayList<>();
				l_x.add(ins);
				map.put(ins.classValue(), l_x);
			}
		}
		return map;
	}
	
	
	private void calDx(List<PuPattern> patterns4Fold4L_x, List<Instance> instanceListL_x, Instances inss) {
		// suppD
		for (int i = 0; i < inss.numInstances(); i++) {
			Instance ins = inss.instance(i);
			for (PuPattern pattern : patterns4Fold4L_x) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppD();
				}
			}
		}
		// suppL1
		for (Instance ins : instanceListL_x) {
			for (PuPattern pattern : patterns4Fold4L_x) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppL1();
				}
			}
		}
		// dx
		int dSize = inss.numInstances();
		int lSize = instanceListL_x.size();
		for (PuPattern pattern : patterns4Fold4L_x) {
			double dx = pattern.getSuppL1() * Math.log(dSize/pattern.getSuppD()) / Math.log(2);
			pattern.setDx(dx);
			pattern.setIdf(Math.log(dSize/pattern.getSuppD()) / Math.log(2));
		}
	}


}
