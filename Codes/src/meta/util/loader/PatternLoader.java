package meta.util.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.PuPattern;
import meta.util.constants.Constant;
import weka.core.Instances;

public class PatternLoader {
	private static BufferedReader bReader = null;
	
	/* attribute value pairs (reverse) */
	private static Map<Integer, AttrValEntry> reverseMap = null;
	
	
	
	/**
	 * ���� fp pattern
	 * @param inss
	 * @param fold
	 * @return
	 * @throws IOException
	 */
	
	public static List<PuPattern> loadTrain_FoldX_FpPatterns(Instances inss, int fold) throws IOException {
		String src = Constant.FP_TRAIN_PATTERN_FOLDER +  Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		
		bReader = new BufferedReader(new FileReader(src));
		
		if (reverseMap == null) {
			reverseMap = ItemLoader.loadItemsByReverse(inss);
		}
		
		List<PuPattern> patList = new ArrayList<PuPattern>();
		String line = null;
		while (true) {
			line = bReader.readLine();
			if (line == null || line.equals("")) {
				break;
			}
			PuPattern pattern = new PuPattern();
			String[] sp = line.split("\\|");
			
			// pattern entrys
			String[] entrys = sp[0].split("\\s+");
			for (String idStr : entrys) {
				if (idStr.equals("")) {
					System.out.println("bug");
				}
				pattern.addEntry(reverseMap.get(Integer.parseInt(idStr)));	
			}
			// global support
			pattern.setSupport(Integer.parseInt(sp[1]));	
			
			patList.add(pattern);
		}
		return patList;
	}
	
	
	/**
	 * ���� pu pattern
	 * @param inss
	 * @param fold
	 * @param c0_OR_c1
	 * @return
	 * @throws IOException
	 */
	public static List<PuPattern> loadTrain_L_FoldX_PuPatterns(Instances inss, int fold, int c0_OR_c1) throws IOException {
		String src = Constant.PU_TRAIN_L0_PATTERN_FILE_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		if (c0_OR_c1 == 1) {
			src = Constant.PU_TRAIN_L1_PATTERN_FILE_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		}
		
		bReader = new BufferedReader(new FileReader(src));
		
		if (reverseMap == null) {
			reverseMap = ItemLoader.loadItemsByReverse(inss);
		}
		
		List<PuPattern> patList = new ArrayList<PuPattern>();
		String line = null;
		while (true) {
			line = bReader.readLine();
			if (line == null || line.equals("")) {
				break;
			}
			PuPattern pattern = new PuPattern();
			String[] sp = line.split("\\|");
			
			// pattern entrys
			String[] entrys = sp[0].split("\\s+");
			for (String idStr : entrys) {
				if (idStr.equals("")) {
					System.out.println("bug");
				}
				pattern.addEntry(reverseMap.get(Integer.parseInt(idStr)));	
			}
			// global support
			pattern.setSupport(Integer.parseInt(sp[1]));	
			
			patList.add(pattern);
		}
		return patList;
	}
	
}
