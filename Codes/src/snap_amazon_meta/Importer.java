package snap_amazon_meta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Importer {
	
	// const.
	public static final int IGNORE = 7; // 头7行（描述性信息和id=0的product）忽略
	public static final String CONN_URL = "jdbc:mysql://localhost:3306/snap_amazon_meta?useUnicode=true&amp;characterEncoding=UTF-8";
	public static final String USERNAME = "root";
	public static final String PASSWORD	= "root";
	
	private static BufferedReader reader;
	private static Connection connection;
	
	
	// store mata 
	private static int id = 0;
	private static String asin = null;
	private static String title = null;
	private static String group = null;
	private static long salesRank = 0L;
	private static int catCount = 0;
	private static int reviewTotal = 0;
	private static int reviewDownload = 0;
	private static float reviewAvgRating = 0f;
	// sql 
	private static String itemSql = "INSERT INTO item(`id`, `asin`, `title`, `group`, `salesrank`, `cat_count`, " +
			"`review_total`, `review_download`, `review_avg_rating`) " +
			" VALUES(?,?,?,?,?,?,?,?,?);";
	private static String catSql = "INSERT INTO category(`cat_id`, `cat_name`, `cat_level`, `item_id`) VALUES(?,?,?,?);";
	private static String reviewSql = "INSERT INTO review(`item_id`, `date`, `customer_id`, `rating`, `votes`, `helpful`) VALUES(?,?,?,?,?,?);";
	// ps
	private static PreparedStatement itemPs = null;
	private static PreparedStatement catPs = null;
	private static PreparedStatement reviewPs = null;
	
	
	public static void main(String[] args) {
		
		try {
			reader = new BufferedReader(new FileReader("E:\\斯坦福snap project数据集\\amazon-meta.txt\\amazon-meta.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// ignore description information and id=0 product
		ignore(IGNORE);
		
		// open connection
		openConn(CONN_URL, USERNAME, PASSWORD);
		
		// process content
		String line = null;
		int itemCount = 0;
		try {
			do {
				itemProcess();
				itemCount++;
				line = reader.readLine();
			} while (line != null); // until end of file
			
			closeConn();
			System.out.println("\nsnap amazon meta insert completed. Total items: " + itemCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private static void itemProcess() throws Exception {
		id = getInt(reader.readLine());				// id 
		asin = getText(reader.readLine());			// asin		
		title = getText(reader.readLine());			// title
		group = getText(reader.readLine());			// group
		salesRank = getLong(reader.readLine());		//salesrank
		reader.readLine();							// similar, not used
		// categories
		catCount = getInt(reader.readLine());		// category count
		
		catPs = connection.prepareStatement(catSql);
		// read category lines
		String catLine = null;
		for (int i = 0; i < catCount; i++) {
			catLine = reader.readLine().trim();
			String[] cats = catLine.split("\\|");
			// insert categories
			for (int j = 1; j < cats.length; j++) {
				String cat = cats[j];
				String catName = cat.substring(0, cat.indexOf('['));
				int catId = Integer.parseInt(cat.substring(cat.indexOf('[')+1, cat.indexOf(']')));
				catPs.setInt(1, catId);
				catPs.setString(2, catName);
				catPs.setInt(3, j);
				catPs.setInt(4, id);
				catPs.executeUpdate();
			}
		}
		System.out.println("item " + id + " categories inserted " + catCount);
		catPs.close();
		
		// review  
		String[] splits = reader.readLine().split(":");
		reviewTotal = getReviewTotal(splits); 			// review total
		reviewDownload = getReviewDownload(splits);		// review download
		reviewAvgRating = getReviewAvgRating(splits);	// review avg rating
		
		reviewPs = connection.prepareStatement(reviewSql);
		// read review lines
		String reviewLine = null;
		for (int i = 0; i < reviewTotal; i++) {
			// insert reviews
			reviewLine = reader.readLine().trim();
			String[] reviewEles = reviewLine.split(" +");
			reviewPs.setInt(1, id);
			reviewPs.setString(2, reviewEles[0]);
			reviewPs.setString(3, reviewEles[2]);
			reviewPs.setInt(4, Integer.parseInt(reviewEles[4]));
			reviewPs.setInt(5, Integer.parseInt(reviewEles[6]));
			reviewPs.setInt(6, Integer.parseInt(reviewEles[8]));
			reviewPs.executeUpdate();
		}
		System.out.println("item " + id + " reviews inserted " + reviewTotal);
		reviewPs.close();
		
		// item
		itemPs = connection.prepareStatement(itemSql);
		itemPs.setInt(1, id);
		itemPs.setString(2, asin);
		itemPs.setString(3, title);
		itemPs.setString(4, group);
		itemPs.setLong(5, salesRank);
		itemPs.setInt(6, catCount);
		itemPs.setInt(7, reviewTotal);
		itemPs.setInt(8, reviewDownload);
		itemPs.setFloat(9, reviewAvgRating);
		itemPs.executeUpdate();
		
		System.err.println("item " + id + " inserted completed.");
	}
	
	private static void openConn(String url, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("success load mysql driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println("failed load mysql driver");
			e.printStackTrace();
		}
	}
	private static void closeConn() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static void ignore(int lines) {
		try {
			for (int i = 0; i < lines; i++) {
				reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static int getInt(String line) {
		return Integer.parseInt(line.split(":")[1].trim());
	}
	private static long getLong(String line) {
		return Long.parseLong(line.split(":")[1].trim());
	}
	private static String getText(String line) {
		return line.split(":")[1].trim();
	}
	private static int getReviewTotal(String[] split) {
		return Integer.parseInt(split[2].trim().split(" ")[0].trim());
	}
	private static int getReviewDownload(String[] split) {
		return Integer.parseInt(split[3].trim().split(" ")[0].trim());
	}
	private static float getReviewAvgRating(String[] split) {
		return Float.parseFloat(split[4].trim());
	}
	
	
	
}
