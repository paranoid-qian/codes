package BoC;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	static Scanner scan = new Scanner(System.in);
	static DecimalFormat format = new DecimalFormat(".00");
	static Map<String, Double> unit = new HashMap<String, Double>();
	static{
		unit.put("dm", 100D);
		unit.put("cm", 10D);
		unit.put("mm", 1D);
		unit.put("um", 0.001D);
		unit.put("nm", 0.000001D);
	}
	
	public static void main(String[] args) {
		double rst = 0;
		int t = Integer.parseInt(scan.nextLine());
		
		for (int i = 1; i <= t; i++) {
			String line = scan.nextLine();
			String[] nums = line.split(" ");
			double[] ds = convert(nums);
			double px = Double.parseDouble(nums[2].substring(0, nums[2].indexOf('p'))); 
			
			rst =  px * ds[0] / ds[1];
			
			System.out.println("Case " + i + ": " + format.format(rst) + "px");
		}
		
	}
	
	public static double[] convert(String[] data) {
		double[] r = {0, 0};
		String d1 = data[0];
		String d2 = data[1];
		
		if(Character.isDigit(d1.charAt(d1.length()-2))) {
			r[0] = Double.parseDouble(d1.substring(0, d1.length()-1)) * 1000;
		} else {
			String u = d1.substring(d1.length()-2, d1.length());
			double change = unit.get(u);
			r[0] = Double.parseDouble(d1.substring(0, d1.length()-2)) * change;
		}
		
		if(Character.isDigit(d2.charAt(d2.length()-2))) {
			r[1] = Double.parseDouble(d2.substring(0, d2.length()-1)) * 1000;
		} else {
			String u = d2.substring(d2.length()-2, d2.length());
			double change = unit.get(u);
			r[1] = Double.parseDouble(d2.substring(0, d2.length()-2)) * change;
		}
		
		return r;
	}
	
	
}
