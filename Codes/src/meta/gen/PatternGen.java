package meta.gen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import meta.util.constants.Constant;
import weka.core.Instance;
import weka.core.Instances;

public class PatternGen {
	
	
	/**
	 * 产生pu pattern
	 * @param inss
	 * @param fold
	 * @param c0_OR_c1
	 * @return
	 * @throws Exception 
	 */
	public static void genPuPatterns4Fold4L_x(List<Instance> trainC_fit, int fold, double c_x) throws Exception {
		
		TransactionGen.genL_X_PuTransactionFile(trainC_fit, fold, c_x);
		File transFile = null;
		while (transFile == null) {
			transFile = new File(Constant.PU_TRAIN_LX_TRANSACTION_FOLDER + fold + Constant.TRANS_PATH + c_x + Constant.TYPE_POSTFIX);
		}
		
		String src = Constant.PU_TRAIN_LX_TRANSACTION_FOLDER + fold + Constant.TRANS_PATH + c_x + Constant.TYPE_POSTFIX;
		String destFolder = Constant.PU_TRAIN_LX_PATTERN_FILE_FOLDER + fold;
		File file = new File(destFolder);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		
		String dest = destFolder + Constant.PATS_PATH + c_x + Constant.TYPE_POSTFIX;
		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -tm -m" + Constant.puItemMinCount + " -s"+ Constant.puMinSupport +"  -v\"|%a\" "
				+ src + " "
				+ dest ;
		 try {
			if (Constant.debug_cmd_pu) {
				System.out.println(cmd);
			}
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 产生fp的pattern
	 * @param inss
	 * @param fold
	 * @return
	 * @throws Exception 
	 */
	public static void genTrain_Foldx_FpPatterns(Instances train, int fold) throws Exception {
		
		// 生成train_x上的transaction文件
		TransactionGen.genFpTrainTransaction(train, fold);
		File transFile = null; 
		while (transFile == null) {
			transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
		}
		String src = Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		String dest = Constant.FP_TRAIN_PATTERN_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		
		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -tm -m"+ Constant.itemMinCount + " -s"+ Constant.minSupport +"  -v\"|%a\" "
				+ src + " "
				+ dest ;
		 try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
