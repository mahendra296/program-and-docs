
import java.util.Scanner;

public class StringRevrse
{

	public static void main(String[] args)
	{
		String rev = "";

		System.out.println("Enter Any String=>");
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();

		int len = s.length();

		for (int i = len - 1; i >= 0; i--)
		{
			rev = rev + s.charAt(i);

		}

		System.out.println("orignal String is : " + s);
		System.out.println("Reverse String is : " + rev);
	}

}
