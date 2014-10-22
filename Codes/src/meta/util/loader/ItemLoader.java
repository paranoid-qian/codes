package meta.util.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.util.constants.Constant;

public class ItemLoader {
	
	/**
	 * load attribute value pairs from items_folder
	 * @param attr
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Map<String, AttrValEntry>> loadItems() throws IOException {
		Map<String, Map<String, AttrValEntry>> pairMap = new HashMap<String, Map<String,AttrValEntry>>(9);
		for (String attr : Constant.DB_TABLE.columns()) {
			Map<String, AttrValEntry> map = new HashMap<String, AttrValEntry>();
			BufferedReader bReader = new BufferedReader(new FileReader(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX));
			while (true) {
				String line = bReader.readLine();
				if (line != null && !line.equals("")) {
					String[] sp = line.split("\\" + Constant.SP);
					map.put(sp[0], new AttrValEntry(Integer.parseInt(sp[1]), attr, sp[0]));   // value(string) - AttrValEntry
				} else {
					break;
				}
			}
			pairMap.put(attr, map);
		}
		System.out.println("Attribute-Value map loaded successfully.");
		return pairMap;
	}
	
	/**
	 * load attribute value pairs from items_folder
	 * reverse store (id - AttrValEntry)
	 * @param attr
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, AttrValEntry> loadItemsByReverse() throws IOException {
		Map<Integer, AttrValEntry> reversePairMap = new HashMap<Integer, AttrValEntry>();
		for (String attr : Constant.DB_TABLE.columns()) {
			BufferedReader bReader = new BufferedReader(new FileReader(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX));
			while (true) {
				String line = bReader.readLine();
				if (line != null && !line.equals("")) {
					String[] sp = line.split("\\" + Constant.SP);
					reversePairMap.put(Integer.parseInt(sp[1]),  new AttrValEntry(Integer.parseInt(sp[1]), attr, sp[0]));  // id - (attr - val)
				} else {
					break;
				}
			}
			bReader.close();
		}
		System.out.println("Attribute-Value reverse map loaded successfully.");
		return reversePairMap;
	}
	
}
