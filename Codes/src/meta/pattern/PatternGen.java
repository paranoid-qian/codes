package meta.pattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import meta.util.constants.Constant;

public class PatternGen {
	
	/**
	 * generate global frequent patterns
	 */
	public static void genGlobalFrequentPatterns() {
		String cmd = "E:\\weka\\dataset\\fpgrowth_origin.exe -x  -s60  "
				+ Constant.TRANSACTION_FILE + " "
				+ Constant.FP_PATTRN_FILE;
		 try {
			System.out.println(cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			
			/*// input to cmd
			BufferedReader bReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line=bReader.readLine()) != null) {
				System.out.println(line);
			}
			*/
			p.waitFor();
			
			// ¼ÇÂ¼ÃüÁî
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.FP_PATTERN_FOLEDR + "cmd.txt"));
			bWriter.write(cmd);
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void genGlobalCosinePatterns() {
		String cmd = "E:\\weka\\dataset\\fpgrowth.exe -x -q1 -c0.5 -s0.05  "
				+ Constant.TRANSACTION_FILE + " "
				+ Constant.COSINE_PATTERN_FILE;
		 try {
			System.out.println(cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			
			/*// input to cmd
			BufferedReader bReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line=bReader.readLine()) != null) {
				System.out.println(line);
			}
			*/
			p.waitFor();
			
			// ¼ÇÂ¼ÃüÁî
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.COSINE_PATTERN_FOLEDR + "cmd.txt"));
			bWriter.write(cmd);
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		PatternGen.genGlobalCosinePatterns();
	}
}
