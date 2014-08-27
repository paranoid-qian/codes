package snap_amazon_meta;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Aggregator {
	
	private static final String folder = "E:\\斯坦福snap project数据集\\result\\";
	private static int label = 0;
	private static Connection connection;

	static {
		connection = DbUtil.openConn();
	}
	
	
	public static void agg(List<String> itemAttrs) throws SQLException {
		//获取attr可能取值
		List<List<String>> attrsValues = AttrValueUtil.getAttrsValues(DbUtil.ITEM_TABLE, itemAttrs); // 一个attr对应一个inner list
		
		// 组合
		List<String> attrValueComposition = compositeAttrValue(attrsValues); // 组合后变成["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
		
		// 构造filename
		String fileName = genFileName(itemAttrs);
		
		// label
		for (String comp : attrValueComposition) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT `id` FROM `item` WHERE " );
			sql.append(comp);
			PreparedStatement ps = connection.prepareStatement(sql.toString());
			System.out.println(sql);
			ResultSet rs = ps.executeQuery();
			
			StringBuilder ids = new StringBuilder();
			if (rs.first()) {
				do {
					ids.append(rs.getInt(1)).append(" ");
				} while (rs.next());
			} else {
				ids.append("0");
			}
			dumpResult(fileName, (label++ + " [" + comp + "]"), ids.toString());
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
	
	public static void dumpResult(String fileName, String label, String ids) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(folder + fileName), true)); // append
			writer.write(label);	// label
			writer.newLine();
			
			writer.write(ids);		// ids
			writer.newLine();
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static String genFileName(List<String> attrs) {
		StringBuilder fileName = new StringBuilder();
		for (String attr : attrs) {
			fileName.append(attr).append(".");
		}
		fileName.append("dat");
		return fileName.toString();
	}
	
	
	public static void main(String[] args) {
		String[] attrs = {"group"};
		try {
			agg(Arrays.asList(attrs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
