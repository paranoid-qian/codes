package snap_amazon_meta.attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import snap_amazon_meta.util.DbUtil;

public class NodeEntity {
	
	//private static HashMap<Integer, NodeEntity> cache = new HashMap<Integer, NodeEntity>();
	
	private static Connection connection = null;
	static {
		connection = DbUtil.openConn();
	}
	
	
	private int itemId;
	private String group;
	private float reviewAvgRating;
	
	private String cat_1;
	private String cat_2;
	private String cat_3;
	private String cat_4;
	
	private int salesrank;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public float getReviewAvgRating() {
		return reviewAvgRating;
	}

	public void setReviewAvgRating(float reviewAvgRating) {
		this.reviewAvgRating = reviewAvgRating;
	}

	public String getCat_1() {
		return cat_1;
	}

	public void setCat_1(String cat_1) {
		this.cat_1 = cat_1;
	}

	public String getCat_2() {
		return cat_2;
	}

	public void setCat_2(String cat_2) {
		this.cat_2 = cat_2;
	}

	public String getCat_3() {
		return cat_3;
	}

	public void setCat_3(String cat_3) {
		this.cat_3 = cat_3;
	}

	public String getCat_4() {
		return cat_4;
	}

	public void setCat_4(String cat_4) {
		this.cat_4 = cat_4;
	}

	public int getSalesrank() {
		return salesrank;
	}

	public void setSalesrank(int salesrank) {
		this.salesrank = salesrank;
	}
	
	
	public static NodeEntity getItem(int id){
		/*if (cache.containsKey(id)) {
			return cache.get(id);
		}*/
		
		NodeEntity item = null;
		// 这个语句无法有效利用索引！！！！
		/*String sql = "SELECT `item`.`item_id`, `group`, `salesrank`, `review_avg_rating`, `cat_1`, `cat_2`, `cat_3`, `cat_4` FROM `item`, `category` " +
				"WHERE `item`.`item_id` = ? AND `category`.`item_id` = ? limit 1";*/
		String sql = "SELECT `item`.`item_id`, `group`, `salesrank`, `review_avg_rating` FROM `item` " +
				"WHERE `item`.`item_id` = ? limit 1";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			//ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				item = new NodeEntity();
				item.setItemId(rs.getInt("item_id"));
				String group = rs.getString("group");
				// 处理group格式
				if (group.indexOf('[') == 0) {
					group = "";
				} else if (group.contains("[")) {
					group = group.substring(0, group.indexOf('['));
				}
						
				item.setGroup(group);
				item.setSalesrank(rs.getInt("salesrank"));
				item.setReviewAvgRating(rs.getFloat("review_avg_rating"));
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "SELECT `cat_3` FROM `category` " +
				"WHERE `category`.`item_id` = ? limit 1";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			//ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				//item.setCat_1(rs.getString("cat_1"));
				//item.setCat_2(rs.getString("cat_2"));
				String cat3 = rs.getString("cat_3");
				// 处理格式
				if (cat3 == null || cat3.equals("") || cat3.indexOf('[') == 0) {
					cat3 = "";
				} else if (cat3.contains("[")) {
					cat3 = cat3.substring(0, cat3.indexOf('['));
				}
				item.setCat_3(cat3);
				//item.setCat_4(rs.getString("cat_4"));
				//cache.put(item.getItemId(), item);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}
	
	public static boolean isSalesrankOverlap(NodeEntity node1, NodeEntity node2, int step) {
		if (node1.getSalesrank()/step == node2.getSalesrank()/step) {
			return true;
		}
		return false;
	}
	
}
