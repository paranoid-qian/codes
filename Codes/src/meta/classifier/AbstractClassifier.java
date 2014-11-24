package meta.classifier;

import java.io.File;

import meta.attributes.ItemGen;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import weka.core.Instances;

public abstract class AbstractClassifier {
	
	
	protected Instances data = null;
	//protected List<Pattern> pats = null;	
	
	public AbstractClassifier() {
		try {
			data = InstanceLoader.loadInstances();
			//pats = PatternLoader.loadPattern(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// 检查文件夹是否存在，否则建立文件夹
		checkItemFolder();
		checkFpFolders();
		checkPuFolders();
		checkCpFolders();
		
		// 构造items
		try {
			ItemGen.genItems();
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
	
	private void checkPuFolders() {
		File file = new File(Constant.PU_TRAIN_L0_PATTERN_FILE_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		file = new File(Constant.PU_TRAIN_L1_PATTERN_FILE_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		file = new File(Constant.PU_TRAIN_L0_TRANSACTION_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		file = new File(Constant.PU_TRAIN_L1_TRANSACTION_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	
	private void checkCpFolders() {
		// fp trans folder
		File file = new File(Constant.CP_TRAIN_TRANSACTION_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		// fp pattern folder
		file = new File(Constant.CP_TRAIN_PATTERN_FOLDER);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	
	
}
