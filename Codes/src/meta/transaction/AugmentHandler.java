package meta.transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.attributes.AttrValPairGen;
import meta.tableconstants.BreastCancerTable;
import meta.util.Constant;

public class AugmentHandler {
	
	public static final String dbName = Constant.DB_TABLE_BREAST_CANCER;
	private static final String PATTRN_SOURCE = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\pattern.txt";
	private static final String AUG_DATASET_DEST = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\aug_dataset.txt";
	
	private static class AttrValEntry {
		public String attr;
		public String val;
		
		public AttrValEntry(String attr, String val) {
			this.attr = attr;
			this.val = val;
		}
	}
	
	private static Map<Integer, AttrValEntry> reversePairMap = new HashMap<Integer, AttrValEntry>();
	private static BufferedWriter bWriter = null;
	private static BufferedReader bReader = null;
	static {
		try {
			bReader = new BufferedReader(new FileReader(PATTRN_SOURCE));
			bWriter = new BufferedWriter(new FileWriter(AUG_DATASET_DEST, true)); // append mode
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	
	public static void augmentDataset() {
		
	}
	
	
	/**
	 * resolve patterns, and mapping them to attr-val pairs
	 * @return
	 * @throws IOException
	 */
	public List<List<AttrValEntry>> resolvePatterns() throws IOException {
		loadPairsByReverse(); // load pair map for resolving
		
		String line = null;
		List<List<AttrValEntry>> patterns = new ArrayList<List<AttrValEntry>>();
		while (true) {
			line = bReader.readLine();
			if (line == null) {
				break;
			}
			
			// revolve patterns
			List<AttrValEntry> pat = new ArrayList<AttrValEntry>();
			String[] sp = line.split(" ");
			for (int i = 0; i < sp.length-2; i++) {	// last two values are support count and cosine value
				int id = Integer.parseInt(sp[i]);
				pat.add(reversePairMap.get(id));
			}
			
		}
	}
	
	
	private static void loadPairsByReverse() {
		try {
			for (String attr : BreastCancerTable.columns) {
				loadDiscretePairsByReverse(attr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadDiscretePairsByReverse(String attr) throws IOException {
		BufferedReader bReader = new BufferedReader(new FileReader(AttrValPairGen.ATTR_VAL_PAIR_DEST_FOLDER + attr + AttrValPairGen.POSTFIX));
		while (true) {
			String line = bReader.readLine();
			if (line != null && !line.equals("")) {
				String[] sp = line.split("\\" + Constant.SP);
				reversePairMap.put(Integer.parseInt(sp[1]),  new AttrValEntry(attr, sp[0]));  // id - (attr - val)
			} else {
				break;
			}
		}
	}
}
