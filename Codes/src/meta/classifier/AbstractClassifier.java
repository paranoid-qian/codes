package meta.classifier;

import java.io.File;

import meta.gen.ItemGen;
import meta.util.constants.Constant;
import meta.util.loader.AttributeLoader;
import meta.util.loader.InstanceLoader;
import weka.core.Instances;

public abstract class AbstractClassifier {
	
	protected Instances data;
	protected int item_count;
	
	public AbstractClassifier() {
		init();
	}
	
	
	// initiate
	private void init() {
		try {
			// load instance
			data = InstanceLoader.loadInstances();
			// load attribute indices
			AttributeLoader.loadAttrIndexs(data);
			// check folders
			checkItemFolder();
			checkFpFolders();
			
			// gen items
			item_count = ItemGen.genItems(data);
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
	
}
