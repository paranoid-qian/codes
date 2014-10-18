package meta.transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.util.DbUtil;
import meta.util.constants.Constant;
import meta.util.loader.ItemLoader;

public class TransactionGen {
	
	private static Map<String, Map<String, AttrValEntry>> pairMap = null;
	private static BufferedWriter bWriter = null;
	
	/**
	 * gen transaction
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void genTransaction() throws SQLException, IOException {
		
		if (pairMap == null) {
			pairMap = ItemLoader.loadItems();
		}
		
		Connection connection = DbUtil.openConn(Constant.DB_NAME);
		bWriter = new BufferedWriter(new FileWriter(Constant.TRANSACTION_FILE, true)); // append mode
		
		String sql = "select * from `" + Constant.DB_TABLE.tableName() + "`;";
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			boolean flag = true;
			StringBuffer sb = new StringBuffer();
			do {
				flag = true;
				sb.delete(0, sb.length());
				for (String col : Constant.DB_TABLE.columns()) {
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
		
		bWriter.close();
		ps.close();
		DbUtil.closeConn(connection);
		//System.out.println(++transCount + " transaction has been written into file.");
		
	}
	
	public static void main(String[] args) {
		try {
			genTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
