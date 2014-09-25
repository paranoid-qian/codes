package snap_amazon_meta.meta;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import snap_amazon_meta.util.Constant;
import snap_amazon_meta.util.DbUtil;

public class Aggregator {
	
	private static int label = 0;
	private static Connection connection;

	static {
		connection = DbUtil.openConn();
	}
	
	public static void agg(List<String> attrValueComposition, String tableName, String fileName) throws SQLException {
		dumpInfo(fileName, attrValueComposition);
		
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
			
			//StringBuilder ids = new StringBuilder();
			List<String> rst = new ArrayList<String>();
			if (rs.first()) {
				do {
					//ids.append(rs.getInt(1)).append(" ");
					rst.add(rs.getInt(1) + " " + label);
				} while (rs.next());
			}
			dumpResult(fileName, rst);
			label++;
		}
		
		// label -> 0
		label = 0;
		
		System.out.println("aggregate completed.\n");
	}
	
	/*public static void agg(List<String> attrValueComposition, String tableName1, String tableName2, String fileName) throws SQLException {
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
		
	}*/
	
	public static void agg(List<String>	itemAttrs, List<String>	catAttrs, List<String>	reviewAttrs) {
		
	}
	
	
	public static List<String> compositeAttrValue(List<List<String>> attrValues) {
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
	
	private static void dumpResult(String fileName, List<String> idLabelList) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(Constant.AGGOUTPUT_FOLDER + fileName), true)); // append
			for (String  idLabel: idLabelList) {
				writer.write(idLabel);		// id label
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void dumpInfo(String fileName, List<String> attrValueComposition) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File(Constant.AGGOUTPUT_FOLDER + fileName), true)); // append
			writer.write(Integer.toString(attrValueComposition.size()));	// label
			writer.newLine();
			int count = 0;
			for (String attrValue : attrValueComposition) {
				writer.write(count++ + ":[" + attrValue + "] ");
			}
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static List<String> genSalesRankValues(String tableName, String attr, int min, int max, int step) {
		List<String> values = new ArrayList<String>();
		int i = min;
		String value = "";
		while (i <= max) {
			value  = "`"+ tableName +"`.`"+ attr +"`>="+ i +" AND `"+ tableName +"`.`"+ attr +"`<"+ (i+step) +"";
			values.add(value);
			i += step;
		}
		return values;
		
	}
	
}
