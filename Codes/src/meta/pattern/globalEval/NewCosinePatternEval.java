package meta.pattern.globalEval;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import meta.transaction.TransactionGen;
import meta.util.constants.Constant;

import weka.core.Instance;
import weka.core.Instances;

public class NewCosinePatternEval extends AbstractGlobalEval {
	
	private int numFolds = 10;

	public NewCosinePatternEval() {
		super();
	}
	
	
	public void eval() {
		Instances inss = new Instances(data);
		
		if (inss.classAttribute().isNominal()) {
			inss.stratify(numFolds);
		}
		// random dataset 
		long s = System.currentTimeMillis();
		System.out.println("random: " + s);
		Random rand = new Random(s);
		inss.randomize(rand);
		
		for (int i = 0; i < numFolds; i++) {
			Instances train = inss.testCV(numFolds, i);
			Instances test = inss.trainCV(numFolds, i);
			
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
			
			// 将train转化为transaction并写文件
			try {
				TransactionGen.genTrainTransaction(trainC_0, 0, i);
				TransactionGen.genTrainTransaction(trainC_1, 1, i);
				
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.TEST_TRANSACTION_FILE_PREFIX + i + Constant.ARFF_POSTFIX, true)); // append mode
				bWriter.write(test.toString());
				bWriter.flush();
				bWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public void genCosinePatterns() {
		for (int i = 0; i < numFolds; i++) {
			// 调用fpgrowth.exe计算c0和c1的pattern
			String cmd = "E:\\weka\\dataset\\fpgrowth.exe -x -q1 -c0.6 -s10 "
					+ Constant.TRAIN_TRANSACTION_FILE_PREFIX+i+"_c0 "
					+ Constant.TRAIN_TRANSACTION_PATTERNS_FOLDER + "pattern_fold_" + i + "_c0";
			
			String cmd2 = "E:\\weka\\dataset\\fpgrowth.exe -x -q1 -c0.6 -s10 "
					+ Constant.TRAIN_TRANSACTION_FILE_PREFIX+i+"_c1 "
					+ Constant.TRAIN_TRANSACTION_PATTERNS_FOLDER + "pattern_fold_" + i + "_c1";
			 try {
				System.out.println(cmd);
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				Process p2 = Runtime.getRuntime().exec(cmd2);
				p2.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		NewCosinePatternEval ncpEval  = new NewCosinePatternEval();
		//ncpEval.eval();
		ncpEval.genCosinePatterns();
	}
}
