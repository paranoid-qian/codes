package snap_amazon_meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttrValueUtil {
	
	private static final String SERI_PATH = "E:\\斯坦福snap project数据集\\amazon-meta.txt\\";
	private static Connection connection = null;
	
	
	// 只能做等值attr离散值抽取
	public static List<List<String>> getAttrsValues(String tableName, List<String> itemAttrs) {
		List<List<String>> attrsValues = new ArrayList<List<String>>(); 
		try {
			for (String attr : itemAttrs) {
				List<String> values = pickAttrValues(tableName, attr);
				if (values != null){ // got cache
					// do noting
				} else {			 // no cache
					connection = DbUtil.openConn();
					
					values = new ArrayList<String>();
					String sql = "SELECT `"+ attr +"` FROM `"+ tableName +"` GROUP BY `"+ attr +"`;";
					PreparedStatement ps =  connection.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					if (rs.first()) {
						do {
							values.add("`"+ tableName +"`.`"+ attr +"`='"+ rs.getString(1) +"'"); // 拼接格式——`item`.`attr1`='value'
						} while (rs.next());
					}
					storeAttrValues(tableName, attr, values);	// store to cache
				}
				attrsValues.add(values);
			}
			DbUtil.closeConn(connection);
		} catch (Exception e) {
			e.printStackTrace();
			DbUtil.closeConn(connection);
		}
		return attrsValues;
	}
	
	
	private static void storeAttrValues(String table, String attr, List<String> attrValues) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERI_PATH + table + "." + attr + ".serialize.dat"));
			oos.writeObject(attrValues);
			System.out.println("序列化" + table + "." + attr + "离散值域 completed.\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> pickAttrValues(String table, String attr) {
		try {
			if (!new File(SERI_PATH + table + "." + attr + ".serialize.dat").exists()) {
				return null;
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERI_PATH +  table + "." + attr + ".serialize.dat"));
			System.out.println("从cache反序列化 " + table + "." + attr + "离散值域 completed.\n");
			return (List<String>)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static void main(String[] args) {
		String[] attrs = {"cat_level"};
		List<List<String>> list = getAttrsValues("category", Arrays.asList(attrs));
		for (List<String> list2 : list) {
			for (String s : list2) {
				System.out.println(s);
			}
		}
	}
	
}
