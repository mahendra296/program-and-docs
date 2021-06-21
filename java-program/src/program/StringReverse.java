
package program;

import java.util.Scanner;

public class StringReverse
{
	public static String reverse(String str)
	{
		if (str.isEmpty())
		{
			return str;
		}
		return reverse(str.substring(1)) + str.charAt(0);
	}
	public static void main(String[] args)
	{
		String rev = "";
		System.out.println("Enter Any String=>");
		Scanner sc = new Scanner(System.in);
		String str = sc.nextLine();

		rev = reverse(str);

		System.out.println(rev);

	}

}
