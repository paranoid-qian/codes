package meta.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
	
	public static final String CONN_URL = "jdbc:mysql://localhost:3306/";
	public static final String CONN_URL_UTF_8 = "?useUnicode=true&amp;characterEncoding=UTF-8";
	public static final String USERNAME = "root";
	public static final String PASSWORD	= "root";
	
	// table names
	/*public static final String ITEM_TABLE = "item";
	public static final String CAT_TABLE = "category";
	public static final String REVIEW_TABLE = "review";*/
	
	
	private static Connection connection = null;
	
	public static Connection openConn(String dbname) {
		if (connection != null) {
			return connection;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("success load mysql driver\n");
			Connection connection = DriverManager.getConnection(CONN_URL + dbname + CONN_URL_UTF_8, USERNAME, PASSWORD);
			return connection;
		} catch (Exception e) {
			System.out.println("failed load mysql driver");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void closeConn(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
