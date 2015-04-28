import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;


public class Test {
	
	public static final int times = 2;
	public static final int numFolds = 10;
	
	
	public static void main(String[] args) throws Exception {
		Instances data = InstanceLoader.loadInstances("E:\\u2ran10test.arff");
		data.randomize(Constant.rand);
		// 随机化train和test集
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
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
			}
		}
	}
	
}
