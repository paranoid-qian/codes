package meta.transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import meta.attributes.AttrValPairGen;
import meta.tableconstants.BreastCancerTable;
import meta.util.Constant;
import meta.util.DbUtil;

public class NaiveTransactionHandler {
	
	public static final String dbName = Constant.DB_TABLE_BREAST_CANCER;
	private static final String TRANSACTION_DEST = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\transaction.txt";
	
	private static Map<String, Map<String, Integer>> pairMap = new HashMap<String, Map<String,Integer>>();
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
		try {
			genTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * gen transaction
	 * @throws SQLException 
	 */
	public static void genTransaction() throws SQLException {
		Connection connection = DbUtil.openConn(dbName);
		String sql = "select * from `" + BreastCancerTable.table_name + "`;";
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			boolean flag = true;
			StringBuffer sb = new StringBuffer();
			do {
				flag = true;
				sb.delete(0, sb.length());
				for (String col : BreastCancerTable.columns) {
					String val = rs.getString(col);
					if (val.equals("?")) {
						flag = false;
						break; // 此条数据无效
					} 
					sb.append(pairMap.get(col).get(val) + " ");
				}
				if (flag) {
					try {
						bWriter.write(sb.toString());
						bWriter.newLine();
						bWriter.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			} while (rs.next());
		}
		
		//System.out.println(++transCount + " transaction has been written into file.");
		
	}
	
	public static void loadAttrValPairs() {
		try {
			for (String attr : BreastCancerTable.columns) {
				pairMap.put(attr, loadDiscretePairs(attr));
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
				map.put(sp[0], Integer.parseInt(sp[1]));   // key(string) - value(int)
			} else {
				break;
			}
		}
		return map;
	}
}
