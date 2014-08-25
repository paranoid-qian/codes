package BoC;

import java.util.Arrays;
import java.util.Scanner;

public class Main2 {
	
	static Scanner scan = new Scanner(System.in);
	static int[] ans = null;
	static int[] tree = null;
	static int[] depth = null;
	
	public static void changeDepthL2R(int root, int l, int r, int d) {
		
	}
	
	
	public static void main(String[] args) {
		int n = Integer.parseInt(scan.nextLine());
		
		for (int i = 1; i <= n; i++) {
			int nodeCount = Integer.parseInt(scan.nextLine());
			ans = new int[nodeCount+1];
			tree = new int[nodeCount+1];
			depth = new int[nodeCount+1];
			Arrays.fill(ans, 0);
			Arrays.fill(tree, 0);
			Arrays.fill(depth, 0);
			
			depth[1] = 1;
			// 构造树结构，构造子树关系
			for (int j = 2; j <= nodeCount; j++) {
				int f = Integer.parseInt(scan.nextLine());
				tree[j] = f;
				depth[j] = depth[f] + 1;
			}
			
			String change = scan.nextLine();
			String[] s = change.split(" ");
			int root= Integer.parseInt(s[0]);
			int l = Integer.parseInt(s[1]);
			int r = Integer.parseInt(s[2]);
			int d = Integer.parseInt(s[3]);
			
			changeDepthL2R(root, l, r, d);
			
		}
		
		
	}
	
}
