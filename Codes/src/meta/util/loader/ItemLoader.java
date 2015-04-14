package meta.util.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import weka.core.Instances;

import meta.entity.Item;
import meta.util.constants.Constant;

public class ItemLoader {
	
	private static Map<String, Map<String, Item>> itemMap = null;
	private static Map<Integer, Item> reverseItemMap = null;
	
	
	/**
	 * load attribute value pairs from items_folder
	 * 
	 * pairMap的结构： <attr - <val - AttrValEntry>>
	 * 
	 * @param attr
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Map<String, Item>> loadItems(Instances inss) throws IOException {
		if (itemMap != null) {
			return itemMap;
		}
		Map<String, Map<String, Item>> pairMap = new HashMap<String, Map<String,Item>>();
		for (int i = 0; i < inss.numAttributes()-1; i++) {
			String attr = inss.attribute(i).name();
			Map<String, Item> map = new HashMap<String, Item>();
			BufferedReader bReader = new BufferedReader(new FileReader(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX));
			while (true) {
				String line = bReader.readLine();
				if (line != null && !line.equals("")) {
					String[] sp = line.split("\\" + Constant.SP);
					map.put(sp[0], new Item(Integer.parseInt(sp[1]), attr, sp[0]));   // value(string) - AttrValEntry
				} else {
					break;
				}
			}
			pairMap.put(attr, map);
		}
		//System.out.println("Attribute-Value map loaded successfully.");
		itemMap = pairMap;
		return pairMap;
	}
	
	/**
	 * load attribute value pairs from items_folder
	 * 
	 * reversePairMap结构：<id - AttrValEntry>
	 * 
	 * @param attr
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Item> loadItemsByReverse(Instances inss) throws IOException {
		if (reverseItemMap != null) {
			return reverseItemMap;
		}
		Map<Integer, Item> reversePairMap = new HashMap<Integer, Item>();
		for (int i = 0; i < inss.numAttributes()-1; i++) {
			String attr = inss.attribute(i).name();
			BufferedReader bReader = new BufferedReader(new FileReader(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX));
			while (true) {
				String line = bReader.readLine();
				if (line != null && !line.equals("")) {
					String[] sp = line.split("\\" + Constant.SP);
					reversePairMap.put(Integer.parseInt(sp[1]),  new Item(Integer.parseInt(sp[1]), attr, sp[0]));  // id - (attr - val)
				} else {
					break;
				}
			}
			bReader.close();
		}
		//System.out.println("Attribute-Value reverse map loaded successfully.");
		reverseItemMap = reversePairMap;
		return reversePairMap;
	}
	
}
