
package test;

import java.util.Scanner;

public class multiply
{
	public static void main(String[] args)
	{
		//        Mul m1 = new Mul();
		int no1, no2, n1 = 0, n2 = 0, s1 = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter no1 :=> ");
		no1 = Integer.parseInt(sc.nextLine());
		if (no1 < 0)
		{
			n1 = ~no1 + 1;
			System.out.println(n1);
		}
		else
		{
			n1 = no1;
		}
		System.out.println("Enter no2 :=> ");
		no2 = Integer.parseInt(sc.nextLine());
		if (no2 < 0)
		{
			n2 = ~no2 + 1;
			System.out.println(n2);
		}
		else
		{
			n2 = no2;
		}
		for (int i = 1; i <= n2; i++)
		{
			s1 = s1 + n1;
		}
		if (no1 < 0 && no2 < 0)
		{
			System.out.println(s1);
		}
		else if (no1 < 0 || no2 < 0)
		{
			System.out.println("-" + s1);
		}
		else
		{
			System.out.println(s1);
		}
	}
}
