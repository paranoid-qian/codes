package snap_amazon_meta;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Aggregator {
	
	private static final String folder = "E:\\斯坦福snap project数据集\\result\\";
	private static int label = 0;
	private static Connection connection;

	static {
		connection = DbUtil.openConn();
	}
	
	
	
	
	public static void agg(List<String> itemAttrs) throws SQLException {
		// 构造filename
		String fileName =  genFileName(itemAttrs);
		
		//获取attr可能取值
		List<List<String>> attrsValues = null;
		
		
		
		// 组合
		List<String> attrValueComposition = null;
		compositeAttrValue(attrsValues);
		
		
		// label
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT `id` FROM item WHERE " );
		for (String comp : attrValueComposition) {
			sql.append(comp);
			PreparedStatement ps = connection.prepareStatement(sql.toString());
			ResultSet rs = ps.executeQuery();
			
			
		}
		
		
		// label -> 0
		label = 0;
		
		System.out.println("aggregate completed.\n");
	}
	
	public static void agg(List<String>	itemAttrs, List<String>	catAttrs) {
		
		
	}
	
	public static void agg(List<String>	itemAttrs, List<String>	catAttrs, List<String>	reviewAttrs) {
		
	}
	
	
	
	
	public static List<String> compositeAttrValue(List<List<String>> attrValues) {
		List<String> attrValuesSql = new ArrayList<String>();
		for (List<String> attrValue: attrValues) {
			attrValuesSql = dualComposite(attrValuesSql, attrValue);
		}
		return attrValuesSql;
	}
	
	public static List<String> dualComposite(List<String> l1, List<String> l2) {
		if (l1.isEmpty()) {
			return l2;
		}
		List<String> rst = new ArrayList<String>();
		for (String v1 : l1) {
			for (String v2 : l2) {
				rst.add(v1 + " AND " + v2);
			}
		}
		return rst;
	}
	
	public static void dumpResult(String fileName, String ids) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(folder + fileName), true)); // append
		writer.write(ids);
		writer.newLine();
		writer.flush();
	}
	
	public static String genFileName(List<String> attrs) {
		StringBuilder fileName = new StringBuilder();
		for (String attr : attrs) {
			fileName.append(attr).append(".");
		}
		fileName.append("dat");
		return fileName.toString();
	}
	
}
