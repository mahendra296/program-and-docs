package program;

import java.util.Scanner;

public class ReversString
{

	public static void main(String[] args)
	{

		/*    String rev = "";
		
		System.out.println("Enter Any String=>");
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();
		
		int len = s.length();
		
		for (int i = len - 1; i >= 0; i--) {
		    rev = rev + s.charAt(i);
		}
		
		System.out.println("orignal String is : " + s);
		System.out.println("Reverse String is : " + rev);
		    
		*/

		/*    
		StringBuffer s =new StringBuffer("Mahi");
		System.out.println(s.reverse());
		
		*/

		/*
		System.out.println("Enter Any Assci code=>");
		Scanner sc = new Scanner(System.in);
		int a = sc.nextInt();
		
		System.out.println("The value of " + a + " is : " + (char)a);
		*/

		System.out.println("Enter Any Character =>");
		Scanner sc = new Scanner(System.in);
		String s = sc.next();

		char c = s.charAt(0);
		int a = (int) c;

		System.out.println("The value of  is : " + a);
	}
}
