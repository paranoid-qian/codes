package snap_amazon_meta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {
	
	public static void processGroup_SalesRank() {
		String[] itemAttrs = {"group"};
		// 构造filename
		String fileName = "group.salesrank_step("+ Constant.rankStep +").dat";
		try {
			List<String> list = Arrays.asList(itemAttrs);
			
			//获取attr可能取值
			List<List<String>> attrsValues = AttrValueUtil.getAttrsValues(DbUtil.ITEM_TABLE, list); // 一个attr对应一个inner list
			List<String> salesValues = Aggregator.genSalesRankValues(DbUtil.ITEM_TABLE, "salesrank", Constant.minRank, Constant.maxRank, Constant.rankStep);
			attrsValues.add(salesValues);
			
			// 组合
			List<String> attrValueComposition = Aggregator.compositeAttrValue(attrsValues); // 组合后变成["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
			
			Aggregator.agg(attrValueComposition, DbUtil.ITEM_TABLE, fileName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	// test
	public static void main(String[] args) {
		
		processGroup_SalesRank();
		
	}
	
}
