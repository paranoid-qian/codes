package meta.attributes;

import java.util.HashMap;
import java.util.Map;

public class AttributeIndexs {
	
	private static Map<String, Integer> indexMap = new HashMap<String, Integer>();
	
	public static void add(String attr, int index) {
		indexMap.put(attr, index);
	}
	
	public static int get(String attr) {
		return indexMap.containsKey(attr) ? indexMap.get(attr) : -1;
	}
	
}
