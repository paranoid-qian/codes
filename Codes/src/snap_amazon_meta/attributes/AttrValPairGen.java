package snap_amazon_meta.attributes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import snap_amazon_meta.util.Constant;
import snap_amazon_meta.util.DbUtil;

/**
 * ����attr-val�ԣ����Ѷ�д�뵽�ļ���
 * @author paranoid
 *
 */
public class AttrValPairGen {
	
	public static int incrementId = 0;
	
	public static final String ATTR_VAL_PAIR_DEST_FOLDER = "E:\\˹̹��snap project���ݼ�\\transaction\\attr_val_pairs\\";
	public static final String POSTFIX = "_pairs.txt";
	public static BufferedWriter bWriter = null;
	public static PreparedStatement ps = null;
	
	/**
	 * ��ɢ���Եķ��������γ�value-id��
	 * @param table
	 * @param attr
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void genDiscreteAttrPairs(String table, String attr) throws SQLException, IOException {
		bWriter = new BufferedWriter(new FileWriter(new File(ATTR_VAL_PAIR_DEST_FOLDER + attr + POSTFIX), true)); // append mode
		
		Connection connection = DbUtil.openConn();
		String sql = "SELECT `"+ attr +"` FROM `"+ table +"` WHERE `"+ attr+ "` is not null GROUP BY `"+ attr +"`;";
		ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			//Map<String, Integer> map = new HashMap<String, Integer>();
			String last = "";
			do {
				String val = rs.getString(attr);
				//map.put(val, incrementId++);
				/* ȥ���������� �� ���ֻ�����֣���ignore */
				if (val.indexOf('[') == 0) {
					// ֻ�����֣���Ч����
					continue;
				} else {
					if (val.contains("[")) {
						val = val.substring(0, val.indexOf('['));
					}
					// ����
					if (last.equals(val)) {
						// һ�����
						continue;
					} else {
						bWriter.write(val + Constant.SP + incrementId++);
						bWriter.newLine();
					}
					last = val;
				}
				
			} while (rs.next());
		}
		System.out.println("success write attr-val-id tuples for " + attr);
		System.out.println("next id = " + incrementId);
		bWriter.flush();
		ps.close();
		DbUtil.closeConn(connection);
	}
	
	
	public static void genContinousAttrPairs(String table, String attr, int step) throws IOException {
		bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ATTR_VAL_PAIR_DEST_FOLDER + attr + POSTFIX), "UTF-8"));
		int min = -1; 
		int max = 3798351;
		
		int i = min;
		String val = null;
		while (i <= max) {
			val  = i + Constant.SP + incrementId++; // [left - id
			bWriter.write(val);
			bWriter.newLine();
			i += step;
		}
		System.out.println("success write attr-val-id tuples for " + attr);
		System.out.println("next id = " + incrementId);
		bWriter.flush();
	}
	
	
	
	public static void main(String[] args) {
		try {
			genDiscreteAttrPairs("item", "group");
			genDiscreteAttrPairs("item", "review_avg_rating");
			//genDiscreteAttrPairs("category", "cat_1");
			//genDiscreteAttrPairs("category", "cat_2");
			genDiscreteAttrPairs("category", "cat_3");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			genContinousAttrPairs("item", "salesrank", 10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
