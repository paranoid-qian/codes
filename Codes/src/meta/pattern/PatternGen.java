package meta.pattern;

import java.io.File;
import java.io.IOException;
import java.util.List;

import meta.entity.PuPattern;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instances;

public class PatternGen {
	
	
	/**
	 * 产生pu pattern
	 * @param inss
	 * @param fold
	 * @param c0_OR_c1
	 * @return
	 */
	public static void genTrain_XPuPatterns(Instances inss, int fold, double c_x) {
		String src = Constant.PU_TRAIN_LX_TRANSACTION_FOLDER + fold + Constant.TRANS_PATH + c_x + Constant.TYPE_POSTFIX;
		
		String destFolder = Constant.PU_TRAIN_LX_PATTERN_FILE_FOLDER + fold;
		File file = new File(destFolder);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		
		String dest = destFolder + Constant.PATS_PATH + c_x + Constant.TYPE_POSTFIX;
		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -tc -s"+ Constant.minSupport +"  -v\"|%a\" "
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
	 */
	public static void genTrain_XFpPatterns(Instances inss, int fold) {
		String src = Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		String dest = Constant.FP_TRAIN_PATTERN_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		
		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -tc -s"+ Constant.minSupport +"  -v\"|%a\" "
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
	
	/**
	 * 产生cp
	 * @param inss
	 * @param fold
	 * @return
	 */
//	public static List<PuPattern> genTrain_XCpPatterns(Instances inss, int fold) {
//		String src = Constant.CP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
//		String dest = Constant.CP_TRAIN_PATTERN_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
//		
//		String cmd = "E:\\weka\\dataset\\fpgrowth.exe -x -q1 -s"+ Constant.minCosineSupport +" -c" + Constant.minCosine + " "
//				+ src + " "
//				+ dest ;
//		 try {
//			//System.out.println(cmd);
//			Process p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
//			
//			// 记录命令
//			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.FP_PATTERN_FOLEDR + "cmd.txt"));
//			bWriter.write(cmd);
//			bWriter.flush();
//			bWriter.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		 
//		// load pattern
//		List<PuPattern> train_X_FpPatterns = null;
//		try {
//			train_X_FpPatterns = PatternLoader.loadTrain_FoldX_CpPatterns(inss, fold);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return train_X_FpPatterns;
//	}
//	
//	
//	/**
//	 * generate global frequent patterns
//	 */
//	public static void genGlobalFrequentPatterns() {
//		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -tc -s50  -v\"|%a\" "
//				+ Constant.TRANSACTION_FILE + " "
//				+ Constant.FP_PATTRN_FILE;
//		 try {
//			System.out.println(cmd);
//			Process p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
//			
//			// 记录命令
//			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.FP_PATTERN_FOLEDR + "cmd.txt"));
//			bWriter.write(cmd);
//			bWriter.flush();
//			bWriter.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void genGlobalCosinePatterns() {
//		String cmd = "E:\\weka\\dataset\\fpgrowth.exe -x -q1 -c0.4 -s0  "
//				+ Constant.TRANSACTION_FILE + " "
//				+ Constant.COSINE_PATTERN_FILE;
//		 try {
//			System.out.println(cmd);
//			Process p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
//			
//			// 记录命令
//			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.COSINE_PATTERN_FOLEDR + "cmd2.txt"));
//			bWriter.write(cmd);
//			bWriter.flush();
//			bWriter.close();
//			
//			// 格式化cosine pattern(取消support值的括号，便于excel处理)
//			formatCp4Excel();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	// 处理cp的输出格式
//	public static void formatCp4Excel() {
//		try {
//			BufferedReader bReader = new BufferedReader(new FileReader(Constant.COSINE_PATTERN_FILE));
//			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.COSINE_PATTERN_FORMAT_FILE));
//			
//			String line = null;
//			StringBuffer sb = new StringBuffer();
//			while ((line=bReader.readLine()) != null) {
//				sb.delete(0, sb.length());
//				
//				line = line.replace("(", "");
//				line = line.replace(")", "");
//				String[] sp = line.split("\\s+|\t");
//				
//				for (int i = 0; i < sp.length-2; i++) {
//					sb.append(sp[i] + " ");
//				}
//				sb.append("|" + sp[sp.length-2]);
//				sb.append("|" + sp[sp.length-1]);
//				
//				//System.out.println(line);
//				bWriter.write(sb.toString() + "\n");
//			}
//			
//			bWriter.flush();
//			bReader.close();
//			bWriter.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	// 处理fp的输出格式
//	public static void formatFp4Excel() {
//		try {
//			BufferedReader bReader = new BufferedReader(new FileReader(Constant.FP_PATTRN_FILE));
//			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.FP_PATTRN_FORMAT_FILE));
//			
//			String line = null;
//			StringBuffer sb = new StringBuffer();
//			while ((line=bReader.readLine()) != null) {
//				sb.delete(0, sb.length());
//				
//				line = line.replace("(", "");
//				line = line.replace(")", "");
//				String[] sp = line.split("\\s+|\t");
//				
//				for (int i = 0; i < sp.length-2; i++) {
//					sb.append(sp[i] + " ");
//				}
//				sb.append("|" + sp[sp.length-2]);
//				sb.append("|" + sp[sp.length-1]);
//				
//				//System.out.println(line);
//				bWriter.write(sb.toString() + "\n");
//			}
//			
//			bWriter.flush();
//			bReader.close();
//			bWriter.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	public static void main(String[] args) {
//		PatternGen.genGlobalFrequentPatterns();
//		//PatternGen.formatCp4Excel();
//	}
}
