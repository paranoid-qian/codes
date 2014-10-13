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
import meta.attributes.EdgeAttrPair;
import meta.attributes.NodeEntity;
import meta.util.Constant;

import snap_amazon_meta.network.EdgeEnumerator;
import snap_amazon_meta.network.EdgePair;

public class TransactionGen {
	
	private static Map<String, Map<String, Integer>> pairMap = new HashMap<String, Map<String,Integer>>();
	//private static final int ATTR_VAL_PAIRS_COUNT = 619;
	
	
	private static int transCount = 0;
	private static int edgeCount = 0;
	private static final String TRANSACTION_DEST = "E:\\斯坦福snap project数据集\\transaction\\transaction.txt";
	private static BufferedWriter bWriter = null;
	static {
		try {
			bWriter = new BufferedWriter(new FileWriter(TRANSACTION_DEST, true)); // append mode
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		loadAttrValPairs();
		genTransaction();
	}
	
	
	
	/**
	 * gen transaction
	 */
	public static void genTransaction() {
		NodeEntity curFromNode = new NodeEntity();
		curFromNode.setItemId(-1);
		EdgePair edge = null;
		
		
		//int[] transaction = new int[ATTR_VAL_PAIRS_COUNT];
		List<Integer> transaction = new ArrayList<Integer>();
		while (EdgeEnumerator.hasNextEdge()) {
			edgeCount++;
			// log
			System.out.println(edgeCount + " edge has been loaded.");
			
			//Arrays.fill(transaction, 0);
			transaction.clear();
			
			edge = EdgeEnumerator.nextEdge();
			
			// ignore directed edges
			if (edge.getTo() < edge.getFrom()) {
				System.out.println(edgeCount + " has been processed already.");
				continue;
			}
			
			/*if (edge.getTo() == 355) {
				System.out.println("aaa");
			}*/
			
			NodeEntity from = null;
			NodeEntity to = null;
			// 减少mysql访问次数
			if (edge.getFrom() == curFromNode.getItemId()) {
				to = NodeEntity.getItem(edge.getTo());
				from = curFromNode;
			} else {
				from = NodeEntity.getItem(edge.getFrom());
				if (from != null) {
					curFromNode = from;
				}
				to = NodeEntity.getItem(edge.getTo());
			}
			
			
			if (from == null || to == null) {
				// discontinued product
				// discard from transaction
			} else {
				String key = "";
				
				// group
				if(from.getGroup()!=null && to.getGroup()!=null) {
					key = EdgeAttrPair.key(from.getGroup(), to.getGroup());
					if (pairMap.get("group").containsKey(key)) {
						int index = pairMap.get("group").get(key);
						transaction.add(index);
					} else {
						key = EdgeAttrPair.key(to.getGroup(), from.getGroup());
						if (pairMap.get("group").containsKey(key)) {
							int index = pairMap.get("group").get(key);
							transaction.add(index);
						} else {
							continue;
						}
					}
					
				} else {
					continue;
				}
				
				// review avg rating
				if (from.getReviewAvgRating()>=0 && to.getReviewAvgRating()>=0) {
					key = EdgeAttrPair.key(Float.toString(from.getReviewAvgRating()), Float.toString(to.getReviewAvgRating()));
					if (pairMap.get("review_avg_rating").containsKey(key)) {
						int index = pairMap.get("review_avg_rating").get(key);
						transaction.add(index);
					} else {
						key = EdgeAttrPair.key(Float.toString(to.getReviewAvgRating()), Float.toString(from.getReviewAvgRating()));
						if (pairMap.get("review_avg_rating").containsKey(key)) {
							int index = pairMap.get("review_avg_rating").get(key);
							transaction.add(index);
						} else {
							continue;
						}
					}
					
				} else {
					continue;
				}
				
				// cat_3
				if (from.getCat_3()!=null && to.getCat_3()!=null) {
					key = EdgeAttrPair.key(from.getCat_3(), to.getCat_3());
					if (pairMap.get("cat_3").containsKey(key)) {
						int index = pairMap.get("cat_3").get(key);
						transaction.add(index);
					} else {
						key = EdgeAttrPair.key(to.getCat_3(), from.getCat_3());
						if (pairMap.get("cat_3").containsKey(key)) {
							int index = pairMap.get("cat_3").get(key);
							transaction.add(index);
						} else {
							continue;
						}
					}
					
				} else {
					continue;
				}
				
				// salesrank
				if (true) {
					key = EdgeAttrPair.key(Integer.toString(from.getSalesrank()/10000 * 10000 - 1), Integer.toString(to.getSalesrank()/10000 * 10000 - 1));
					if (pairMap.get("salesrank").containsKey(key)) {
						int index = pairMap.get("salesrank").get(key);
						transaction.add(index);
					} else {
						key = EdgeAttrPair.key(Integer.toString(to.getSalesrank()/10000 * 10000 - 1), Integer.toString(from.getSalesrank()/10000 * 10000 - 1));
						if (pairMap.get("salesrank").containsKey(key)) {
							int index = pairMap.get("salesrank").get(key);
							transaction.add(index);
						} else {
							continue;
						}
					}
				} 
				
				// write to file
				try {
					if (!transaction.isEmpty()) {
						//bWriter.write("("+ edge.getFrom() + "," + edge.getTo() + ")|");
						for (int i : transaction) {
							bWriter.write(i + " ");
						}
						bWriter.newLine();
						bWriter.flush();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			//System.out.println(++transCount + " transaction has been written into file.");
		}
		
	}
	
	public static void loadAttrValPairs() {
		String[] discreteAttrs = {"group", "review_avg_rating", "cat_3"};
		try {
			for (String attr : discreteAttrs) {
				pairMap.put(attr, loadDiscretePairs(attr));
			}
			
			String[] continousAttrs = {"salesrank"};
			for (String attr : continousAttrs) {
				pairMap.put(attr, loadContinousPairs(attr));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// log
		System.out.println("Attr-Val pairs have been loaded successfully.");
	}
	
	private static Map<String, Integer> loadDiscretePairs(String attr) throws IOException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader bReader = new BufferedReader(new FileReader(AttrValPairGen.ATTR_VAL_PAIR_DEST_FOLDER + attr + AttrValPairGen.POSTFIX));
		while (true) {
			String line = bReader.readLine();
			if (line != null && !line.equals("")) {
				String[] sp = line.split("\\" + Constant.SP);
				map.put(EdgeAttrPair.key(sp[0], sp[1]), Integer.parseInt(sp[2]));   // key(string) - value(int)
			} else {
				break;
			}
		}
		return map;
	}
	
	private static Map<String, Integer> loadContinousPairs(String attr) throws IOException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader bReader = new BufferedReader(new FileReader(AttrValPairGen.ATTR_VAL_PAIR_DEST_FOLDER + attr + AttrValPairGen.POSTFIX));
		while (true) {
			String line = bReader.readLine();
			if (line != null && !line.equals("")) {
				String[] sp = line.split("\\" + Constant.SP);
				map.put(EdgeAttrPair.key(sp[0], sp[1]), Integer.parseInt(sp[2])); // 取区间的start值作为inner key
			} else {
				break;
			}
		}
		return map;
	}
	
	
	
}
