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
	
	private static int label = 0;
	private static Connection connection;

	static {
		connection = DbUtil.openConn();
	}
	
	
	public static void agg(List<String> itemAttrs, String  tableName) throws SQLException {
		//获取attr可能取值
		List<List<String>> attrsValues = AttrValueUtil.getAttrsValues(tableName, itemAttrs); // 一个attr对应一个inner list
		
		// 组合
		List<String> attrValueComposition = compositeAttrValue(attrsValues); // 组合后变成["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
		
		// 构造filename
		String fileName = genFileName(itemAttrs);
		
		dumpInfo(fileName, attrValueComposition.size());
		
		// label
		for (String comp : attrValueComposition) {
			StringBuilder sql = new StringBuilder();
			//sql.append("SELECT `item`.`id` FROM `item` WHERE " );
			sql.append("SELECT DISTINCT `"+ tableName +"`.`item_id` FROM `"+ tableName +"` WHERE " );
			sql.append(comp);
			//sql.append(" GROUP BY `category`.`item_id`");
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
	
	public static void agg(List<String>	itemAttrs, String tableName1, List<String>	catAttrs, String tableName2) throws SQLException {
		//获取attr可能取值
		List<List<String>> itemAttrsValues = AttrValueUtil.getAttrsValues(tableName1, itemAttrs); // 一个attr对应一个inner list
		List<List<String>> catAttrsValues = AttrValueUtil.getAttrsValues(tableName2, catAttrs); 	 // 一个attr对应一个inner list
		itemAttrsValues.addAll(catAttrsValues);
		
		// 组合
		List<String> attrValueComposition = compositeAttrValue(itemAttrsValues); // 组合后变成["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
		
		// 构造filename
		List<String> attrs = new ArrayList<String>();
		attrs.addAll(itemAttrs);
		attrs.addAll(catAttrs);
		String fileName = genFileName(attrs);
		
		dumpInfo(fileName, attrValueComposition.size());
		
		// label
		for (String comp : attrValueComposition) {
			StringBuilder sql = new StringBuilder();
			//sql.append("SELECT `item`.`id` FROM `item` WHERE " );
			sql.append("SELECT DISTINCT `"+ tableName1 +"`.`item_id` FROM `"+ tableName1 +"`, `"+ tableName2 +"` WHERE " );
			sql.append(comp);
			sql.append(" AND `"+ tableName1 +"`.`item_id`=`"+ tableName2 +"`.`item_id`");
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
	
	public static void agg(List<String>	itemAttrs, List<String>	catAttrs, List<String>	reviewAttrs) {
		
	}
	
	
	
	private static List<String> compositeAttrValue(List<List<String>> attrValues) {
		List<String> attrValuesSql = new ArrayList<String>();
		for (List<String> attrValue: attrValues) {
			attrValuesSql = dualComposite(attrValuesSql, attrValue);
		}
		return attrValuesSql;
	}
	
	private static List<String> dualComposite(List<String> l1, List<String> l2) {
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
	
	private static void dumpResult(String fileName, String label, String ids) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(Constant.AGGOUTPUT_FOLDER + fileName), true)); // append
			writer.write(label);	// label
			writer.newLine();
			
			writer.write(ids);		// ids
			writer.newLine();
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dumpInfo(String fileName, int labelCount) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(Constant.AGGOUTPUT_FOLDER + fileName), true)); // append
			writer.write(Integer.toString(labelCount));	// label
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String genFileName(List<String> attrs) {
		StringBuilder fileName = new StringBuilder();
		for (String attr : attrs) {
			fileName.append(attr).append(Constant.DOT);
		}
		fileName.append("dat");
		return fileName.toString();
	}
	
	
	// test
	public static void main(String[] args) {
		String[] itemAttrs = {"group"};
		String[] catAttrs = {"cat_1"};
		try {
			agg(Arrays.asList(itemAttrs), DbUtil.ITEM_TABLE, Arrays.asList(catAttrs), DbUtil.CAT_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
