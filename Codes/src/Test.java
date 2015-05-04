import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import meta.util.loader.InstanceLoader;


public class Test {
	
	public static final int times = 1;
	public static final int numFolds = 10;
	
	
	public static void main(String[] args) throws Exception {
		Instances data = InstanceLoader.loadInstances("E:\\Test\\u2ran10test.arff");
		String path = "E:\\Test\\u2ran10test划分\\";
		//long s = System.currentTimeMillis();
		long s = 1430207783311L;
		data.randomize(new Random(s));
		// 随机化train和test集
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		System.out.println(s);
		for (int i = 0; i < times; i++) {
			for (int fold = 0; fold < numFolds; fold++) {
				Instances train = data.testCV(numFolds, fold);
				Instances test = data.trainCV(numFolds, fold);
				
				Evaluation eval = new Evaluation(data);
				eval.setPriors(train);
				Classifier tree = new J48();
				tree.buildClassifier(train);
				eval.evaluateModel(tree, test);
				
				
				System.out.println("---------------------------------------------------");
				System.out.println("Fold-" + fold);
				System.out.println(eval.toClassDetailsString());
				System.out.println(eval.toMatrixString());
				System.out.println(tree);
				System.out.println("---------------------------------------------------");
				
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(path + "fold-" + fold + "-train.txt"));
				BufferedWriter bWriter2 = new BufferedWriter(new FileWriter(path + "fold-" + fold + "-test.txt")); 
				bWriter.write(train.toString());
				bWriter2.write(test.toString());
				
				bWriter.close();
				bWriter2.close();
			}
		}
	}
	
}
