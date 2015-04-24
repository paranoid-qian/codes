package meta.classifier;

import java.io.File;
import meta.gen.ItemGen;
import meta.util.constants.Constant;
import meta.util.loader.AttributeLoader;
import meta.util.loader.InstanceLoader;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class EvalResource{
	
	private static volatile EvalResource resource;
	
	private Classifier classifier = Constant.classifier;
	private Instances data;
	private int numFolds = Constant.numFolds;
	private int numItems;
	//private double trainRatio = Constant.trainRatio;
	
	
	public Instances getInstances() {
		return this.data;
	}
	public Classifier getClassifier() {
		return this.classifier;
	}
	public int getNumFolds() {
		return this.numFolds;
	}
	public int getNumItems() {
		return this.numItems;
	}
	
	/**
	 * initiate resource and return it
	 * @return
	 */
	public static EvalResource initResource() {
		if (resource == null) {
			resource = new EvalResource();
		}
		return resource;
	}
	private EvalResource() {
		init();	
		// 输出参数
		System.out.println("random\t" + Constant.s);
		System.out.println("numFolds\t" + Constant.numFolds);
		System.out.println("min_support\t" + Constant.minSupport);
		System.out.println("puMin_support\t" + Constant.puMinSupport);
		System.out.println("ins_cover\t" + Constant.instance_coverage);
	}
	
	// initiate
	private void init() {
		try {
			// load instance
			data = InstanceLoader.loadInstances();
			data.randomize(Constant.rand);
			// 随机化train和test集
			if (data.classAttribute().isNominal()) {
				data.stratify(numFolds);
			}
			
			// load attribute indices
			AttributeLoader.loadAttrIndexs(data);
			// check folders
			checkItemFolder();
			checkFpFolders();
			
			// generate items and store numItems
			numItems = ItemGen.genItems(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Check item folder
	private void checkItemFolder() {
		File file = new File(Constant.ITEMS_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	
	// check fp folders
	private void checkFpFolders() {
		// fp trans folder
		File file = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		// fp pattern folder
		file = new File(Constant.FP_TRAIN_PATTERN_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	

}
