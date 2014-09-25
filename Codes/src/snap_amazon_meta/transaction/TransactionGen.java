package snap_amazon_meta.transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snap_amazon_meta.attributes.AttrValPairGen;
import snap_amazon_meta.attributes.NodeEntity;
import snap_amazon_meta.network.EdgeEnumerator;
import snap_amazon_meta.network.EdgePair;
import snap_amazon_meta.util.Constant;

public class TransactionGen {
	
	private static Map<String, Map<String, Integer>> pairMap = new HashMap<String, Map<String,Integer>>();
	//private static final int ATTR_VAL_PAIRS_COUNT = 619;
	
	
	private static int transCount = 0;
	private static int edgeCount = 0;
	private static final String TRANSACTION_DEST = "E:\\斯坦福snap project数据集\\transaction\\transaction_debug.txt";
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
				if(from.getGroup().equals(to.getGroup())) {
					if (pairMap.get("group").containsKey(from.getGroup())) {
						int index = pairMap.get("group").get(from.getGroup());
						transaction.add(index);
					} else {
						continue;
					}
					
				}
				if (from.getReviewAvgRating() == to.getReviewAvgRating()) {
					int index = pairMap.get("review_avg_rating").get(Float.toString(from.getReviewAvgRating()));
					transaction.add(index);
				}
				/*if (from.getCat_1() !=null && to.getCat_1() != null && from.getCat_1().equals(to.getCat_1())) {
					int index = pairMap.get("cat_1").get(from.getCat_1());
					transaction.add(index);
				}
				if (from.getCat_2() !=null && to.getCat_2() != null &&  from.getCat_2().equals(to.getCat_2())) {
					int index = pairMap.get("cat_2").get(from.getCat_2());
					transaction.add(index);
				}*/
				if (from.getCat_3() !=null && to.getCat_3() != null &&  from.getCat_3().equals(to.getCat_3())) {
					if (pairMap.get("cat_3").containsKey(from.getCat_3())) {
						int index = pairMap.get("cat_3").get(from.getCat_3());
						transaction.add(index);
					} else {
						continue;
					}
					
				}
//				if (from.getCat_4() !=null && to.getCat_4() != null && from.getCat_4().equals(to.getCat_4())) {
//					int index = pairMap.get("cat_4").get(from.getCat_4());
//					transaction[index] = 1;
//				}
				if (NodeEntity.isSalesrankOverlap(from, to, 10000)) {
					int key = from.getSalesrank()/10000 * 10000 - 1;
					Map<String, Integer> aMap = pairMap.get("salesrank"); // 以区间的start值作为inner-key
					int index = aMap.get(Integer.toString(key));
					transaction.add(index);
				}
				
				// write to file
				try {
					bWriter.write("("+ edge.getFrom() + "," + edge.getTo() + ")|");
					for (int i : transaction) {
						bWriter.write(i + " ");
					}
					bWriter.newLine();
					bWriter.flush();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			System.out.println(++transCount + " transaction has been written into file.");
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
				map.put(sp[0], Integer.parseInt(sp[1]));
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
				map.put(sp[0], Integer.parseInt(sp[1])); // 取区间的start值作为inner key
			} else {
				break;
			}
		}
		return map;
	}
	
	
	
}
