package snap_amazon_meta.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EdgeEnumerator {
	
	private static final String NETWORK_SRC = "E:\\斯坦福snap project数据集\\amazon0601.txt\\Amazon0601.txt";
	private static BufferedReader bReader = null;
	private static String line = null;
	static {
		try {
			bReader = new BufferedReader(new FileReader(NETWORK_SRC));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * hasNextEdge
	 * @return
	 */
	public static boolean hasNextEdge() {
		try {
			line= bReader.readLine();
			if (line == null || line.equals("")) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * return nextEdge Pair
	 * @return
	 */
	public static EdgePair nextEdge() {
		EdgePair ep = new EdgePair();
		
		String[] sp = line.split("\\t");
		ep.setFrom(Integer.parseInt(sp[0]));
		ep.setTo(Integer.parseInt(sp[1]));
		
		return ep;
	}
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		hasNextEdge();
		EdgePair ep = nextEdge();
		System.out.println(ep.getFrom() + " " + ep.getTo());
	}
	
	
}
