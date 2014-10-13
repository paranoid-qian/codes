package meta.attributes;

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
import java.util.ArrayList;
import java.util.List;

import meta.tableconstants.BreastCancerTable;
import meta.util.Constant;
import meta.util.DbUtil;


/**
 * ����attr-val�ԣ����Ѷ�д�뵽�ļ���
 * @author paranoid
 *
 */
public class AttrValPairGen {
	
	/*
	 * ����ʵ�����ݱ��޸�
	 */
	public static final String dbName = Constant.DB_TABLE_BREAST_CANCER;
	public static final String ATTR_VAL_PAIR_DEST_FOLDER = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\attr-pairs\\";
	
	
	public static int incrementId = 0;
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
		
		Connection connection = DbUtil.openConn(dbName);
		String sql = "SELECT `"+ attr +"` FROM `"+ table +"` WHERE `"+ attr+ "` is not null GROUP BY `"+ attr +"`;";
		ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			List<String> list = new ArrayList<String>();
			
			//String last = "";
			do {
				String val = rs.getString(attr);
				if (val.equals("?")) {
					continue;
				}
				list.add(val);
				//map.put(val, incrementId++);
				/* ȥ���������� �� ���ֻ�����֣���ignore 
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
						list.add(val);
					}
					last = val;
				}*/
			
			} while (rs.next());
			
			// ���
			/*for (int i = 0; i < list.size(); i++) {
				for (int j = i; j < list.size(); j++) {
					bWriter.write(list.get(i) + Constant.SP + list.get(j) + Constant.SP + incrementId++);
					bWriter.newLine();
				}
				
			}*/
			for (int i = 0; i < list.size(); i++) {
				bWriter.write(list.get(i) + Constant.SP + incrementId++);
				bWriter.newLine();
			}
			
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
		
		int s = min;
		List<Integer> list = new ArrayList<Integer>();
		
		while (s <= max) {
			list.add(s);
			s += step;
		}
		// ��ϳ�pair
		for (int i = 0; i < list.size(); i++) {
			for (int j = i; j < list.size(); j++) {
				bWriter.write(list.get(i) + Constant.SP + list.get(j) + Constant.SP + incrementId++);
				bWriter.newLine();
			}
			
		}
		System.out.println("success write attr-val-id tuples for " + attr);
		System.out.println("next id = " + incrementId);
		bWriter.flush();
	}
	
	
	
	public static void main(String[] args) {
		try {
			for (String attr : BreastCancerTable.columns) {
				genDiscreteAttrPairs(BreastCancerTable.table_name, attr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
