package meta.agg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import meta.util.Constant;


public class Importer {
	private static BufferedReader reader;
	private static BufferedWriter itemWriter;
	private static BufferedWriter catWriter;
	private static BufferedWriter reviewWriter;
	
	// store mata 
	private static String id = null;
	private static String asin = null;
	private static String title = null;
	private static String group = null;
	private static String salesRank = "";
	private static String catCount = "";
	private static String reviewTotal = "";
	private static String reviewDownload = "";
	private static String reviewAvgRating = "";
	
	public static void importItems() {
		try {
			reader = new BufferedReader(new FileReader(Constant.SOURCE));
			itemWriter = new BufferedWriter(new FileWriter(Constant.ITEM_DEST));
			catWriter = new BufferedWriter(new FileWriter(Constant.CAT_DEST));
			reviewWriter = new BufferedWriter(new FileWriter(Constant.REVIEW_DEST));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// ignore description information and id=0 product
		ignore(Constant.IGNORE);
		
		// process content
		String line = null;
		int itemCount = 0;
		try {
			do {
				itemProcess();
				itemCount++;
				line = reader.readLine();
			} while (line != null); // until end of file
			
			System.out.println("\nsnap amazon meta insert completed. Total items: " + itemCount);
			reader.close();
			
			itemWriter.close();
			catWriter.close();
			reviewWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void itemProcess() throws Exception {
		id = getText(reader.readLine());				// id 
		asin = getText(reader.readLine());			// asin		
		String tmp = reader.readLine();
		if (tmp.trim().equals("discontinued product")) {
			return;
		}
		title = getText(tmp);						// title
		group = getText(reader.readLine());			// group
		salesRank = getText(reader.readLine());		//salesrank
		reader.readLine();							// similar, not used
		
		// categories
		catCount = getText(reader.readLine());		// category count
		int count = Integer.parseInt(catCount);
		// read category lines
		String catLine = null;
		StringBuilder curCat = new StringBuilder();
		for (int i = 0; i < count; i++) {
			curCat.delete(0, curCat.length());
			
			catLine = reader.readLine();
			String[] cats = catLine.split("\\|");
			
			int cat_level = cats.length > 8 ? 8 : cats.length;	// 最多考虑7层cat
			// set variables
			curCat.append(id);
			for (int l = 1; l < cat_level; l++) {
				String cat = cats[l];
				String catName = cat; //cat.substring(0, cat.indexOf('['));
				/*
				 * 在source文件中，有些只有分类编号，没有具体的名字
				 */
				//int catId = Integer.parseInt(cat.substring(cat.indexOf('[')+1, cat.indexOf(']')));
				curCat.append(Constant.SP).append(catName);
			}
			// insert categories
			catWriter.write(curCat.toString());
			catWriter.newLine();
		}
		//System.out.println("item " + id + " categories inserted " + catCount);
		
		// review  
		String[] splits = reader.readLine().split(":");
		reviewTotal = getReviewTotal(splits); 			// review total
		reviewDownload = getReviewDownload(splits);		// review download
		reviewAvgRating = getReviewAvgRating(splits);	// review avg rating
		int reviewCount = Integer.parseInt(reviewDownload);
		
		// read review lines
		String reviewLine = null;
		StringBuilder reviewBuilder = new StringBuilder();
		for (int i = 0; i < reviewCount; i++) {		// 取download的条数
			reviewBuilder.delete(0, reviewBuilder.length());
			
			// insert reviews
			reviewLine = reader.readLine().trim();
			String[] reviewEles = reviewLine.split(" +");
			
			reviewBuilder.append(id)
				.append(Constant.SP).append(reviewEles[0])
				.append(Constant.SP).append(reviewEles[2])
				.append(Constant.SP).append(reviewEles[4])
				.append(Constant.SP).append(reviewEles[6])
				.append(Constant.SP).append(reviewEles[8]);
			//reviewPs.executeUpdate();
			reviewWriter.write(reviewBuilder.toString());
			reviewWriter.newLine();
		}
		//System.out.println("item " + id + " reviews inserted " + reviewTotal);
		
		StringBuilder itemBuilder = new StringBuilder();
		itemBuilder.append(id)
			.append(Constant.SP).append(asin)
			//.append(Constant.SP).append(title)
			.append(Constant.SP).append(group)
			.append(Constant.SP).append(salesRank)
			.append(Constant.SP).append(catCount)
			.append(Constant.SP).append(reviewTotal)
			.append(Constant.SP).append(reviewDownload)
			.append(Constant.SP).append(reviewAvgRating);
		itemWriter.write(itemBuilder.toString());
		itemWriter.newLine();
		
		itemWriter.flush();
		catWriter.flush();
		reviewWriter.flush();
		
		System.err.println("item " + id + " inserted completed.\n");
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
	private static String getReviewTotal(String[] split) {
		return split[2].trim().split(" +")[0];
	}
	private static String getReviewDownload(String[] split) {
		return split[3].trim().split(" +")[0];
	}
	private static String getReviewAvgRating(String[] split) {
		return split[4].trim();
	}
	
	public static void main(String[] args) {
		importItems();
	}
	
	
}
