package meta.classifier;

import java.io.File;

import meta.item.ItemGen;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import weka.core.Instances;

public abstract class AbstractClassifier {
	
	protected Instances data;
	protected int item_count;
	
	public AbstractClassifier() {
		try {
			data = InstanceLoader.loadInstances();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 检查文件夹是否存在，否则建立文件夹
		checkItemFolder();
		checkFpFolders();
		//checkCpFolders();
		
		// 构造items
		try {
			item_count = ItemGen.genItems(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkItemFolder() {
		// item folder
		File file = new File(Constant.ITEMS_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		} else {
			System.out.println("ok");
		}
	}
	
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
		// fp ig trans folder
		file = new File(Constant.FP_IG_TRAIN_TRANSACTION_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		// fp ig pattern folder
		file = new File(Constant.FP_IG_TRAIN_PATTERN_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	
//	private void checkCpFolders() {
//		// fp trans folder
//		File file = new File(Constant.CP_TRAIN_TRANSACTION_FOLDER);
//		if (!file.exists() && !file.isDirectory()) {
//			file.mkdirs();
//		}
//		// fp pattern folder
//		file = new File(Constant.CP_TRAIN_PATTERN_FOLDER);
//		if (!file.exists() && !file.isDirectory()) {
//			file.mkdirs();
//		}
//	}
	
	
}
