package meta.util.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.util.constants.Constant;
import weka.core.Instances;

public class PatternLoader {
	private static BufferedReader bReader = null;
	
	/* attribute value pairs (reverse) */
	private static Map<Integer, AttrValEntry> reverseMap = null;
	
	
	
	/**
	 * ‘ÿ»Î fp pattern
	 * @param inss
	 * @param fold
	 * @return
	 * @throws IOException
	 */
	
	public static List<Pattern> loadTrain_FoldX_FpPatterns(Instances inss, int fold) throws IOException {
		String src = Constant.FP_TRAIN_PATTERN_FOLDER +  Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		
		bReader = new BufferedReader(new FileReader(src));
		
		if (reverseMap == null) {
			reverseMap = ItemLoader.loadItemsByReverse(inss);
		}
		
		List<Pattern> patList = new ArrayList<Pattern>();
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
	 * ‘ÿ»Î pu pattern
	 * @param inss
	 * @param fold
	 * @param c0_OR_c1
	 * @return
	 * @throws IOException
	 */
	public static List<PuPattern> loadTrain_L_FoldX_PuPatterns(Instances inss, int fold, double c_x) throws IOException {
		String src = Constant.PU_TRAIN_LX_PATTERN_FILE_FOLDER + fold + Constant.PATS_PATH + c_x+ Constant.TYPE_POSTFIX;
		
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
	 * ‘ÿ»Î fp pattern
	 * @param inss
	 * @param fold
	 * @return
	 * @throws IOException
	 */
	
	public static List<PuPattern> loadTrain_FoldX_CpPatterns(Instances inss, int fold) throws IOException {
		String src = Constant.CP_TRAIN_PATTERN_FOLDER +  Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX;
		
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
			String[] sp = line.split("\\s+");
			
			// pattern entrys
			for (int i = 0; i<sp.length-2; i++) {
				String idStr = sp[i];
				if (idStr.equals("")) {
					System.out.println("bug");
				}
				pattern.addEntry(reverseMap.get(Integer.parseInt(idStr)));	
			}
			patList.add(pattern);
		}
		return patList;
	}
	
}
