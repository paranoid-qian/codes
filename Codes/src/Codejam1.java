import java.util.Arrays;
import java.util.Scanner;


public class Codejam1 {
	
	static String[] m1 = new String[4];
	static String[] m2 = new String[4];
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) {
		int n = Integer.parseInt(scan.nextLine());
		int i=0;
		while(++i <= n) {
			int l1 = Integer.parseInt(scan.nextLine());
			int j=0;
			while(j < 4) {
				m1[j++] = scan.nextLine();
			}
			int l2 = Integer.parseInt(scan.nextLine());
			j=0;
			while(j < 4) {
				m2[j++] = scan.nextLine();
			}
			
			String r1 = m1[l1-1];
			String r2 = m2[l2-1];
			String[] nums1 = r1.split(" ");
			String[] nums2 = r2.split(" ");
			
			int[] flag = new int[17];
			Arrays.fill(flag, 0);
			
			for (String s : nums1) {
				flag[Integer.parseInt(s)]++;
			}
			for (String s : nums2) {
				flag[Integer.parseInt(s)]++;
			}
			
			int count = 0;
			int index = 1;
			for (int k=1; k<flag.length; k++) {
				if(flag[k]==2) {
					index = k;
					count++;
				}
			}
			
			if(count == 0) {
				System.out.println("Case #" + i + ": Volunteer cheated!");
			} else if (count == 1) {
				System.out.println("Case #" + i + ": " + index);
			} else {
				System.out.println("Case #" + i + ": Bad magician!");
			}
		}
	}
}