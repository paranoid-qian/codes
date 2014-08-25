import java.util.Scanner;


public class Codejam2 {
	
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		int n = Integer.parseInt(scan.nextLine());
		int i = 0;
		
		while(++i <= n) {
			String l = scan.nextLine();
			String[] ls = l.split(" ");
			double c = Double.parseDouble(ls[0]);
			double f = Double.parseDouble(ls[1]);
			double x = Double.parseDouble(ls[2]);
			double curF = 2;
			
			int farmCount = 0;
			double time = 0;
			while(true) {
				curF = 2 + farmCount * f;
				if(x/curF <= (c/curF + x/(curF+f))) {
					time += x/curF;
					break;
				}
				time += c/curF;
				farmCount++;
			}
			System.out.println("Case #"+ i +": " + time);
		}
	}
}
