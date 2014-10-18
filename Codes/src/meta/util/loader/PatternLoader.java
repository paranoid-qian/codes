package meta.util.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.Pattern;
import meta.util.constants.Constant;

public class PatternLoader {
	private static BufferedReader bReader = null;
	
	/* attribute value pairs (reverse) */
	private static Map<Integer, AttrValEntry> reverseMap = null;
	
	/* indicate measure numbers in pattern file */
	private static final int ME_NUM = 2;
	
	
	/**
	 * load pattern and format it
	 * @return
	 * @throws IOException 
	 */
	public static List<Pattern> loadPattern() throws IOException {
		bReader = new BufferedReader(new FileReader(Constant.PATTRN_FILE));
		
		if (reverseMap == null) {
			reverseMap = ItemLoader.loadItemsByReverse();
		}
		
		List<Pattern> patList = new ArrayList<Pattern>();
		String line = null;
		while (true) {
			line = bReader.readLine();
			if (line == null || line.equals("")) {
				break;
			}
			Pattern pattern = new Pattern();
			String[] sp = line.split(" ");
			for (int i = 0; i < sp.length-ME_NUM; i++) {
				int itemId = Integer.parseInt(sp[i]);
				pattern.addEntry(reverseMap.get(itemId));
			}
			patList.add(pattern);
		}
		return patList;
	}
	
	
	/*public static void main(String[] args) {
		try {
			List<Pattern> list = PatternLoader.loadPattern();
			for (Pattern pattern : list) {
				for (AttrValEntry entry : pattern.entrys()) {
					System.out.print(entry.getId() + " ");
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
